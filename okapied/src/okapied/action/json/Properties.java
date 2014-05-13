package okapied.action.json;

import java.util.List;

import okapied.entity.Property;
import okapied.service.PropertyService;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;

public class Properties 
{
    Integer ownerId;
    private String LIST = "list";
    private String ERROR = "error";
    PropertyService propertyService;
    List<Property> properties;
    Integer limit;
    Integer offset;
    Long size;
    
    public void nullServices()
    {
    	propertyService = null;
    }
    
    private static final Logger log = Logger.getLogger(Properties.class);
   
    public String list()
    {
    	if( !LoginStatus.isUser(ownerId) )
    	{
    		log.error("Invalid user trying to access owner properties of user: " + ownerId);
    		nullServices();
    		return ERROR;
    	}
    	properties = propertyService.getOwnerProperties(ownerId,limit,offset);
    	size = propertyService.getTotalProperties(ownerId);
    	nullServices();
	    return LIST;
    }

	public Integer getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(Integer userId) {
		this.ownerId = userId;
	}

	public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
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
   
}
