package okapied.action.app;

import java.util.List;

import okapied.entity.OkaOption;
import okapied.entity.Property;
import okapied.exception.OkaSecurityException;
import okapied.service.PropertyDetailsService;
import okapied.service.PropertyService;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ActionSupport;

public class ListBaseAction extends ActionSupport
{
    String LIST = "list";
    String LIST_SUBMIT = "listsubmit";
    
    Property property;
    Integer propertyId;
	
    PropertyService propertyService;
    PropertyDetailsService propertyDetailsService;
	String message;
	
	List<OkaOption> options;
    
    public static Logger log = Logger.getLogger(PropertyText.class);
    
	public void loadProperty() throws OkaSecurityException
	{
		if( propertyId == null ) return;
	    property = propertyService.get(propertyId);
	    checkPermission();
	}
	
	public void checkPermission() throws OkaSecurityException
	{
	    if( !property.getOwner().getId().equals(LoginStatus.getUser().getId()) )
	    {
	    	property = null;
	    	log.warn("Illegal access to property: " + propertyId);
	    	throw new OkaSecurityException("You must be the owner to edit the property details");
	    }
	}
    
	@SkipValidation
    public String list()
    {
		try
		{
		    loadProperty();
		}
		catch(OkaSecurityException e)
		{
			message = e.getMessage();
			return ERROR;
		}
    	return LIST;
    }

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
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

	public PropertyDetailsService getPropertyDetailsService() {
		return propertyDetailsService;
	}

	public void setPropertyDetailsService(
			PropertyDetailsService propertyDetailsService) {
		this.propertyDetailsService = propertyDetailsService;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<OkaOption> getOptions() {
		return options;
	}

	public void setOptions(List<OkaOption> options) {
		this.options = options;
	}
	
	public String getTitle()
	{
		return "Place Listing";
	}
	
	public String getDescription()
	{
	    return "Place Listing";	
	}
}
