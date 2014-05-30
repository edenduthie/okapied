package okapied.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

import com.opensymphony.xwork2.config.ConfigurationManager;

public class CustomActionMapper extends DefaultActionMapper {
	
	public static final String PARSED_URL = "parsedURL";
	public static final String PARAMETER_MAP = "parameterMap";
	public static final Logger log = Logger.getLogger(CustomActionMapper.class);
	public static Properties namespaceActionMap;

	@Override
	public ActionMapping getMapping(HttpServletRequest request,
			ConfigurationManager configurationManager) {
//        Map<String, ActionConfig> actions =
//            configurationManager.getConfiguration().
//               getPackageConfig("sync-facade").getActionConfigs();
		
		try 
		{
			namespaceActionMap = new Properties();
			namespaceActionMap.load(this.getClass().getClassLoader().getResourceAsStream("namespaceActions.properties"));
		} 
		catch (IOException e) 
		{
			log.error(e.getMessage());
		}
		log.debug("Input url: " + request.getRequestURL().toString());
        ParsedURL url = new ParsedURL(request.getRequestURL().toString());
        ActionMapping mapping = new ActionMapping();
        if( url.getNamespace() != null ) 
        {
        	if( Configuration.instance().validStaticNamespace(url.getNamespace()))
        	{
        		log.debug("Static namespace");
        		return null;
        	}
        	mapping.setNamespace("/"+url.getNamespace());
        	log.debug("Namespace: " + "/"+url.getNamespace());
        }
        String defaultAction = namespaceActionMap.getProperty(url.getNamespace());
        log.debug("Default action: " + defaultAction);
        if( defaultAction == null )
        {
        	defaultAction = url.getAction();
        	log.debug("Default action was null setting to: " + defaultAction);
        }
        if( defaultAction == namespaceActionMap.getProperty("Rentals") )
        {
        	log.debug("Default aciton is rentals");
        	if( url.getPropertyName() != null )
        	{
        		defaultAction = Configuration.instance().getStringProperty("PROPERTY_ACTION");
        		log.debug("Proprerty name is not null: " + defaultAction);
        	}
        }
       	mapping.setName(defaultAction + "_" + url.getMethod());
       	log.debug("Mapping name: " + mapping.getName());
        // pass the parsed url and parameters to the action
        Map<String,Object> params = new HashMap<String,Object>();
        params.put(PARSED_URL, url);
        Map<String,String[]> parameterMap = request.getParameterMap();
        //System.out.println("Looking for countryId");
        //System.out.println(parameterMap.get("countryId"));
        params.put(PARAMETER_MAP,parameterMap);
        mapping.setParams(params);
        return mapping;
	}
/*
	@Override
	public ActionMapping getMappingFromActionName(String actionName) 
	{
		return super.getMappingFromActionName(actionName);
System.out.println("Get mapping from action name:" + actionName);
		ActionMapping mapping = new ActionMapping();
		mapping.setName("Header_list");
		mapping.setNamespace("/Components");
		mapping.setMethod("list");
System.out.println("Name:"+mapping.getName());
System.out.println("Namespace:"+mapping.getNamespace());
		return mapping;
	}
	*/

	@Override
	public String getUriFromActionMapping(ActionMapping arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
