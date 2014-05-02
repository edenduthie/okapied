package okapied.action;

import okapied.util.Configuration;

public class ContactAction extends BaseAction
{
    public String list()
    {
    	return LIST;
    }
    
    public String getEmail()
    {
    	return Configuration.instance().getStringProperty("ADMIN_EMAIL");
    }
    
    public String getTitle()
    {
    	return "Contact Okapied";
    }
    
    public String getDescription()
    {
    	return "Provides methods to contact Okapied if you need any assistance.";
    }
}
