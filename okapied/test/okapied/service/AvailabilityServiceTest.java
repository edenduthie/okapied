package okapied.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import okapied.BaseTest;
import okapied.entity.Availability;
import okapied.entity.AvailabilityWeekDay;
import okapied.entity.Booking;
import okapied.entity.Country;
import okapied.entity.CurrencyCode;
import okapied.entity.Flooring;
import okapied.entity.Matchable;
import okapied.entity.OkapiedGrantedAuthority;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Price;
import okapied.entity.PriceWeekDay;
import okapied.entity.Property;
import okapied.entity.PropertyDay;
import okapied.entity.PropertyType;
import okapied.entity.Region;
import okapied.exception.AlreadyBookedException;
import okapied.exception.OkaSecurityException;
import okapied.util.Configuration;
import okapied.util.DateUtil;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AvailabilityServiceTest extends BaseTest
{
    @Autowired
    AvailabilityService availabilityService;
	
	@Autowired
	AccountService accountService;
	
	Float testPrice = 100f;
	
	public void setUp()
	{
    	av = Generator.availability();
    	saveAvailability();		
	}
	
	public void setUpPrice()
	{
    	price = Generator.price();
    	savePrice();		
	}
	
	public void setUpPriceAndAvailability()
	{
		av = Generator.availability();
    	saveAvailability();	
    	price = Generator.price();
    	price.setProperty(av.getProperty());
    	dao.persist(price);
	}
	
	public void tearDown()
	{
    	removeAvailability();
	}
	
	public void tearDownPrice()
	{
    	removePrice();
	}
	
	public void tearDownPriceAndAvailability()
	{
    	removePriceAndAvailability();
	}
    
    @Test
    public void getMatchingStart()
    {
    	setUp();
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
    	List<Matchable> results = availabilityService.get(property, period);
    	Assert.assertEquals(results.size(),1);
    	
    	tearDown();
    }
    
    @Test
    public void getMatchingEnd()
    {
    	setUp();
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
    	start.add(Calendar.DAY_OF_YEAR, displayDays);
    	System.out.println("END:"+start.getTime());
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
    	
    	List<Matchable> results = availabilityService.get(property, period);
    	Assert.assertEquals(results.size(),1);
    	
    	tearDown();
    }
    
    @Test
    public void getStartInMiddle()
    {
    	setUp();
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
    	start.add(Calendar.DAY_OF_YEAR, -5);
    	System.out.println("END:"+start.getTime());
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
    	
    	List<Matchable> results = availabilityService.get(property, period);
    	Assert.assertEquals(results.size(),1);
    	
    	tearDown();
    }
    
    @Test
    public void getEndInMiddle()
    {
    	setUp();
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
    	start.add(Calendar.DAY_OF_YEAR, 5);
    	System.out.println("END:"+start.getTime());
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
    	List<Matchable> results = availabilityService.get(property, period);
    	Assert.assertEquals(results.size(),1);
    	
    	tearDown();
    }
    
    @Test
    public void availableZeroMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertFalse(result);
    }
    
    @Test
    public void availableOneMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	avList.add(av);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertTrue(result);
    }
    
    @Test
    public void availableOneFalseMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	av.setAvailable(0);
    	avList.add(av);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertFalse(result);
    }
    
    @Test
    public void availableOneTrueOneFalseMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	av.setAvailable(0);
    	avList.add(av);
    	Availability av2 = Generator.availability();
    	avList.add(av2);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertFalse(result);
    }
    
    @Test
    public void getMatchingStartWithDays()
    {
    	setUp();
    	
    	AvailabilityWeekDay av2 = Generator.availabilityWeekDay();
    	av2.setProperty(property);
    	dao.persist(av2);
    	
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
    	List<Matchable> results = availabilityService.get(property, period);
    	Assert.assertEquals(results.size(),2);
    	
    	dao.delete(AvailabilityWeekDay.class.getName(), "id", av2.getId());
    	
    	tearDown();
    } 
    
    @Test
    public void availableNoMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	
    	Availability av = Generator.availability(); // starts on today for 14 days
    	avList.add(av);
    	
    	av = Generator.availability();
    	Calendar endCalendar = Calendar.getInstance();
    	endCalendar.setTime(av.getStartDate());
    	endCalendar.add(Calendar.DAY_OF_YEAR, 1); // make 1 day long
    	av.setEndDate(endCalendar.getTime());
    	av.setDayOnlyBool(true);
    	avList.add(av);
    	
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, -5);
    	
    	AvailabilityWeekDay avWD = new AvailabilityWeekDay(testDate.get(Calendar.DAY_OF_WEEK)+1,true);
    	avList.add(avWD);

    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertFalse(result);
    }
    
    @Test
    public void availableDayMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, -5);
    	
    	Availability av = Generator.availability(); // starts on today for 14 days
    	avList.add(av);
    	
    	av = Generator.availability();
    	av.setStartDate(testDate.getTime());
    	Calendar endCalendar = Calendar.getInstance();
    	endCalendar.setTime(testDate.getTime());
    	endCalendar.add(Calendar.DAY_OF_YEAR, 1); // make 1 day long
    	av.setEndDate(endCalendar.getTime());
    	av.setDayOnlyBool(true);
    	avList.add(av);
    	
    	AvailabilityWeekDay avWD = new AvailabilityWeekDay(testDate.get(Calendar.DAY_OF_WEEK)+1,true);
    	avList.add(avWD);

    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertTrue(result);
    }
    
    @Test
    public void availableDayMatchOthersFalseMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	
    	Availability av = Generator.availability(); // starts on today for 14 days
    	av.setAvailableBool(false);
    	avList.add(av);
    	
    	av = Generator.availability();
    	av.setStartDate(testDate.getTime());
    	Calendar endCalendar = Calendar.getInstance();
    	endCalendar.setTime(testDate.getTime());
    	endCalendar.add(Calendar.DAY_OF_YEAR, 1); // make 1 day long
    	av.setEndDate(endCalendar.getTime());
    	av.setDayOnlyBool(true);
    	avList.add(av);
    	
    	AvailabilityWeekDay avWD = new AvailabilityWeekDay(testDate.get(Calendar.DAY_OF_WEEK),true);
    	avWD.setAvailableBool(false);
    	avList.add(avWD);

    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertTrue(result);
    }
    
    @Test
    public void availableDayFalseMatchOthersMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	
    	Availability av = Generator.availability(); // starts on today for 14 days
    	av.setAvailableBool(true);
    	avList.add(av);
    	
    	av = Generator.availability();
    	av.setStartDate(testDate.getTime());
    	Calendar endCalendar = Calendar.getInstance();
    	endCalendar.setTime(testDate.getTime());
    	endCalendar.add(Calendar.DAY_OF_YEAR, 1); // make 1 day long
    	av.setEndDate(endCalendar.getTime());
    	av.setDayOnlyBool(true);
    	av.setAvailableBool(false);
    	avList.add(av);
    	
    	AvailabilityWeekDay avWD = new AvailabilityWeekDay(testDate.get(Calendar.DAY_OF_WEEK),true);
    	avWD.setAvailableBool(true);
    	avList.add(avWD);

    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertFalse(result);
    }
    
    @Test
    public void availableWeekDayMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, -5);
    	
    	Availability av = Generator.availability(); // starts on today for 14 days
    	av.setAvailableBool(true);
    	avList.add(av);
    	
    	av = Generator.availability();
    	Calendar endCalendar = Calendar.getInstance();
    	endCalendar.setTime(av.getStartDate());
    	endCalendar.add(Calendar.DAY_OF_YEAR, 1); // make 1 day long
    	av.setEndDate(endCalendar.getTime());
    	av.setDayOnlyBool(true);
    	av.setAvailableBool(true);
    	avList.add(av);
    	
    	AvailabilityWeekDay avWD = new AvailabilityWeekDay(testDate.get(Calendar.DAY_OF_WEEK),true);
    	avWD.setAvailableBool(true);
    	avList.add(avWD);

    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertTrue(result);
    }
    
    @Test
    public void availableWeekDayMatchFalseAvMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	
    	Availability av = Generator.availability(); // starts on today for 14 days
    	av.setAvailableBool(false);
    	avList.add(av);
    	
    	AvailabilityWeekDay avWD = new AvailabilityWeekDay(testDate.get(Calendar.DAY_OF_WEEK),true);
    	avWD.setAvailableBool(true);
    	avList.add(avWD);

    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertTrue(result);
    }
    
    @Test
    public void availableAvMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	
    	Availability av = Generator.availability(); // starts on today for 14 days
    	av.setAvailableBool(true);
    	avList.add(av);

    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertTrue(result);
    }
    
    @Test
    public void availableAvFalseMatch()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	
    	Availability av = Generator.availability(); // starts on today for 14 days
    	av.setAvailableBool(false);
    	avList.add(av);

    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertFalse(result);
    }
    
    @Test
    public void getMatchingStartWrongProperty()
    {
    	setUp();
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		Property searchProperty = new Property();
		searchProperty.setId(999);
    	List<Matchable> results = availabilityService.get(searchProperty, period);
    	System.out.println("RESULTs:"+results.size());
    	Assert.assertEquals(results.size(),0);
    	
    	tearDown();
    }
    
    @Test
    public void getStartAndEndCompleteCoverage()
    {
    	setUp();
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		Calendar longAgo = Calendar.getInstance();
		longAgo.setTime(av.getStartDate());
		longAgo.add(Calendar.YEAR, -100);
		av.setStartDate(longAgo.getTime());
		
		Calendar future = Calendar.getInstance();
		future.setTime(av.getEndDate());
		future.add(Calendar.YEAR, 100);
		av.setEndDate(future.getTime());
		dao.update(av);
		
    	List<Matchable> results = availabilityService.get(property, period);
    	Assert.assertEquals(results.size(),1);
    	
    	tearDown();
    }
    
    @Test
    public void getMatchingStartWithBookings()
    {
    	setUp();
    	Booking booking = Generator.booking();
    	booking.setProperty(property);
    	Collection<GrantedAuthority> auths = booking.getUser().getAuthorities();
    	for( GrantedAuthority auth : auths )
    	{
    		dao.persist((OkapiedGrantedAuthority)auth);
    	}
    	dao.persist(booking.getUser());
    	dao.persist(booking);
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
    	List<Matchable> results = availabilityService.get(property, period);
    	Assert.assertEquals(results.size(),2);
    	
    	dao.deleteAll(Booking.class.getName());
    	accountService.remove(booking.getUser());
    	dao.deleteAll(OkapiedGrantedAuthority.class.getName());
    	tearDown();
    }
    
    @Test
    public void availableOneMatchBookingNotReserved()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	avList.add(av);
    	Booking booking = Generator.booking();
    	booking.setReservedBool(false);
    	booking.setId(1);
    	avList.add(booking);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertFalse(result);
    	Assert.assertFalse(propertyDay.isAvailable());
    	Assert.assertFalse(propertyDay.isReserved());
    	Assert.assertTrue(propertyDay.getBookingDay());
    }
    
    @Test
    public void availableOneMatchBookingReserved()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	avList.add(av);
    	Booking booking = Generator.booking();
    	booking.setReservedBool(true);
    	avList.add(booking);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertTrue(result);
    	Assert.assertTrue(propertyDay.isAvailable());
    	Assert.assertTrue(propertyDay.isReserved());
    }
    
    @Test
    public void availableOneMatchBookingReservedButExpired()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	avList.add(av);
    	Booking booking = Generator.booking();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_RESERVATION);
    	booking.setReservedBool(true);
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.HOUR, -1);
    	booking.setExpirationDate(now.getTime());
    	avList.add(booking);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertTrue(result);
    	Assert.assertTrue(propertyDay.isAvailable());
    	Assert.assertFalse(propertyDay.isReserved());
    }
    
    @Test
    public void availableOneMatchBookingCancelled()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	avList.add(av);
    	Booking booking = Generator.booking();
    	booking.setReservedBool(false);
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CANCELLED);
    	avList.add(booking);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertTrue(result);
    	Assert.assertTrue(propertyDay.isAvailable());
    	Assert.assertFalse(propertyDay.isReserved());
    }
    
    @Test
    public void availableOneMatchBookingNotReservedTrumpsWeekDay()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	AvailabilityWeekDay av = Generator.availabilityWeekDay();
    	avList.add(av);
    	Booking booking = Generator.booking();
    	booking.setReservedBool(false);
    	avList.add(booking);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.set(Calendar.DAY_OF_WEEK, av.getDayOfWeek());
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertFalse(result);
    	Assert.assertFalse(propertyDay.isAvailable());
    	Assert.assertFalse(propertyDay.isReserved());
    }
    
    @Test
    public void availableOneMatchBookingNotReservedTrumpsDayOnly()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	av.setDayOnlyBool(true);
    	avList.add(av);
    	Booking booking = Generator.booking();
    	booking.setReservedBool(false);
    	avList.add(booking);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	//testDate.add(Calendar.DAY_OF_YEAR, 5);
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertFalse(result);
    	Assert.assertFalse(propertyDay.isAvailable());
    	Assert.assertFalse(propertyDay.isReserved());
    }
    
    @Test
    public void testNotAvailableOnLastDayOfAvailability()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	Calendar startDate = Calendar.getInstance();
    	DateUtil.clearTime(startDate);
    	av.setStartDate(startDate.getTime());
    	Calendar endDate = Calendar.getInstance();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,5);
    	av.setEndDate(endDate.getTime());
    	avList.add(av);
    	Calendar testDate = Calendar.getInstance();
    	DateUtil.clearTime(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertFalse(result);
    }
    
    @Test
    public void testAvailableForOneDay()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	av.setDayOnlyBool(true);
    	Calendar startDate = Calendar.getInstance();
    	DateUtil.clearTime(startDate);
    	av.setStartDate(startDate.getTime());
    	Calendar endDate = Calendar.getInstance();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,1);
    	av.setEndDate(endDate.getTime());
    	avList.add(av);
    	Calendar testDate = Calendar.getInstance();
    	testDate.setTime(startDate.getTime());
    	boolean result = availabilityService.available(avList, testDate);
    	Assert.assertTrue(result);
    }
    
    @Test
    public void getBookingsSkipCurrentUser()
    {
    	setUp();
    	
    	Booking booking = Generator.booking();
    	booking.setProperty(property);
    	booking.setReservedBool(true);
    	booking.setUser(property.getOwner());
    	user = booking.getUser();
    	booking.setStartDate(av.getStartDate());
    	booking.setEndDate(av.getEndDate());
    	dao.persist(booking);
    	
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
    	List<Matchable> results = availabilityService.
    	    getBookingsSkipCurrentUser(property, period,user);
    	Assert.assertEquals(results.size(),0);
    	
    	dao.deleteAll(Booking.class.getName());  
    	tearDown();
    }
    
    @Test
    public void getBookingsSkipCurrentUserReturnsIfUserDifferent()
    {
    	setUp();
    	
    	OkapiedUserDetails user2 = Generator.okapiedUserDetails();
    	user2.setUsername("User2");
    	user2.setAuthorities(user.retrieveAuthorities());
    	dao.persist(user2);
    	
    	Booking booking = Generator.booking();
    	booking.setProperty(property);
    	booking.setReservedBool(true);
    	booking.setUser(user2);
    	booking.setStartDate(av.getStartDate());
    	booking.setEndDate(av.getEndDate());
    	dao.persist(booking);
    	
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
    	List<Matchable> results = availabilityService.
    	    getBookingsSkipCurrentUser(property, period,user);
    	Assert.assertEquals(results.size(),1);
    	
    	dao.deleteAll(Booking.class.getName());
    	accountService.remove(user2);
    	tearDown();
    }
    
    @Test
    public void getSkipCurrentUser()
    {
    	setUp();
    	
    	Booking booking = Generator.booking();
    	booking.setProperty(property);
    	booking.setReservedBool(true);
    	booking.setUser(property.getOwner());
    	user = booking.getUser();
    	booking.setStartDate(av.getStartDate());
    	booking.setEndDate(av.getEndDate());
    	dao.persist(booking);
    	
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
    	List<Matchable> results = availabilityService.
    	    getSkipCurrentUser(property, period,user);
    	Assert.assertEquals(results.size(),1);
    	
    	dao.deleteAll(Booking.class.getName());  
    	tearDown();
    }
    
    @Test
    public void isWithinValidPeriod()
    {
    	Property p = Generator.property();
    	p.updateTz("GMT");
    	Calendar now = Calendar.getInstance();
    	Generator.clear(now);
    	PropertyDay result = new PropertyDay();
    	Assert.assertTrue(availabilityService.isWithinValidPeriod(now,p,result));
    }
    
    @Test
    public void isWithinValidPeriodYesterdayNotWithin()
    {
    	Property p = Generator.property();
    	p.updateTz("GMT");
    	Calendar now = Calendar.getInstance();
    	Generator.clear(now);
    	now.add(Calendar.DAY_OF_YEAR,-1);
    	PropertyDay result = new PropertyDay();
    	Assert.assertFalse(availabilityService.isWithinValidPeriod(now,p,result));
    }
    
    @Test
    public void isWithinValidPeriod6MonthsNotWithin()
    {
    	Property p = Generator.property();
    	p.updateTz("GMT");
    	Calendar now = Calendar.getInstance();
    	Generator.clear(now);
    	now.add(Calendar.DAY_OF_YEAR,Configuration.instance().getIntProperty("MAX_FORWARD_BOOKING_DAYS"));
    	PropertyDay result = new PropertyDay();
    	Assert.assertFalse(availabilityService.isWithinValidPeriod(now,p,result));
    }
    
    @Test
    public void availableOneMatchBookingNotReservedDetailed()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	avList.add(av);
    	Booking booking = Generator.booking();
    	booking.setId(45);
    	booking.setReservedBool(false);
    	avList.add(booking);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay, true);
    	Assert.assertFalse(result);
    	Assert.assertFalse(propertyDay.isAvailable());
    	Assert.assertFalse(propertyDay.isReserved());
    	Assert.assertEquals(propertyDay.getBookingId(),booking.getId());
    }
    
    @Test
    public void availableOneMatchBookingReservedDetailed()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	avList.add(av);
    	Booking booking = Generator.booking();
    	booking.setId(45);
    	booking.setReservedBool(true);
    	avList.add(booking);
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay, true);
    	Assert.assertFalse(result);
    	Assert.assertFalse(propertyDay.isAvailable());
    	Assert.assertTrue(propertyDay.isReserved());
    	Assert.assertEquals(propertyDay.getBookingId(),booking.getId());
    }
    
    @Test
    public void getAvailabilitiesDayOnly()
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(true);
    	dao.update(av);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property, start);
		Assert.assertEquals(result.size(),1);
		Assert.assertEquals(result.get(0).getId(),av.getId());
    	
    	tearDown();
    }
    
    @Test
    public void getAvailabilitiesDayOnlyNoPrice()
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(true);
    	dao.update(av);
    	
    	Price price = Generator.price();
    	price.setProperty(av.getProperty());
    	price.setStartDate(av.getStartDate());
    	price.setEndDate(av.getEndDate());
    	price.setDayOnlyBool(true);
    	dao.persist(price);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property, start);
		Assert.assertEquals(result.size(),1);
		Assert.assertEquals(result.get(0).getId(),av.getId());
    	
		dao.delete(Price.class.getName(),"id",price.getId());
    	tearDown();
    }
    
    @Test
    public void getAvailabilitiesDayOnlyWrongDay()
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	Calendar wrongStart = property.retrieveCurrentDay();;
    	wrongStart.add(Calendar.DAY_OF_YEAR,1);
    	av.setStartDate(wrongStart.getTime());
    	av.setDayOnlyBool(true);
    	dao.update(av);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property, start);
		Assert.assertEquals(result.size(),0);
    	
    	tearDown();
    }
    
    @Test
    public void getAvailabilitiesDayOnlyNotDayOnly()
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(false);
    	dao.update(av);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property, start);
		Assert.assertEquals(result.size(),0);
    	
    	tearDown();
    }
    
    @Test
    public void makeAvailableDayOnlyAlreadyExisting() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(true);
    	av.setAvailableBool(false);
    	dao.update(av);
    	
    	availabilityService.makeAvailableDayOnly(property, start, false, property.getOwner().getId());
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property, start);
		Assert.assertEquals(result.size(),1);
		Assert.assertEquals(result.get(0).getId(),av.getId());
		Assert.assertEquals(result.get(0).getAvailableBool(),false);
    	
    	tearDown();
    }
    
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
    public void makeAvailableDayOnlyBooked() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	try
    	{
    	    availabilityService.makeAvailableDayOnly(property, startDate, false, property.getOwner().getId());
    	    Assert.fail("Was able to make a property unavailable when it was booked");
    	}
    	catch( AlreadyBookedException e ) {}
    	
    	tearDownBooking();
    }
    
    @Test
    public void makeAvailableDayOnlyNoExisting() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(false);
    	av.setAvailableBool(true);
    	dao.update(av);
    	
    	availabilityService.makeAvailableDayOnly(property, start, false, property.getOwner().getId());
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property, start);
		Assert.assertEquals(result.size(),1);
		Assert.assertNotSame(result.get(0).getId(),av.getId());
		Assert.assertEquals(result.get(0).getAvailableBool(),false);
    	
		dao.delete(Availability.class.getName(), "id", result.get(0).getId());
    	tearDown();
    }
    
    @Test
    public void makeAvailableDayOnlyWrongUser() throws AlreadyBookedException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	
    	try
    	{
    	    availabilityService.makeAvailableDayOnly(property, start, false, -999);
    	    Assert.fail();
    	}
    	catch( OkaSecurityException e) {}
    	
    	tearDown();
    }
    
    @Test
    public void makeAvailableDayOnlyAlreadyExistingPrice() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpPrice();

    	Calendar start = property.retrieveCurrentDay();
    	price.setStartDate(start.getTime());
    	price.setDayOnlyBool(true);
    	price.setAvailableBool(false);
    	dao.update(price);
    	
    	Float changedPrice = 4444f;
    	
    	availabilityService.makeAvailableDayOnly(property, start, false, property.getOwner().getId(),changedPrice);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property, start,changedPrice);
		Assert.assertEquals(result.size(),1);
		Assert.assertEquals(result.get(0).getId(),price.getId());
		Assert.assertEquals(result.get(0).getAvailableBool(),false);
		Assert.assertEquals(((Price)result.get(0)).getPrice(),changedPrice);
    	
    	removePrice();
    }
    
    @Test
    public void makeAvailableDayOnlyNoExistingPrice() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpPrice();

    	Calendar start = property.retrieveCurrentDay();
    	price.setStartDate(start.getTime());
    	price.setDayOnlyBool(false);
    	price.setAvailableBool(true);
    	dao.update(price);
    	
    	Float newPrice = 3434f;
    	
    	availabilityService.makeAvailableDayOnly(property, start, false, property.getOwner().getId(),newPrice);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property, start, newPrice);
		Assert.assertEquals(result.size(),1);
		Assert.assertNotSame(result.get(0).getId(),price.getId());
		Assert.assertEquals(result.get(0).getAvailableBool(),false);
		Assert.assertEquals(((Price)result.get(0)).getPrice(),newPrice);
    	
		dao.delete(Availability.class.getName(), "id", result.get(0).getId());
    	tearDownPrice();
    }
    
    @Test
    public void isWithinValidPeriod6MonthsWithinDetailed()
    {
    	Property p = Generator.property();
    	p.updateTz("GMT");
    	Calendar now = Calendar.getInstance();
    	Generator.clear(now);
    	now.add(Calendar.DAY_OF_YEAR,Configuration.instance().getIntProperty("MAX_FORWARD_BOOKING_DAYS"));
    	PropertyDay result = new PropertyDay();
    	Assert.assertTrue(availabilityService.isWithinValidPeriod(now,p,result,true));
    }
    
    @Test
    public void makeAvailablePeriodWrongUser() throws AlreadyBookedException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	
    	try
    	{
    	    availabilityService.makeAvailablePeriod(property, start, start,-999, null);
    	    Assert.fail();
    	}
    	catch( OkaSecurityException e) {}
    	
    	tearDown();
    }
    
    @Test
    public void makeAvailablePeriodBooked() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	try
    	{
    	    availabilityService.makeAvailablePeriod(property, startDate, endDate,
    	        property.getOwner().getId(),null);
    	    Assert.fail("Was able to make a property unavailable when it was booked");
    	}
    	catch( AlreadyBookedException e ) {}
    	
    	tearDownBooking();
    }
    
    @Test
    public void getAvailabilitiesPeriod()
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,10);
    	av.setStartDate(start.getTime());
    	av.setEndDate(end.getTime());
    	av.setDayOnlyBool(false);
    	dao.update(av);
    	
		List<Availability> result = availabilityService.getAvailabilitiesPeriod(property,start,end,null);
		Assert.assertEquals(result.size(),1);
		Assert.assertEquals(result.get(0).getId(),av.getId());
    	
    	tearDown();
    }
	
    @Test
    public void getAvailabilitiesPeriodNoPrice()
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,10);
    	av.setStartDate(start.getTime());
    	av.setEndDate(end.getTime());
    	av.setDayOnlyBool(false);
    	dao.update(av);
    	
    	Price price = Generator.price();
    	price.setProperty(av.getProperty());
    	price.setStartDate(av.getStartDate());
    	price.setEndDate(av.getEndDate());
    	price.setDayOnlyBool(false);
    	dao.persist(price);
    	
		List<Availability> result = availabilityService.getAvailabilitiesPeriod(property,start,end,null);
		Assert.assertEquals(result.size(),1);
		Assert.assertEquals(result.get(0).getId(),av.getId());
    	
		dao.delete(Price.class.getName(),"id", price.getId());
    	tearDown();
    }
    
    @Test
    public void makeAvailablePeriodAlreadyExisting() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(false);
    	av.setAvailableBool(false);
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,10);
    	av.setEndDate(end.getTime());
    	dao.update(av);
    	
    	availabilityService.makeAvailablePeriod(property, start, end, property.getOwner().getId(), null);
    	
		List<Availability> result = availabilityService.getAvailabilitiesPeriod(property,start,end,null);
		Assert.assertEquals(result.size(),1);
		Assert.assertEquals(result.get(0).getId(),av.getId());
		Assert.assertEquals(result.get(0).getAvailableBool(),true);
    	
    	tearDown();
    }
    
    @Test
    public void makeAvailablePeriodAlreadyExistingPrice() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpPriceAndAvailability();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,10);
    	av.setEndDate(end.getTime());
    	av.setDayOnlyBool(false);
    	av.setAvailableBool(false);
    	dao.update(av);
    	
    	price.setStartDate(start.getTime());
    	price.setEndDate(end.getTime());
    	dao.update(price);
    	
    	Float changedPrice = 4444f;
    	
    	availabilityService.makeAvailablePeriod(property,start,end,property.getOwner().getId(),changedPrice);
    	
		List<Availability> result = availabilityService.getAvailabilitiesPeriod(property,start,end,changedPrice);
		Assert.assertEquals(result.size(),1);
		Assert.assertEquals(result.get(0).getId(),price.getId());
		Assert.assertEquals(result.get(0).getAvailableBool(),false);
		Assert.assertEquals(((Price)result.get(0)).getPrice(),changedPrice);
    
		tearDownPriceAndAvailability();
    }
    
    @Test
    public void makeAvailablePeriodNoExisting() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(true);
    	av.setAvailableBool(true);
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,10);
    	av.setEndDate(end.getTime());
    	dao.update(av);
    	
    	availabilityService.makeAvailablePeriod(property, start, end, property.getOwner().getId(), null);
    	
		List<Availability> result = availabilityService.getAvailabilitiesPeriod(property, start, end, null);
		Assert.assertEquals(result.size(),1);
		Assert.assertNotSame(result.get(0).getId(),av.getId());
		Assert.assertEquals(result.get(0).getAvailableBool(),false);
    	
		dao.delete(Availability.class.getName(), "id", result.get(0).getId());
    	tearDown();
    }
    
    @Test
    public void makeAvailablePeriodNoExistingPrice() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpPrice();

    	Calendar start = property.retrieveCurrentDay();
    	price.setStartDate(start.getTime());
    	price.setDayOnlyBool(true);
    	price.setAvailableBool(true);
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,10);
    	price.setEndDate(end.getTime());
    	dao.update(price);
    	
    	Float newPrice = 12312f;
    	
    	availabilityService.makeAvailablePeriod(property, start, end, property.getOwner().getId(),newPrice);
    	
		List<Availability> result = availabilityService.getAvailabilitiesPeriod(property, start, end, newPrice);
		Assert.assertEquals(result.size(),1);
		Assert.assertNotSame(result.get(0).getId(),price.getId());
		Assert.assertEquals(result.get(0).getAvailableBool(),true);
		Assert.assertEquals(((Price)result.get(0)).getPrice(),newPrice);
    	
		dao.delete(Price.class.getName(), "id", result.get(0).getId());
    	tearDownPrice();
    }
    
    @Test
    public void deleteAllDayOnlyMatchingStart() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(true);
    	av.setAvailableBool(false);
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,1);
    	av.setEndDate(end.getTime());
    	dao.update(av);
    	
    	end.add(Calendar.DAY_OF_YEAR, 9);
    	
    	availabilityService.deleteAllDayOnly(property, start, end, null);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property,start,null);
		Assert.assertEquals(result.size(),0);
    	
    	removeProperty();
    }
    
    @Test
    public void deleteAllDayOnlyOutOfPeriod() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(true);
    	av.setAvailableBool(false);
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,1);
    	av.setEndDate(end.getTime());
    	dao.update(av);
    	
    	Calendar testStart = property.retrieveCurrentDay();
    	testStart.add(Calendar.DAY_OF_YEAR,1);
    	
    	availabilityService.deleteAllDayOnly(property, testStart, end, null);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property,start,null);
		Assert.assertEquals(result.size(),1);
    	
    	tearDown();
    }
    
    @Test
    public void deleteAllDayOnlyOutOfPeriodEndToFar() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(true);
    	av.setAvailableBool(false);
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,10);
    	av.setEndDate(end.getTime());
    	dao.update(av);
    	
    	Calendar testEnd = property.retrieveCurrentDay();
    	testEnd.add(Calendar.HOUR, -1);
    	
    	availabilityService.deleteAllDayOnly(property, start, testEnd, null);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property,start,null);
		Assert.assertEquals(result.size(),1);
    	
    	tearDown();
    }
    
    @Test
    public void deleteAllDayOnlyMatchingStartNoPriceDeleted() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	Calendar start = property.retrieveCurrentDay();
    	av.setStartDate(start.getTime());
    	av.setDayOnlyBool(true);
    	av.setAvailableBool(false);
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR,1);
    	av.setEndDate(end.getTime());
    	dao.update(av);
    	
    	price = Generator.price();
    	price.setProperty(av.getProperty());
    	price.setStartDate(av.getStartDate());
    	price.setEndDate(av.getEndDate());
    	dao.persist(price);
    	
    	end.add(Calendar.DAY_OF_YEAR, 9);
    	
    	availabilityService.deleteAllDayOnly(property, start, end, null);
    	
		List<Availability> result = availabilityService.getAvailabilitiesDayOnly(property,start,null);
		Assert.assertEquals(result.size(),0);
		
		List<Price> prices = dao.getAll(Price.class.getName());
		Assert.assertEquals(prices.size(),1);
    	
		removePrice();
    }
    
    @Test
    public void testMonthYearToPeriod()
    {
    	Property property = Generator.property();
    	property.getLocation().setTz("GMT");
    	Calendar[] period = availabilityService.monthYearToPeriod(property,3,2011);
        Assert.assertEquals(period[0].get(Calendar.HOUR_OF_DAY),period[1].get(Calendar.HOUR_OF_DAY));
    }
    
    @Test
    public void getMatchingStartNoPrice()
    {
    	setUp();
    	
    	Price price = Generator.price();
    	price.setProperty(av.getProperty());
    	dao.persist(price);
    	
    	int displayDays = 14;

    	Calendar start = Calendar.getInstance();
    	Generator.clear(start);
		List<Calendar> period = new ArrayList<Calendar>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(start.getTime());
			period.add(newCalendar);
			start.add(Calendar.DAY_OF_YEAR, 1);
		}
		
    	List<Matchable> results = availabilityService.get(property, period);
    	Assert.assertEquals(results.size(),1);
    	
    	dao.delete(Price.class.getName(),"id", price.getId());
    	tearDown();
    }
    
    @Test
    public void deleteAllNoPrice() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();
    	
    	price = Generator.price();
    	price.setProperty(av.getProperty());
    	dao.persist(price);
    	
    	availabilityService.deleteAll(property, false);
    	
		List<Availability> result = dao.getAll(Availability.class.getName());
		Assert.assertEquals(result.size(),1);
		Assert.assertTrue(result.get(0) instanceof Price);
    	
		removePrice();
    }
    
    @Test
    public void deleteAllPrice() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();
    	
    	Price price = Generator.price();
    	price.setProperty(av.getProperty());
    	dao.persist(price);
    	
    	availabilityService.deleteAll(property, true);
    	
		List<Availability> result = dao.getAll(Availability.class.getName());
		Assert.assertEquals(result.size(),1);
		Assert.assertTrue(result.get(0) instanceof Availability);
		Assert.assertFalse(result.get(0) instanceof Price);
    	
    	tearDown();
    }
    
    @Test
    public void makeAvailableWeekDayWrongUser() throws AlreadyBookedException
    {
    	setUp();
    	
    	try
    	{ 
    		availabilityService.makeAvailableWeekDay(property,0,-999,null);
    		Assert.fail("Wrong user");
    	}
    	catch( OkaSecurityException e ) {}
    	
    	tearDown();
    }
    
    @Test
    public void makeAvailableWeekDayBooking() throws OkaSecurityException
    {
    	setUpBooking();
    	Calendar startDate = property.retrieveCurrentDay();
    	Calendar endDate = property.retrieveCurrentDay();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,10);
    	booking.setStartDate(startDate.getTime());
    	booking.setEndDate(endDate.getTime());
    	dao.update(booking);
    	
    	try
    	{ 
    		availabilityService.makeAvailableWeekDay(property,0,property.getOwner().getId(),null);
    		Assert.fail("Active Booking");
    	}
    	catch( AlreadyBookedException e ) {}
    	
    	tearDownBooking();
    }
    
    public void setUpWD()
    {
    	avWd = Generator.availabilityWeekDay();
    	saveAvailabilityWD();	
    }
    
	public void tearDownWD()
	{
    	removeAvailabilityWD();
	}
    
    @Test
    public void getAvailabilitiesWeekDay()
    {
    	setUpWD();

        List<AvailabilityWeekDay> results = availabilityService.getAvailabilitiesWeekDay(
            property,avWd.getDayOfWeek(),null);
        Assert.assertEquals(results.size(),1);
    	
    	tearDownWD();
    }
    
    @Test
    public void getAvailabilitiesWeekDayWithPrice()
    {
    	setUp();
    	
    	PriceWeekDay price = Generator.priceWeekDay();
    	price.setProperty(av.getProperty());
    	dao.persist(price);

        List<AvailabilityWeekDay> results = availabilityService.getAvailabilitiesWeekDay(
            property,price.getDayOfWeek(),price.getPrice());
        Assert.assertEquals(results.size(),1);
    	
        dao.delete(PriceWeekDay.class.getName(),"id", price.getId());
    	tearDown();
    }
    
    @Test
    public void makeAvailableWeekDayAlreadyExisting() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpWD();
    	avWd.setAvailableBool(false);
    	dao.update(avWd);

        availabilityService.makeAvailableWeekDay(
            property,avWd.getDayOfWeek(),property.getOwner().getId(),null);
        
        List<AvailabilityWeekDay> avs = availabilityService.getAvailabilitiesWeekDay(property, 
            avWd.getDayOfWeek(), null);
        Assert.assertEquals(avs.size(),1);
        Assert.assertTrue(avs.get(0).getAvailableBool());
    	
    	tearDownWD();
    }
    
    @Test
    public void makeAvailableWeekDayAlreadyExistingPrice() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpWD();

    	PriceWeekDay price = Generator.priceWeekDay();
    	price.setProperty(avWd.getProperty());
    	price.setDayOfWeek(avWd.getDayOfWeek());
    	dao.persist(price);
    	
    	Float newPrice = 9383f;

        availabilityService.makeAvailableWeekDay(
            property,avWd.getDayOfWeek(),property.getOwner().getId(),newPrice);
        
        List<AvailabilityWeekDay> avs = availabilityService.getAvailabilitiesWeekDay(property, 
            avWd.getDayOfWeek(),newPrice);
        Assert.assertEquals(avs.size(),1);
        Assert.assertEquals(((PriceWeekDay)avs.get(0)).getPrice(),newPrice);
    	
        dao.delete(PriceWeekDay.class.getName(),"id",price.getId());
    	tearDownWD();
    }
    
    @Test
    public void makeAvailableWeekDayNoExsiting() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	int dayOfWeek = 4;
    	
        availabilityService.makeAvailableWeekDay(
            property,dayOfWeek,property.getOwner().getId(),null);
        
        List<AvailabilityWeekDay> avs = availabilityService.getAvailabilitiesWeekDay(property, 
            dayOfWeek, null);
        Assert.assertEquals(avs.size(),1);
        Assert.assertFalse(avs.get(0).getAvailableBool());
    	
        dao.delete(AvailabilityWeekDay.class.getName(),"id",avs.get(0).getId());
    	tearDown();
    }
    
    @Test
    public void makeAvailableWeekDayNoExsitingPrice() throws AlreadyBookedException, OkaSecurityException
    {
    	setUp();

    	int dayOfWeek = 4;
    	Float newPrice = 32f;
    	
        availabilityService.makeAvailableWeekDay(
            property,dayOfWeek,property.getOwner().getId(),newPrice);
        
        List<AvailabilityWeekDay> avs = availabilityService.getAvailabilitiesWeekDay(property, 
            dayOfWeek,newPrice);
        Assert.assertEquals(avs.size(),1);
        Assert.assertEquals(((PriceWeekDay)avs.get(0)).getPrice(),newPrice);
    	
        dao.delete(PriceWeekDay.class.getName(),"id",avs.get(0).getId());
    	tearDown();
    }
    
    @Test
    public void deleteAllNoPriceWeekDay() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpWD();
    	
    	priceWd = Generator.priceWeekDay();
    	priceWd.setProperty(avWd.getProperty());
    	dao.persist(priceWd);
    	
    	availabilityService.deleteAllWeekDay(property, false);
    	
		List<AvailabilityWeekDay> result = dao.getAll(AvailabilityWeekDay.class.getName());
		Assert.assertEquals(result.size(),1);
		Assert.assertTrue(result.get(0) instanceof PriceWeekDay);
		
		removePriceWD();
    }
    
    @Test
    public void deleteAllWDPrice() throws AlreadyBookedException, OkaSecurityException
    {
    	setUpWD();
    	
    	priceWd = Generator.priceWeekDay();
    	priceWd.setProperty(property);
    	dao.persist(priceWd);
    	
    	availabilityService.deleteAllWeekDay(property, true);
    	
		List<AvailabilityWeekDay> result = dao.getAll(AvailabilityWeekDay.class.getName());
		Assert.assertEquals(result.size(),1);
		Assert.assertTrue(result.get(0) instanceof AvailabilityWeekDay);
		Assert.assertFalse(result.get(0) instanceof PriceWeekDay);
    	
    	tearDownWD();
    }
    
    @Test
    public void testGetAvailabiltyWDNoPrice()
    {
    	setUpWD();
    	
    	priceWd = Generator.priceWeekDay();
    	priceWd.setProperty(avWd.getProperty());
    	dao.persist(priceWd);
    	
    	List<Matchable> results = availabilityService.getAvailabilityWeekDay(property);
    	Assert.assertEquals(results.size(),1);
    	
    	dao.delete(PriceWeekDay.class.getName(),"id",priceWd.getId());
    	tearDownWD();
    }
    
    @Test
    public void availableBookingFirstDay()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	avList.add(av);
    	booking = Generator.booking();
    	booking.setReservedBool(false);
    	saveBooking();
    	avList.add(booking);
    	Calendar testDate = booking.getProperty().retrieveCurrentDay();
    	testDate.setTime(booking.getStartDate());
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertFalse(result);
    	Assert.assertFalse(propertyDay.isAvailable());
    	Assert.assertFalse(propertyDay.isReserved());
    	Assert.assertTrue(propertyDay.getFirstDayOfBooking());
    	
    	removeBooking();
    }
    
    @Test
    public void availableBookingNotFirstDay()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	avList.add(av);
    	booking = Generator.booking();
    	booking.setReservedBool(false);
    	saveBooking();
    	avList.add(booking);
    	Calendar testDate = booking.getProperty().retrieveCurrentDay();
    	testDate.setTime(booking.getStartDate());
    	testDate.add(Calendar.DAY_OF_YEAR,1);
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertFalse(result);
    	Assert.assertFalse(propertyDay.isAvailable());
    	Assert.assertFalse(propertyDay.isReserved());
    	Assert.assertFalse(propertyDay.getFirstDayOfBooking());
    	
    	removeBooking();
    }
    
    @Test
    public void availableBookingLastDay()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	Calendar endDate = av.getProperty().retrieveCurrentDay();
    	endDate.setTime(av.getEndDate());
    	endDate.add(Calendar.DAY_OF_YEAR,1);
    	av.setEndDate(endDate.getTime());
    	avList.add(av);
    	booking = Generator.booking();
    	booking.setReservedBool(false);
    	saveBooking();
    	avList.add(booking);
    	Calendar testDate = booking.getProperty().retrieveCurrentDay();
    	testDate.setTime(booking.getEndDate());
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertTrue(result);
    	Assert.assertTrue(propertyDay.isAvailable());
    	Assert.assertFalse(propertyDay.isReserved());
    	Assert.assertTrue(propertyDay.getLastDayOfBooking());
    	
    	removeBooking();
    }
    
    @Test
    public void availableBookingNotLastDay()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	Availability av = Generator.availability();
    	Calendar endDate = av.getProperty().retrieveCurrentDay();
    	endDate.setTime(av.getEndDate());
    	endDate.add(Calendar.DAY_OF_YEAR,1);
    	av.setEndDate(endDate.getTime());
    	avList.add(av);
    	booking = Generator.booking();
    	booking.setReservedBool(false);
    	saveBooking();
    	avList.add(booking);
    	Calendar testDate = booking.getProperty().retrieveCurrentDay();
    	testDate.setTime(booking.getEndDate());
    	testDate.add(Calendar.DAY_OF_YEAR, -1);
    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertFalse(propertyDay.getLastDayOfBooking());
    	
    	removeBooking();
    }
    
    @Test
    public void availableAvMatchNotBooking()
    {
    	List<Matchable> avList = new ArrayList<Matchable>();
    	
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	
    	Availability av = Generator.availability(); // starts on today for 14 days
    	av.setAvailableBool(true);
    	avList.add(av);

    	PropertyDay propertyDay = new PropertyDay();
    	boolean result = availabilityService.available(avList, testDate, propertyDay);
    	Assert.assertTrue(result);
    	Assert.assertFalse(propertyDay.getBookingDay());
    }
}
