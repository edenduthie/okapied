package okapied.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.NoResultException;

import okapied.BaseTest;
import okapied.entity.Availability;
import okapied.entity.Booking;
import okapied.entity.CurrencyCode;
import okapied.entity.Flooring;
import okapied.entity.OkapiedGrantedAuthority;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Price;
import okapied.entity.Property;
import okapied.entity.PropertyDay;
import okapied.entity.PropertyType;
import okapied.exception.AvailabilityException;
import okapied.exception.CancellationException;
import okapied.exception.ExchangeRateNotFoundException;
import okapied.exception.OkaSecurityException;
import okapied.exception.PriceException;
import okapied.exception.InputValidationException;
import okapied.util.Configuration;
import okapied.util.DateUtil;
import okapied.util.Generator;

import org.easymock.EasyMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.testng.Assert;
import org.testng.annotations.Test;

import adaptivepayments.AdaptivePayments;

import com.paypal.svcs.services.PPFaultMessage;
import com.paypal.svcs.types.ap.Receiver;
import com.paypal.svcs.types.ap.RefundInfo;
import com.paypal.svcs.types.ap.RefundInfoList;
import com.paypal.svcs.types.ap.RefundRequest;
import com.paypal.svcs.types.ap.RefundResponse;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

public class BookingServiceTest extends BaseTest
{
    @Autowired
    BookingServiceInterface bookingService;
    
    @Autowired
    PropertyService propertyService;
    
    public void setUp()
    {
    	av = Generator.availability();
    	property = av.getProperty();
    	saveLocations();
    	dao.persist(property.getCurrencyCode());
    	dao.persist(property.getPropertyDetails().getFlooring());
    	dao.persist(property.getPropertyDetails().getType());
    	property.updateTz("Africa/Bujumbura");
    	Collection<GrantedAuthority> auths = property.getOwner().getAuthorities();
    	for( GrantedAuthority auth : auths )
    	{
    		dao.persist((OkapiedGrantedAuthority)auth);
    	}
    	dao.persist(property.getOwner());
    	dao.persist(property);
    	dao.persist(av);
    	
    	price = Generator.price();
    	price.setProperty(property);
    	Calendar startDate = propertyService.getCurrentDay(property);
    	DateUtil.clearTime(startDate);
    	price.setStartDate(startDate.getTime());
    	dao.persist(price);	
    	
    	booking = Generator.booking();
    	property.getOwner().setEmail("eduthi_1298638751_biz@gatorlogic.com");
    	dao.update(property.getOwner());
    	booking.setProperty(property);
    	booking.setReservedBool(true);
    	booking.setTotal(15.01f);
        auths = booking.getUser().getAuthorities();
    	for( GrantedAuthority auth : auths )
    	{
    		dao.persist((OkapiedGrantedAuthority)auth);
    	}
    	booking.getUser().setIp("127.0.0.1");
    	booking.getUser().setEmail("eduthi_1298629553_per@gatorlogic.com");
    	dao.persist(booking.getUser());
    	user = booking.getUser();
    	dao.persist(booking);
    }
    
    public Booking createSecondBooking()
    {
    	Booking booking2 = Generator.booking();
    	booking2.setProperty(property);
    	booking2.setReservedBool(true);
    	booking2.setTotal(15.01f);
    	Collection<GrantedAuthority> auths = booking.getUser().getAuthorities();
        booking2.getUser().getAuthorities();
    	for( GrantedAuthority auth : auths )
    	{
    		dao.persist((OkapiedGrantedAuthority)auth);
    	}
    	booking2.getUser().setIp("127.0.0.1");
    	booking2.getUser().setEmail("eduthi_1298629553_per@gatorlogic.com");
    	dao.persist(booking2.getUser());
    	dao.persist(booking2);
    	return booking2;
    }
    
    public void removeSecondBooking(Booking booking2)
    {
    	dao.deleteAll(Booking.class.getName());
    	accountService.remove(booking2.getUser());
    }
    
    public void tearDown()
    {
    	//dao.deleteAll(Booking.class.getName());   
    	try
    	{
    	    dao.delete(Booking.class.getName(),"id",booking.getId());
    	}
    	catch( NoResultException e ) {}
    	dao.delete(Availability.class.getName(), "id", av.getId());
    	dao.delete(Price.class.getName(), "id", price.getId());
    	dao.delete(Property.class.getName(), "id", property.getId());
    	accountService.remove(booking.getUser());
    	accountService.remove(property.getOwner());
    	dao.deleteAll(OkapiedGrantedAuthority.class.getName());
    	dao.deleteAll(Flooring.class.getName());
    	dao.deleteAll(PropertyType.class.getName());
    	dao.deleteAll(CurrencyCode.class.getName());
    	removeLocations();
    }
    
    @Test
    public void validateDates() throws InputValidationException
    {
    	Calendar startDate = DateUtil.getCurrentCalendarCompareTZ();
    	Calendar endDate = DateUtil.getCurrentCalendarCompareTZ();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,1);
        bookingService.validateDates(startDate, endDate);
    }
    
    
    @Test
    public void validateDatesStartSameAsEnd()
    {
    	Calendar startDate = DateUtil.getCurrentCalendarCompareTZ();
    	Calendar endDate = DateUtil.getCurrentCalendarCompareTZ();
    	endDate.setTime(startDate.getTime());
    	try
    	{
    		bookingService.validateDates(startDate, endDate);
    		Assert.fail();
    	}
    	catch(InputValidationException ve)
    	{
    		//
    	}
    }
    
    @Test
    public void validateDatesStartGTEnd()
    {
    	Calendar startDate = DateUtil.getCurrentCalendarCompareTZ();
    	Calendar endDate = DateUtil.getCurrentCalendarCompareTZ();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.HOUR, -1);
    	try
    	{
    		bookingService.validateDates(startDate, endDate);
    		Assert.fail();
    	}
    	catch(InputValidationException ve)
    	{
    		//
    	}
    }
    
    @Test
    public void validateDatesTooLong()
    {
    	Calendar startDate = DateUtil.getCurrentCalendarCompareTZ();
    	Calendar endDate = DateUtil.getCurrentCalendarCompareTZ();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, Configuration.instance().getIntProperty("MAXIMUM_BOOKING_INTERVAL_DAYS")+1);
    	try
    	{
    		bookingService.validateDates(startDate, endDate);
    		Assert.fail();
    	}
    	catch(InputValidationException ve)
    	{
    		//
    	}
    }
    
    @Test
    public void validateDatesTooFarInFuture()
    {
    	Calendar startDate = DateUtil.getCurrentCalendarCompareTZ();
    	startDate.add(Calendar.DAY_OF_YEAR,Configuration.instance().getIntProperty("MAX_FORWARD_BOOKING_DAYS"));
    	Calendar endDate = DateUtil.getCurrentCalendarCompareTZ();
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR,1);
    	try
    	{
    		bookingService.validateDates(startDate, endDate);
    		Assert.fail();
    	}
    	catch(InputValidationException ve)
    	{
    	}
    }
    
    @Test
    public void dropExistingReservations()
    {
    	setUp();
    	
    	bookingService.dropExistingReservations(property, booking.getUser());
    	List<Booking> results = bookingService.getBookingsForUser(booking.getUser());
    	Assert.assertEquals(results.size(),0);
    	
        tearDown();
    }
    
    @Test
    public void dropExistingReservationsNotReservation()
    {
    	setUp();
    	booking.setReservedBool(false);
    	dao.update(booking);
    	
    	bookingService.dropExistingReservations(property, booking.getUser());
    	List<Booking> results = bookingService.getBookingsForUser(booking.getUser());
    	Assert.assertEquals(results.size(),1);
    	
        tearDown();
    }
    
    @Test
    public void dropExpiredReservationsExpiredReserved()
    {
    	setUp();
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.HOUR, -1); // only expire by 1 hour to check timezone issues
    	booking.setExpirationDate(now.getTime());
    	booking.setReservedBool(true);
    	dao.update(booking);
    	
    	bookingService.dropExpiredReservations(booking.getUser());
    	List<Booking> results = bookingService.getBookingsForUser(booking.getUser());
    	Assert.assertEquals(results.size(),0);
    	
        tearDown();
    }
    
    @Test
    public void dropExpiredReservationsExpiredNotReserved()
    {
    	setUp();
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.HOUR, -1); // only expire by 1 hour to check timezone issues
    	booking.setExpirationDate(now.getTime());
    	booking.setReservedBool(false);
    	dao.update(booking);
    	
    	bookingService.dropExpiredReservations(booking.getUser());
    	List<Booking> results = bookingService.getBookingsForUser(booking.getUser());
    	Assert.assertEquals(results.size(),1);
    	
        tearDown();
    }
    
    @Test
    public void dropExpiredReservationsExpiredReservedWrongUser()
    {
    	setUp();
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.HOUR, -1); // only expire by 1 hour to check timezone issues
    	booking.setExpirationDate(now.getTime());
    	booking.setReservedBool(true);
    	dao.update(booking);
    	OkapiedUserDetails wrongUser = new OkapiedUserDetails();
    	wrongUser.setId(-111);
    	
    	bookingService.dropExpiredReservations(wrongUser);
    	List<Booking> results = bookingService.getBookingsForUser(booking.getUser());
    	Assert.assertEquals(results.size(),1);
    	
        tearDown();
    }
    
    @Test
    public void dropExistingReservationsWithInfoList()
    {
    	setUp();
    	
    	List<PropertyDay> infoList = new ArrayList<PropertyDay>();
    	for( int i=0; i < 10; ++i )
    	{
    		PropertyDay day = new PropertyDay();
    		day.setPrice(10.0f);
    		dao.persist(day);
    		infoList.add(day);
    	}
    	booking.setInfoList(infoList);
    	dao.update(booking);
    	
    	bookingService.dropExistingReservations(property, booking.getUser());
    	List<Booking> results = bookingService.getBookingsForUser(booking.getUser());
    	Assert.assertEquals(results.size(),0);
    	
        tearDown();
    }
    
    @Test
    public void calculateTotal() throws PriceException
    {
    	setUp();
    	
    	Calendar startDate = propertyService.getCurrentDay(property);
    	Calendar endDate = propertyService.getCurrentDay(property);
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, Generator.DAYS);
    	List<Calendar> period = DateUtil.getPeriodExcludingEnd(startDate, endDate);
    	Assert.assertEquals(period.size(),Generator.DAYS);
    	
    	Float total = bookingService.calculateTotal(property, period);
    	Float expected = price.getPrice()*Generator.DAYS;
    	Assert.assertEquals(total,expected);
    	
    	tearDown();
    }
    
    @Test
    public void makeReservation() throws AvailabilityException, InputValidationException, PriceException
    {
    	setUp();
    	
    	Calendar startDate = propertyService.getCurrentDay(property);
    	Calendar endDate = propertyService.getCurrentDay(property);
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, Generator.DAYS);
    	
    	av.setStartDate(startDate.getTime());
    	// we set the availability end date to one day before the booking period end
    	// date as the last day of the booking period need not be available, we would
    	// book the night
    	av.setEndDate(endDate.getTime());
    	dao.update(av);
    	
    	Booking newBooking = 
    		bookingService.makeReservation(property, user, startDate, endDate, 2);
    	Assert.assertFalse(newBooking.expired());
    	Assert.assertTrue(newBooking.getReservedBool());
    	Assert.assertEquals(newBooking.getTotal(),1400.0f);
    	Assert.assertEquals(newBooking.getBookingStatus(),Booking.BOOKING_STATUS_RESERVATION);
    	
    	List<Booking> bookings = bookingService.getBookingsForUser(user);
    	Assert.assertEquals(bookings.size(),2);
    	
    	dao.delete(Booking.class.getName(),"id",newBooking.getId());
    	tearDown();
    }
    
    @Test
    public void makeReservationInvalidDates() throws AvailabilityException, PriceException
    {
    	setUp();
    	
    	Calendar startDate = propertyService.getCurrentDay(property);
    	Calendar endDate = propertyService.getCurrentDay(property);
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, -Generator.DAYS);
    	
    	av.setStartDate(startDate.getTime());
    	// we set the availability end date to one day before the booking period end
    	// date as the last day of the booking period need not be available, we would
    	// book the night
    	av.setEndDate(endDate.getTime());
    	dao.update(av);
    	
    	try
    	{
    	    Booking newBooking = 
    		    bookingService.makeReservation(property, user, startDate, endDate, 2);
    	    Assert.fail();
    	}
    	catch( InputValidationException e ) {}
    	
    	tearDown();
    }
    
    @Test
    public void makeReservationPropertyNotAvailable() 
        throws AvailabilityException, InputValidationException, PriceException
    {
    	setUp();
    	
    	Calendar startDate = propertyService.getCurrentDay(property);
    	Calendar endDate = propertyService.getCurrentDay(property);
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, Generator.DAYS);
    	
    	av.setStartDate(startDate.getTime());
    	// we set the availability end date to one day before the booking period end
    	// date as the last day of the booking period need not be available, we would
    	// book the night
    	av.setEndDate(endDate.getTime());
    	dao.update(av);
    	
    	//extend the end date so the property will not be available
    	endDate.add(Calendar.DAY_OF_YEAR, 1);
    	
    	try
    	{
    	    Booking newBooking = 
    		    bookingService.makeReservation(property, user, startDate, endDate, 2);
            Assert.fail();
    	}
    	catch( AvailabilityException e)
    	{
    		// good
    	}
    	
    	tearDown();
    }
    
    @Test
    public void calcualteTotalService() throws AvailabilityException, InputValidationException, PriceException
    {
    	setUp();
    	
    	Calendar startDate = propertyService.getCurrentDay(property);
    	Calendar endDate = propertyService.getCurrentDay(property);
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, Generator.DAYS);
    	
    	av.setStartDate(startDate.getTime());
    	av.setEndDate(endDate.getTime());
    	dao.update(av);
    	
    	bookingService.dropExistingReservations(property, user);
    	
    	Float total = bookingService.calculateTotal(property, startDate, endDate);
    	Float expected = price.getPrice()*Generator.DAYS;
    	Assert.assertEquals(total,expected);
    	
    	tearDown();
    }
    
    @Test
    public void makeReservationJustOneDay() throws AvailabilityException, InputValidationException, PriceException
    {
    	setUp();
    	
    	Calendar startDate = propertyService.getCurrentDay(property);
    	Calendar endDate = propertyService.getCurrentDay(property);
    	endDate.setTime(startDate.getTime());
    	endDate.add(Calendar.DAY_OF_YEAR, 1);
    	
    	av.setStartDate(startDate.getTime());
    	av.setEndDate(endDate.getTime());
    	dao.update(av);
    	
    	Booking newBooking = 
    		bookingService.makeReservation(property, user, startDate, endDate, 2);
    	Assert.assertFalse(newBooking.expired());
    	Assert.assertTrue(newBooking.getReservedBool());
    	Assert.assertEquals(newBooking.getTotal(),100.0f);
    	
    	List<Booking> bookings = bookingService.getBookingsForUser(user);
    	Assert.assertEquals(bookings.size(),2);
    	
    	dao.delete(Booking.class.getName(),"id",newBooking.getId());
    	tearDown();
    }
    
    @Test
    public void loadBookingWrongUser() throws FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	setUp();
    	try
    	{
    		bookingService.loadBooking(booking.getId(), -999);
    		Assert.fail();
    	}
    	catch( OkaSecurityException e ) {}
    	tearDown();
    }
    
    @Test
    public void loadBookingCompleted() 
        throws OkaSecurityException, FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	dao.update(booking);
    	Booking result = bookingService.loadBooking(booking.getId(),booking.getUser().getId());
    	Assert.assertNotNull(result);
    	Assert.assertEquals(result.getBookingStatus(),Booking.BOOKING_STATUS_CONFIRMED);
    	tearDown();
    }
    
    @Test
    public void loadBookingPendingButComplete() 
        throws OkaSecurityException, FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	BookingServiceInterface mock = createMock(BookingService.class);
    	expect(mock.retrieveBookingStatus(booking.getId())).andReturn(BookingServiceInterface.STATUS_INCOMPLETE); 
    	bookingService.setListener(mock);
    	replay(mock);
    	Booking result = bookingService.loadBooking(booking.getId(),booking.getUser().getId());
    	Assert.assertNotNull(result);
    	Assert.assertEquals(result.getBookingStatus(),Booking.BOOKING_STATUS_CONFIRMED);
    	verify(mock);
    	tearDown();
    }
    
    @Test
    public void loadBookingPendingButStillPending() 
        throws OkaSecurityException, FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	BookingServiceInterface mock = createMock(BookingService.class);
    	expect(mock.retrieveBookingStatus(booking.getId())).andReturn(BookingServiceInterface.STATUS_CREATED); 
    	bookingService.setListener(mock);
    	replay(mock);
    	Booking result = bookingService.loadBooking(booking.getId(),booking.getUser().getId());
    	Assert.assertNotNull(result);
    	Assert.assertEquals(result.getBookingStatus(),Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	verify(mock);
    	tearDown();
    }
    
    @Test
    public void loadBookingPendingButError() 
        throws OkaSecurityException, FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	BookingServiceInterface mock = createMock(BookingService.class);
    	expect(mock.retrieveBookingStatus(booking.getId())).andReturn(BookingServiceInterface.STATUS_ERROR); 
    	bookingService.setListener(mock);
    	replay(mock);
    	Booking result = bookingService.loadBooking(booking.getId(),booking.getUser().getId());
    	Assert.assertNotNull(result);
    	Assert.assertEquals(result.getBookingStatus(),Booking.BOOKING_STATUS_ERROR);
    	verify(mock);
    	tearDown();
    }
    
    @Test
    public void getBookingsWithStatus()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	
    	List<Booking> results = bookingService.getBookingsWithStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	Assert.assertEquals(results.size(),1);
    	
    	tearDown();
    }
    
    @Test
    public void getBookingsWithStatusWrongStatus()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	
    	List<Booking> results = bookingService.getBookingsWithStatus(Booking.BOOKING_STATUS_CANCELLED);
    	Assert.assertEquals(results.size(),0);
    	
    	tearDown();
    }
    
    @Test
    public void checkAllPendingPayments() throws FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	BookingServiceInterface mock = createMock(BookingService.class);
    	expect(mock.retrieveBookingStatus(booking.getId())).andReturn(BookingServiceInterface.STATUS_INCOMPLETE); 
    	bookingService.setListener(mock);
    	replay(mock);
    	List<Booking> bookings = bookingService.checkAllPendingPayments();
    	Assert.assertEquals(bookings.size(),1);
    	Assert.assertEquals(bookings.get(0).getBookingStatus(),Booking.BOOKING_STATUS_CONFIRMED);
    	Assert.assertFalse(bookings.get(0).getReservedBool());
    	verify(mock);
    	tearDown();
    }
    
    @Test
    public void loadAllConfirmedBookingsUnpaidToPropertyOwners()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setPaymentCompleteBool(false);
    	dao.update(booking);
    	List<Booking> results = bookingService.loadAllConfirmedBookingsUnpaidToPropertyOwners();
    	Assert.assertEquals(results.size(),1);
    	tearDown();
    }
    
    @Test
    public void loadAllConfirmedBookingsUnpaidToPropertyOwnersWrongStatus()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking.setPaymentCompleteBool(false);
    	dao.update(booking);
    	List<Booking> results = bookingService.loadAllConfirmedBookingsUnpaidToPropertyOwners();
    	Assert.assertEquals(results.size(),0);
    	tearDown();
    }
    
    @Test
    public void loadAllConfirmedBookingsUnpaidToPropertyOwnersPaymentComplete()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking.setPaymentCompleteBool(true);
    	dao.update(booking);
    	List<Booking> results = bookingService.loadAllConfirmedBookingsUnpaidToPropertyOwners();
    	Assert.assertEquals(results.size(),0);
    	tearDown();
    }
    
    @Test
    public void isPaymentTimeMinutesBefore()
    {
    	Booking booking = Generator.booking();
    	booking.getProperty().getLocation().setTz("Australia/Melbourne");
    	Calendar startDate = booking.getProperty().retrieveCurrentTime();
    	startDate.add(Calendar.HOUR_OF_DAY, -Configuration.instance().getIntProperty("PAYMENT_DATE_HOUR_OFFSET"));
    	startDate.add(Calendar.MINUTE,2);
    	booking.setStartDate(startDate.getTime());
    	
    	Assert.assertFalse(bookingService.isPaymentTime(booking));
    }
    
    @Test
    public void isPaymentTimeMinutesAfter()
    {
    	Booking booking = Generator.booking();
    	booking.getProperty().getLocation().setTz("Australia/Melbourne");
    	Calendar startDate = booking.getProperty().retrieveCurrentTime();
    	startDate.add(Calendar.HOUR_OF_DAY, -Configuration.instance().getIntProperty("PAYMENT_DATE_HOUR_OFFSET"));
    	startDate.add(Calendar.MINUTE,-2);
    	booking.setStartDate(startDate.getTime());
    	
    	Assert.assertTrue(bookingService.isPaymentTime(booking));
    }
    
    @Test
    public void getAdaptivePayments() throws IOException, FatalException, SSLConnectionException
    {
         AdaptivePayments ap = bookingService.getAdaptivePayments();	
    }
    
    @Test
    public void processBookingStatusReciptIncomplete()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking.setReservedBool(true);
    	dao.update(booking);
    	
    	bookingService.processBookingStatusReciept(booking, BookingServiceInterface.STATUS_INCOMPLETE);
    	Assert.assertEquals(booking.getBookingStatus(),Booking.BOOKING_STATUS_CONFIRMED);
    	Assert.assertFalse(booking.getReservedBool());
    	
    	tearDown();
    }
    
    @Test
    public void processBookingStatusReciptError()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking.setReservedBool(true);
    	dao.update(booking);
    	
    	bookingService.processBookingStatusReciept(booking, BookingServiceInterface.STATUS_ERROR);
    	Assert.assertEquals(booking.getBookingStatus(),Booking.BOOKING_STATUS_ERROR);
    	Assert.assertFalse(booking.getReservedBool());
    	
    	tearDown();
    }
    
    @Test
    public void cancelBookingWrongUser() throws CancellationException
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking.setReservedBool(true);
    	dao.update(booking);
    	
    	try
    	{
    	    bookingService.cancelBooking(booking,-999);
    	    Assert.fail();
    	}
    	catch( CancellationException e ) {}
    	
    	tearDown();
    }
    
    @Test
    public void cancelBookingWrongStatus() throws CancellationException
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setReservedBool(true);
    	dao.update(booking);
    	
    	try
    	{
    	    bookingService.cancelBooking(booking,booking.getUser().getId());
    	    Assert.fail();
    	}
    	catch( CancellationException e ) {}
    	
    	tearDown();
    }
    
    @Test
    public void cancelBooking() throws AddressException, MessagingException, CancellationException
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	bookingService.cancelBooking(booking,booking.getUser().getId());
    	Booking bookingResult = bookingService.get(booking.getId());
    	Assert.assertEquals(bookingResult.getBookingStatus(),Booking.BOOKING_STATUS_CANCELLED);
    	tearDown();
    }
    
    @Test
    public void overlappingBookingsYes()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking2.setReservedBool(true);
    	dao.update(booking2);
    	
    	Assert.assertTrue(bookingService.overlappingBookings(booking));
    	
    	removeSecondBooking(booking2);
    	tearDown();
    }
    
    @Test
    public void overlappingBookingsNoMatchStart()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getEndDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking2.setReservedBool(true);
    	dao.update(booking2);
    	
    	Assert.assertFalse(bookingService.overlappingBookings(booking));
    	
    	removeSecondBooking(booking2);
    	tearDown();
    }
    
    @Test
    public void overlappingBookingsNoMatchEnd()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getStartDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking2.setReservedBool(true);
    	dao.update(booking2);
    	
    	Assert.assertFalse(bookingService.overlappingBookings(booking));
    	
    	removeSecondBooking(booking2);
    	tearDown();
    }
    
    @Test
    public void overlappingBookingsWrongProperty()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking2.setReservedBool(true);
    	dao.update(booking2);
    	
    	Property property2 = Generator.property();
    	property2.setCurrencyCode(property.getCurrencyCode());
    	property2.getPropertyDetails().setFlooring(property.getPropertyDetails().getFlooring());
    	property2.getPropertyDetails().setType(property.getPropertyDetails().getType());
    	property2.setOwner(property.getOwner());
    	property2.setLocation(property.getLocation());
    	dao.persist(property2);
    	booking2.setProperty(property2);
    	dao.update(booking2);
    	
    	Assert.assertFalse(bookingService.overlappingBookings(booking));
    	
    	removeSecondBooking(booking2);
    	dao.delete(Property.class.getName(),"id",property2.getId());
    	tearDown();
    }
    
    @Test
    public void overlappingBookingsWrongCode()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_CANCELLED);
    	booking2.setReservedBool(true);
    	dao.update(booking2);
    	
    	Assert.assertFalse(bookingService.overlappingBookings(booking));
    	
    	removeSecondBooking(booking2);
    	tearDown();
    }
    
    @Test
    public void overlappingBookingsSameUser()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking2.setReservedBool(true);
    	OkapiedUserDetails oldUser = booking2.getUser();
    	booking2.setUser(booking.getUser());
    	dao.update(booking2);
    	
    	Assert.assertFalse(bookingService.overlappingBookings(booking));
    	
    	booking2.setUser(oldUser);
    	removeSecondBooking(booking2);
    	tearDown();
    } 
    
    @Test
    public void overlappingBookingsNotReserved()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getEndDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_RESERVATION);
    	booking2.setReservedBool(false);
    	dao.update(booking2);
    	
    	Assert.assertFalse(bookingService.overlappingBookings(booking));
    	
    	removeSecondBooking(booking2);
    	tearDown();
    }
    
    @Test 
    public void updateCompelteBookingNoOverlapping()
    {
    	setUp();
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking.setReservedBool(true);
    	dao.update(booking);
    	
    	bookingService.updateCompleteBooking(booking);
    	
    	Booking result = bookingService.get(booking.getId());
    	Assert.assertEquals(result.getBookingStatus(),Booking.BOOKING_STATUS_CONFIRMED);
    	Assert.assertEquals(result.getReservedBool(),false);
    	
    	tearDown();
    }
    
    @Test 
    public void updateCompelteBookingOverlappingRefundSuccess() throws FatalException, PPFaultMessage
    {
    	setUp();
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking.setReservedBool(true);
    	// must make the booking expired
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.HOUR_OF_DAY, -1);
    	booking.setExpirationDate(now.getTime());
    	dao.update(booking);
    	
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking2.setReservedBool(true);
    	dao.update(booking2);
    	
    	// create a mock reply to the refund
    	AdaptivePayments mock = createMock(AdaptivePayments.class);
    	RefundResponse response = new RefundResponse();
    	List<RefundInfo> refundInfoList = new ArrayList<RefundInfo>();
    	RefundInfo info = new RefundInfo();
    	Receiver receiver = new Receiver();
    	receiver.setEmail(Configuration.instance().getStringProperty("MY_PAYPAL_EMAIL"));
    	info.setReceiver(receiver);
    	info.setEncryptedRefundTransactionId("awefawef");
		info.setRefundFeeAmount(new BigDecimal("0"));
		info.setRefundGrossAmount(new BigDecimal("10"));
		info.setRefundNetAmount(new BigDecimal("10"));
		info.setRefundStatus(BookingServiceInterface.STATUS_REFUNDED);
		info.setRefundTransactionStatus(BookingServiceInterface.STATUS_REFUNDED);
		info.setTotalOfAllRefunds(new BigDecimal("10"));
		refundInfoList.add(info);
		RefundInfoList list = createMock(RefundInfoList.class);
		expect(list.getRefundInfo()).andReturn(refundInfoList); 
		response.setRefundInfoList(list);
    	expect(mock.refund(EasyMock.anyObject(RefundRequest.class))).andReturn(response); 
    	replay(list);
    	replay(mock);
    	bookingService.getRefundService().setAdaptivePayments(mock);
    	
    	bookingService.updateCompleteBooking(booking);
    	
    	Booking result = bookingService.get(booking.getId());
    	Assert.assertEquals(result.getBookingStatus(),Booking.BOOKING_STATUS_REFUND_FULL);
    	Assert.assertEquals(result.getReservedBool(),false);
    	
    	removeSecondBooking(booking2);
    	verify(list);
    	verify(mock);
    	tearDown();
    }
    
    @Test 
    public void updateCompelteBookingOverlappingNotExpired() throws FatalException, PPFaultMessage
    {
    	setUp();
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking.setReservedBool(true);
    	// must make the booking expired
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.HOUR_OF_DAY, 10);
    	booking.setExpirationDate(now.getTime());
    	dao.update(booking);
    	
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking2.setReservedBool(true);
    	dao.update(booking2);
    	
    	bookingService.updateCompleteBooking(booking);
    	
    	Booking result = bookingService.get(booking.getId());
    	Assert.assertEquals(result.getBookingStatus(),Booking.BOOKING_STATUS_CONFIRMED);
    	Assert.assertEquals(result.getReservedBool(),false);
    	
    	removeSecondBooking(booking2);
    	tearDown();
    }
    
    @Test 
    public void updateCompelteBookingOverlappingRefundError() throws FatalException, PPFaultMessage
    {
    	setUp();
    	
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking.setReservedBool(true);
    	// must make the booking expired
    	Calendar now = DateUtil.getCurrentCalendarCompareTZ();
    	now.add(Calendar.HOUR_OF_DAY, -1);
    	booking.setExpirationDate(now.getTime());
    	dao.update(booking);
    	
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	booking2.setReservedBool(true);
    	dao.update(booking2);
    	
    	// create a mock reply to the refund
    	AdaptivePayments mock = createMock(AdaptivePayments.class);
    	RefundResponse response = new RefundResponse();
    	List<RefundInfo> refundInfoList = new ArrayList<RefundInfo>();
    	RefundInfo info = new RefundInfo();
    	Receiver receiver = new Receiver();
    	receiver.setEmail(Configuration.instance().getStringProperty("MY_PAYPAL_EMAIL"));
    	info.setReceiver(receiver);
    	info.setEncryptedRefundTransactionId("awefawef");
		info.setRefundFeeAmount(new BigDecimal("0"));
		info.setRefundGrossAmount(new BigDecimal("10"));
		info.setRefundNetAmount(new BigDecimal("10"));
		info.setRefundStatus(BookingServiceInterface.STATUS_ERROR);
		info.setRefundTransactionStatus(BookingServiceInterface.STATUS_REFUNDED);
		info.setTotalOfAllRefunds(new BigDecimal("10"));
		refundInfoList.add(info);
		RefundInfoList list = createMock(RefundInfoList.class);
		expect(list.getRefundInfo()).andReturn(refundInfoList); 
		response.setRefundInfoList(list);
    	expect(mock.refund(EasyMock.anyObject(RefundRequest.class))).andReturn(response); 
    	replay(list);
    	replay(mock);
    	bookingService.getRefundService().setAdaptivePayments(mock);
    	
    	bookingService.updateCompleteBooking(booking);
    	
    	Booking result = bookingService.get(booking.getId());
    	Assert.assertEquals(result.getBookingStatus(),Booking.BOOKING_STATUS_ERROR);
    	Assert.assertEquals(result.getReservedBool(),false);
    	
    	removeSecondBooking(booking2);
    	verify(list);
    	verify(mock);
    	tearDown();
    }
    
    @Test
    public void getBookingsWithStatusNoConfirmationEmail()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setConfirmationEmailBool(true);
    	dao.update(booking);
    	
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking2.setConfirmationEmailBool(false);
    	dao.update(booking2);
    	
    	List<Booking> results = bookingService.getBookingsWithStatusNoConfirmationEmail(
    	    Booking.BOOKING_STATUS_CONFIRMED);
    	Assert.assertEquals(results.size(),1);
    	Assert.assertEquals(results.get(0).getId(),booking2.getId());
    	
    	removeSecondBooking(booking2);
    	tearDown();
    }
    
    @Test
    public void sendConfirmationEmails()
    {
    	setUp();
    	booking.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking.setConfirmationEmailBool(true);
    	dao.update(booking);
    	
    	Booking booking2 = createSecondBooking();
    	booking2.setStartDate(booking.getStartDate());
    	booking2.setEndDate(booking.getEndDate());
    	booking2.setBookingStatus(Booking.BOOKING_STATUS_CONFIRMED);
    	booking2.setConfirmationEmailBool(false);
    	dao.update(booking2);
    	
    	bookingService.sendConfirmationEmails();
    	
    	Booking result = bookingService.get(booking2.getId());
    	Assert.assertTrue(result.getConfirmationEmailBool());
    	
    	removeSecondBooking(booking2);
    	tearDown();
    }
    
//    @Test
//    public void deductPayPalFeesUsd() throws ExchangeRateNotFoundException
//    {
//    	Booking booking = Generator.booking();
//    	CurrencyCode code = Generator.currencyCode();
//    	code.setUsd(1.0f);
//    	booking.setCurrencyCode(code);
//    	
//    	double amount = 100f;
//    	
//    	BigDecimal result = bookingService.deductPayPalFees(amount,booking);
//    	
//    	Assert.assertEquals(result.toPlainString(),"96.79999542236328125");
//    }
    
//    @Test
//    public void deductPayPalFeesAUD() throws ExchangeRateNotFoundException
//    {
//    	Booking booking = Generator.booking();
//    	CurrencyCode code = Generator.currencyCode();
//    	code.setUsd(1.1f);
//    	code.setCode("AUD");
//    	booking.setCurrencyCode(code);
//    	
//    	double amount = 100f;
//    	
//    	BigDecimal result = bookingService.deductPayPalFees(amount,booking);
//    	
//    	Assert.assertEquals(result.toPlainString(),"96.8272705078125");
//    	//Assert.assertEquals(result.toPlainString(),"96.79999542236328125");
//    }
    
    @Test
    public void testFormatDate()
    {
    	Calendar startDate = Calendar.getInstance();
    	startDate.setTimeInMillis(0);
    	startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
    	String result = bookingService.formatDate(startDate.getTime(),"GMT");
    	Assert.assertEquals("Thursday 1 January 1970 Greenwich Mean Time",result);
    }
}
