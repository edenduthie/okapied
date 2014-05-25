package okapied.service;

import java.util.Calendar;
import java.util.List;

import okapied.BaseTest;
import okapied.entity.Availability;
import okapied.entity.Booking;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Property;
import okapied.util.DateUtil;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BookingEntityServiceTest extends BaseTest
{
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
    public void getBookingsForDayEqualStartDate()
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getBookingsForDay(property, startDate);
    	Assert.assertEquals(bookings.size(),1);
    	Assert.assertEquals(bookings.get(0).getId(),booking.getId());
    	
    	tearDownBooking();
    }
    
    @Test
    public void getBookingsForDayEqualEndDate()
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getBookingsForDay(property, endDate);
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getBookingsForDayStartDateAfter()
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	startDate.add(Calendar.DAY_OF_YEAR,-1);
    	List<Booking> bookings = bookingEntityService.getBookingsForDay(property, startDate);
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getBookingsForDayInTheMiddle()
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	startDate.add(Calendar.DAY_OF_YEAR,5);
    	List<Booking> bookings = bookingEntityService.getBookingsForDay(property, startDate);
    	Assert.assertEquals(bookings.size(),1);
    	Assert.assertEquals(bookings.get(0).getId(),booking.getId());
    	
    	tearDownBooking();
    }
    
    @Test
    public void getBookingsForDayEqualStartDateExpiredReserved()
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	booking.setReservedBool(true);
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.HOUR_OF_DAY,-1);
    	booking.setExpirationDate(now.getTime());
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getBookingsForDay(property, startDate);
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getBookingsForPeriodEqualStartAndEnd()
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	System.out.println(startDate.getTime());
    	System.out.println(endDate.getTime());
    	
    	List<Booking> bookings = bookingEntityService.getBookingsForPeriod(property, startDate, endDate);
    	Assert.assertEquals(bookings.size(),1);
    	Assert.assertEquals(bookings.get(0).getId(),booking.getId());
    	
    	tearDownBooking();
    }
    
    @Test
    public void getBookingsForPeriodStartEqualEnd()
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	System.out.println(startDate.getTime());
    	System.out.println(endDate.getTime());
    	
    	List<Booking> bookings = bookingEntityService.getBookingsForPeriod(property, endDate, endDate);
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getBookingsForPeriodEndEqualStart()
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getBookingsForPeriod(property, startDate, startDate);
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getActiveBookings()
    {
        setUpBooking();	
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getActiveBookings(property);
    	Assert.assertEquals(bookings.size(),1);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getActiveBookingsEqualEndDate()
    {
        setUpBooking();	
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getActiveBookings(property);
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getMyBookings()
    {
    	setUpBooking();
    	
    	Booking booking2 = Generator.booking();
    	booking2.setUser(booking.getUser());
    	booking2.setProperty(booking.getProperty());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.update(booking);
    	dao.persist(booking2);
    	
    	List<Booking> bookings = bookingEntityService.getMyBookings(booking.getUser().getId(),100,0);
    	Assert.assertEquals(bookings.size(),2);
    	Assert.assertEquals(bookings.get(0).getId(),booking2.getId());
    	Assert.assertEquals(bookings.get(1).getId(),booking.getId());
    	Assert.assertEquals(bookings.get(0).getProperty().getName(),property.getName());
    	
    	dao.delete(Booking.class.getName(),"id",booking2.getId());
    	tearDownBooking();
    }
    
    @Test
    public void getMyBookingsPagination()
    {
    	setUpBooking();
    	
    	Booking booking2 = Generator.booking();
    	booking2.setUser(booking.getUser());
    	booking2.setProperty(booking.getProperty());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.update(booking);
    	dao.persist(booking2);
    	
    	Booking booking3 = Generator.booking();
    	booking3.setUser(booking.getUser());
    	booking3.setProperty(booking.getProperty());
    	booking3.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.persist(booking3);
    	
    	List<Booking> bookings = bookingEntityService.getMyBookings(booking.getUser().getId(),1,1);
    	Assert.assertEquals(bookings.size(),1);
    	Assert.assertEquals(bookings.get(0).getId(),booking2.getId());
    	
    	dao.delete(Booking.class.getName(),"id",booking2.getId());
    	dao.delete(Booking.class.getName(),"id",booking3.getId());
    	tearDownBooking();
    }
    
    @Test
    public void getMyBookingsSize()
    {
    	setUpBooking();
    	
    	Booking booking2 = Generator.booking();
    	booking2.setUser(booking.getUser());
    	booking2.setProperty(booking.getProperty());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.update(booking);
    	dao.persist(booking2);
    	
    	Booking booking3 = Generator.booking();
    	booking3.setUser(booking.getUser());
    	booking3.setProperty(booking.getProperty());
    	booking3.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.persist(booking3);
    	
    	long size = bookingEntityService.getMyBookingsSize(booking.getUser().getId());
    	Assert.assertEquals(size,3);
    	
    	dao.delete(Booking.class.getName(),"id",booking2.getId());
    	dao.delete(Booking.class.getName(),"id",booking3.getId());
    	tearDownBooking();
    }
    
    @Test
    public void getPropertyBookings()
    {
    	setUpBooking();
    	
    	Calendar start = property.retrieveCurrentDay();
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	booking.setStartDate(start.getTime());
    	booking.setEndDate(end.getTime());
    	dao.update(booking);
    	
    	Booking booking2 = Generator.booking();
    	booking2.setUser(booking.getUser());
    	booking2.setProperty(booking.getProperty());
    	Calendar newStart = property.retrieveCurrentDay();
    	newStart.add(Calendar.DAY_OF_YEAR,2);
    	Calendar newEnd = property.retrieveCurrentDay();
    	newEnd.add(Calendar.DAY_OF_YEAR,2);
    	booking2.setStartDate(newStart.getTime());
    	booking2.setEndDate(newEnd.getTime());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.update(booking);
    	dao.persist(booking2);
    	
    	List<Booking> bookings = bookingEntityService.getPropertyBookings(booking.getProperty().getOwner().getId(),
    	    100,0);
    	Assert.assertEquals(bookings.size(),2);
    	Assert.assertEquals(bookings.get(0).getId(),booking2.getId());
    	Assert.assertEquals(bookings.get(1).getId(),booking.getId());
    	Assert.assertEquals(bookings.get(0).getProperty().getName(),property.getName());
    	
    	dao.delete(Booking.class.getName(),"id",booking2.getId());
    	tearDownBooking();
    }
    
    @Test
    public void getPropertyBookingsPagination()
    {
    	setUpBooking();
    	
    	Calendar start = property.retrieveCurrentDay();
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	booking.setStartDate(start.getTime());
    	booking.setEndDate(end.getTime());
    	dao.update(booking);
    	
    	Booking booking2 = Generator.booking();
    	booking2.setUser(booking.getUser());
    	booking2.setProperty(booking.getProperty());
    	Calendar newStart = property.retrieveCurrentDay();
    	newStart.add(Calendar.DAY_OF_YEAR,2);
    	Calendar newEnd = property.retrieveCurrentDay();
    	newEnd.add(Calendar.DAY_OF_YEAR,2);
    	booking2.setStartDate(newStart.getTime());
    	booking2.setEndDate(newEnd.getTime());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.update(booking);
    	dao.persist(booking2);
    	
    	Booking booking3 = Generator.booking();
    	booking3.setUser(booking.getUser());
    	booking3.setProperty(booking.getProperty());
    	Calendar newStart2 = property.retrieveCurrentDay();
    	newStart2.add(Calendar.DAY_OF_YEAR,3);
    	Calendar newEnd2 = property.retrieveCurrentDay();
    	newEnd2.add(Calendar.DAY_OF_YEAR,3);
    	booking3.setStartDate(newStart2.getTime());
    	booking3.setEndDate(newEnd2.getTime());
    	dao.persist(booking3);
    	
    	List<Booking> bookings = bookingEntityService.getPropertyBookings(booking.getProperty().getOwner().getId(),1,1);
    	Assert.assertEquals(bookings.size(),1);
    	Assert.assertEquals(bookings.get(0).getId(),booking2.getId());
    	
    	dao.delete(Booking.class.getName(),"id",booking2.getId());
    	dao.delete(Booking.class.getName(),"id",booking3.getId());
    	tearDownBooking();
    }
    
    @Test
    public void getPropertyBookingsSize()
    {
    	setUpBooking();
    	
    	Booking booking2 = Generator.booking();
    	booking2.setUser(booking.getUser());
    	booking2.setProperty(booking.getProperty());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.update(booking);
    	dao.persist(booking2);
    	
    	Booking booking3 = Generator.booking();
    	booking3.setUser(booking.getUser());
    	booking3.setProperty(booking.getProperty());
    	booking3.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.persist(booking3);
    	
    	long size = bookingEntityService.getPropertyBookingsSize(booking.getProperty().getOwner().getId());
    	Assert.assertEquals(size,3);
    	
    	dao.delete(Booking.class.getName(),"id",booking2.getId());
    	dao.delete(Booking.class.getName(),"id",booking3.getId());
    	tearDownBooking();
    }
    
    @Test
    public void getConfirmedBookingsNoReminderDayBeforeStart()
    {
    	setUpBooking();
    	
    	Calendar tomorrow = booking.getProperty().retrieveCurrentTime();
    	tomorrow.add(Calendar.DAY_OF_YEAR,1);
    	Calendar startDate = tomorrow;
    	booking.setStartDate(startDate.getTime());
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getConfirmedBookingsNoReminderDayBeforeStart();
    	
    	Assert.assertEquals(bookings.size(),1);
    	Assert.assertEquals(bookings.get(0).getId(),booking.getId());
    	
    	tearDownBooking();
    }
    
    @Test
    public void getConfirmedBookingsNoReminderDayBeforeStartOneHourEarly()
    {
    	setUpBooking();
    	
    	Calendar tomorrow = booking.getProperty().retrieveCurrentTime();
    	tomorrow.add(Calendar.DAY_OF_YEAR,1);
    	Calendar startDate = booking.getProperty().retrieveCurrentTime();
    	startDate.setTime(tomorrow.getTime());
    	startDate.add(Calendar.HOUR, 1);
    	booking.setStartDate(startDate.getTime());
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getConfirmedBookingsNoReminderDayBeforeStart();
    	
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getConfirmedBookingsNoReminderDayBeforeStartWrongStatus()
    {
    	setUpBooking();
    	
    	Calendar tomorrow = booking.getProperty().retrieveCurrentTime();
    	tomorrow.add(Calendar.DAY_OF_YEAR,1);
    	Calendar startDate = booking.getProperty().retrieveCurrentTime();
    	startDate.setTime(tomorrow.getTime());
    	booking.setStartDate(startDate.getTime());
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_REFUND_FULL);
    	
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getConfirmedBookingsNoReminderDayBeforeStart();
    	
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getConfirmedBookingsNoFeedbackDayAfterEnd()
    {
    	setUpBooking();
    	
    	Calendar now = booking.getProperty().retrieveCurrentTime();
    	Calendar endDate = booking.getProperty().retrieveCurrentTime();
    	endDate.setTime(now.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, -1);
    	booking.setEndDate(endDate.getTime());
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getConfirmedBookingsNoFeedbackDayAfterEnd();
    	
    	Assert.assertEquals(bookings.size(),1);
    	Assert.assertEquals(bookings.get(0).getId(),booking.getId());
    	
    	tearDownBooking();
    }
    
    @Test
    public void getConfirmedBookingsNoFeedbackDayAfterEndWrongStatus()
    {
    	setUpBooking();
    	
    	Calendar now = booking.getProperty().retrieveCurrentTime();
    	Calendar endDate = booking.getProperty().retrieveCurrentTime();
    	endDate.setTime(now.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, -1);
    	booking.setEndDate(endDate.getTime());
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_ERROR);
    	
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getConfirmedBookingsNoFeedbackDayAfterEnd();
    	
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getConfirmedBookingsNoFeedbackDayAfterEndEmailAlreadySent()
    {
    	setUpBooking();
    	
    	Calendar now = booking.getProperty().retrieveCurrentTime();
    	Calendar endDate = booking.getProperty().retrieveCurrentTime();
    	endDate.setTime(now.getTime());
    	booking.setEndDate(endDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, -1);
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setFeedbackEmailBool(true);
    	
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getConfirmedBookingsNoFeedbackDayAfterEnd();
    	
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
    
    @Test
    public void getConfirmedBookingsNoFeedbackDayAfterEndWrongDate()
    {
    	setUpBooking();
    	
    	Calendar now = booking.getProperty().retrieveCurrentTime();
    	Calendar endDate = booking.getProperty().retrieveCurrentTime();
    	endDate.setTime(now.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, 1);
    	endDate.add(Calendar.HOUR,1);
    	booking.setEndDate(endDate.getTime());
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	
    	dao.update(booking);
    	
    	List<Booking> bookings = bookingEntityService.getConfirmedBookingsNoFeedbackDayAfterEnd();
    	
    	Assert.assertEquals(bookings.size(),0);
    	
    	tearDownBooking();
    }
}
