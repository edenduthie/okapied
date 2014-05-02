package okapied.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import okapied.entity.Place;
import okapied.util.Configuration;
import okapied.util.ParsedURL;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;


public class BaseAction extends ActionSupport implements ServletRequestAware
{
	private static final Logger log = Logger.getLogger(Search.class);
	HttpServletRequest request;
	public static String LIST = "list";
	
	Integer COUNTRY_INDEX = 0;
	Integer REGION_INDEX = 1;
	Integer LOCATION_INDEX = 2;
	Integer PROPERTY_INDEX = 3;
	
	String countryString;
	String regionString;
	String locationString;
	String propertyString;
	
	String countryName;
	String regionName;
	String locationName;
	String propertyName;
	
	String placeType;
	
	ParsedURL parsedURL;
	
	//Country country;
	
	//CountryService countryService;
	public Boolean mainPage = false;
	
	public void preload()
	{
		try
		{
	    	ParsedURL url = new ParsedURL(request.getRequestURL().toString());
	    	parsedURL = url;
	    	countryString = url.getParameter(COUNTRY_INDEX);
	    	regionString = url.getParameter(REGION_INDEX);
	    	locationString = url.getParameter(LOCATION_INDEX);
	    	propertyString = url.getParameter(PROPERTY_INDEX);
	    	
	    	if( countryString != null ) countryName = URLDecoder.decode(countryString,Configuration.instance().getStringProperty("ENCODING"));
	    	if( regionString != null ) regionName = URLDecoder.decode(regionString,Configuration.instance().getStringProperty("ENCODING"));
            if( locationString != null ) locationName = URLDecoder.decode(locationString,Configuration.instance().getStringProperty("ENCODING"));
            if( propertyString != null ) propertyName = URLDecoder.decode(propertyString,Configuration.instance().getStringProperty("ENCODING"));
            
            if( propertyName != null ) placeType = Place.PROPERTY;
            else if( locationName != null ) placeType = Place.LOCATION;
            else if( countryName != null && regionName != null && locationName == null ) placeType = Place.REGION;
            else if( countryName != null && regionName == null && locationName == null ) placeType = Place.COUNTRY;
		}
		catch( UnsupportedEncodingException e )
		{
			log.error(e.getMessage());
		}
	}
	
	
	public Boolean getMainPage() {
		return mainPage;
	}

	public void setMainPage(Boolean mainPage) {
		this.mainPage = mainPage;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) 
	{
		this.request = request;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

//	public CountryService getCountryService() {
//		return countryService;
//	}
//
//	public void setCountryService(CountryService countryService) {
//		this.countryService = countryService;
//	}


	public String getLocationName() {
		return locationName;
	}


	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}


	public String getPlaceType() {
		return placeType;
	}


	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}
	
	public String getClientIp()
	{
		return request.getRemoteAddr();
	}


	public HttpServletRequest getRequest() {
		return request;
	}
}
