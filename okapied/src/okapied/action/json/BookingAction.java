package okapied.action.json;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import okapied.entity.Booking;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Property;
import okapied.exception.AvailabilityException;
import okapied.exception.PriceException;
import okapied.exception.InputValidationException;
import okapied.service.BookingServiceInterface;
import okapied.service.PropertyService;
import okapied.util.Configuration;
import okapied.util.DateUtil;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;

public class BookingAction
{
    public static final String RESERVATION = "reservation";
    private static final Logger log = Logger.getLogger(BookingAction.class);
    
    Integer propertyId;
    String start;
    String end;
    Integer people;
    long period;
    String currency;
    
    Booking booking;
    String message;
    
    BookingServiceInterface bookingService;
    PropertyService propertyService;
    
    HttpServletRequest request;
    
    public void nullServices()
    {
    	bookingService = null;
    	propertyService = null;
    	request = null;
    }
    
    public String reservation()
    {
    	
    	Property property = propertyService.get(propertyId);
    	OkapiedUserDetails user = LoginStatus.getUser();
    	Calendar startDate = propertyService.getDate(start,property);
    	Calendar endDate = propertyService.getDate(end,property);
		SimpleDateFormat sdf = new SimpleDateFormat(Configuration.instance().getStringProperty("DATE_FORMAT"));
		sdf.setTimeZone(startDate.getTimeZone());
    	log.info("User reservation: " + user.getId() + " property: " + property.getId() + " " + start + " " + end);
    	
    	try {
			booking = bookingService.makeReservation(property, user, startDate, endDate, people);
			currency = booking.getProperty().getCurrencyCode().getCode();
			booking.setProperty(null);
			booking.setUser(null);
			
			start = sdf.format(startDate.getTime());
			end = sdf.format(endDate.getTime());			
			period = DateUtil.differenceInDays(startDate, endDate);
			
		} catch (AvailabilityException e) {
			message = e.getMessage();
		} catch (InputValidationException e) {
			message = e.getMessage();
		} catch (PriceException e) {
			message = e.getMessage();
		}
		nullServices();
    	return RESERVATION;
    }

	public Integer getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public BookingServiceInterface getBookingService() {
		return bookingService;
	}

	public void setBookingService(BookingServiceInterface bookingService) {
		this.bookingService = bookingService;
	}

	public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public Integer getPeople() {
		return people;
	}

	public void setPeople(Integer people) {
		this.people = people;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
