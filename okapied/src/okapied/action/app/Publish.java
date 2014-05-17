package okapied.action.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okapied.entity.CurrencyCode;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Property;
import okapied.entity.PropertyType;
import okapied.exception.OkaSecurityException;
import okapied.exception.InputValidationException;
import okapied.service.AccountService;
import okapied.service.PropertyService;
import okapied.service.ReferenceService;
import okapied.util.Configuration;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ActionSupport;

public class Publish extends ActionSupport implements PageAction
{	
	ReferenceService referenceService;
	
	Integer propertyId;
	
	Property property;
	
	Integer propertyTypeId;
	Integer currencyCodeId;
	Integer locationId;
	Boolean alwaysAvailable = true;
	Integer dailyRate;
	String bedrooms;
	
	String EDIT_SUBMIT = "editsubmit";
	String LIST_SUBMIT = "listsubmit";
	String DETAILS = "details";
	String LIST = "list";
	String EDIT = "edit";
	
	PropertyService propertyService;
	AccountService accountService;
	
	String message;
	String methodName;
	
	public static Logger log = Logger.getLogger(Publish.class);
	
	public static String STUDIO_OPTION = "studio";
	
	public void loadProperty() throws OkaSecurityException
	{
		if( propertyId == null ) return;
	    property = propertyService.get(propertyId);
	    checkPermission();
	    if( property.getDefaultPrice() != null )
	    {
	    	dailyRate = property.getDefaultPrice().getPrice().intValue();
	    }
	    if( property.getDefaultAvailability() != null  )
	    {
	    	alwaysAvailable = true;
	    }
	    else
	    {
	    	alwaysAvailable = false;
	    }
	    if( property.getCurrencyCode() != null )
	    {
	    	currencyCodeId = property.getCurrencyCode().getId();
	    }
	    if( property.getPropertyDetails() != null && property.getPropertyDetails().getType() != null )
	    {
	    	propertyTypeId = property.getPropertyDetails().getType().getId();
	    }
	    loadBedrooms(property);
	}
	
	public void loadBedrooms(Property property)
	{
	    if( property != null && 
	        property.getPropertyDetails() != null && 
	        property.getPropertyDetails().getBedrooms() != null )
	    {
	    	if( property.getPropertyDetails().getBedrooms() == 0 )
	    	{
	    		bedrooms = STUDIO_OPTION;
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
	    	throw new OkaSecurityException("You must be the owner to edit a property");
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
		if( property == null )
		{
			property = new Property();
			property.setMinNights(1);
			methodName = LIST;
		}
		else methodName = EDIT;
    	return LIST;
    }
	
	public String validateFields()
	{
		if( dailyRate == null )
		{
			addFieldError("dailyRate","Please provide a default daily rate for the property");
			return ERROR;
		}
		if( locationId == null )
		{
			addFieldError("location","Please enter the Cisy/Suburb that the property is in");
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String listsubmit()
	{
		System.out.println("METHOD NAME: " + methodName);
		String validationResult = validateFields();
		if( !validationResult.equals(SUCCESS) ) return validationResult;
		
        setBedrooms(property,bedrooms);
		
		Property result;
		try {
			result = propertyService.saveProperty(property,currencyCodeId,
					locationId,alwaysAvailable,new Float(dailyRate),LoginStatus.getUser().getId(),propertyTypeId,true);
		} catch (InputValidationException e) {
			log.error(e);
			addFieldError("property.name",e.getMessage());
			return ERROR;
		}
		setPropertyId(result.getId());
		return LIST_SUBMIT;
	}
	
	public void setBedrooms(Property property, String bedrooms)
	{
		if( property.getPropertyDetails() != null )
		{
			if( bedrooms != null )
			{
			    int value;
			    if( bedrooms.equals(STUDIO_OPTION) ) value = 0;
			    else value = Integer.parseInt(bedrooms);
			    property.getPropertyDetails().setBedrooms(value);
			}
		}
	}
	
	public String editsubmit()
	{
		String validationResult = validateFields();
		if( !validationResult.equals(SUCCESS) ) return validationResult;
		Property result;
		property.setId(propertyId);
		setBedrooms(property,bedrooms);
		try {
			result = propertyService.editProperty(property,currencyCodeId,locationId,
			    alwaysAvailable,new Float(dailyRate), LoginStatus.getUser().getId(),propertyTypeId);
		} catch (OkaSecurityException e) {
			message = e.getMessage();
			return ERROR;
		} catch (InputValidationException e) {
			addFieldError("property.name",e.getMessage());
			log.error(e);
			return ERROR;
		}
		setPropertyId(result.getId());
		message = "Changes Saved";
		methodName = EDIT;
		property = result;
	    return EDIT_SUBMIT;
	}
	
	public String details()
	{
		return DETAILS;
	}
	
	public List<PropertyType> getPropertyTypes()
	{
		return referenceService.getAllPropertyTypes();
	}
	
	public List<CurrencyCode> getCurrencyCodes()
	{
		return referenceService.getAllCurrencyCodes();
	}
	
	public List<Integer> getSleepsOptions()
	{
		List<Integer> list = new ArrayList<Integer>();
		for( int i=0; i <= Configuration.instance().getIntProperty("MAX_SLEEPS_OPTIONS"); ++i )
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
			if( i == 0 ) list.add(STUDIO_OPTION);
			else list.add(new Integer(i).toString());
		}
		return list;
	}

	public ReferenceService getReferenceService() {
		return referenceService;
	}

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	public Integer getPropertyTypeId() {
		return propertyTypeId;
	}

	public void setPropertyTypeId(Integer propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}

	public Integer getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	public Integer getCurrencyCodeId() {
		return currencyCodeId;
	}

	public void setCurrencyCodeId(Integer currencyCodeId) {
		this.currencyCodeId = currencyCodeId;
	}

	public void setAlwaysAvailable(Boolean alwaysAvailable) {
		this.alwaysAvailable = alwaysAvailable;
	}

	public Integer getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(Integer dailyRate) {
		this.dailyRate = dailyRate;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getAlwaysAvailable() {
		return alwaysAvailable;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public int getPage()
	{
		return 1;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	
	public Map<String,String> getRefundPolicyOptions()
	{
		Map<String,String> options = new HashMap<String,String>();
		options.put(Property.REFUND_POLICY_FLEXIBLE,"Flexible");
		options.put(Property.REFUND_POLICY_STANDARD,"Standard");
		options.put(Property.REFUND_POLICY_STRICT,"Strict");
		return options;
		
	}

	public String getBedrooms() {
		return bedrooms;
	}

	public void setBedrooms(String bedrooms) {
		this.bedrooms = bedrooms;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	public boolean getValidPayPal()
	{
        if( methodName.equals(EDIT) ) return true;
		OkapiedUserDetails user = LoginStatus.getUser();
		if( user == null ) return false;
		else
		{
			try
			{ 
				return accountService.validPayPalAccount(user);
			}
			catch( InputValidationException e)
			{
				return false;
			}
		}
	}
	
	public String getTitle()
	{
	    return "Essential Details";	
	}
	
	public String getDescription()
	{
		return "Provide the essential details of your place which are a requirement for listing";
	}
}
