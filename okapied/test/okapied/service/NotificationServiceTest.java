package okapied.service;

import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.BaseTest;
import okapied.entity.Booking;
import okapied.util.Configuration;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NotificationServiceTest extends BaseTest
{
	@Autowired NotificationService notificationService;
	@Autowired BookingEntityService bookingEntityService;
	
    public void setUpBooking()
    {
    	booking = Generator.booking();
    	saveBooking();
    }
    
    public void tearDownBooking()
    {
    	removeBooking();
    }
	
    @Test
    public void emailUserReminder() throws AddressException, MessagingException
    {
    	Booking booking = Generator.booking();
    	booking.getUser().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getUser().setFirstName("Eden");
    	booking.getUser().setLastName("Duthie");
    	booking.setId(234234);
    	booking.getProperty().getPropertyDetails().setCheckInInstructions("CHECK IN INSTRUCTIONS");
    	booking.getProperty().getPropertyDetails().setUnit("1");
    	booking.getProperty().getPropertyDetails().setStreetNumber("234");
    	booking.getProperty().getPropertyDetails().setStreet("Pakington Street");
    	booking.getProperty().getPropertyDetails().setPostcode("3101");
    	
    	notificationService.emailUserReminder(booking);
    }
    
    @Test
    public void emailOwnerReminder()
    {
    	Booking booking = Generator.booking();
    	booking.getProperty().getOwner().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getProperty().getOwner().setFirstName("Eden");
    	booking.getProperty().getOwner().setLastName("Duthie");
    	booking.setId(234234);
    	booking.getUser().setFirstName("Eden");
    	booking.getUser().setLastName("Duthie");
    	booking.getUser().setEmail("eduthie@gmail.com");
    	notificationService.emailOwnerReminder(booking);
    }
    
    @Test
    public void emailRemindersDayBeforeBooking()
    {
    	setUpBooking();
    	
    	booking.getUser().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getUser().setFirstName("Eden");
    	booking.getUser().setLastName("Duthie");
    	dao.update(booking.getUser());
    	booking.getProperty().getPropertyDetails().setCheckInInstructions("CHECK IN INSTRUCTIONS");
    	booking.getProperty().getPropertyDetails().setUnit("1");
    	booking.getProperty().getPropertyDetails().setStreetNumber("234");
    	booking.getProperty().getPropertyDetails().setStreet("Pakington Street");
    	booking.getProperty().getPropertyDetails().setPostcode("3101");
    	dao.update(booking.getProperty().getPropertyDetails());
    	
    	Calendar tomorrow = booking.getProperty().retrieveCurrentTime();
    	tomorrow.add(Calendar.DAY_OF_YEAR,1);
    	Calendar startDate = tomorrow;
    	booking.setStartDate(startDate.getTime());
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	
    	dao.update(booking);
    	
    	notificationService.emailRemindersDayBeforeBooking();
    	
    	Booking result = bookingEntityService.get(booking.getId());
    	Assert.assertTrue(result.getReminderEmailBool());
    	
    	tearDownBooking();
    }
    
    @Test
    public void emailUserFeedback() throws AddressException, MessagingException
    {
    	Booking booking = Generator.booking();
    	booking.getUser().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getUser().setFirstName("Eden");
    	booking.getUser().setLastName("Duthie");
    	booking.setId(234234);
    	booking.getProperty().getPropertyDetails().setCheckInInstructions("CHECK IN INSTRUCTIONS");
    	booking.getProperty().getPropertyDetails().setUnit("1");
    	booking.getProperty().getPropertyDetails().setStreetNumber("234");
    	booking.getProperty().getPropertyDetails().setStreet("Pakington Street");
    	booking.getProperty().getPropertyDetails().setPostcode("3101");
    	
    	notificationService.emailUserFeedback(booking);
    }
    
    @Test
    public void emailFeedbackReminderDayAfterBooking()
    {
    	setUpBooking();
    	
    	booking.getUser().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getUser().setFirstName("Eden");
    	booking.getUser().setLastName("Duthie");
    	dao.update(booking.getUser());
    	booking.getProperty().getPropertyDetails().setCheckInInstructions("CHECK IN INSTRUCTIONS");
    	booking.getProperty().getPropertyDetails().setUnit("1");
    	booking.getProperty().getPropertyDetails().setStreetNumber("234");
    	booking.getProperty().getPropertyDetails().setStreet("Pakington Street");
    	booking.getProperty().getPropertyDetails().setPostcode("3101");
    	dao.update(booking.getProperty().getPropertyDetails());
    	
    	Calendar now = booking.getProperty().retrieveCurrentTime();
    	now.add(Calendar.DAY_OF_YEAR,-1);
    	now.add(Calendar.HOUR,-1);
    	booking.setEndDate(now.getTime());
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	
    	dao.update(booking);
    	
    	notificationService.emailFeedbackReminderDayAfterBooking();
    	
    	Booking result = bookingEntityService.get(booking.getId());
    	Assert.assertTrue(result.getFeedbackEmailBool());
    	
    	tearDownBooking();
    }
}
