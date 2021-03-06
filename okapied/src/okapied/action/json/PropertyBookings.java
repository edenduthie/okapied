package okapied.action.json;

import java.util.List;

import okapied.entity.Booking;
import okapied.entity.Property;
import okapied.service.BookingEntityService;
import okapied.service.PropertyService;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;

/**
 * Returns a list summary of all the bookings made on properties for the given user
 * Used to populate the list on the left sidebar.
 * 
 * @author eduthie
 *
 */
public class PropertyBookings 
{
    private String LIST = "list";
    private String ERROR = "error";
    BookingEntityService bookingEntityService;
    List<Booking> bookings;
    Integer limit;
    Integer offset;
    Long size;
    
    public void nullServices()
    {
    	bookingEntityService = null;
    }
    
    private static final Logger log = Logger.getLogger(PropertyBookings.class);
   
    public String list()
    {
    	bookings = bookingEntityService.getPropertyBookings(LoginStatus.getUser().getId(), limit, offset);
    	size = bookingEntityService.getPropertyBookingsSize(LoginStatus.getUser().getId());
    	nullServices();
	    return LIST;
    }

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public BookingEntityService getBookingEntityService() {
		return bookingEntityService;
	}

	public void setBookingEntityService(BookingEntityService bookingEntityService) {
		this.bookingEntityService = bookingEntityService;
	}

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
   
}
