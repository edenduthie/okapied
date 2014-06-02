package okapied.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.entity.Booking;
import okapied.exception.BookingException;
import okapied.exception.InputValidationException;
import okapied.util.Configuration;
import okapied.util.DateUtil;

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
import com.paypal.svcs.types.common.ClientDetailsType;
import com.paypal.svcs.types.common.ErrorData;
import com.paypal.svcs.types.common.RequestEnvelope;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

@Transactional
public class BookingService2 extends BookingService
{
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
//		rec1.setPrimary(true);
		list.getReceiver().add(rec1);
		
//		Receiver rec2 = new Receiver();
//		rec2.setAmount(roundBigDecimal(booking.getPropertyOwnerAmount()));
//		rec2.setEmail(booking.getProperty().getOwner().getEmail());
//        log.debug("Secondary reciever: " + rec2.getEmail() + " amount: " + rec1.getAmount());
//		rec2.setPrimary(false);
//		list.getReceiver().add(rec2);
		
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
//		request.setActionType("PAY_PRIMARY");
		request.setActionType("PAY");
		request.setFeesPayer(Configuration.instance().getStringProperty("PAYPAL_FEES_PAYER_DIRECT"));
		
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
    
    /**
     *  Finalise the payment and send an email to the owner. Done 23 hours after booking
     *  start date.
     * @throws InputValidationException 
     */
    public Booking sendMoneyToOwner(Booking booking) 
        throws FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
		NVPCallerServices caller = new NVPCallerServices();
	    
		try
		{
			if( !accountService.validPayPalAccount(booking.getProperty().getOwner()))
			{
				log.error("Failed to pay owner for booking: " + booking.getId() + " invalid PayPal account");
				emailMeFailedToPayOwner(booking,"Invalid PayPal account");
				return booking;
			}
			
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
			encoder.add("L_EMAIL0",booking.getProperty().getOwner().getEmail());
			encoder.add("CURRENCYCODE",booking.getCurrencyCode().getCode());
//			BigDecimal amount = deductPayPalFees(booking.getPropertyOwnerAmount(),booking);
			BigDecimal amount = new BigDecimal(booking.getPropertyOwnerAmount());
			amount = amount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
			encoder.add("L_Amt0",amount.toPlainString());
			String strNVPString = encoder.encode();
			String ppresponse =	(String) caller.call(strNVPString);
			
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
				log.error("Failed to complete payment to owner booking id: " + booking.getId() + " ack: " + strAck);
				emailMeFailedToPayOwner(booking,strAck);
			}
			else
			{
	        	log.info("Booking payment complete: " + booking.getId());
	        	booking.setPaymentCompleteBool(true);
	        	dao.update(booking);
	        	emailOwnerPaymentCompletion(booking);
				return booking;
			}
		}
		catch( PayPalException e )
		{
			log.error(e);
			log.error("Failed to pay owner for booking: " + booking.getId());
			emailMeFailedToPayOwner(booking,e.getMessage());
		} 
		catch (InputValidationException e) 
		{
			log.error(e);
			log.error("Failed to pay owner for booking: " + booking.getId());
			emailMeFailedToPayOwner(booking,"Failed to check PayPal account, " + e.getMessage());
		} 
//		catch (ExchangeRateNotFoundException e) 
//		{
//			log.error(e);
//			log.error("Failed to pay owner for booking: " + booking.getId());
//			emailMeFailedToPayOwner(booking,e.getMessage());
//		}
		return booking;
    }
    
	public void emailMeFailedToPayOwner(Booking booking, String problem)
	{
		String subject = "FAILED TO PAY OWNER";
    	String message = 
    	    "Failed to pay owner.\n" +
    	    "Booking id: " + booking.getId() + "\n" +
    	    "Property id: " + booking.getProperty().getId() + "\n" +
    	    "Owner: " + booking.getProperty().getOwner().getUsername() + "\n" +
    	    "Message: " + problem;
    	try {
			mailService.sendMessage(Configuration.instance().getStringProperty("ADMIN_EMAIL"), subject, message);
		} catch (AddressException e) {
			log.error(e.getMessage());
		} catch (MessagingException e) {
			log.error(e.getMessage());
		}
	}

    public void processBookingStatusReciept(Booking booking, String status) 
    {
    	if( status.equals(STATUS_INCOMPLETE) || status.equals(STATUS_COMPLETE))
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
}
