package okapied.action.app;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okapied.action.BaseAction;
import okapied.entity.Booking;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Property;
import okapied.exception.AvailabilityException;
import okapied.exception.BookingException;
import okapied.exception.CancellationException;
import okapied.exception.PriceException;
import okapied.exception.InputValidationException;
import okapied.service.BookingServiceInterface;
import okapied.service.PropertyService;
import okapied.util.Configuration;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;

import com.paypal.svcs.services.PPFaultMessage;
import com.paypal.svcs.types.common.ErrorData;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

public class BookingAction extends BaseAction
{
	Integer propertyId;
	PropertyService propertyService;
	Property property;
	
	String redirectUrl;
	Integer bookingId;
	String start;
	String end;
	Integer people;
	String message;
	
	Booking booking;
	
	BookingServiceInterface bookingService;
	
	private static final Logger log = Logger.getLogger(BookingAction.class);
	
	public static final String START = "start";
	public static final String ERROR = "error";
	public static final String CANCEL = "cancel";
	
	public String list()
    {
		if( propertyId != null )
		{
		    property = propertyService.get(propertyId);
		}
		else
		{
			log.error("Calling Booking.list with a null propertyId");
		}
    	return LIST;
    }
	
	public String cancel()
    {
		message = "<strong>Booking cancelled</strong>";
		if( bookingId != null )
		{
		    booking = bookingService.get(bookingId);
		    try {
				bookingService.cancelBooking(booking, LoginStatus.getUserId());
			} catch (CancellationException e) {
				message = e.getMessage();
			}
		    property = booking.getProperty();
		    this.propertyId = property.getId();
		}
		else
		{
			log.error("Calling Booking.cancel with a null bookingId");
		}
    	return CANCEL;
    }
	
	public String complete()
    {
		message = "<strong>Booking Complete</strong> You will receive a confirmation email shortly.";
		if( propertyId != null )
		{
		    property = propertyService.get(propertyId);
		}
		else
		{
			log.error("Calling Booking.list with a null propertyId");
		}
    	return CANCEL;
    }
	
	public String start()
	{
		if( propertyId != null )
		{
		    property = propertyService.get(propertyId);
		}
		try
		{
			booking = bookingService.get(bookingId);
		}
		catch(Exception e)
		{
			log.debug("Booking not found: " + bookingId);
			try {
				reservation();
			} catch (AvailabilityException e1) {
				message = e1.getMessage();
				return ERROR;
			} catch (InputValidationException e1) {
				message = e1.getMessage();
				return ERROR;
			} catch (PriceException e1) {
				message = e1.getMessage();
				return ERROR;
			}
		}

		try {
			redirectUrl = bookingService.startBooking(booking);
		} catch (FatalException e) {
			log.error(e);
			message = getErrorMessage(booking);
			return ERROR;
		} catch (SSLConnectionException e) {
			log.error(e);
			message = "<strong>Not to worry, connection error.</strong> There was a problem connecting " +
			    "to the PayPal service, try again.";
			return ERROR;
		} catch (PPFaultMessage e) {
			log.error(e);
			message = getErrorMessage(booking);
			return ERROR;
		} catch (IOException e) {
			log.error(e);
			message = getErrorMessage(booking);
			return ERROR;
		} catch (BookingException e) {
			log.error(e);
			message= e.getMessage();
			return ERROR;
		}
		return START;
	}
	
	public String getErrorMessage(Booking booking)
	{
		String errorMessage =
			"<strong>Well, that isn't great.</strong> Unfortunately there had been a problem processing your booking." +
			" Check that the email address entered for your account <i>" + LoginStatus.getUser().getEmail() +
			"</i> is a valid PayPal account and you are authorised to send the required funds of " +
			booking.getTotal() + " " + booking.getProperty().getCurrencyCode().getCode() + "." +
			" Please try again!";
		return errorMessage;
	}
	
    public void reservation() throws AvailabilityException, InputValidationException, PriceException
    {
    	Property property = propertyService.get(propertyId);
    	OkapiedUserDetails user = LoginStatus.getUser();
    	Calendar startDate = propertyService.getDate(start,property);
    	Calendar endDate = propertyService.getDate(end,property);
		SimpleDateFormat sdf = new SimpleDateFormat(Configuration.instance().getStringProperty("DATE_FORMAT"));
		sdf.setTimeZone(startDate.getTimeZone());
    	log.info("User reservation: " + user.getId() + " property: " + property.getId() + " " + start + " " + end);
		booking = bookingService.makeReservation(property, user, startDate, endDate, people);
    }

	public Integer getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public BookingServiceInterface getBookingService() {
		return bookingService;
	}

	public void setBookingService(BookingServiceInterface bookingService) {
		this.bookingService = bookingService;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public Integer getPeople() {
		return people;
	}

	public void setPeople(Integer people) {
		this.people = people;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getTitle()
	{
		String result = "Booking";
		if( property != null && property.getName() != null )
		{
			result += " " + property.getName();
		}
		return result;
	}
	
	public String getDescription()
	{
		return "Booking page to book a place on Okapied";
	}
}
