package okapied.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okapied.BaseTest;
import okapied.entity.Booking;
import okapied.entity.CurrencyCode;
import okapied.entity.Feedback;
import okapied.entity.Flooring;
import okapied.entity.OkapiedGrantedAuthority;
import okapied.entity.Property;
import okapied.entity.PropertyType;
import okapied.entity.UserFeedback;
import okapied.exception.FeedbackException;
import okapied.exception.OkaSecurityException;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FeedbackServiceTest extends BaseTest
{
    @Autowired
    FeedbackService feedbackService;
    
    Feedback feedback;
    
    public void setUp()
    {
    	property = Generator.property();
    	saveProperty();
    	feedback = Generator.feedback();
    	feedback.getBooking().setProperty(property);
    	dao.persist(feedback.getBooking().getUser());
    	dao.persist(feedback.getBooking());
    	feedback.setProperty(property);
    	dao.persist(feedback);
    	booking = feedback.getBooking();
    }
    
    public void tearDown()
    {
    	dao.delete(Feedback.class.getName(), "id", feedback.getId());
    	dao.delete(Booking.class.getName(), "id", feedback.getBooking().getId());
    	accountService.remove(feedback.getBooking().getUser());
    	dao.deleteAll(OkapiedGrantedAuthority.class.getName());
    	dao.delete(Property.class.getName(), "id", property.getId());
    	dao.deleteAll(Flooring.class.getName());
    	dao.deleteAll(PropertyType.class.getName());
    	dao.deleteAll(CurrencyCode.class.getName());
        accountService.remove(property.getOwner());
        dao.deleteAll(OkapiedGrantedAuthority.class.getName());
    	removeLocations();
    }
    
	@Test
	public void getFeedback()
	{	
		setUp();
    	List<Feedback> feedbackList = feedbackService.getFeedback(property.getId(),null,null,null);
    	Assert.assertEquals(1,feedbackList.size());
    	Assert.assertNull(feedbackList.get(0).getProperty());
    	tearDown();
	}
	
	@Test
	public void getFeedbackPositive()
	{	
		setUp();
    	List<Feedback> feedbackList = feedbackService.getFeedback(property.getId(),true,null,null);
    	Assert.assertEquals(1,feedbackList.size());
    	for( Feedback feedback : feedbackList )
    	{
    		Assert.assertTrue(feedback.getPositive());
    	}
    	tearDown();
	}
	
	@Test
	public void getFeedbackNegative()
	{	
		setUp();
    	List<Feedback> feedbackList = feedbackService.getFeedback(property.getId(),false,null,null);
    	Assert.assertEquals(0,feedbackList.size());
    	tearDown();
	}
	
	@Test
	public void getFeedbackLimit()
	{	
		setUp();
		
    	Feedback feedback2 = Generator.feedback();
    	feedback2.getBooking().setProperty(property);
    	feedback2.getBooking().setUser(feedback.getBooking().getUser());
    	dao.persist(feedback2.getBooking());
    	feedback2.setProperty(property);
    	dao.persist(feedback2);
		
    	List<Feedback> feedbackList = feedbackService.getFeedback(property.getId(),null,1,null);
    	Assert.assertEquals(1,feedbackList.size());
    	
    	dao.delete(Feedback.class.getName(), "id", feedback2.getId());
    	dao.delete(Booking.class.getName(), "id", feedback2.getBooking().getId());
    	tearDown();
	}
	
	@Test
	public void getFeedbackOffset()
	{	
		setUp();
		
    	Feedback feedback2 = Generator.feedback();
    	feedback2.getBooking().setProperty(property);
    	feedback2.getBooking().setUser(feedback.getBooking().getUser());
    	dao.persist(feedback2.getBooking());
    	feedback2.setProperty(property);
    	dao.persist(feedback2);
		
    	List<Feedback> feedbackList = feedbackService.getFeedback(property.getId(),null,null,1);
    	Assert.assertEquals(1,feedbackList.size());
    	
    	dao.delete(Feedback.class.getName(), "id", feedback2.getId());
    	dao.delete(Booking.class.getName(), "id", feedback2.getBooking().getId());
    	tearDown();
	}
	
	@Test
	public void getFeedbackCorrectOrder()
	{	
		setUp();
		
		List<Feedback> testList = new ArrayList<Feedback>();
		for( int i=0; i < 10; ++i )
		{
	    	Feedback feedback2 = Generator.feedback();
	    	feedback2.getBooking().setProperty(property);
	    	feedback2.getBooking().setUser(feedback.getBooking().getUser());
	    	dao.persist(feedback2.getBooking());
	    	feedback2.setProperty(property);
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(feedback2.getDateLeft());
	    	if( i < 5 ) calendar.add(Calendar.DAY_OF_YEAR, i);
	    	else calendar.add(Calendar.DAY_OF_YEAR, -i);
	    	feedback2.setDateLeft(calendar.getTime());
	    	dao.persist(feedback2);
	    	testList.add(feedback2);
		}
		
    	List<Feedback> feedbackList = feedbackService.getFeedback(property.getId(),null,null,null);
    	Assert.assertEquals(feedbackList.size(),11);
    	
    	Feedback lastFeedback = null;
    	for( Feedback feedbackResult : feedbackList )
    	{
    		if( lastFeedback != null )
    		{
    			int result = lastFeedback.getDateLeft().compareTo(feedbackResult.getDateLeft());
    			Assert.assertTrue(result >= 0);
    		}
    		lastFeedback = feedbackResult;
    	}
    	
    	for( Feedback feedbackDelete : testList )
    	{
    	    dao.delete(Feedback.class.getName(), "id", feedbackDelete.getId());
    	    dao.delete(Booking.class.getName(), "id", feedbackDelete.getBooking().getId());
    	}
    	
    	tearDown();
	}
	
	@Test
	public void getFeedbackSize()
	{	
		setUp();
		
		List<Feedback> testList = new ArrayList<Feedback>();
		for( int i=0; i < 10; ++i )
		{
	    	Feedback feedback2 = Generator.feedback();
	    	feedback2.getBooking().setProperty(property);
	    	feedback2.getBooking().setUser(feedback.getBooking().getUser());
	    	dao.persist(feedback2.getBooking());
	    	feedback2.setProperty(property);
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(feedback2.getDateLeft());
	    	if( i < 5 ) calendar.add(Calendar.DAY_OF_YEAR, i);
	    	else calendar.add(Calendar.DAY_OF_YEAR, -i);
	    	feedback2.setDateLeft(calendar.getTime());
	    	dao.persist(feedback2);
	    	testList.add(feedback2);
		}
		
    	Long size  = feedbackService.getFeedbackSize(property.getId(),null);
    	Assert.assertEquals(new Long(11),size);
    	
    	for( Feedback feedbackDelete : testList )
    	{
    	    dao.delete(Feedback.class.getName(), "id", feedbackDelete.getId());
    	    dao.delete(Booking.class.getName(), "id", feedbackDelete.getBooking().getId());
    	}
    	
    	tearDown();
	}
	
	@Test
	public void leaveFeedbackUserWrongUser() throws FeedbackException
	{
		setUp();
		try
		{
			feedbackService.leaveFeedbackUser(feedback.getBooking().getId(),feedback,-999);
		}
		catch( OkaSecurityException e ){}
		tearDown();
	}
	
	@Test
	public void leaveFeedbackUserNotReady() throws OkaSecurityException
	{
		setUp();
		booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
		dao.update(booking);
		try
		{
			feedbackService.leaveFeedbackUser(feedback.getBooking().getId(),feedback,
			    feedback.getBooking().getUser().getId());
		}
		catch( FeedbackException e ){}
		tearDown();
	}
	
	@Test
	public void leaveFeedbackPositive() throws OkaSecurityException, FeedbackException
	{
		setUp();
		booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
		Calendar start = property.retrieveCurrentDay();
		start.set(Calendar.DAY_OF_YEAR, -1);
		booking.setStartDate(start.getTime());
		dao.update(booking);
		
		Feedback feedback2 = Generator.feedback();
		feedback2.setBooking(null);
		
	    feedbackService.leaveFeedbackUser(feedback.getBooking().getId(),feedback2,
			feedback.getBooking().getUser().getId());
	    
	    Property propertyResult = (Property) dao.get(Property.class.getName(),"id",property.getId());
	    Assert.assertEquals(propertyResult.getPositiveFeedback(),new Integer(1));
	    Assert.assertEquals(propertyResult.getNegativeFeedback(),new Integer(0));
	    Assert.assertEquals(propertyResult.getPercentagePositive(),new Integer(100));
	    
	    Feedback feedbackResult = feedbackService.get(feedback2.getId());
	    Assert.assertNotNull(feedbackResult.getDateLeft());
	    Assert.assertNotNull(feedbackResult.getProperty());
	    
	    Booking bookingResult = (Booking) dao.get(Booking.class.getName(),"id",booking.getId());
	    Assert.assertNotNull(bookingResult.getUserFeedback());
	    Assert.assertEquals(bookingResult.getUserFeedback().getId(),feedback2.getId());

	    booking.setUserFeedback(null);
	    dao.update(booking);
	    dao.delete(Feedback.class.getName(), "id", feedback2.getId());
		tearDown();
	}
	
	@Test
	public void leaveFeedbackPropertyValuesSet() throws OkaSecurityException, FeedbackException
	{
		setUp();
		booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
		Calendar start = property.retrieveCurrentDay();
		start.set(Calendar.DAY_OF_YEAR, -1);
		booking.setStartDate(start.getTime());
		dao.update(booking);
		
		property.setAccuracy(4f);
		property.setCleanliness(4f);
		property.setValueForMoney(4f);
		property.setRating(4f);
		property.setNegativeFeedback(1);
		property.setPositiveFeedback(0);
		dao.update(property);
		
		Feedback feedback2 = Generator.feedback();
		feedback2.setAccuracy(2);
		feedback2.setRating(2);
		feedback2.setValueForMoney(2);
		feedback2.setCleanliness(2);
		feedback2.setBooking(null);
		
	    feedbackService.leaveFeedbackUser(feedback.getBooking().getId(),feedback2,
			feedback.getBooking().getUser().getId());
	    
	    Property propertyResult = (Property) dao.get(Property.class.getName(),"id",property.getId());
	    Assert.assertEquals(propertyResult.getPositiveFeedback(),new Integer(1));
	    Assert.assertEquals(propertyResult.getNegativeFeedback(),new Integer(1));
	    Assert.assertEquals(propertyResult.getPercentagePositive(),new Integer(50));
	    Assert.assertEquals(propertyResult.getAccuracy(),3f);
	    Assert.assertEquals(propertyResult.getCleanliness(),3f);
	    Assert.assertEquals(propertyResult.getValueForMoney(),3f);
	    Assert.assertEquals(propertyResult.getRating(),3f);
	    
	    Feedback feedbackResult = feedbackService.get(feedback2.getId());
	    Assert.assertNotNull(feedbackResult.getDateLeft());
	    Assert.assertNotNull(feedbackResult.getProperty());
	    
	    Booking bookingResult = (Booking) dao.get(Booking.class.getName(),"id",booking.getId());
	    Assert.assertNotNull(bookingResult.getUserFeedback());
	    Assert.assertEquals(bookingResult.getUserFeedback().getId(),feedback2.getId());

	    booking.setUserFeedback(null);
	    dao.update(booking);
	    dao.delete(Feedback.class.getName(), "id", feedback2.getId());
		tearDown();
	}
	
	@Test
	public void leaveFeedbackNegative() throws OkaSecurityException, FeedbackException
	{
		setUp();
		booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
		Calendar start = property.retrieveCurrentDay();
		start.set(Calendar.DAY_OF_YEAR, -1);
		booking.setStartDate(start.getTime());
		dao.update(booking);
		
		Feedback feedback2 = Generator.feedback();
		feedback2.setPositive(false); 
		feedback2.setBooking(null);
		
	    feedbackService.leaveFeedbackUser(feedback.getBooking().getId(),feedback2,
			feedback.getBooking().getUser().getId());
	    
	    Property propertyResult = (Property) dao.get(Property.class.getName(),"id",property.getId());
	    Assert.assertEquals(propertyResult.getPositiveFeedback(),new Integer(0));
	    Assert.assertEquals(propertyResult.getNegativeFeedback(),new Integer(1));
	    Assert.assertEquals(propertyResult.getPercentagePositive(),new Integer(0));

	    booking.setUserFeedback(null);
	    dao.update(booking);
	    dao.delete(Feedback.class.getName(), "id", feedback2.getId());
		tearDown();
	}
	
	@Test
	public void leaveFeedbackOwnerWrongUser() throws FeedbackException
	{
		setUp();
		UserFeedback feedback2 = Generator.userFeedback();
		try
		{
			feedbackService.leaveFeedbackOwner(feedback.getBooking().getId(),feedback2,-999);
		}
		catch( OkaSecurityException e ){}
		tearDown();
	}
	
	@Test
	public void leaveFeedbackOwnerNotReady() throws OkaSecurityException
	{
		setUp();
		booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
		dao.update(booking);
		UserFeedback feedback2 = Generator.userFeedback();
		try
		{
			feedbackService.leaveFeedbackOwner(feedback.getBooking().getId(),feedback2,
			    feedback.getBooking().getProperty().getOwner().getId());
		}
		catch( FeedbackException e ){}
		tearDown();
	}
	
	@Test
	public void leaveFeedbackOwnerPositive() throws OkaSecurityException, FeedbackException
	{
		setUp();
		booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
		Calendar start = property.retrieveCurrentDay();
		start.set(Calendar.DAY_OF_YEAR, -1);
		booking.setEndDate(start.getTime());
		dao.update(booking);
		
		UserFeedback feedback2 = Generator.userFeedback();
		feedback2.setBooking(null);
		
	    feedbackService.leaveFeedbackOwner(feedback.getBooking().getId(),feedback2,
			feedback.getBooking().getProperty().getOwner().getId());
	    
	    UserFeedback feedbackResult = (UserFeedback) dao.get(UserFeedback.class.getName(),"id", feedback2.getId());
	    Assert.assertNotNull(feedbackResult.getDateLeft());
	    Assert.assertNotNull(feedbackResult.getUser());
	    
	    Booking bookingResult = (Booking) dao.get(Booking.class.getName(),"id",booking.getId());
	    Assert.assertNotNull(bookingResult.getOwnerFeedback());
	    Assert.assertEquals(bookingResult.getOwnerFeedback().getId(),feedback2.getId());

	    booking.setOwnerFeedback(null);
	    dao.update(booking);
	    dao.delete(UserFeedback.class.getName(), "id", feedback2.getId());
		tearDown();
	}
	
	@Test
	public void leaveFeedbackOwnerNegative() throws OkaSecurityException, FeedbackException
	{
		setUp();
		booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
		Calendar start = property.retrieveCurrentDay();
		start.set(Calendar.DAY_OF_YEAR, -1);
		booking.setEndDate(start.getTime());
		dao.update(booking);
		
		UserFeedback feedback2 = Generator.userFeedback();
		feedback2.setPositive(false);
		feedback2.setBooking(null);
		
	    feedbackService.leaveFeedbackOwner(feedback.getBooking().getId(),feedback2,
			feedback.getBooking().getProperty().getOwner().getId());
	    
	    UserFeedback feedbackResult = (UserFeedback) dao.get(UserFeedback.class.getName(),"id", feedback2.getId());
	    Assert.assertNotNull(feedbackResult.getDateLeft());
	    Assert.assertNotNull(feedbackResult.getUser());
	    
	    Booking bookingResult = (Booking) dao.get(Booking.class.getName(),"id",booking.getId());
	    Assert.assertNotNull(bookingResult.getOwnerFeedback());
	    Assert.assertEquals(bookingResult.getOwnerFeedback().getId(),feedback2.getId());

	    booking.setOwnerFeedback(null);
	    dao.update(booking);
	    dao.delete(UserFeedback.class.getName(), "id", feedback2.getId());
		tearDown();
	}
}
