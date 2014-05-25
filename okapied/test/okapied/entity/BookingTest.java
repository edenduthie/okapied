package okapied.entity;

import java.util.Calendar;

import okapied.util.Configuration;
import okapied.util.DateUtil;
import okapied.util.Generator;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BookingTest 
{
    @Test
    public void getPropertyOwnerAmount()
    {
    	Booking booking = Generator.booking();
    	booking.setTotal(100.10f);
    	Assert.assertEquals(booking.getPropertyOwnerAmount(),new Double(93.09));
    }
    
    @Test
    public void getMyAmount()
    {
    	Booking booking = Generator.booking();
    	booking.setTotal(100.10f);
    	Assert.assertEquals(booking.getMyAmount(),new Double(7.01));
    }
    
    @Test
    public void updateExpiration()
    {
    	Booking booking = Generator.booking();
    	booking.updateExpiration();
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	Calendar expiration = DateUtil.getCurrentCalendarCompareTZ();
    	expiration.setTime(booking.getExpirationDate());
    	now.add(Calendar.MINUTE, Configuration.instance().getIntProperty("BOOKING_TIMEOUT_MIN"));
    	Assert.assertEquals(now.get(Calendar.HOUR),expiration.get(Calendar.HOUR));
    	Assert.assertEquals(now.get(Calendar.DAY_OF_YEAR),expiration.get(Calendar.DAY_OF_YEAR));
    	Assert.assertEquals(now.get(Calendar.YEAR),expiration.get(Calendar.YEAR));
    }
    
    @Test
    public void readyToLeaveBookingUser()
    {
    	Booking booking = Generator.booking();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	Calendar start = booking.getProperty().retrieveCurrentDay();
    	start.add(Calendar.HOUR, -1);
    	booking.setStartDate(start.getTime());
    	Assert.assertTrue(booking.readyToLeaveFeedbackUser());
    }
    
    @Test
    public void readyToLeaveBookingUserNotYet()
    {
    	Booking booking = Generator.booking();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	Calendar start = booking.getProperty().retrieveCurrentDay();
    	start.add(Calendar.HOUR, 1);
    	booking.setStartDate(start.getTime());
    	Assert.assertFalse(booking.readyToLeaveFeedbackUser());
    }
    
    @Test
    public void readyToLeaveBookingUserWrongStatus()
    {
    	Booking booking = Generator.booking();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	Calendar start = booking.getProperty().retrieveCurrentDay();
    	start.add(Calendar.HOUR, -1);
    	booking.setStartDate(start.getTime());
    	Assert.assertFalse(booking.readyToLeaveFeedbackUser());
    }
    
    @Test
    public void readyToLeaveFeedbackOwner()
    {
    	Booking booking = Generator.booking();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	Calendar end = booking.getProperty().retrieveCurrentDay();
    	end.add(Calendar.HOUR, -1);
    	booking.setEndDate(end.getTime());
    	Assert.assertTrue(booking.readyToLeaveFeedbackOwner());
    }
    
    @Test
    public void readyToLeaveFeedbackOwnerNotYet()
    {
    	Booking booking = Generator.booking();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	Calendar end = booking.getProperty().retrieveCurrentDay();
    	end.add(Calendar.HOUR, 1);
    	booking.setEndDate(end.getTime());
    	Assert.assertFalse(booking.readyToLeaveFeedbackOwner());
    }
    
    @Test
    public void readyToLeaveFeedbackOwnerWrongStatus()
    {
    	Booking booking = Generator.booking();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	Calendar end = booking.getProperty().retrieveCurrentDay();
    	end.add(Calendar.HOUR, -1);
    	booking.setEndDate(end.getTime());
    	Assert.assertFalse(booking.readyToLeaveFeedbackOwner());
    }
    
    @Test
    public void isRefundAvailableString()
    {
    	Booking booking = Generator.booking();
    	booking.setRefundPolicy(Property.REFUND_POLICY_STRICT);
    	Assert.assertFalse(booking.isRefundAvailable());
    }
    
    @Test
    public void isRefundAvailableTrue()
    {
    	Booking booking = Generator.booking();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setRefundPolicy(Property.REFUND_POLICY_STANDARD);
    	Assert.assertTrue(booking.isRefundAvailable());
    }
    
    @Test
    public void isRefundAvailableWrongStatus()
    {
    	Booking booking = Generator.booking();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_REFUND_FULL);
    	booking.setRefundPolicy(Property.REFUND_POLICY_STANDARD);
    	Assert.assertFalse(booking.isRefundAvailable());
    }
    
    @Test
    public void isRefundAvailableAlreadyPaid()
    {
    	Booking booking = Generator.booking();
    	booking.setRefundPolicy(Property.REFUND_POLICY_STANDARD);
    	booking.setPaymentCompleteBool(true);
    	Assert.assertFalse(booking.isRefundAvailable());
    }
    
    @Test
    public void bookedGreaterThan60DaysAgoYes()
    {
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.DAY_OF_YEAR, -60);
    	Booking booking = Generator.booking();
    	booking.setBookingDate(now.getTime());
    	
    	Assert.assertTrue(booking.bookedGreaterThan60DaysAgo());
    }
    
    @Test
    public void bookedGreaterThan60DaysAgoNo()
    {
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.DAY_OF_YEAR, -59);
    	now.add(Calendar.HOUR_OF_DAY,-23);
    	Booking booking = Generator.booking();
    	booking.setBookingDate(now.getTime());
    	
    	Assert.assertFalse(booking.bookedGreaterThan60DaysAgo());
    }
    
    @Test
    public void retrieveFirstDayOfBookingMatch()
    {
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	Calendar compare = DateUtil.getCurrentCalendarCompareTZ();
    	compare.setTime(now.getTime());
    	Booking booking = Generator.booking();
    	booking.setStartDate(now.getTime());
    	Assert.assertTrue(booking.retrieveFirstDayOfBooking(compare));
    }
    
    @Test
    public void retrieveFirstDayOfBookingNoMatch()
    {
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	Calendar compare = DateUtil.getCurrentCalendarCompareTZ();
    	compare.setTime(now.getTime());
    	compare.add(Calendar.HOUR,1);
    	Booking booking = Generator.booking();
    	booking.setStartDate(now.getTime());
    	Assert.assertFalse(booking.retrieveFirstDayOfBooking(compare));
    }
    
    @Test
    public void retrieveLastDayOfBookingMatch()
    {
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	Calendar compare = DateUtil.getCurrentCalendarCompareTZ();
    	compare.setTime(now.getTime());
    	Booking booking = Generator.booking();
    	booking.setEndDate(now.getTime());
    	Assert.assertTrue(booking.retrieveLastDayOfBooking(compare));
    }
    
    @Test
    public void retrieveLastDayOfBookingNoMatch()
    {
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	Calendar compare = DateUtil.getCurrentCalendarCompareTZ();
    	compare.setTime(now.getTime());
    	compare.add(Calendar.HOUR,1);
    	Booking booking = Generator.booking();
    	booking.setEndDate(now.getTime());
    	Assert.assertFalse(booking.retrieveLastDayOfBooking(compare));
    }
}
