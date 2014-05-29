package okapied.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ParsedURL 
{
	public static final Logger log = Logger.getLogger(ParsedURL.class);
	
	String url;
	List<String> parameters;
	public static final String DEFAULT_ACTION = "Search";
	public static final String DEFAULT_NAMESPACE = "Rentals";
	public static final String DEFAULT_METHOD = "list";
	public static final String PROPERTY_ACTION = "Property";
	
    public ParsedURL(String url)
    {
    	this.url = url;
    	parse();
    }
    
    public void parse()
    {
    	int indexOfSemiColon = url.indexOf(';');
    	if( indexOfSemiColon > 0 ) url = url.substring(0,indexOfSemiColon); 
    	String[] splitString = url.split("/");
    	parameters = new ArrayList<String>();
    	for( String string : splitString )
    	{
    		parameters.add(string);
    	}
    }
    
    public String getNamespace()
    {
    	if( parameters.size() <= Configuration.instance().getIntProperty("BASE_URL_INDEX") )
    	{
    		return DEFAULT_NAMESPACE;
    	}
    	else
    	{
    		String namespace = parameters.get(Configuration.instance().getIntProperty("BASE_URL_INDEX"));
    		if( Configuration.instance().validNamespace(namespace) ||
    		    Configuration.instance().validStaticNamespace(namespace))
    		{
    			return namespace;
    		}
    		else
    		{
    			return DEFAULT_NAMESPACE;
    		}
    	}
    }
    
    public String getAction()
    {
    	if( parameters.size() <= 1 + Configuration.instance().getIntProperty("BASE_URL_INDEX")) 
    	{
    		return DEFAULT_ACTION;
    	}
    	else
    	{
    		String action = parameters.get(1 + Configuration.instance().getIntProperty("BASE_URL_INDEX"));
    		if( Configuration.instance().validAction(action) )
    		{
    			return action;
    		}
    		else
    		{
    			return DEFAULT_ACTION;
    		}
    	}
    }
    
    public String getParameter(int index)
    {
    	if( parameters.size() <= index + Configuration.instance().getIntProperty("BASE_URL_INDEX")) 
    	{
    		return null;
    	}
    	else
    	{
    		return parameters.get(index + Configuration.instance().getIntProperty("BASE_URL_INDEX"));
    	}
    }
    
    public String getMethod()
    {
		String method = parameters.get(parameters.size()-1);
		if( Configuration.instance().validMethod(method) )
		{
			return method;
		}
		else
		{
			return DEFAULT_METHOD;
		}
    }
    
    public String getPropertyName()
    {
    	int propertyIndex = Configuration.instance().getIntProperty("BASE_URL_INDEX")+
		    Configuration.instance().getIntProperty("PROPERTY_NAME_POSITION");
    	String propertyName = null;
    	if( parameters.size() == (propertyIndex+1) )
    	{
    	    try {
				propertyName = URLDecoder.decode(parameters.get(propertyIndex),
						Configuration.instance().getStringProperty("ENCODING"));
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage());
				return null;
			}	
    	}
    	if( propertyName == null ) return null;
    	propertyName = propertyName.trim();
    	if( propertyName.length() <= 0 ) return null;
    	if( propertyName.charAt(0) == '?' || propertyName.charAt(0) == '#' ) return null;
    	return propertyName;
    }

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
