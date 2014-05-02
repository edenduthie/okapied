package okapied.action;

import java.util.List;

import okapied.action.app.Publish;
import okapied.entity.Feedback;
import okapied.entity.Property;
import okapied.service.FeedbackService;
import okapied.service.PropertyService;
import okapied.util.ParsedURL;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;
import org.springframework.web.util.HtmlUtils;

public class PropertyAction extends BaseAction 
{
	private static final Logger log = Logger.getLogger(PropertyAction.class);
	
	Property property;
	PropertyService propertyService;
	String propertyName;
	FeedbackService feedbackService;
	
	public String list()
    {
		preload();
		ParsedURL url = new ParsedURL(request.getRequestURL().toString());
	    propertyName = url.getPropertyName();
		if( propertyName != null )
		{
			property = propertyService.search(countryName, regionName, locationName, propertyName);
		}
    	return LIST;
    }
	
	public List<Feedback> getLatestFeedback()
	{
		List<Feedback> latestFeedback = feedbackService.getFeedback(property.getId(),null,2,0);
		return latestFeedback;
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

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
	}

	public void setFeedbackService(FeedbackService feedbackService) {
		this.feedbackService = feedbackService;
	}
	
	public String getPropertyDescription()
	{
		String description = property.getPropertyDetails().getDescription();
		description = HtmlUtils.htmlEscape(description);
		description = description.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
		return description;
	}
	
	public String getBedrooms()
	{
		if( property != null )
		{
			if( property.getPropertyDetails() != null && property.getPropertyDetails().getBedrooms() != null )
			{
				if( property.getPropertyDetails().getBedrooms() == 0 ) return Publish.STUDIO_OPTION;
				else return new Integer(property.getPropertyDetails().getBedrooms()).toString();
			}
		}
		return null;
	}
	
	public boolean getOwnerOfProperty()
	{
		if( property.getOwner().getId().equals(LoginStatus.getUser().getId()) ) return true;
		else return false;
	}
	
	public String getDescription()
	{
		return property.getPropertyDetails().getFullName();
	}
}
