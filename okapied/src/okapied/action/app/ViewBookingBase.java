package okapied.action.app;

import java.util.HashSet;
import java.util.Set;

import okapied.entity.Booking;
import okapied.entity.Feedback;
import okapied.exception.OkaSecurityException;
import okapied.service.BookingServiceInterface;
import okapied.service.FeedbackService;
import okapied.util.Configuration;
import okapied.web.LoginStatus;
import okapied.web.WebUtils;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.paypal.svcs.services.PPFaultMessage;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

public class ViewBookingBase extends ActionSupport implements Preparable
{
	String LIST = "list";
	String FEEDBACK = "feedback";
	String REFUND = "refund";
	
    Integer id;
    Booking booking;
    String message;
    Feedback feedback;
    String positive;
    String reason;
    
    static String POSTIVE = "Positive";
    static String NEGATIVE = "Negative";
    
    BookingServiceInterface bookingService;
    FeedbackService feedbackService;
    static Logger log = Logger.getLogger(ViewBooking.class);
    
    @SkipValidation
    public String list() throws Exception
    {
    	return LIST;
    }
    
    public Integer getCurrentUserId()
    {
    	return LoginStatus.getUserId();
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BookingServiceInterface getBookingService() {
		return bookingService;
	}

	public void setBookingService(BookingServiceInterface bookingService) {
		this.bookingService = bookingService;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}
	
	public String getCheckInText()
	{
		return WebUtils.nl2lb(booking.getProperty().getPropertyDetails().getCheckInInstructions());
	}
	
	public Set<Integer> getFeedbackOptions()
	{
		int largestScore = Configuration.instance().getIntProperty("LARGEST_FEEDBACK_SCORE");
		Set<Integer> scores = new HashSet<Integer>();
		for( int i=0; i <= largestScore; ++i )
		{
			scores.add(i);
		}
		return scores;
	}

	public Feedback getFeedback() {
		return feedback;
	}

	public void setFeedback(Feedback feedback) {
		this.feedback = feedback;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
	}

	public void setFeedbackService(FeedbackService feedbackService) {
		this.feedbackService = feedbackService;
	}

	public String getPositive() {
		return positive;
	}

	public void setPositive(String positive) {
		this.positive = positive;
	}

	@Override
	public void prepare() throws Exception {
    	String faultMessage = "There was a problem loading your booking, how annoying";
    	
        try {
			booking = bookingService.loadBooking(id,LoginStatus.getUser().getId());
		} catch (OkaSecurityException e) {
			log.error(e);
			message = e.getMessage();
		} catch (FatalException e) {
			log.error(e);
			message = faultMessage;
		} catch (SSLConnectionException e) {
			log.error(e);
			message = faultMessage;
		} catch (PPFaultMessage e) {
			log.error(e);
			message = faultMessage;
		}
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getTitle()
	{
	    return "View Booking";
	}
	
	public String getDescription()
	{
		return "View a Booking";
	}
}
