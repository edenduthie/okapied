package okapied.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.BaseTest;
import okapied.dao.DAO;
import okapied.entity.Availability;
import okapied.entity.Booking;
import okapied.entity.CurrencyCode;
import okapied.entity.Flooring;
import okapied.entity.OkapiedGrantedAuthority;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Price;
import okapied.entity.Property;
import okapied.entity.PropertyType;
import okapied.exception.BookingException;
import okapied.exception.ExchangeRateNotFoundException;
import okapied.util.Configuration;
import okapied.util.DateUtil;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.svcs.services.PPFaultMessage;
import com.paypal.svcs.types.common.ErrorData;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

public class BookingService2PaymentsTest extends BaseTest
{
    @Autowired BookingServiceInterface bookingService2;
    @Autowired DAO dao;
    @Autowired PropertyService propertyService;
    @Autowired AccountService accountService;
    @Autowired RefundServiceInterface refundService;
    
    OkapiedUserDetails owner;
    
    public void setUp()
    {
    	av = Generator.availability();
    	saveAvailability();
    	
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
    	Collection<GrantedAuthority> auths = booking.getUser().getAuthorities();
    	for( GrantedAuthority auth : auths )
    	{
    		dao.persist((OkapiedGrantedAuthority)auth);
    	}
    	booking.getUser().setIp("127.0.0.1");
    	booking.getUser().setEmail("eduthi_1298629553_per@gatorlogic.com");
    	dao.persist(booking.getUser());
    	user = booking.getUser();
    	booking.setCurrencyCode(Generator.currencyCode());
    	booking.getCurrencyCode().setUsd(1.0f);
    	dao.persist(booking.getCurrencyCode());
    	dao.persist(booking);
    }
    
    public void tearDown()
    {
    	dao.deleteAll(Booking.class.getName());
    	accountService.remove(booking.getUser());
    	dao.deleteAll(OkapiedGrantedAuthority.class.getName());
    	
    	dao.delete(Availability.class.getName(), "id", av.getId());
    	dao.delete(Price.class.getName(), "id", price.getId());
    	dao.delete(Property.class.getName(), "id", property.getId());
    	dao.deleteAll(CurrencyCode.class.getName());
    	dao.deleteAll(Flooring.class.getName());
    	dao.deleteAll(PropertyType.class.getName());
    	removeLocations();
    }
    
    @Test
    public void startBooking() throws FatalException, SSLConnectionException, PPFaultMessage, IOException, BookingException
    {
    	setUp();

    	String redirect;
    	try
    	{
    	    redirect = bookingService2.startBooking(booking);
    	}
    	catch( PPFaultMessage e)
    	{
    		for( ErrorData ed : e.getFaultInfo().getError())
    		{
    			System.out.println(ed.getMessage());
    		}
    		throw e;
    	}
    	System.out.println(redirect);
    	Assert.assertEquals(booking.getBookingStatus(),Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	Assert.assertNotNull(booking.getPayKey());
    	Assert.assertNotNull(booking.getTrackingId());
    	tearDown();
    }
    
    @Test
    public void startBookingFrozenOwner() throws FatalException, SSLConnectionException, PPFaultMessage, IOException, BookingException
    {
    	setUp();

    	String redirect;
    	try
    	{
    		booking.getProperty().getOwner().setFrozenBool(true);
    		dao.update(booking.getProperty().getOwner());
    	    redirect = bookingService2.startBooking(booking);
    	    Assert.fail();
    	}
    	catch( BookingException e) {}

    	tearDown();
    }
    
    @Test
    public void verifyCompletion()
    {
    	Booking booking = Generator.booking();
    	booking.setTrackingId("dummytrackingid");
    	booking.setPayKey("dummypaykey");
    	booking.getUser().setEmail("dummySenderEmail");
    	Assert.assertTrue(bookingService2.verifyCompletion(booking,"dummytrackingid","dummypaykey","dummySenderEmail"));
    }
    
    @Test
    public void verifyCompletionBadTrackingId()
    {
    	Booking booking = Generator.booking();
    	booking.setTrackingId("dummytrackingid");
    	booking.setPayKey("dummypaykey");
    	booking.getUser().setEmail("dummySenderEmail");
    	Assert.assertFalse(bookingService2.verifyCompletion(booking,"badtrackingid","dummypaykey","dummySenderEmail"));
    }
    
    @Test
    public void verifyCompletionBadPayKey()
    {
    	Booking booking = Generator.booking();
    	booking.setTrackingId("dummytrackingid");
    	booking.setPayKey("dummypaykey");
    	booking.getUser().setEmail("dummySenderEmail");
    	Assert.assertFalse(bookingService2.verifyCompletion(booking,"dummytrackingid","badpaykey","dummySenderEmail"));
    }
    
//    @Test
//    public void verifyCompletionBadSenderEmail()
//    {
//    	Booking booking = Generator.booking();
//    	booking.setTrackingId("dummytrackingid");
//    	booking.setPayKey("dummypaykey");
//    	booking.getUser().setEmail("dummySenderEmail");
//    	Assert.assertFalse(bookingService2.verifyCompletion(booking,"dummytrackingid","dummypaykey","badSenderEmail"));
//    }
    
    @Test
    public void emailCancellation() throws AddressException, MessagingException
    {
    	Booking booking = Generator.booking();
    	booking.getUser().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getUser().setFirstName("Eden");
    	booking.getUser().setLastName("Duthie");
    	booking.setId(234234);
    	bookingService2.emailCancellation(booking);
    }
    
    @Test
    public void completeBooking()
    {
    	setUp();
    	booking.getUser().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getUser().setFirstName("Eden");
    	booking.getUser().setLastName("Duthie");
    	dao.update(booking.getUser());
    	booking.setTrackingId("dummytrackingid");
    	booking.setPayKey("dummypaykey");
    	dao.update(booking);
    	booking.getProperty().getOwner().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.setBookingStatus(Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	dao.update(booking);
    	dao.update(booking.getProperty().getOwner());
    	
    	bookingService2.processIPN(booking.getId(), booking.getTrackingId(), booking.getPayKey(),
    	    booking.getUser().getEmail(), BookingServiceInterface.STATUS_INCOMPLETE);
    	
    	Booking bookingResult = bookingService2.get(booking.getId());
    	Assert.assertEquals(bookingResult.getBookingStatus(),Booking.BOOKING_STATUS_CONFIRMED);
    	
    	tearDown();
    }
    
    @Test
    public void emailCompletion() throws AddressException, MessagingException
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
    	
    	bookingService2.emailCompletion(booking);
    }
    
    @Test
    public void testEmailPropertyOwnerCompetion()
    {
    	Booking booking = Generator.booking();
    	booking.getProperty().getOwner().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getProperty().getOwner().setFirstName("Eden");
    	booking.getProperty().getOwner().setLastName("Duthie");
    	booking.setId(234234);
    	booking.getUser().setFirstName("Eden");
    	booking.getUser().setLastName("Duthie");
    	booking.getUser().setEmail("eduthie@gmail.com");
    	bookingService2.emailPropertyOwnerCompletion(booking);
    }
    
    @Test
    public void emailOwnerPaymentCompletion()
    {
    	Booking booking = Generator.booking();
    	booking.getProperty().getOwner().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getProperty().getOwner().setFirstName("Eden");
    	booking.getProperty().getOwner().setLastName("Duthie");
    	booking.setId(234234);
    	bookingService2.emailOwnerPaymentCompletion(booking);
    }
    
    @Test
    public void retriveStatusPending() throws FatalException, SSLConnectionException, PPFaultMessage, IOException, BookingException
    {
    	setUp();
    	String redirect;
    	try
    	{
    	    redirect = bookingService2.startBooking(booking);
    	    String result = bookingService2.retrieveBookingStatus(booking.getId());
    	    Assert.assertNotNull(result);
    	    Assert.assertEquals(BookingServiceInterface.STATUS_CREATED,result);
    	}
    	catch( PPFaultMessage e)
    	{
    		for( ErrorData ed : e.getFaultInfo().getError())
    		{
    			System.out.println(ed.getMessage());
    		}
    		throw e;
    	}
    	System.out.println(redirect);
    	Assert.assertEquals(booking.getBookingStatus(),Booking.BOOKING_STATUS_PENDING_PAYMENT);
    	Assert.assertNotNull(booking.getPayKey());
    	Assert.assertNotNull(booking.getTrackingId());
    	tearDown();
    }
 
//    @Test
//    public void testMakePayment()
//    {
//    	try {
//			System.out.println(bookingService2.startBooking(10));
//		} catch (FatalException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SSLConnectionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (PPFaultMessage e) {
//			for( ErrorData error : e.getFaultInfo().getError())
//			{
//				System.out.println(error.getMessage());
//			}
//			e.printStackTrace();
//		}
//    }
    
//    @Test
//    public void testCheckBookingStatus() throws FatalException, SSLConnectionException, PPFaultMessage, IOException
//    {
//    	Booking booking = bookingService2.get(13);
//    	try
//    	{
//    		bookingService2.checkBookingStatus(booking);
//    	}
//    	catch( PPFaultMessage e )
//    	{
//    		for( ErrorData error : e.getFaultInfo().getError())
//    		{
//    			System.out.println(error.getMessage());
//    		}
//    		throw e;
//    	}
//    }
    
//    @Test
//    public void completeSequence() 
//        throws FatalException, SSLConnectionException, PPFaultMessage, IOException, RefundException, BookingException
//    {
//    	setUp();
//    	
//    	booking.getUser().setEmail(Configuration.instance().getStringProperty("MY_PAYPAL_EMAIL"));
//    	dao.update(booking.getUser());
//    	
//    	CurrencyCode currency = new CurrencyCode();
//		currency.setCode("USD");
//		dao.persist(currency);
//		booking.getProperty().setCurrencyCode(currency);
//		booking.setRefundPolicy(Property.REFUND_POLICY_STANDARD);
//		dao.update(booking);
//
//    	String redirect;
//    	try
//    	{
//    	    redirect = bookingService2.startBooking(booking);
//    	    System.out.println(redirect);
//    	    Assert.assertEquals(booking.getBookingStatus(),Booking.BOOKING_STATUS_PENDING_PAYMENT);
////    	    refundService.issueRefund(booking,booking.getUser().getId());
////    	    Assert.assertEquals(booking.getBookingStatus(),Booking.BOOKING_STATUS_REFUND_FULL);
//    	    bookingService2.checkBookingStatus(booking);
//    	    Assert.assertEquals(booking.getBookingStatus(),Booking.BOOKING_STATUS_CONFIRMED);
//    	    bookingService2.sendMoneyToOwner(booking);
//    	    Assert.assertTrue(booking.getPaymentCompleteBool());
//    	}
//    	catch( PPFaultMessage e)
//    	{
//    		for( ErrorData ed : e.getFaultInfo().getError())
//    		{
//    			System.out.println(ed.getMessage());
//    		}
//    		throw e;
//    	}
//    	System.out.println(redirect);
//    	tearDown();
//    }
    
    @Test
    public void emailOwnerError()
    {
    	Booking booking = Generator.booking();
    	booking.getProperty().getOwner().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getProperty().getOwner().setFirstName("Eden");
    	booking.getProperty().getOwner().setLastName("Duthie");
    	booking.setId(234234);
    	booking.getProperty().setId(23423);
    	bookingService2.emailPropertyOwnerError(booking);
    }
    
    @Test
    public void emailOwnerWrongCurrency()
    {
    	Booking booking = Generator.booking();
    	booking.getProperty().getOwner().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getProperty().getOwner().setFirstName("Eden");
    	booking.getProperty().getOwner().setLastName("Duthie");
    	booking.getProperty().setCurrencyCode(Generator.currencyCode());
    	booking.getProperty().getCurrencyCode().setName("Australian Dollars");
    	booking.setId(234234);
    	booking.getProperty().setId(23423);
    	bookingService2.emailOwnerWrongCurrency(booking);
    }
    
    @Test
    public void emailMeWrongCurrency()
    {
    	Booking booking = Generator.booking();
    	booking.getProperty().getOwner().setEmail(Configuration.instance().getStringProperty("ADMIN_EMAIL"));
    	booking.getProperty().getOwner().setFirstName("Eden");
    	booking.getProperty().getOwner().setLastName("Duthie");
    	booking.getProperty().setCurrencyCode(Generator.currencyCode());
    	booking.getProperty().getCurrencyCode().setName("Australian Dollars");
    	booking.setId(234234);
    	booking.getProperty().setId(23423);
    	booking.getProperty().setId(23423);
    	bookingService2.emailMeWrongCurrency(booking);
    }
    
    @Test
    public void sendMoneyToOwner() throws FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	setUp();
    	
    	booking.setPaymentCompleteBool(false);
    	booking.getProperty().getOwner().setFirstName("Eden");
    	booking.getProperty().getOwner().setLastName("Duthie");
    	dao.update(booking.getProperty().getOwner());
    	
    	bookingService2.sendMoneyToOwner(booking);
    	
    	Assert.assertTrue(booking.getPaymentCompleteBool());
    	
    	tearDown();
    }
    
    @Test
    public void sendMoneyToOwnerWrongFirstLastName() throws FatalException, SSLConnectionException, PPFaultMessage, IOException
    {
    	setUp();
    	booking.setPaymentCompleteBool(false);
    	
    	bookingService2.sendMoneyToOwner(booking);
    	
    	Assert.assertFalse(booking.getPaymentCompleteBool());
    	
    	tearDown();
    }
}
