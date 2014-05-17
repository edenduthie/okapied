package okapied.action.app;

import java.io.IOException;

import okapied.entity.Charges;
import okapied.exception.ExchangeRateNotFoundException;
import okapied.exception.FeedbackException;
import okapied.exception.InvalidPayPalAccountException;
import okapied.exception.OkaSecurityException;
import okapied.exception.PaymentException;
import okapied.exception.RefundException;
import okapied.service.RefundServiceInterface;
import okapied.util.Configuration;
import okapied.web.LoginStatus;
import okapied.web.WebUtils;

import org.apache.struts2.interceptor.validation.SkipValidation;

public class ViewBooking extends ViewBookingBase
{
	RefundServiceInterface refundService;
	
    public String feedback()
    {
        try 
        {
        	if( positive == null ) throw new FeedbackException("Please select whether the stay was positive or negative");
        	else
        	{
        		if( positive.equals(POSTIVE)) feedback.setPositive(true);
        		else feedback.setPositive(false);
        	}
        	feedbackService.leaveFeedbackUser(id, feedback,LoginStatus.getUser().getId());
        	message = "Feedback Recieved";
		} 
        catch (OkaSecurityException e) 
        {
			log.error(e);
			message = e.getMessage();
		} 
        catch (FeedbackException e) 
        {
			log.error(e);
			message = e.getMessage();
		}
    	return FEEDBACK;
    }
    
    @SkipValidation
    public String refund()
    {
    	try {
			refundService.issueRefund(booking,LoginStatus.getUserId());
			message = "Booking Cancelled";
		} catch (SecurityException e) {
			message = "You do not have permission to access this feature";
			log.error(e);
			return INPUT;
		} catch (RefundException e) {
			message= "Failed to issue refund";
			log.error(e);
			return INPUT;
		} catch (PaymentException e) {
			log.error(e);
		} catch (InvalidPayPalAccountException e) {
			message = "<p>An automatic partial refund can only be processed into a valid PayPal account. " +
			    " Please go to <a href='" + Configuration.instance().getStringProperty("BASE_REQUEST_URL") +
			    "/Account/Account/edit'>your account</a> page and enter a valid PayPal email before " +
			    "requesting an automatic refund. Your email, first and last names must match those in " +
			    "your PayPal account." + "</p>" +
			    "<p>Alternatively contact us at <a href='mailto:" + Configuration.instance().getStringProperty("ADMIN_EMAIL") +
			    "'>" + Configuration.instance().getStringProperty("ADMIN_EMAIL") + "</a> " + 
			    "to request a refund.</p>";
			log.error(e);
			return INPUT;
			    
		} catch (IOException e) {
			message = "Embarassing, there has been an error, entirely our fault! Please try again or contact us at <a href='mailto:" + 
			    Configuration.instance().getStringProperty("ADMIN_EMAIL") +
			    "'>" + Configuration.instance().getStringProperty("ADMIN_EMAIL") + "</a>";
			log.error(e);
			return INPUT;
		} catch (ExchangeRateNotFoundException e) {
			message = "Embarassing, there has been an error, entirely our fault! Please try again or contact us at <a href='mailto:" + 
		    Configuration.instance().getStringProperty("ADMIN_EMAIL") +
		    "'>" + Configuration.instance().getStringProperty("ADMIN_EMAIL") + "</a>";
		log.error(e);
		return INPUT;
		}
    	return REFUND;
    }
    
    public Float getRefundAmount()
    {
		Charges charges = refundService.getCharges(booking);
		return charges.refundAmount;
    }
    
	public String getUserFeedbackText()
	{
		return WebUtils.nl2lb(booking.getUserFeedback().getText());
	}

	public RefundServiceInterface getRefundService() {
		return refundService;
	}

	public void setRefundService(RefundServiceInterface refundService) {
		this.refundService = refundService;
	}
}
