package okapied.action.app;

import java.util.ArrayList;
import java.util.List;

import okapied.entity.Flooring;
import okapied.entity.OkaOption;
import okapied.entity.Property;
import okapied.exception.OkaSecurityException;
import okapied.service.PropertyDetailsService;
import okapied.service.PropertyService;
import okapied.service.ReferenceService;
import okapied.util.Configuration;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ActionSupport;

public class Details extends ActionSupport implements PageAction
{
    public static final String LIST = "list";
    public static final String LIST_SUBMIT = "listsubmit";
    
    Integer propertyId;
	PropertyService propertyService;
	ReferenceService referenceService;
	PropertyDetailsService propertyDetailsService;
	
	Property property;
	Integer flooringId;
	
	String message;
	
	List<OkaOption> options;
	
	String kitchenOptions;
	String outdoorOptions;
	String bathroomOptions;
	String amOptions;
	String multiOptions;
	
	String distanceToBeachM = null;
	
	public static final Logger log = Logger.getLogger(Details.class);
	
	String bedrooms;
	
	public void loadProperty() throws OkaSecurityException
	{
		if( propertyId == null ) return;
	    property = propertyService.get(propertyId);
	    checkPermission();
	    loadBedrooms(property);
	}
	
	public void loadBedrooms(Property property)
	{
	    if( property.getPropertyDetails() != null && property.getPropertyDetails().getBedrooms() != null )
	    {
	    	if( property.getPropertyDetails().getBedrooms() == 0 )
	    	{
	    		bedrooms = Publish.STUDIO_OPTION;
	    	}
	    	else
	    	{
	    	    bedrooms = property.getPropertyDetails().getBedrooms().toString();
	    	}
	    }
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
			options = referenceService.getAllOptions();
			propertyDetailsService.parseAllOptions(property.getPropertyDetails(), options);
		}
		catch(OkaSecurityException e)
		{
			message = e.getMessage();
			return ERROR;
		}
    	return LIST;
    }
    
    public String listsubmit()
    {
    	log.debug("Kitchen options: " + kitchenOptions);
		Property result;
		property.setId(propertyId);
		setBedrooms(property,bedrooms);
		try {
			if( distanceToBeachM != null && distanceToBeachM.length() > 0) 
				property.getPropertyDetails().setDistanceToBeachMeters(new Integer(distanceToBeachM));
			else property.getPropertyDetails().setDistanceToBeachMeters(null);

			result = propertyService.savePropertyDetails(property,flooringId,LoginStatus.getUser().getId(),
			    kitchenOptions,outdoorOptions,bathroomOptions,amOptions,multiOptions);
			setPropertyId(result.getId());
			message = "Changes Saved";
//		    loadProperty();
//			options = referenceService.getAllOptions();
//			propertyDetailsService.parseAllOptions(property.getPropertyDetails(), options);
		} 
		catch (OkaSecurityException e) 
		{
			message = e.getMessage();
			return ERROR;
		}
		catch( NumberFormatException e )
		{
			addFieldError("distanceToBeachM","Distance to beach must be a number");
			return ERROR;
		}
		finally
		{
		    try {
				loadProperty();
			} catch (OkaSecurityException e) {
				log.error(e.getMessage());
			}
			options = referenceService.getAllOptions();
			propertyDetailsService.parseAllOptions(property.getPropertyDetails(), options);
		}
    	return LIST_SUBMIT;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public List<Integer> getNumberList()
	{
		List<Integer> list = new ArrayList<Integer>();
		for( int i=0; i <= Configuration.instance().getIntProperty("MAX_NUMBER_OPTION"); ++i )
		{
			list.add(new Integer(i));
		}
		return list;
	}
	
	public List<String> getBedroomsOptions()
	{
		List<String> list = new ArrayList<String>();
		for( int i=0; i <= Configuration.instance().getIntProperty("MAX_BEDROOMS_OPTIONS"); ++i )
		{
			if( i == 0 ) list.add(Publish.STUDIO_OPTION);
			else list.add(new Integer(i).toString());
		}
		return list;
	}

    public List<Flooring> getFlooring()
    {
    	return referenceService.getAllFlooring();
    }

	public Integer getFlooringId() {
		return flooringId;
	}

	public void setFlooringId(Integer flooringId) {
		this.flooringId = flooringId;
	}

	public ReferenceService getReferenceService() {
		return referenceService;
	}

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	public List<OkaOption> getOptions() {
		return options;
	}

	public void setOptions(List<OkaOption> options) {
		this.options = options;
	}

	public PropertyDetailsService getPropertyDetailsService() {
		return propertyDetailsService;
	}

	public void setPropertyDetailsService(
			PropertyDetailsService propertyDetailsService) {
		this.propertyDetailsService = propertyDetailsService;
	}

	public String getKitchenOptions() {
		return kitchenOptions;
	}

	public void setKitchenOptions(String kitchenOptions) {
		this.kitchenOptions = kitchenOptions;
	}

	public String getOutdoorOptions() {
		return outdoorOptions;
	}

	public void setOutdoorOptions(String outdoorOptions) {
		this.outdoorOptions = outdoorOptions;
	}

	public String getBathroomOptions() {
		return bathroomOptions;
	}

	public void setBathroomOptions(String bathroomOptions) {
		this.bathroomOptions = bathroomOptions;
	}

	public String getAmOptions() {
		return amOptions;
	}

	public void setAmOptions(String amOptions) {
		this.amOptions = amOptions;
	}

	public String getMultiOptions() {
		return multiOptions;
	}

	public void setMultiOptions(String multiOptions) {
		this.multiOptions = multiOptions;
	}

	public String getDistanceToBeachM() {
		return distanceToBeachM;
	}

	public void setDistanceToBeachM(String distanceToBeachM) {
		this.distanceToBeachM = distanceToBeachM;
	}

	public int getPage() {
		return 2;
	}

	public String getBedrooms() {
		return bedrooms;
	}

	public void setBedrooms(String bedrooms) {
		this.bedrooms = bedrooms;
	}
	
	public void setBedrooms(Property property, String bedrooms)
	{
		if( property.getPropertyDetails() != null )
		{
			if( bedrooms != null )
			{
			    int value;
			    if( bedrooms.equals(Publish.STUDIO_OPTION) ) value = 0;
			    else value = Integer.parseInt(bedrooms);
			    property.getPropertyDetails().setBedrooms(value);
			}
		}
	}
	
	public String getTitle()
	{
		return "Features and Details";
	}
	
	public String getDescription()
	{
	    return "Select the main features of your place";
	}
	
	public boolean getRoom()
	{
		if( property == null || property.getPropertyDetails() == null ) return false;
		if( property.getPropertyDetails().getType().getName().equals("room") || 
				property.getPropertyDetails().getType().getName().equals("b&b"))
		{
			return true;
		}
		return false;
	}
}
