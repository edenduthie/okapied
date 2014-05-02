package okapied.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import okapied.entity.FeaturedProperty;
import okapied.entity.PhotoGallery;
import okapied.entity.Property;
import okapied.entity.Region;
import okapied.service.PhotoService;
import okapied.service.PropertyService;
import okapied.util.Configuration;
import okapied.util.DateUtil;

import org.apache.log4j.Logger;


public class Search extends BaseAction
{
	private static final Logger log = Logger.getLogger(Search.class);

	Region region;

	List<Property> properties;

	PropertyService propertyService;
	PhotoService photoService;
	
	List<Calendar> period;
	List<String> periodDisplay;
	List<Boolean> weekends;
	Integer displayDays;
	Calendar startDay;
	Integer limit;
	Integer offset;
	Integer firstPage = 1;
	Long size;
	
	String propertyListDateFormat;
	
	int maxString = 30;
	
	PhotoGallery photoGallery;
	
	Long time;
	String date;
	
	List<Property> latestListings;
	
	FeaturedProperty featuredProperty;
	
	public Search()
	{
		displayDays = Configuration.instance().getIntProperty("DISPLAY_DAYS");
		propertyListDateFormat = Configuration.instance().getStringProperty("PROPERTY_LIST_DATE_FORMAT");
	}

	public String list()
    {
		preload();
		if( !getMainPage() && countryName != null )
		{
			if( limit == null || limit == 0 ) limit = Configuration.instance().getIntProperty("DEFAULT_NUM_SEARCH_RESULTS");
			if( offset == null ) offset = 0;
		    properties = propertyService.search(countryName,regionName,locationName,offset,limit);
		    size = propertyService.searchSize(countryName,regionName,locationName);
		    log.info("Loaded " + properties.size() + " properties");
		    prepareDisplay();
		}
    	return LIST;
    }
	
	public void prepareDisplay()
	{
		if( properties == null || properties.size() <= 0 )
		{
			return;
		}
		startDay = propertyService.getCurrentDay(properties.get(0));
		if( time != null )
		{
			startDay.setTimeInMillis(time);
			startDay.add(Calendar.DAY_OF_YEAR, 1);
		}
		else if( date != null )
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setTimeZone(startDay.getTimeZone());
			try
			{
				System.out.println("Parsing date: " + date);
			    Date dateTime = sdf.parse(date);
			    startDay.setTime(dateTime);
			    DateUtil.clearTime(startDay);
			    System.out.println("Resetting display: " + startDay.getTime());
			}
			catch( ParseException e )
			{
				log.error(e.getMessage());
			}
		}
		Calendar currentDay = startDay;
		period = new ArrayList<Calendar>();
		periodDisplay = new ArrayList<String>();
		weekends = new ArrayList<Boolean>();
		for( int i=0; i < displayDays; ++i )
		{
			Calendar copy = new GregorianCalendar(startDay.getTimeZone());
			copy.setTime(currentDay.getTime());
			period.add(copy);
			SimpleDateFormat sdf = new SimpleDateFormat(propertyListDateFormat);
			periodDisplay.add(sdf.format(currentDay.getTime()));
			if( DateUtil.weekend(currentDay) ) weekends.add(true);
			else weekends.add(false);
			currentDay.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		List<LoadAvailabilityThread> threads = new ArrayList<LoadAvailabilityThread>();
		
		for( Property property : properties )
		{
			LoadAvailabilityThread thread = new LoadAvailabilityThread(property, period, propertyService);
			threads.add(thread);
			thread.start();
		    //propertyService.availability(property,period);
		}
		
		try 
		{
			for( Thread thread : threads )
			{
	            thread.join();
			}
		} 
		catch (InterruptedException e) 
		{
			log.error(e.getMessage());
		}
	}
	
	public class LoadAvailabilityThread extends Thread
	{
		Property property;
		List<Calendar> period;
		PropertyService propertyService;
		
		public LoadAvailabilityThread(Property property, List<Calendar> period, 
		    PropertyService propertyService )
		{
			this.property = property;
			this.period = period;
			this.propertyService = propertyService;
		}
		
		public void run() 
		{
			propertyService.availability(property,period);
	    }
	}
    
    public String execute()
    {
    	return "SUCCESS";
    }

	@Override
	public void setServletRequest(HttpServletRequest request) 
	{
		this.request = request;
	}
	
    public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}
	
//	public Country getCountry() {
//		return country;
//	}
//
//	public void setCountry(Country country) {
//		this.country = country;
//	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
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

	public List<String> getPeriodDisplay() {
		return periodDisplay;
	}

	public void setPeriodDisplay(List<String> periodDisplay) {
		this.periodDisplay = periodDisplay;
	}
	
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public int getMaxString() {
		return maxString;
	}

	public void setMaxString(int maxString) {
		this.maxString = maxString;
	}

	public List<Calendar> getPeriod() {
		return period;
	}

	public void setPeriod(List<Calendar> period) {
		this.period = period;
	}

	public List<Boolean> getWeekends() {
		return weekends;
	}

	public void setWeekends(List<Boolean> weekends) {
		this.weekends = weekends;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
	
	public boolean getLastPage()
	{
		if( (offset + limit) >= getSize() ) return true;
		else return false;
	}

	public PhotoGallery getPhotoGallery() {
		if( photoGallery == null )
		{
			photoGallery = photoService.getGallery(Configuration.instance().getStringProperty("GALLERY_NAME"));
		}
		return photoGallery;
	}

	public void setPhotoGallery(PhotoGallery photoGallery) {
		this.photoGallery = photoGallery;
	}

	public PhotoService getPhotoService() {
		return photoService;
	}

	public void setPhotoService(PhotoService photoService) {
		this.photoService = photoService;
	}

	public Integer getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(Integer firstPage) {
		this.firstPage = firstPage;
	}

	public FeaturedProperty getFeaturedProperty() 
	{
		if( featuredProperty == null )
		{
			featuredProperty = propertyService.getFeaturedProperty();
		}
		return featuredProperty;
	}

	public List<Property> getLatestListings() {
		if( latestListings == null )
		{
			latestListings = propertyService.getLatestListings();
		}
		return latestListings;
	}

	public void setLatestListings(List<Property> latestListings) {
		this.latestListings = latestListings;
	}
}
