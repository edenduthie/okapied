package okapied.action;

import okapied.util.Configuration;

public class PrivacyAction extends BaseAction
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
    	return "Okapied Privacy Policy";
    }
}
