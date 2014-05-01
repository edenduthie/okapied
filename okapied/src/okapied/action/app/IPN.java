package okapied.action.app;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.action.BaseAction;
import okapied.service.BookingServiceInterface;

import org.apache.log4j.Logger;

public class IPN extends BaseAction
{
    public static final String LIST = "list";
    private static final Logger log = Logger.getLogger(IPN.class);
    
    Integer bookingId;
    String tracking_id;
    String sender_email;
    String pay_key;
    String action_type;
    String status;
    
    BookingServiceInterface bookingService;
  
/*
    payment_request_date
    fees_payer
    bookingId
    transaction[0].is_primary_receiver
    transaction[1].receiver
    transaction[1].is_primary_receiver
    transaction_type
    transaction[1].pending_reason
    verify_sign
    log_default_shipping_address_in_transaction
    transaction[1].id
    sender_email
    transaction[0].amount
    pay_key
    reverse_all_parallel_payments_on_error
    ipn_notification_url
    action_type
    transaction[1].amount
    notify_version
    tracking_id
    transaction[0].status_for_sender_txn
    transaction[1].id_for_sender_txn
    test_ipn
    cancel_url
    transaction[0].pending_reason
    status
    transaction[0].status
    charset
    return_url
    transaction[0].id
    transaction[1].status
    transaction[0].receiver
    transaction[0].id_for_sender_txn
    transaction[1].status_for_sender_txn   
     
    Example Success:

    tracking_id:6bcc77e590afb030e54380a0a2c4cca7
    sender_email:eduthi_1298629553_per@gatorlogic.com
    pay_key:AP-90Y88915A0298053L
    action_type:PAY
    status:COMPLETED
*/
    
    public String list()
    {
    	log.info("IPN: " + bookingId);
//    	log.debug("IPN received: " + bookingId);
//    	log.debug("tracking_id:" + tracking_id);
//    	log.debug("sender_email:" + sender_email);
//    	log.debug("pay_key:" + pay_key);
//    	log.debug("action_type:" + action_type);
//    	log.debug("status:" + status);
    	
    	bookingService.processIPN(bookingId, tracking_id, pay_key, sender_email, status);
    	
    	return LIST;
    }

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public String getTracking_id() {
		return tracking_id;
	}

	public void setTracking_id(String tracking_id) {
		this.tracking_id = tracking_id;
	}

	public String getSender_email() {
		return sender_email;
	}

	public void setSender_email(String sender_email) {
		this.sender_email = sender_email;
	}

	public String getPay_key() {
		return pay_key;
	}

	public void setPay_key(String pay_key) {
		this.pay_key = pay_key;
	}

	public String getAction_type() {
		return action_type;
	}

	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BookingServiceInterface getBookingService() {
		return bookingService;
	}

	public void setBookingService(BookingServiceInterface bookingService) {
		this.bookingService = bookingService;
	}
}
