package okapied.action.app;

import okapied.entity.UserFeedback;
import okapied.exception.CancellationException;
import okapied.exception.FeedbackException;
import okapied.exception.OkaSecurityException;
import okapied.util.Configuration;
import okapied.web.LoginStatus;
import okapied.web.WebUtils;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class ViewBookingOwner extends ViewBookingBase
{
    static Logger log = Logger.getLogger(ViewBookingOwner.class);
    
    UserFeedback userFeedback;

	public UserFeedback getUserFeedback() {
		return userFeedback;
	}

	public void setUserFeedback(UserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}
    
	@SkipValidation
    public String feedback()
    {
        try 
        {
        	if( positive == null ) throw new FeedbackException("Please select whether the stay was positive or negative");
        	else
        	{
        		if( positive.equals(POSTIVE)) userFeedback.setPositive(true);
        		else userFeedback.setPositive(false);
        	}
        	feedbackService.leaveFeedbackOwner(id, userFeedback,
        	    LoginStatus.getUser().getId());
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
    
	public String getOwnerFeedbackText()
	{
		return WebUtils.nl2lb(booking.getOwnerFeedback().getText());
	}
    
//    @SkipValidation
//    public String acceptcancel()
//    {
//    	try
//    	{
//    		cancelService.acceptCancellationOwner(id,LoginStatus.getUser().getId());
//    		message="The booking has been cancelled.";
//    	}
//        catch (OkaSecurityException e) 
//        {
//			log.error(e);
//			message = e.getMessage();
//		} 
//    	return ACCEPT_CANCEL;
//    }
}
