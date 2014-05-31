package okapied.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.dao.DAO;
import okapied.entity.Booking;
import okapied.entity.Charges;
import okapied.entity.Property;
import okapied.entity.Refund;
import okapied.exception.ExchangeRateNotFoundException;
import okapied.exception.InputValidationException;
import okapied.exception.InvalidPayPalAccountException;
import okapied.exception.PaymentException;
import okapied.exception.RefundException;
import okapied.exception.RepeatedTransactionException;
import okapied.util.Configuration;
import okapied.util.DateUtil;
import okapied.util.MathUtil;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import adaptivepayments.AdaptivePayments;

import com.paypal.sdk.core.nvp.NVPDecoder;
import com.paypal.sdk.core.nvp.NVPEncoder;
import com.paypal.sdk.exceptions.PayPalException;
import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.services.NVPCallerServices;
import com.paypal.svcs.services.PPFaultMessage;
import com.paypal.svcs.types.ap.PayRequest;
import com.paypal.svcs.types.ap.PayResponse;
import com.paypal.svcs.types.ap.Receiver;
import com.paypal.svcs.types.ap.ReceiverList;
import com.paypal.svcs.types.ap.RefundInfo;
import com.paypal.svcs.types.ap.RefundRequest;
import com.paypal.svcs.types.ap.RefundResponse;
import com.paypal.svcs.types.common.ClientDetailsType;
import com.paypal.svcs.types.common.RequestEnvelope;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

@Transactional
public class RefundService implements RefundServiceInterface 
{
	public static final Logger log = Logger.getLogger(RefundService.class);
	
	DAO dao;
	MailService mailService;
	AdaptivePayments adaptivePayments = null;
	PropertyService propertyService;
	AccountService accountService;
	
    /* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#fullRefund(okapied.entity.Booking)
	 */
    @Override
	public void fullRefund(Booking booking) 
        throws RefundException, RepeatedTransactionException
    {
    	AdaptivePayments ap;
		try {
			ap = getAdaptivePayments();
			fullRefund(booking,ap);
			return;
		} catch (IOException e) {
			log.error(e);
		} catch (FatalException e) {
			log.error(e);
		} catch (SSLConnectionException e) {
			log.error(e);
		}
    	log.error("There has been an error issuing a full refund for the booking: " + booking.getId());
    	booking.setBookingStatus(Booking.BOOKING_STATUS_ERROR);
    	dao.update(booking);
    	throw new RefundException("Error refunding booking, please contact the administrator");
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#fullRefund(okapied.entity.Booking, adaptivepayments.AdaptivePayments)
	 */
    @Override
	@Transactional(rollbackFor={NullPointerException.class})
    public void fullRefund(Booking booking, AdaptivePayments ap) 
        throws RefundException, RepeatedTransactionException
    {
    	RefundRequest request = new RefundRequest();
    	request.setPayKey(booking.getPayKey());
    	request.setTrackingId(booking.getTrackingId());
    	
    	RequestEnvelope en = new RequestEnvelope();
    	en.setErrorLanguage("en_US");
    	request.setRequestEnvelope(en);
    	
    	RefundResponse response;
		try {
			response = ap.refund(request);
	    	if( response.getRefundInfoList() != null )
	    	{
		    	for( RefundInfo info : response.getRefundInfoList().getRefundInfo() )
		    	{
		            if( info.getReceiver().getEmail().equals(Configuration.instance().getStringProperty("MY_PAYPAL_EMAIL")) )
		            {
		            	if(info.getRefundStatus().equals(BookingServiceInterface.STATUS_ALREADY_REFUNDED))
		            	{
		            		throw new RepeatedTransactionException("Duplicated refund attempted for booking: " + booking.getId());
		            	}
		            	
		        		Refund refund = new Refund();
		        		refund.setEncryptedRefundTransactionId(info.getEncryptedRefundTransactionId());
		        		if( info.getRefundFeeAmount() != null )
		        		    refund.setRefundFeeAmount(info.getRefundFeeAmount().floatValue());
		        		if( info.getRefundGrossAmount() != null )
		        			refund.setRefundGrossAmount(info.getRefundGrossAmount().floatValue());
		        		if( info.getRefundNetAmount() != null )
		        		    refund.setRefundNetAmount(info.getRefundNetAmount().floatValue());
		        		refund.setRefundStatus(info.getRefundStatus());
		        		refund.setRefundTransactionStatus(info.getRefundTransactionStatus());
		        		if( info.getTotalOfAllRefunds() != null)
		        		    refund.setTotalOfAllRefunds(info.getTotalOfAllRefunds().floatValue());
		        		
		        		booking.setRefund(refund);
		        		dao.update(booking);
		        		
		            	if( info.getRefundStatus().equals(BookingServiceInterface.STATUS_REFUNDED) )
		            	{
		            		if( info.getRefundFeeAmount().compareTo(new BigDecimal(0)) == 1 )
		            		{
		            			log.error("Full refund charged a fee for booking: " + booking.getId());
		            		}
		            		log.info("Booking refunded, booking id: " + booking.getId());
		            		booking.setBookingStatus(Booking.BOOKING_STATUS_REFUND_FULL);
		            		dao.update(booking);
		            		return;
		            	}
		            }
		    	}
	    	}
		} catch (FatalException e) {
	    	log.error(e);
		} catch (PPFaultMessage e) {
			log.error(e);
		}
    	
    	log.error("There has been an error issuing a full refund for the booking: " + booking.getId());
    	booking.setBookingStatus(Booking.BOOKING_STATUS_ERROR);
    	dao.update(booking);
    	throw new RefundException("Error refunding booking, please contact the administrator");
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getAdaptivePayments()
	 */
    @Override
	public AdaptivePayments getAdaptivePayments() throws IOException, FatalException, SSLConnectionException
    {
    	if( adaptivePayments != null ) 
    	{
    		log.info("Retrieving manually set adaptive payments");
    		return adaptivePayments;
    	}
    	AdaptivePayments ap = new AdaptivePayments(getPayPalProperties());
    	return ap;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getPayPalProperties()
	 */
    @Override
	public Properties getPayPalProperties() throws IOException
    {
    	InputStream in = getClass().getResourceAsStream("paypal_sdk_client.properties");
    	Properties sdkClientProperties = new Properties();
    	sdkClientProperties.load( in );
    	return sdkClientProperties;
    }
    
    /* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#setAdaptivePayments(adaptivepayments.AdaptivePayments)
	 */
    @Override
	public void setAdaptivePayments(AdaptivePayments ap)
    {
    	this.adaptivePayments = ap;
    }
    
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#emailUserFullRefundFail(okapied.entity.Booking)
	 */
	@Override
	public void emailUserFullRefundFail(Booking booking)
	{
		String subject = "Okapied payment timeout";
    	String message = 
    	    "Hi " + booking.getUser().getName() + ",\n\n" +
    	    "You paid for a booking for the property " +
    	    booking.getProperty().getName() +
    	    " booking reference: " + booking.getId() +
    	    "\n\n" +
    	    "Unfortunately your booking timed out and someone else reserved the property before the payment was recieved." +
    	    " You will be issued with a full refund." +
    	    " This will be processed as soon as possible." +
    	    " Please contact us at " + Configuration.instance().getStringProperty("ADMIN_EMAIL") +
    	    " if you have any questions." +
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
	 * @see okapied.service.RefundServiceInterface#emailUserFullRefundSuccess(okapied.entity.Booking)
	 */
	@Override
	public void emailUserFullRefundSuccess(Booking booking)
	{
		String subject = "Okapied Booking Refunded";
    	String message = 
    	    "Hi " + booking.getUser().getName() + ",\n\n" +
    	    "You recently paid for a booking for the property " +
    	    booking.getProperty().getName() +
    	    ", booking reference: " + booking.getId() +
    	    "\n\n" +
    	    "Unfortunately your booking timed out and someone else reserved the property before your payment was recieved." +
    	    " You have been issued with a full refund." +
    	    " Please contact us at " + Configuration.instance().getStringProperty("ADMIN_EMAIL") +
    	    " if you have any questions." +
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
	 * @see okapied.service.RefundServiceInterface#emailAdminFullRefundFail(okapied.entity.Booking)
	 */
	@Override
	public void emailAdminFullRefundFail(Booking booking)
	{
		String subject = "REFUND ERROR";
    	String message = 
    	    "Error refunding timed out booking, issue a manual refund immediately.\n" +
    	    "Booking id: " + booking.getId() + "\n" +
    	    "User email: " + booking.getUser().getEmail() + "\n" +
    	    "Paid amount: " + booking.getTotal() + "\n" +
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
	 * @see okapied.service.RefundServiceInterface#emailUserPartialRefundSuccess(okapied.entity.Booking)
	 */
	@Override
	public void emailUserPartialRefundSuccess(Booking booking)
	{
		String subject = "Okapied Booking Refunded";
    	BigDecimal refundAmount = new BigDecimal(booking.getRefund().getRefundGrossAmount());
    	refundAmount = refundAmount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
		String message = 
    	    "Hi " + booking.getUser().getName() + ",\n\n" +
    	    "Your booking for the property " +
    	    booking.getProperty().getName() +
    	    " has been cancelled.\n\n" +
    	    "Booking reference: " + booking.getId() +
    	    "\n\n" +
    	    "You have been refunded " + refundAmount.toPlainString() + " ";
    	if( booking.getCurrencyCode() != null )
    	{
    	    message += booking.getCurrencyCode().getName();
    	}
    	message += "\n\n" +
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
	 * @see okapied.service.RefundServiceInterface#emailOwnerPartialRefundSuccess(okapied.entity.Booking)
	 */
	@Override
	public void emailOwnerPartialRefundSuccess(Booking booking)
	{
		String subject = "Okapied Booking Cancelled";
    	BigDecimal refundAmount = new BigDecimal(booking.getOwnerPayment().getRefundGrossAmount());
    	refundAmount = refundAmount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
    	String message = 
    	    "Hi " + booking.getProperty().getOwner().getName() + ",\n\n" +
    	    "A booking for your property " +
    	    booking.getProperty().getName() +
    	    " has been cancelled.\n\n" +
    	    "Booking reference: " + booking.getId() +
    	    "\n\n" +
    	    "You have received a partial payment of " + refundAmount.toPlainString() + " ";
    	if( booking.getCurrencyCode() != null )
    	{
    	    message += booking.getCurrencyCode().getName();
    	}
    	message += "\n\n" +
    	    "Regards\n\n" +
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
	 * @see okapied.service.RefundServiceInterface#issueRefund(okapied.entity.Booking, java.lang.Integer)
	 */
	@Override
	public void issueRefund(Booking booking, Integer userId) 
	    throws RefundException, SecurityException, PaymentException, InvalidPayPalAccountException, IOException, ExchangeRateNotFoundException
	{
		if( !booking.getUser().getId().equals(userId) )
		{
			log.error("Invalid access to refund booking id: " + booking.getId() + " userid: " + userId);
			throw new SecurityException("You do not have permission to request a refund for this booking");
		}
		
		// check if refund is available
		if( !booking.isRefundAvailable())
		{
			log.error("Tried to get refund when not available: " + booking.getId());
			throw new RefundException("Unfortunately it is not possible to get a refund for this booking");
		}
		
		Charges charges = new Charges();

		// if it is flexible, not more than 60 days ago, and before 12:00 the day before the start
		// day of the booking we want to do a full refund
		if( booking.getRefundPolicy().equals(Property.REFUND_POLICY_FLEXIBLE) && 
			!booking.bookedGreaterThan60DaysAgo() &&
			notTooLateForFullRefund(booking) )
		{
		    sendRefund(booking);
		    emailUserPartialRefundSuccess(booking);
		    return;
		}
		else
		{
			// calculate refund amount
			charges = getCharges(booking);
			if( charges.refundAmount <= 0.0f )
			{
				log.info("Issuing a refund when no refund is due: " + booking.getId());
				throw new RefundException("Balance to be refunded is 0");
			}
	    }
		
		// make a payment, let the user pay the fee
		try
		{
			if( !accountService.validPayPalAccount(booking.getUser()) )
			{
				throw new InvalidPayPalAccountException("A partial refund can only be processed into a PayPal account");
			}
		    directPaymentMass(booking,charges.refundAmount,booking.getUser().getEmail(),true);
		    emailUserPartialRefundSuccess(booking);
		}
		catch( PaymentException e)
		{
			throw new RefundException("Payment of refund failed");
		} 
		catch (InputValidationException e) 
		{
			throw new RefundException("Unable to check status of PayPal account for partial refund");
		}
		
		// pay the owner their percentage (let them pay the pay pal fee)
		if( charges.ownerAmount > 0.0f )
		{
		    directPaymentMass(booking,charges.ownerAmount,booking.getProperty().getOwner().getEmail(),false);
		    emailOwnerPartialRefundSuccess(booking);
		}
	}
	
//	/**
//	 * Deducts the fees that we will have to pay from the refund amounts.
//	 * 1. For the buyer, removes 2% or 1 USD whichever is smaller.
//	 * 2. For the seller, removes 2.9% plus 0.3 USD ( to account for fee when we received the payment )
//	 * @param charges
//	 * @throws ExchangeRateNotFoundException 
//	 */
//	public void deductPayPalFees(Charges charges, Booking booking) throws ExchangeRateNotFoundException 
//	{
//		CurrencyCode currency = booking.getCurrencyCode();
//		if( currency.getUsd() == null || currency.getUsd() <=0 )
//		{
//			throw new ExchangeRateNotFoundException("The exchange rate for currency " + currency.getId() + " is missing.");
//		}
//		Float oneUsd = 1f / currency.getUsd();
//		
//		// deduct fees from refund to user
//		Float percentageFee = charges.refundAmount*Configuration.instance().getFloatProperty("REFUND_BUYER_PERCENTAGE_FEE");
//		if( percentageFee > oneUsd ) percentageFee = oneUsd;
//		charges.refundAmount = charges.refundAmount - percentageFee;
//		
//		// deduct fees from the rest that goes to the owner
//		// here we pass on the original PayPal fee that was charged to Okapied, even though the fee for
//		// the refund transaction will be less
//		Float transactionFeeInCurrency = Configuration.instance().getFloatProperty("FEE_PER_TRANSACTION_USD") 
//		    / currency.getUsd();
//		charges.ownerAmount = charges.ownerAmount*Configuration.instance().getFloatProperty("REFUND_HOST_PERCENTAGE_FEE")
//		    - transactionFeeInCurrency;
//	}

	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#directPayment(okapied.entity.Booking, java.lang.Float, java.lang.String)
	 */
	@Override
	public void directPayment(Booking booking, Float amount, String email) throws PaymentException
	{
		directPayment(booking, amount, email, true);
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#directPayment(okapied.entity.Booking, java.lang.Float, java.lang.String, boolean)
	 */
	@Override
	public void directPayment(Booking booking, Float amount, String email, boolean isRefund) 
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
				Refund refund = new Refund();
				refund.setPayKey(payKey);
				refund.setTrackingId(trackingId);
				refund.setRefundDate(now);
				refund.setTotalOfAllRefunds(amount);
				refund.setRefundGrossAmount(amount);
				refund.setEmailRefundSentTo(email);
				dao.persist(refund);
				if( isRefund ) 
				{
					booking.setRefund(refund);
					booking.setBookingStatus(Booking.BOOKING_STATUS_REFUND_PARTIAL);
					dao.update(booking);
				}
				else
				{
					booking.setOwnerPayment(refund);
					booking.setBookingStatus(Booking.BOOKING_STATUS_REFUND_PARTIAL_OWNER_PAID);
					dao.update(booking);
				}
				log.info("Refund success, key: " + payKey);
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
		
		throw new PaymentException("Refund payment failed");
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#directPaymentMass(okapied.entity.Booking, java.lang.Float, java.lang.String, boolean)
	 */
	@Override
	public void directPaymentMass(Booking booking, Float amount, String email, boolean isRefund) 
        throws PaymentException, IOException 
	{
		log.info("Direct mass payment amount: " + amount);
		
		NVPCallerServices caller = new NVPCallerServices();
		    
		try
		{
			APIProfile profile = ProfileFactory.createSignatureAPIProfile();
			Properties properties = new Properties();
			properties.load(getClass().getResourceAsStream("paypal_sdk_client.properties"));
			profile.setAPIPassword(properties.getProperty("X-PAYPAL-SECURITY-PASSWORD"));
			profile.setAPIUsername(properties.getProperty("X-PAYPAL-SECURITY-USERID"));
			profile.setDelayTime(500);
			profile.setEnvironment(Configuration.instance().getStringProperty("PAY_PAL_MASS_ENVIRONMENT"));
			profile.setMaximumRetries(1);
			profile.setSignature(properties.getProperty("X-PAYPAL-SECURITY-SIGNATURE"));
			profile.setTimeout(10000);
			caller.setAPIProfile(profile);
			
		    NVPEncoder encoder = new NVPEncoder();
			encoder.add("METHOD","MassPay");
			encoder.add("EMAILSUBJECT","Okapied Payment Processed");
			encoder.add("RECEIVERTYPE","EmailAddress");
			encoder.add("L_EMAIL0",email);
			encoder.add("L_Amt0",MathUtil.roundBigDecimal(amount).toPlainString());
			String strNVPString = encoder.encode();
			
			String ppresponse =	(String) caller.call( strNVPString);
			
			NVPDecoder resultValues = new NVPDecoder();
			resultValues.decode(ppresponse);
			String strAck = resultValues.get("ACK");
		
			Date now = DateUtil.getCurrentCalendarCompareTZ().getTime();
			
			for( Object key : resultValues.getMap().keySet() )
			{
				Object value = resultValues.getMap().get(key);
				log.info("Key: " + key + " value: " + value);
			}
			
			if(strAck !=null && !(strAck.equals("Success") || strAck.equals("SuccessWithWarning")))
			{
				log.error("Failed to complete payment booking id: " + booking.getId() + " ack: " + strAck);
				booking.setBookingStatus(Booking.BOOKING_STATUS_ERROR);
				dao.update(booking);
			}
			else
			{
				Refund refund = new Refund();
				refund.setTrackingId(resultValues.get("CORRELATIONID"));
				refund.setRefundDate(now);
				refund.setTotalOfAllRefunds(amount);
				refund.setRefundGrossAmount(amount);
				refund.setEmailRefundSentTo(email);
				dao.persist(refund);
				if( isRefund ) 
				{
					booking.setRefund(refund);
					booking.setBookingStatus(Booking.BOOKING_STATUS_REFUND_PARTIAL);
					dao.update(booking);
				}
				else
				{
					booking.setOwnerPayment(refund);
					booking.setBookingStatus(Booking.BOOKING_STATUS_REFUND_PARTIAL_OWNER_PAID);
					dao.update(booking);
				}
				log.info("Refund success, ack: " + strAck);
				return;
			}
		}
		catch( PayPalException e )
		{
			log.error(e);
		}
		
		throw new PaymentException("Refund payment failed");
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getCharges(okapied.entity.Booking)
	 */
	@Override
	public Charges getCharges(Booking booking)
	{
		Charges charges = new Charges();
		if( booking.getRefundPolicy().equals(Property.REFUND_POLICY_FLEXIBLE) )
		{
			Float total = getTotalFullPercentage(booking);
			Float ownerTotal = booking.getTotal() - total;
			if( total.equals(booking.getTotal()) )
			{
				charges.refundAmount = total;
				charges.ownerAmount = 0f;
			}
			else
			{
		        charges.refundAmount =  removeMyCut(total);
		        charges.ownerAmount = removeMyCut(ownerTotal);
			}
		}
		else if( booking.getRefundPolicy().equals(Property.REFUND_POLICY_STANDARD) )
		{
            calculateStandardRefund(charges,booking);
		}
		return charges;
	}
	
	@Override
	public void calculateStandardRefund(Charges charges, Booking booking)
	{
	    float t = booking.getTotal();
	    float a = getTotalFullPercentage(booking);  // total - days stayed
	    float b =  t - a; // days stayed
	    float fraction = new Float(getFraction(booking)); // fraction to be refunded
	    float c = a*fraction;
	    float d = (a-c) + b; // owner amount = fraction left after refund + days stayed
	    
	    charges.refundAmount = removeMyCut(c);
	    charges.ownerAmount = removeMyCut(d);
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getFraction(okapied.entity.Booking)
	 */
	@Override
	public double getFraction(Booking booking)
	{
		Calendar startDate = booking.getProperty().retrieveCurrentDay();
		startDate.setTime(booking.getStartDate());
		Calendar currentDate = booking.getProperty().retrieveCurrentTime();
		long millisInADay = 24*60*60*1000;
		long difference = startDate.getTimeInMillis() - currentDate.getTimeInMillis();
		if( difference > Configuration.instance().getIntProperty("30_DAYS")*millisInADay )
		{
			return Configuration.instance().getDoubleProperty("FRACTION_30_DAYS");
		}
		else if( difference > Configuration.instance().getIntProperty("7_DAYS")*millisInADay )
		{
			return Configuration.instance().getDoubleProperty("FRACTION_7_DAYS");
		}
		else
		{
			return Configuration.instance().getDoubleProperty("FRACTION_LAST_MINUTE");
		}
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getTotalFullPercentage(okapied.entity.Booking)
	 */
	@Override
	public Float getTotalFullPercentage(Booking booking)
	{
		Float refundTotal = 0.0f;
		
		Integer index = getIndexOfFirstRefundDay(booking);
		if( index == null ) return refundTotal;
		
		for( int i=index; i < booking.getInfoList().size(); ++i )
		{
			refundTotal += booking.getInfoList().get(i).getPrice();
		}
		
	    return refundTotal;
	}
	
	public boolean notTooLateForFullRefund(Booking booking)
	{
		Integer index = getIndexOfFirstRefundDay(booking);
		if( index == null || index <= 0 ) return true;
		else return false;
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#removeMyCut(java.lang.Float)
	 */
	@Override
	public Float removeMyCut(Float total)
	{
		if( total == null ) return null;
		BigDecimal totalBD = new BigDecimal(total);
		BigDecimal myFraction = totalBD.multiply(Configuration.instance().getBigDecimalProperty("MY_FRACTION"));
		BigDecimal result = totalBD.subtract(myFraction);
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Float.valueOf(twoDForm.format(result.doubleValue()));
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getRefundPeriod(okapied.entity.Booking)
	 */
	@Override
	public List<Calendar> getRefundPeriod(Booking booking)
	{
		List<Calendar> period = new ArrayList<Calendar>();
		
		Calendar currentTime = booking.getProperty().retrieveCurrentTime();
		boolean nextDay = false;
		if( currentTime.get(Calendar.HOUR_OF_DAY) > 12 || 
			( currentTime.get(Calendar.HOUR_OF_DAY) == 12 && 
			    (currentTime.get(Calendar.MINUTE) > 0) ||  currentTime.get(Calendar.SECOND) > 0) )
		{
			nextDay = true;
		}
		Calendar nextAvailableCancelDay = booking.getProperty().retrieveCurrentDay();
		nextAvailableCancelDay.add(Calendar.DAY_OF_YEAR,1);
		if( nextDay ) nextAvailableCancelDay.add(Calendar.DAY_OF_YEAR,1);
		
		Calendar currentDay = booking.getProperty().retrieveCurrentDay();
		currentDay.setTime(booking.getStartDate());
		Calendar endDate = booking.getProperty().retrieveCurrentDay();
		endDate.setTime(booking.getEndDate());
		
		while( currentDay.getTimeInMillis() < endDate.getTimeInMillis() )
		{
			if( nextAvailableCancelDay.getTimeInMillis() <= currentDay.getTimeInMillis() )
			{
			    Calendar newDay = booking.getProperty().retrieveCurrentDay();
			    newDay.setTime(currentDay.getTime());
			    //System.out.println("NEW: " + newDay.getTime() + " END: " + endDate.getTime());
			    period.add(newDay);
			}
			currentDay.add(Calendar.DAY_OF_YEAR,1);
		}
		
		return period;
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getIndexOfFirstRefundDay(okapied.entity.Booking)
	 */
	@Override
	public Integer getIndexOfFirstRefundDay(Booking booking)
	{	
		Calendar currentTime = booking.getProperty().retrieveCurrentTime();
		boolean nextDay = getNextDay(currentTime);
		Calendar nextAvailableCancelDay = booking.getProperty().retrieveCurrentDay();
		nextAvailableCancelDay.add(Calendar.DAY_OF_YEAR,1);
		if( nextDay ) nextAvailableCancelDay.add(Calendar.DAY_OF_YEAR,1);
		
		Calendar currentDay = booking.getProperty().retrieveCurrentDay();
		currentDay.setTime(booking.getStartDate());
		Calendar endDate = booking.getProperty().retrieveCurrentDay();
		endDate.setTime(booking.getEndDate());
		
		int index = 0;
		
		while( currentDay.getTimeInMillis() < endDate.getTimeInMillis() )
		{
			if( nextAvailableCancelDay.getTimeInMillis() <= currentDay.getTimeInMillis() )
			{
			    return index;
			}
			currentDay.add(Calendar.DAY_OF_YEAR,1);
			++index;
		}
		return null;
	}
	
	public boolean getNextDay(Calendar currentTime)
	{
		boolean nextDay = false;
		if( currentTime.get(Calendar.HOUR_OF_DAY) > 12 )
		{
			nextDay = true;
		}
		if( currentTime.get(Calendar.HOUR_OF_DAY) == 12 && 
		    ((currentTime.get(Calendar.MINUTE) > 0) ||  (currentTime.get(Calendar.SECOND) > 0)) )
		{
			nextDay = true;
		}
		return nextDay;
	}
	
	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#sendRefund(okapied.entity.Booking)
	 */
	@Override
	public void sendRefund(Booking booking) 
	    throws RefundException
	{		
    	RefundRequest request = new RefundRequest();
    	request.setPayKey(booking.getPayKey());
    	request.setTrackingId(booking.getTrackingId());
    	
    	RequestEnvelope en = new RequestEnvelope();
    	en.setErrorLanguage("en_US");
    	request.setRequestEnvelope(en);
    	
    	RefundResponse response;
		try {
			AdaptivePayments ap = getAdaptivePayments();
			response = ap.refund(request);
	    	if( response.getRefundInfoList() != null )
	    	{
		    	for( RefundInfo info : response.getRefundInfoList().getRefundInfo() )
		    	{
		            if( info.getReceiver().getEmail().equals(Configuration.instance().getStringProperty("MY_PAYPAL_EMAIL")) )
		            {	
		            	if( info.getRefundStatus().equals(BookingServiceInterface.STATUS_REFUNDED) )
		            	{
			        		Refund refund = new Refund();
			        		refund.setEncryptedRefundTransactionId(info.getEncryptedRefundTransactionId());
			        		refund.setRefundFeeAmount(info.getRefundFeeAmount().floatValue());
			        		refund.setRefundGrossAmount(info.getRefundGrossAmount().floatValue());
			        		refund.setRefundNetAmount(info.getRefundNetAmount().floatValue());
			        		refund.setRefundStatus(info.getRefundStatus());
			        		refund.setRefundTransactionStatus(info.getRefundTransactionStatus());
			        		refund.setTotalOfAllRefunds(info.getTotalOfAllRefunds().floatValue());
			        		booking.setRefund(refund);
			        		dao.update(booking);
		            		if( info.getRefundFeeAmount().compareTo(new BigDecimal(0)) == 1 )
		            		{
		            			log.error("Full refund charged a fee for booking: " + booking.getId());
		            		}
		            		log.info("Booking refunded, booking id: " + booking.getId());
		            		booking.setBookingStatus(Booking.BOOKING_STATUS_REFUND_FULL);
		            		dao.update(booking);
		            		return;
		            	}
		            	else
		            	{
		            		log.error("Invalid refund status: " + info.getRefundStatus());
		            	}
		            }
		    	}
	    	}
		} catch (FatalException e) {
	    	log.error(e);
		} catch (PPFaultMessage e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} catch (SSLConnectionException e) {
			log.error(e);
		}
    	
    	log.error("There has been an error issuing a full refund for the booking: " + booking.getId());
    	booking.setBookingStatus(Booking.BOOKING_STATUS_ERROR);
    	dao.update(booking);
    	throw new RefundException("Error refunding booking, please contact the administrator");
	}

	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getDao()
	 */
	@Override
	public DAO getDao() {
		return dao;
	}

	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#setDao(okapied.dao.DAO)
	 */
	@Override
	public void setDao(DAO dao) {
		this.dao = dao;
	}

	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getMailService()
	 */
	@Override
	public MailService getMailService() {
		return mailService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#setMailService(okapied.service.MailService)
	 */
	@Override
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getPropertyService()
	 */
	@Override
	public PropertyService getPropertyService() {
		return propertyService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#setPropertyService(okapied.service.PropertyService)
	 */
	@Override
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#getAccountService()
	 */
	@Override
	public AccountService getAccountService() {
		return accountService;
	}

	/* (non-Javadoc)
	 * @see okapied.service.RefundServiceInterface#setAccountService(okapied.service.AccountService)
	 */
	@Override
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
}
