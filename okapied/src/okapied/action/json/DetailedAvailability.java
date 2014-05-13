package okapied.action.json;

import java.util.List;

import okapied.entity.OkapiedUserDetails;
import okapied.entity.Property;
import okapied.entity.PropertyDay;
import okapied.service.PropertyService;
import okapied.web.LoginStatus;

public class DetailedAvailability 
{
	private PropertyService propertyService;
    public List<PropertyDay> days;
    private String LIST = "list";
    
    private Integer propertyId;
    private Integer month;
    private Integer year;
    private Integer numMonths;
    
    public void nullServices()
    {
    	propertyService = null;
    }
    
    public String list()
    {
    	OkapiedUserDetails user = LoginStatus.getUser();
    	Property property = propertyService.get(propertyId);
    	propertyService.availabilityMonths(property, month, year, numMonths, LoginStatus.getUser());
    	days = property.getInfoList();
    	nullServices();
    	return LIST;
    }

	public List<PropertyDay> getDays() {
		return days;
	}

	public void setDays(List<PropertyDay> days) {
		this.days = days;
	}

	public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public Integer getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
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

	public Integer getNumMonths() {
		return numMonths;
	}

	public void setNumMonths(Integer numMonths) {
		this.numMonths = numMonths;
	}
}
