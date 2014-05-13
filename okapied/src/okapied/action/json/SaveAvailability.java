package okapied.action.json;

import java.util.Calendar;

import okapied.entity.Property;
import okapied.exception.AlreadyBookedException;
import okapied.exception.OkaSecurityException;
import okapied.exception.PriceException;
import okapied.service.AvailabilityService;
import okapied.service.PropertyService;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;

public class SaveAvailability extends ActionSupport 
{
	static String LIST = "list";
	static String CLEAR = "clear";

	Integer propertyId;
	Integer day;
	Integer month;
	Integer year;
	Integer av;
	AvailabilityService availabilityService;
	PropertyService propertyService;
	String message;
	Float price;
	Integer dayOfWeek;
	
	public void nullServices()
	{
		availabilityService = null;
		propertyService = null;
	}

	static Logger log = Logger.getLogger(SaveAvailability.class);

	public String list() {
		Property property = propertyService.get(propertyId);
		try {
			if( dayOfWeek != null )
			{
				availabilityService.makeAvailableWeekDay(property,dayOfWeek,LoginStatus.getUser().getId(),price);
			}
			else if( day == null )
			{
				Calendar[] period = availabilityService.monthYearToPeriod(property, month, year);
				System.out.println("START: " + period[0].getTime());
				System.out.println("END: " + period[1].getTime());
				availabilityService.makeAvailablePeriod(property,period[0],period[1],
			        LoginStatus.getUser().getId(),price);
			}
			else
			{
				boolean availability = false;
				if (av.equals(1))
					availability = true;
				else
					availability = false;
			    availabilityService.makeAvailableDayOnly(property, day, month,
					    year, availability, LoginStatus.getUser().getId(),price);
			}
			message = "success";
		} catch (AlreadyBookedException e) {
			message = e.getMessage();
			log.error(e);
		} catch (OkaSecurityException e) {
			log.error(e);
			message = e.getMessage();
		}
		nullServices();
		return LIST;
	}
	
	public String clear()
	{
		Property property = propertyService.get(propertyId);
		try {
			if( price == null )
			{
				boolean availability = false;
				if (av.equals(1))
					availability = true;
				else
					availability = false;
				propertyService.clearAndMakeAvailable(property,availability,LoginStatus.getUser().getId());
			}
			else
			{
				if( price == -1 ) price = null;
				propertyService.clearAndSetPrice(property,LoginStatus.getUser().getId(),price);
			}
			message = "success";
		} catch (OkaSecurityException e) {
			log.error(e);
			message = e.getMessage();
		} catch (PriceException e) {
			log.error(e);
			message = e.getMessage();
		}
		nullServices();
		return CLEAR;
	}

	public Integer getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public AvailabilityService getAvailabilityService() {
		return availabilityService;
	}

	public void setAvailabilityService(AvailabilityService availabilityService) {
		this.availabilityService = availabilityService;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public Integer getAv() {
		return av;
	}

	public void setAv(Integer av) {
		this.av = av;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
}
