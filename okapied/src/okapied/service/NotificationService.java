package okapied.service;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.dao.DAO;
import okapied.entity.Booking;
import okapied.util.Configuration;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NotificationService 
{
    MailService mailService;
    BookingEntityService bookingEntityService;
    DAO dao;
    
    static Logger log = Logger.getLogger(NotificationService.class);
    
    public void emailRemindersDayBeforeBooking()
    {
    	List<Booking> bookings = bookingEntityService.getConfirmedBookingsNoReminderDayBeforeStart();
    	for( Booking booking : bookings )
    	{
    		emailUserReminder(booking);
    		emailOwnerReminder(booking);
    		booking.setReminderEmailBool(true);
    		dao.update(booking);
    	}
    }
    
	public void emailUserReminder(Booking booking)
	{
		String subject = "Okapied Booking Reminder";
		SimpleDateFormat sdf = new SimpleDateFormat(Configuration.instance().getStringProperty("DATE_FORMAT"));
    	String message = 
    	    "Hi " + booking.getUser().getName() + ",\n\n" +
    	    "This is a reminder that you are scheduled to check into the property " +
    	    booking.getProperty().getName() +
    	    " soon.\n\n" +
    	    "You are checking in on " +
    	    sdf.format(booking.getStartDate()) +
    	    " and checking out on " +
    	    sdf.format(booking.getEndDate()) + ".\n\n" +
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
	
	public void emailOwnerReminder(Booking booking)
	{
		String subject = "Okapied Check In Reminder";
		SimpleDateFormat sdf = new SimpleDateFormat(Configuration.instance().getStringProperty("DATE_FORMAT"));
    	String message = 
    	    "Hi " + booking.getProperty().getOwner().getName() + ",\n\n" +
    	    "This is a reminder that " + booking.getUser().getName() + " will be checking into your property " +
    	    booking.getProperty().getName() +
    	    " soon.\n\n" +
    	    "The check in date is " +
    	    sdf.format(booking.getStartDate()) +
    	    " and the check out date is " +
    	    sdf.format(booking.getEndDate()) + ".\n\n" +
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
	
    public void emailFeedbackReminderDayAfterBooking()
    {
    	List<Booking> bookings = bookingEntityService.getConfirmedBookingsNoFeedbackDayAfterEnd();
    	for( Booking booking : bookings )
    	{
    		emailUserFeedback(booking);
    		booking.setFeedbackEmailBool(true);
    		dao.update(booking);
    	}
    }
    
	public void emailUserFeedback(Booking booking)
	{
		String subject = "Okapied Feedback Request";
		SimpleDateFormat sdf = new SimpleDateFormat(Configuration.instance().getStringProperty("DATE_FORMAT"));
    	String message = 
    	    "Hi " + booking.getUser().getName() + ",\n\n" +
    	    "You just checked out of the property " +
    	    booking.getProperty().getName() +
    	    ".\n\n" +
    	    "We hope you had a great time and " +
    	    "it would be great if you could leave some feedback. " +
    	    "Accurate feedback is very important and gives others a more balanced idea about the quality of a property. " +
    	    "Feedback can be provided on the booking page via the link below:\n\n" +
    	    Configuration.instance().getStringProperty("BASE_REQUEST_URL") + "/App/ViewBooking/list?id=" + booking.getId() +
    	    "\n\nThanks a lot!" +
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

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public BookingEntityService getBookingEntityService() {
		return bookingEntityService;
	}

	public void setBookingEntityService(BookingEntityService bookingEntityService) {
		this.bookingEntityService = bookingEntityService;
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
}
