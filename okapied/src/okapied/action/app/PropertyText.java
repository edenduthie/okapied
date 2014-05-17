package okapied.action.app;

import okapied.entity.Property;
import okapied.exception.OkaSecurityException;
import okapied.web.LoginStatus;

public class PropertyText extends ListBaseAction implements PageAction
{   
    public int getPage()
    {
    	return 3;
    }
    
    public String listsubmit()
    {
    	System.out.println("Saving description: " + property.getPropertyDetails().getDescription());
		Property result;
		property.setId(propertyId);
		try {
			result = propertyService.savePropertyDescription(property,
			    LoginStatus.getUser().getId());
			setPropertyId(result.getId());
			message = "Changes Saved";
		} 
		catch (OkaSecurityException e) 
		{
			message = e.getMessage();
			return ERROR;
		}
		finally
		{
		    try {
				loadProperty();
			} catch (OkaSecurityException e) {
				log.error(e.getMessage());
			}
		}
    	return LIST_SUBMIT;
    }
    
    public String getTitle()
    {
    	return "Description and Check-In Instructions";
    }
    
    public String getDescription()
    {
        return "Edit the detailed description of your place and the instructions which are show to guests after a booking has been made.";	
    }
}
