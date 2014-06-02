package okapied.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Booking;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.PriceInterface;
import okapied.entity.Property;
import okapied.entity.PropertyDay;
import okapied.exception.AvailabilityException;
import okapied.exception.BookingException;
import okapied.exception.CancellationException;
import okapied.exception.InputValidationException;
import okapied.exception.OkaSecurityException;
import okapied.exception.PaymentException;
import okapied.exception.PriceException;
import okapied.exception.RefundException;
import okapied.exception.RepeatedTransactionException;
import okapied.util.Configuration;
import okapied.util.DateUtil;
import okapied.util.MathUtil;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import adaptivepayments.AdaptivePayments;

import com.paypal.svcs.services.PPFaultMessage;
import com.paypal.svcs.types.ap.ExecutePaymentRequest;
import com.paypal.svcs.types.ap.ExecutePaymentResponse;
import com.paypal.svcs.types.ap.PayRequest;
import com.paypal.svcs.types.ap.PayResponse;
import com.paypal.svcs.types.ap.PaymentDetailsRequest;
import com.paypal.svcs.types.ap.PaymentDetailsResponse;
import com.paypal.svcs.types.ap.Receiver;
import com.paypal.svcs.types.ap.ReceiverList;
import com.paypal.svcs.types.common.ClientDetailsType;
import com.paypal.svcs.types.common.DetailLevelCode;
import com.paypal.svcs.types.common.ErrorData;
import com.paypal.svcs.types.common.RequestEnvelope;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

@Transactional
public class BookingService implements BookingServiceInterface 
{
	PriceService priceService;
	PropertyService propertyService;
	MailService mailService;
	DAO dao;
	BookingServiceInterface listener = null;
	RefundServiceInterface refundService;
	public AdaptivePayments adaptivePayments;
	AccountService accountService;
	
	public static final Logger log = Logger.getLogger(BookingService.class);
	
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#makeReservation(okapied.entity.Property, okapied.entity.OkapiedUserDetails, java.util.Calendar, java.util.Calendar, java.lang.Integer)
	 */
	@Override
	@Transactional(rollbackFor={AvailabilityException.class})
    public Booking makeReservation(Property property, OkapiedUserDetails user, 
    		Calendar startDate, Calendar endDate, Integer people) 
	    throws AvailabilityException, InputValidationException, PriceException
    {
		// validate dates
		validateDates(startDate,endDate);
		
		validateMinBookingPeriod(startDate,endDate,property);
		
    	// we don't drop existing reservations any more since they are ignored for the current user
    	//dropExistingReservations(property,user); 
    	dropExpiredReservations(user); // clean up database
    	List<Calendar> period = DateUtil.getPeriodExcludingEnd(startDate,endDate);
    	propertyService.availability(property,period,user);
    	for( PropertyDay day : property.getInfoList() )
    	{
    		if( !day.isAvailable() || day.isReserved() )
    		{
    			throw new AvailabilityException("Property is not available for the entire given period");
    		}
    	}
    	Booking booking = new Booking();
    	booking.setAvailableBool(false);
    	booking.setEndDate(endDate.getTime());
    	Date compareDate = startDate.getTime();
    	booking.setStartDate(startDate.getTime());
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.MINUTE,Configuration.instance().getIntProperty("BOOKING_RESERVATION_TIME_MIN"));
    	booking.setExpirationDate(now.getTime());
    	booking.setPeople(people);
    	booking.setProperty(property);
    	booking.setReservedBool(true);
    	booking.setBookingStatus(Booking.BOOKING_STATUS_RESERVATION);
    	booking.setInfoList(property.getInfoList());
    	
    	// calculate total
    	Float total = calculateTotal(property,period);
    	booking.setTotal(total);
    	
    	booking.setUser(user);
    	for( PropertyDay day : booking.getInfoList() )
    	{
    		dao.persist(day);
    	}
    	dao.persist(booking);
    	return booking;
    }
	
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#calculateTotal(okapied.entity.Property, java.util.Calendar, java.util.Calendar)
	 */
	@Override
	@Transactional(rollbackFor={AvailabilityException.class})
    public Float calculateTotal(Property property, Calendar startDate, Calendar endDate) 
	    throws AvailabilityException, InputValidationException, PriceException
    {	
		// validate dates
		validateDates(startDate,endDate);
    	
    	List<Calendar> period = DateUtil.getPeriodExcludingEnd(startDate,endDate);
    	propertyService.availability(property,period);
    	for( PropertyDay day : property.getInfoList() )
    	{
    		if( !day.isAvailable() || day.isReserved() )
    		{
    			throw new AvailabilityException("Property is not available for the entire given period");
    		}
    	}
    	
    	// calculate total
    	Float total = calculateTotal(property,period);
    	return total;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#dropExistingReservations(okapied.entity.Property, okapied.entity.OkapiedUserDetails)
	 */
    @Override
	public void dropExistingReservations(Property property, OkapiedUserDetails user)
    {
    	
    	String queryString = "from Booking where property.id=:propertyId and" +
    	    " user.id=:userId and reserved=:reserved";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("propertyId",property.getId());
    	query.setParameter("userId",user.getId());
    	query.setParameter("reserved", 1);
    	List<Booking> bookings = query.getResultList();
    	for( Booking booking : bookings ) dao.remove(booking);
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#dropExpiredReservations(okapied.entity.OkapiedUserDetails)
	 */
    @Override
	public void dropExpiredReservations(OkapiedUserDetails user)
    {
        Calendar now = DateUtil.getCurrentCalendarCompareTZ();
        String queryString = "from Booking where user.id=:userId" +
            " and expirationDate <  :currentDate and reserved=:reserved";
        Query query = dao.getEntityManager().createQuery(queryString);
        query.setParameter("currentDate",now.getTime());
        query.setParameter("reserved", 1);
        query.setParameter("userId",user.getId());
        List<Booking> bookings = query.getResultList();
        for( Booking booking : bookings ) dao.remove(booking);
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#calculateTotal(okapied.entity.Property, java.util.List)
	 */
    @Override
	public Float calculateTotal(Property property, List<Calendar> period)
       throws PriceException
    {
    	List<PriceInterface> prices = priceService.get(property, period);
    	Float total = 0f;
    	for( Calendar date : period )
    	{
    		PriceInterface price = priceService.price(prices, date);
    		if( price.getPrice() == null ) 
    	       throw new PriceException("No price for property on date: " +
    	           date.getTime().toString() + " propertyId: " + property.getId());
    		total += price.getPrice();
    		
    	}
    	return total;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#validateDates(java.util.Calendar, java.util.Calendar)
	 */
    @Override
	public void validateDates(Calendar startDate, Calendar endDate) throws InputValidationException
    {
    	if( startDate.compareTo(endDate) >= 0 ) 
    		throw new InputValidationException("Start must be before end date");
    	
        long bookingIntervalDays = Configuration.instance().getIntProperty("MAXIMUM_BOOKING_INTERVAL_DAYS");
        long millisInADay = 1000*60*60*24;
        long bookingIntervalMillis = bookingIntervalDays*millisInADay;
        if( endDate.getTimeInMillis() - startDate.getTimeInMillis() > bookingIntervalMillis)
    		throw new InputValidationException("Exceeded maximum booking period of " + 
    				Configuration.instance().getIntProperty("MAXIMUM_BOOKING_INTERVAL_DAYS") +
    				" days");
    	
    	Calendar lastDate = new GregorianCalendar(endDate.getTimeZone());
    	lastDate.setTimeZone(endDate.getTimeZone());
    	lastDate.add(Calendar.DAY_OF_YEAR,Configuration.instance().getIntProperty("MAX_FORWARD_BOOKING_DAYS"));
    	if( endDate.compareTo(lastDate) > 0 )
    	{
    		throw new InputValidationException("Booking are allow ed a maximum of " +
    				Configuration.instance().getIntProperty("MAX_FORWARD_BOOKING_DAYS") +
    				" days in advance");
    	}
    }
    
	public void validateMinBookingPeriod(Calendar startDate, Calendar endDate, Property property) 
	    throws InputValidationException
    {	
        long bookingIntervalDays = property.getMinNights();
        long millisInADay = 1000*60*60*24;
        long bookingIntervalMillis = bookingIntervalDays*millisInADay;
        if( endDate.getTimeInMillis() - startDate.getTimeInMillis() < bookingIntervalMillis)
    		throw new InputValidationException(
    		    "The minimum booking period for this property is " + bookingIntervalDays + " nights");
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getBookingsForUser(okapied.entity.OkapiedUserDetails)
	 */
    @Override
	@SuppressWarnings("unchecked")
	public List<Booking> getBookingsForUser(OkapiedUserDetails user)
    {
    	String queryString = "from Booking where user.id=:id";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("id",user.getId());
    	return query.getResultList();
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#startBooking(java.lang.Integer)
	 */
    @Override
	public String startBooking(Integer id) throws FatalException, SSLConnectionException, PPFaultMessage, IOException, BookingException
    {
    	Booking booking = get(id);
    	return startBooking(booking);
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#startBooking(okapied.entity.Booking)
	 */
    @Override
	@Transactional(rollbackFor={FatalException.class,SSLConnectionException.class,PPFaultMessage.class})
    public String startBooking(Booking booking) 
        throws FatalException, SSLConnectionException, IOException, BookingException, PPFaultMessage
    {	    	
    	if( booking.getProperty().retrieveFrozen() )
    	{
    		log.error("Attempted to book property that is frozen booking: " + booking.getId());
    		throw new BookingException("Bookings are temporarily not being accepted for this property");
    	}
    	
        AdaptivePayments ap = getAdaptivePayments();	
        PayRequest request   = new PayRequest();
		ReceiverList list = new ReceiverList();
		ClientDetailsType cl = new ClientDetailsType();
		RequestEnvelope en = new RequestEnvelope();
		
		Receiver rec1 = new Receiver();
		rec1.setAmount(roundBigDecimal(booking.getTotal()));
		rec1.setEmail(Configuration.instance().getStringProperty("MY_PAYPAL_EMAIL"));
        log.debug("Primary reciever: " + rec1.getEmail() + " amount: " + rec1.getAmount());
		rec1.setPrimary(true);
		list.getReceiver().add(rec1);
		
		Receiver rec2 = new Receiver();
		rec2.setAmount(roundBigDecimal(booking.getPropertyOwnerAmount()));
		rec2.setEmail(booking.getProperty().getOwner().getEmail());
        log.debug("Secondary reciever: " + rec2.getEmail() + " amount: " + rec1.getAmount());
		rec2.setPrimary(false);
		list.getReceiver().add(rec2);
		
		en.setErrorLanguage("en_US");
		//cl.setDeviceId("pudurSDK");
		cl.setIpAddress(booking.getUser().getIp());
		cl.setApplicationId(Configuration.instance().getStringProperty("PAYPAL_APPLICATION_ID"));
		
		String cancelUrl = Configuration.instance().getStringProperty("BASE_REQUEST_URL") + "/App/Booking/cancel?bookingId=" + booking.getId();
		String returnUrl = Configuration.instance().getStringProperty("BASE_REQUEST_URL") + "/App/ViewBooking/list?id=" + booking.getId();
		
		request.setCancelUrl(cancelUrl);
		request.setReturnUrl(returnUrl);
		//request.setSenderEmail(booking.getUser().getEmail());
		request.setCurrencyCode(booking.getProperty().getCurrencyCode().getCode());
		request.setClientDetails(cl);
		request.setReceiverList(list);
		request.setRequestEnvelope(en);
		request.setActionType("PAY_PRIMARY");
		request.setFeesPayer(Configuration.instance().getStringProperty("PAYPAL_FEES_PAYER"));
		
		String trackingId = generateTrackingKey(booking.getId());
		request.setTrackingId(trackingId);
		String notificationUrl = Configuration.instance().getStringProperty("IPN_URL") + booking.getId();
		request.setIpnNotificationUrl(notificationUrl);
		
		log.debug("Notification url: " + notificationUrl);
		
		Date now = DateUtil.getCurrentCalendarCompareTZ().getTime();
		
		PayResponse response;
		
		try
		{
		    response  = ap.pay(request);
		}
		catch( PPFaultMessage e)
		{
			for( ErrorData errorData : e.getFaultInfo().getError() )
			{
				if( errorData.getErrorId() == WRONG_CURRENCY_ERROR_ID )
				{
					emailOwnerWrongCurrency(booking);
					emailMeWrongCurrency(booking);
					log.error("Property currency is not set up in owner PayPay, property: " + booking.getProperty().getId());
				}
			}
			throw e;
		}
		
		String payKey = response.getPayKey();
		booking.setPayKey(payKey);
		booking.setTrackingId(trackingId);
		booking.updateExpiration();
		booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
		booking.setBookingDate(now);
		booking.setRefundPolicy(booking.getProperty().getRefundPolicy());
		booking.setCurrencyCode(booking.getProperty().getCurrencyCode());
		booking.setConfirmationEmailBool(false);
		dao.update(booking);
		
		log.info("Payment success, key: " + payKey);
		return Configuration.instance().getStringProperty("PAYPAL_REDIRECT_URL") + payKey;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#processIPN(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	@Transactional(rollbackFor={AddressException.class,MessagingException.class})
    public void processIPN(Integer bookingId, String trackingId, String payKey, String senderEmail, String status) 
    {	
    	Booking booking = get(bookingId);  	
    	if( !verifyCompletion(booking,trackingId,payKey,senderEmail) )
    	{
    		return;
    	}
    	if( booking.getBookingStatus().equals(Booking.BOOKING_STATUS_PENDING_PAYMENT) )
    	{
            processBookingStatusReciept(booking, status);
    	}
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#verifyCompletion(okapied.entity.Booking, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public boolean verifyCompletion(Booking booking, String trackingId, String payKey, String senderEmail)
    {
    	if( booking.getTrackingId().equals(trackingId) &&
    		booking.getPayKey().equals(payKey)  ) 
    	    return true;
    	else
    	{
    		log.error("Booking confirmation request denied" +
    		    " bookingId: " + booking.getId() +
    		    " trackingId: " + trackingId +
    		    " payKey: " + payKey +
    		    " senderEmail: " + senderEmail);
    	    return false;
    	}
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#cancelBooking(okapied.entity.Booking, java.lang.Integer)
	 */
    @Override
	public void cancelBooking(Booking booking, Integer userId) throws CancellationException
    {
    	if( !booking.getUser().getId().equals(userId) )
    	{
    		log.error("Attempt to cancel a booking that is not owned booking id: " + booking.getId());
    		throw new CancellationException("You do not own the booking and hence cannot cancel it");
    	}
    	if( !booking.getBookingStatus().equals(Booking.BOOKING_STATUS_PENDING_PAYMENT) )
    	{
    		log.error("Attempt to cancel a booking with incorrect status booking id: " + booking.getId());
    		throw new CancellationException("The given booking cannot be cancelled");
    	}
    	updateCancelledBooking(booking);
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#emailCancellation(okapied.entity.Booking)
	 */
    @Override
	public void emailCancellation(Booking booking)
    {
    	String subject = "Okapied Booking Cancelled";
    	String message = 
    	    "Hi " + booking.getUser().getName() + ",\n\n" +
    	    "This message is to let you know that your booking for the property " +
    	    booking.getProperty().getName() +
    	    " has been cancelled.\n\n" +
    	    "The booking reference id is: " + booking.getId() + "\n\n" +
    	    "If you have any queries please contact us on " +
            Configuration.instance().getStringProperty("ADMIN_EMAIL") +
    	    "\n\n" +
    	    "Regards\n\n" +
    	    "Okapied";
    	try {
			mailService.sendMessage(booking.getUser().retrieveBestEmail(), subject, message);
		} catch (AddressException e) {
			log.error(e.getMessage());
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
    }

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#emailCompletion(okapied.entity.Booking)
	 */
	@Override
	public void emailCompletion(Booking booking)
	{
		String subject = "Okapied Booking Confirmation";
    	String message = 
    	    "Hi " + booking.getUser().getName() + ",\n\n" +
    	    "This message is to let you know that your booking for the property " +
    	    booking.getProperty().getName() +
    	    " has been confirmed.\n\n" +
    	    "You are checking in on " +
    	    formatDate(booking.getStartDate(),booking.getProperty().getLocation().getTz()) +
    	    " and checking out on " +
    	    formatDate(booking.getEndDate(),booking.getProperty().getLocation().getTz()) + ".\n\n" +
    	    "Standard Okapied check in time is 2pm and check out time is 11am\n\n" +
    	    "Booking reference number: " + booking.getId() + "\n\n" +
    	    "The property address is: " + 
    	        booking.getProperty().retrieveAddressText() + "\n\n";
    	if( booking.getProperty().getPropertyDetails().getCheckInInstructions() != null &&
    	    booking.getProperty().getPropertyDetails().getCheckInInstructions().trim().length() > 0 )
    	{
    	    message += "The following check in instructions have been provided by the property owner:\n\n" +
    	    booking.getProperty().getPropertyDetails().getCheckInInstructions() + "\n\n";
    	}
    	message += "You can view and manage your booking at the following location: " +
    	    Configuration.instance().getStringProperty("BASE_REQUEST_URL") + "/App/ViewBooking/list?id=" + booking.getId() +
    	    "\n\nHave a great time!" +
    	    "\n\nRegards\n\n" +
    	    "Okapied";
    	try {
			mailService.sendMessage(booking.getUser().retrieveBestEmail(), subject, message);
		} catch (AddressException e) {
			log.error(e.getMessage());
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#emailPropertyOwnerCompletion(okapied.entity.Booking)
	 */
	@Override
	public void emailPropertyOwnerCompletion(Booking booking)
	{
		String subject = "Your Property has been Okapied";
    	String message = 
    	    "Hi " + booking.getProperty().getOwner().getName() + ",\n\n" +
    	    "This message is to let you know that a booking for your property " +
    	    booking.getProperty().getName() +
    	    " has been confirmed.\n\n" +
    	    "The guests are checking in on " +
    	    formatDate(booking.getStartDate(),booking.getProperty().getLocation().getTz()) +
    	    " and checking out on " +
    	    formatDate(booking.getEndDate(),booking.getProperty().getLocation().getTz()) + ".\n\n" +
    	    "Standard Okapied check in time is 2pm and check out time is 11am\n\n" +
    	    "Booking reference number: " + booking.getId() + "\n\n" +
    	    "The booking is under " + booking.getUser().getName() + " who you can contact at " +
    	    booking.getUser().retrieveBestEmail() + "\n\n" +
    	    "You can view the details of this booking here " + 
    	    Configuration.instance().getStringProperty("BASE_REQUEST_URL") + "/App/ViewBookingOwner/list?id=" + booking.getId() +
    	    "\n\nRegards\n\n" +
    	    "Okapied";
    	try {
			mailService.sendMessage(booking.getProperty().getOwner().retrieveBestEmail(), subject, message);
		} catch (AddressException e) {
			log.error(e.getMessage());
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#emailPropertyOwnerError(okapied.entity.Booking)
	 */
	@Override
	public void emailPropertyOwnerError(Booking booking)
	{
		String subject = "Okapied Booking Error";
    	String message = 
    	    "Hi " + booking.getProperty().getOwner().getName() + ",\n\n" +
    	    "There has been an error processing a booking for your property " +
    	    booking.getProperty().getName() +
    	    "\n\n" +
    	    " This is commonly caused by a PayPal configuration error." +
    	    " Please make sure that the currency specified on the property page: " +
    	    Configuration.instance().getStringProperty("BASE_REQUEST_URL") +
    	    "/App/Publish/list?propertyId=" + booking.getProperty().getId() +
    	    " is a currency that is accepted by your PayPal account." +
    	    " Also make sure that the email you have provided to Okapied is a current valid PayPal account.\n\n" +
    	    " Please contact us at " + Configuration.instance().getStringProperty("ADMIN_EMAIL") +
    	    " if you need any help." +
    	    "\n\nRegards\n\n" +
    	    "Okapied";
    	try {
			mailService.sendMessage(booking.getProperty().getOwner().retrieveBestEmail(), subject, message);
		} catch (AddressException e) {
			log.error(e.getMessage());
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#emailOwnerWrongCurrency(okapied.entity.Booking)
	 */
	@Override
	public void emailOwnerWrongCurrency(Booking booking)
	{
		String subject = "Okapied Booking Error";
    	String message = 
    	    "Hi " + booking.getProperty().getOwner().getName() + ",\n\n" +
    	    "This is a warning that Okapied is unable to process bookings for your property " +
    	    booking.getProperty().getName() +
    	    "\n\n" +
    	    "Your PayPal account " + booking.getProperty().getOwner().getEmail() +
    	    " is not configured to accept the currency of the property: " 
    	    + booking.getProperty().getCurrencyCode().getName() + ".\n\n" +
    	    "Please either set up your PayPal account to accept this currency" +
    	    " or change the currency of the property on the property listing page " +
    	    Configuration.instance().getStringProperty("BASE_REQUEST_URL") + 
    	    "/App/Publish/list?propertyId=" + booking.getProperty().getId() +
    	    " to the currency of your PayPal account. Otherwise no one will be able to book your property.\n\n" +
    	    " Please contact us at " + Configuration.instance().getStringProperty("ADMIN_EMAIL") +
    	    " if you need any help." +
    	    "\n\nRegards\n\n" +
    	    "Okapied";
    	try {
			mailService.sendMessage(booking.getProperty().getOwner().retrieveBestEmail(), subject, message);
		} catch (AddressException e) {
			log.error(e.getMessage());
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#emailMeWrongCurrency(okapied.entity.Booking)
	 */
	@Override
	public void emailMeWrongCurrency(Booking booking)
	{
		String subject = "WRONG CURRENCY";
    	String message = 
    	    "Wrong currency for property.\n" +
    	    "Booking id: " + booking.getId() + "\n" +
    	    "Property id: " + booking.getProperty().getId() + "\n" +
    	    "Currency: " + booking.getProperty().getCurrencyCode().getCode();
    	try {
			mailService.sendMessage(Configuration.instance().getStringProperty("ADMIN_EMAIL"), subject, message);
		} catch (AddressException e) {
			log.error(e.getMessage());
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
	}
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#roundBigDecimal(double)
	 */
    @Override
	public BigDecimal roundBigDecimal(double value)
    {
    	return MathUtil.roundBigDecimal(value);
    }
    
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#generateTrackingKey(java.lang.Integer)
	 */
	@Override
	public String generateTrackingKey(Integer bookingId)
	{
		return MathUtil.generateTrackingKey(bookingId);
	}

    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#get(java.lang.Integer)
	 */
    @Override
	public Booking get(Integer id)
    {
    	return (Booking) dao.get(Booking.class.getName(), "id", id);
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getWithCancel(java.lang.Integer)
	 */
    @Override
	public Booking getWithCancel(Integer id)
    {
    	Booking booking = (Booking) dao.get(Booking.class.getName(), "id", id);
    	booking.getCancel();
    	return booking;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#retrieveBookingStatus(java.lang.Integer)
	 */
    @Override
	public String retrieveBookingStatus(Integer id) 
        throws FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	Booking booking = get(id);
    	AdaptivePayments ap = getAdaptivePayments();
    	PaymentDetailsRequest request = new PaymentDetailsRequest();
    	request.setPayKey(booking.getPayKey());
    	request.setTrackingId(booking.getTrackingId());
    	
    	RequestEnvelope en = new RequestEnvelope();
    	en.setErrorLanguage("en_US");
    	request.setRequestEnvelope(en);
    	
    	PaymentDetailsResponse response = ap.paymentDetails(request);
    	return response.getStatus();
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#loadBooking(java.lang.Integer, java.lang.Integer)
	 */
    @Override
	@Transactional(rollbackFor={OkaSecurityException.class,FatalException.class,
        SSLConnectionException.class,PPFaultMessage.class,IOException.class})
    public Booking loadBooking(Integer id, Integer userId) 
        throws OkaSecurityException, FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	log.debug("Loading booking: " + id);
    	Booking booking = get(id);
    	if( userId.equals(booking.getUser().getId()) ||
    	    userId.equals(booking.getProperty().getOwner().getId()) )
    	{
	    	if( booking.getBookingStatus() != null && 
	    	    booking.getBookingStatus().equals(Booking.BOOKING_STATUS_PENDING_PAYMENT) )
	    	{
	    	    booking = checkBookingStatus(booking);
	    	}
	    	return booking;
    	}
    	else
    	{
    		log.error("Attempted to access booking when not user or owner, booking id: " + id + 
    		    " userId: " + userId);
    		throw new OkaSecurityException("You must have made the booking or be the owner of the property to view a booking");
    	}
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#checkBookingStatus(okapied.entity.Booking)
	 */
    @Override
	@Transactional(rollbackFor={AddressException.class,MessagingException.class})
    public Booking checkBookingStatus(Booking booking) 
        throws FatalException, SSLConnectionException, PPFaultMessage, IOException
    {	
    	log.info("Checking the status of booking: " + booking.getId());
    	if( booking.getBookingStatus() != null && 
	    	    booking.getBookingStatus().equals(Booking.BOOKING_STATUS_PENDING_PAYMENT) )
    	{
    		String status = getListener().retrieveBookingStatus(booking.getId());
    		processBookingStatusReciept(booking, status);
    	}
    	return booking;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#updateCompleteBooking(okapied.entity.Booking)
	 */
    @Override
	public void updateCompleteBooking(Booking booking)
    {
    	// check to see if there are any overlapping bookings
    	if( overlappingBookings(booking) )
    	{
    		if( booking.expired() )
    		{
    			try
    			{
    				refundService.fullRefund(booking);
    				refundService.emailUserFullRefundSuccess(booking);
    			}
    			catch(RefundException e)
    			{
    				refundService.emailUserFullRefundFail(booking);
    				refundService.emailAdminFullRefundFail(booking);
    			} 
    			catch (RepeatedTransactionException e) 
    			{
					log.info(e);
				}
    			return;
    		}
    		else
    		{
    			log.error("Overlapping booking: booking id: " +
    			    booking.getId());
    		}
    	}

		booking.setReservedBool(false);
		booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
	    log.info("Booking complete: " + booking.getId());
//      Removed now, only scheduler sends out completion emails
//	    emailCompletion(booking);
//	    emailPropertyOwnerCompletion(booking);
		dao.update(booking);
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#updateErrorBooking(okapied.entity.Booking)
	 */
    @Override
	public void updateErrorBooking(Booking booking)
    {
		booking.setReservedBool(false);
		booking.setBookingStatus(Booking.BOOKING_STATUS_ERROR);
    	log.error("Booking error: " + booking.getId());
    	emailPropertyOwnerError(booking);
		dao.update(booking);
    }
    
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#updateCancelledBooking(okapied.entity.Booking)
	 */
    @Override
	public void updateCancelledBooking(Booking booking)
    {
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CANCELLED);
    	booking.setReservedBool(false);
    	dao.update(booking);
    	emailCancellation(booking);
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getBookingsWithStatus(java.lang.String)
	 */
    @Override
	public List<Booking> getBookingsWithStatus(String status)
    {
    	String queryString = "from Booking where bookingStatus=:status";
    	Query query = dao.getEntityManager().createQuery(queryString);
        query.setParameter("status",status);
        return query.getResultList();
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getBookingsWithStatusNoConfirmationEmail(java.lang.String)
	 */
    @Override
	public List<Booking> getBookingsWithStatusNoConfirmationEmail(String status)
    {
    	String queryString = "from Booking where bookingStatus=:status";
    	queryString += " and confirmationEmail = 0";
    	Query query = dao.getEntityManager().createQuery(queryString);
        query.setParameter("status",status);
        return query.getResultList();
    }
    
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#checkAllPendingPayments()
	 */
    @Override
	public List<Booking> checkAllPendingPayments() 
    {
    	List<Booking> bookingsPendingPayment = getBookingsWithStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	for( Booking booking : bookingsPendingPayment )
    	{
    		try {
				checkBookingStatus(booking);
			} catch (FatalException e) {
				log.error(e);
				log.error("Failed to check booking status for booking: + " + booking.getId());
			} catch (SSLConnectionException e) {
				log.error(e);
				log.error("Failed to check booking status for booking: + " + booking.getId());
			} catch (PPFaultMessage e) {
				log.error(e);
				log.error("Failed to check booking status for booking: + " + booking.getId());
				for( ErrorData error : e.getFaultInfo().getError() ) log.error(error.getErrorId() + " " + error.getMessage());
			} catch (IOException e) {
				log.error(e);
				log.error("Failed to check booking status for booking: + " + booking.getId());
			}
    	}
    	return bookingsPendingPayment;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#sendMoneyToPropertyOwners()
	 */
    @Override
	public void sendMoneyToPropertyOwners()
    {
    	log.debug("Sending money to all property owners");
    	// load all Bookings that have status complete but have not been paid
    	List<Booking> bookings = loadAllConfirmedBookingsUnpaidToPropertyOwners();
    	
    	for( Booking booking : bookings )
    	{
    		log.debug("Confirmed unpaid booking: " + booking.getId());
            if( isPaymentTime(booking) && !booking.getProperty().getOwner().getFrozenBool() )
            {
                 try {
					sendMoneyToOwner(booking);
				} catch (FatalException e) {
					log.error(e);
					log.error("Failed to send money to owner for booking: " + booking.getId());
				} catch (SSLConnectionException e) {
					log.error(e);
					log.error("Failed to send money to owner for booking: " + booking.getId());
				} catch (PPFaultMessage e) {
					log.error(e);
					log.error("Failed to send money to owner for booking: " + booking.getId());
					for( ErrorData error : e.getFaultInfo().getError() ) log.error(error.getErrorId() + " " + error.getMessage());
				} catch (IOException e) {
					log.error(e);
					log.error("Failed to send money to owner for booking: " + booking.getId());
				}
            }
    	}
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#isPaymentTime(okapied.entity.Booking)
	 */
    @Override
	public boolean isPaymentTime(Booking booking)
    {
    	// if the current time in the property timezone is greater than 11am on the day after
    	// the start date
		Calendar currentTimeInPropertyTZ = booking.getProperty().retrieveCurrentTime();
		Calendar startDate = booking.getProperty().retrieveCurrentTime();
		startDate.setTime(booking.getStartDate());	
		int hourOffset = Configuration.instance().getIntProperty("PAYMENT_DATE_HOUR_OFFSET");
		startDate.add(Calendar.HOUR_OF_DAY,hourOffset);
		if( currentTimeInPropertyTZ.getTimeInMillis() > startDate.getTimeInMillis() ) return true;
		else return false;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#loadAllConfirmedBookingsUnpaidToPropertyOwners()
	 */
    @Override
	public List<Booking> loadAllConfirmedBookingsUnpaidToPropertyOwners()
    {
    	String queryString = "from Booking where bookingStatus=:status and paymentComplete=0";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("status",Booking.BOOKING_STATUS_CONFIRMED);
    	return query.getResultList();
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#sendMoneyToOwner(okapied.entity.Booking)
	 */
    @Override
	public Booking sendMoneyToOwner(Booking booking) 
        throws FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	AdaptivePayments ap = getAdaptivePayments();
    	ExecutePaymentRequest request = new ExecutePaymentRequest();
    	request.setPayKey(booking.getPayKey());
    	
    	RequestEnvelope en = new RequestEnvelope();
    	en.setErrorLanguage("en_US");
    	en.setDetailLevel(DetailLevelCode.RETURN_ALL);
    	request.setRequestEnvelope(en);
    	
    	ExecutePaymentResponse response = ap.executePayment(request);
        if( response.getPaymentExecStatus().equals("COMPLETED") )
        {
        	log.info("Booking payment complete: " + booking.getId());
        	booking.setPaymentCompleteBool(true);
        	dao.update(booking);
        	emailOwnerPaymentCompletion(booking);
        }
        else
        {
        	log.error("Error executing payment for booking: " + booking.getId());
        }
    	
    	return booking;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#emailOwnerPaymentCompletion(okapied.entity.Booking)
	 */
    @Override
	public void emailOwnerPaymentCompletion(Booking booking)
    {
		String subject = "You have been paid";
    	String message = 
    	    "Hi " + booking.getProperty().getOwner().getName() + ",\n\n" +
    	    "This message is to let you know that a booking for your property " +
    	    booking.getProperty().getName() +
    	    " has been paid.\n\n" +
    	    " The guests checked in on " +
    	    formatDate(booking.getStartDate(),booking.getProperty().getLocation().getTz()) +
    	    " and are checking out on " +
    	    formatDate(booking.getEndDate(),booking.getProperty().getLocation().getTz()) + "." +
    	    "\n\nRegards\n\n" +
    	    "Okapied";
    	try {
			mailService.sendMessage(booking.getProperty().getOwner().retrieveBestEmail(), subject, message);
		} catch (AddressException e) {
			log.error(e.getMessage());
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getAdaptivePayments()
	 */
    @Override
	public AdaptivePayments getAdaptivePayments() throws IOException, FatalException, SSLConnectionException
    {
    	if( adaptivePayments != null ) return adaptivePayments;
    	InputStream in = getClass().getResourceAsStream("paypal_sdk_client.properties");
    	Properties sdkClientProperties = new Properties();
    	sdkClientProperties.load( in );
    	AdaptivePayments ap = new AdaptivePayments(sdkClientProperties);
    	return ap;
    }
    
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getPropertyService()
	 */
	@Override
	public PropertyService getPropertyService() {
		return propertyService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#setPropertyService(okapied.service.PropertyService)
	 */
	@Override
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getDao()
	 */
	@Override
	public DAO getDao() {
		return dao;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#setDao(okapied.dao.DAO)
	 */
	@Override
	public void setDao(DAO dao) {
		this.dao = dao;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getPriceService()
	 */
	@Override
	public PriceService getPriceService() {
		return priceService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#setPriceService(okapied.service.PriceService)
	 */
	@Override
	public void setPriceService(PriceService priceService) {
		this.priceService = priceService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getMailService()
	 */
	@Override
	public MailService getMailService() {
		return mailService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#setMailService(okapied.service.MailService)
	 */
	@Override
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getListener()
	 */
	@Override
	public BookingServiceInterface getListener() {
		if( listener == null ) return this;
		else return listener;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#setListener(okapied.service.BookingServiceInterface)
	 */
	@Override
	public void setListener(BookingServiceInterface listener) {
		this.listener = listener;
	}
	
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#processBookingStatusReciept(okapied.entity.Booking, java.lang.String)
	 */
    @Override
	public void processBookingStatusReciept(Booking booking, String status) 
    {
    	if( status.equals(STATUS_INCOMPLETE))
        {
		    updateCompleteBooking(booking);
        }
    	else if( status.equals(STATUS_ERROR) )
    	{
    		updateErrorBooking(booking);
    	}
    	else if( status.equals(STATUS_CREATED))
    	{
    		log.info("STATUS_CREATED for booking: " + booking.getId());
    	}
    	else
    	{
    		log.error("Unknown PayPal status received: " + status);
    	}
	}
    
    /* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#overlappingBookings(okapied.entity.Booking)
	 */
    @Override
	public boolean overlappingBookings(Booking booking)
    {
    	String queryString = "from Booking where property.id=:propertyId" +
    	    " and bookingStatus in (:statusCodes)" +
    	    " and user.id != :userId" +
    	    " and startDate < :endDate " +
    	    " and endDate > :startDate";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("propertyId",booking.getProperty().getId());
    	List<String> statusCodes = new ArrayList<String>();
    	statusCodes.add(Booking.BOOKING_STATUS_RESERVATION);
    	statusCodes.add(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	statusCodes.add(Booking.BOOKING_STATUS_CONFIRMED);
    	query.setParameter("statusCodes",statusCodes);
    	query.setParameter("userId",booking.getUser().getId());
    	query.setParameter("startDate",booking.getStartDate());
    	query.setParameter("endDate",booking.getEndDate());
    	
    	List<Booking> bookings = query.getResultList();
    	boolean have = false;
    	for( Booking result : bookings )
    	{
    		if( result.getBookingStatus().equals(Booking.BOOKING_STATUS_RESERVATION) &&
    		    result.expired() )
    		{
    			// expired reservation, doesn't count
    		}
    		else
    		{
    			log.info("Booking Status: " + result.getBookingStatus().equals(Booking.BOOKING_STATUS_RESERVATION));
    			log.info("Expired: " + result.expired());
    			log.info("Overlapping booking id: " + result.getId());
    			have = true;
    		}
    	}
    	return have;
    }
    
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#directPayment(okapied.entity.Booking, java.lang.Float, java.lang.String)
	 */
	@Override
	public void directPayment(Booking booking, Float amount, String email) 
	    throws PaymentException 
	{
		try
		{
	        AdaptivePayments ap = getAdaptivePayments();	
	        PayRequest request   = new PayRequest();
			ReceiverList list = new ReceiverList();
			ClientDetailsType cl = new ClientDetailsType();
			RequestEnvelope en = new RequestEnvelope();
			
			Receiver rec1 = new Receiver();
			rec1.setAmount(MathUtil.roundBigDecimal(amount));
			rec1.setEmail(email);
	        log.debug("Primary reciever: " + rec1.getEmail() + " amount: " + rec1.getAmount());
			list.getReceiver().add(rec1);
			
			en.setErrorLanguage("en_US");
			//cl.setDeviceId("pudurSDK");
			cl.setIpAddress(booking.getUser().getIp());
			cl.setApplicationId(Configuration.instance().getStringProperty("PAYPAL_APPLICATION_ID"));
			
			String cancelUrl = Configuration.instance().getStringProperty("BASE_REQUEST_URL") + "/App/Booking/cancel?bookingId=" + booking.getId();
			String returnUrl = Configuration.instance().getStringProperty("BASE_REQUEST_URL") + "/App/Booking/complete?propertyId=" + booking.getProperty().getId();
			
			request.setCancelUrl(cancelUrl);
			request.setReturnUrl(returnUrl);
			request.setSenderEmail(Configuration.instance().getStringProperty("MY_PAYPAL_EMAIL"));
			request.setCurrencyCode(booking.getProperty().getCurrencyCode().getCode());
			request.setClientDetails(cl);
			request.setReceiverList(list);
			request.setRequestEnvelope(en);
			request.setActionType("PAY");
			request.setFeesPayer("EACHRECEIVER");
			
			String trackingId = MathUtil.generateTrackingKey(booking.getId());
			request.setTrackingId(trackingId);
			String notificationUrl = Configuration.instance().getStringProperty("IPN_URL") + booking.getId();
			request.setIpnNotificationUrl(notificationUrl);
			
			log.debug("Notification url: " + notificationUrl);
			
			Date now = DateUtil.getCurrentCalendarCompareTZ().getTime();
			
			PayResponse response  = ap.pay(request);
			
			if( response.getPaymentExecStatus().equals(BookingServiceInterface.STATUS_COMPLETE))
			{
				String payKey = response.getPayKey();
				booking.setPayKey(payKey);
				booking.setTrackingId(trackingId);
				booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
				dao.update(booking);
				log.info("Booking success, key: " + payKey);
				return;
			}
			else
			{
				log.error("Failed to complete payment booking id: " + booking.getId() + " status: " + response.getPaymentExecStatus());
				booking.setBookingStatus(Booking.BOOKING_STATUS_ERROR);
				dao.update(booking);
			}
		}
		catch( IOException e) {
			log.error(e);
		} catch (FatalException e) {
			log.error(e);
		} catch (SSLConnectionException e) {
			log.error(e);
		} catch (PPFaultMessage e) {
			log.error(e);
		}
		
		throw new PaymentException("Payment failed");
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#getRefundService()
	 */
	@Override
	public RefundServiceInterface getRefundService() {
		return refundService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#setRefundService(okapied.service.RefundService)
	 */
	@Override
	public void setRefundService(RefundServiceInterface refundService) {
		this.refundService = refundService;
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#setAdaptivePayments(adaptivepayments.AdaptivePayments)
	 */
	@Override
	public void setAdaptivePayments(AdaptivePayments ap)
	{
		this.adaptivePayments = ap;
	}

	/* (non-Javadoc)
	 * @see okapied.service.BookingServiceInterface#sendConfirmationEmails()
	 */
	@Override
	public void sendConfirmationEmails() 
	{
		List<Booking> confirmedBookings = getBookingsWithStatusNoConfirmationEmail(Booking.BOOKING_STATUS_CONFIRMED);
		for( Booking booking : confirmedBookings )
		{
			 log.debug("Sending confirmation emails for booking: " + booking.getId());
			 emailCompletion(booking);
			 emailPropertyOwnerCompletion(booking);
			 booking.setConfirmationEmailBool(true);
			 dao.update(booking);
		}
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
//	@Override
//    public BigDecimal deductPayPalFees(double amount, Booking booking) throws ExchangeRateNotFoundException
//    {
//    	Float floatAmount = (float) amount;
//    	
//		CurrencyCode currency = booking.getCurrencyCode();
//		if( currency == null || currency.getUsd() == null || currency.getUsd() <=0 )
//		{
//			throw new ExchangeRateNotFoundException("The exchange rate for currency " + currency.getId() + " is missing.");
//		}
//		
//		// deduct fees from the rest that goes to the owner
//		// here we pass on the original PayPal fee that was charged to Okapied, even though the fee for
//		// the refund transaction will be less
//		Float transactionFeeInCurrency = Configuration.instance().getFloatProperty("FEE_PER_TRANSACTION_USD") 
//		    / currency.getUsd();
//		Float result = floatAmount*Configuration.instance().getFloatProperty("REFUND_HOST_PERCENTAGE_FEE")
//            - transactionFeeInCurrency;
//            
//        return new BigDecimal(result);
//    }
	
	@Override
	public String formatDate(Date date, String tz)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(Configuration.instance().getStringProperty("DATE_FORMAT"));
		sdf.setTimeZone(TimeZone.getTimeZone(tz));
		return sdf.format(date);
	}
}
