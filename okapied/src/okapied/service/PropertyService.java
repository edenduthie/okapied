package okapied.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Availability;
import okapied.entity.CurrencyCode;
import okapied.entity.FeaturedProperty;
import okapied.entity.Flooring;
import okapied.entity.Location;
import okapied.entity.Matchable;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Photo;
import okapied.entity.Price;
import okapied.entity.PriceInterface;
import okapied.entity.Property;
import okapied.entity.PropertyDay;
import okapied.entity.PropertyDetails;
import okapied.entity.PropertyType;
import okapied.exception.InputValidationException;
import okapied.exception.InvalidInputException;
import okapied.exception.OkaSecurityException;
import okapied.exception.PriceException;
import okapied.util.Configuration;
import okapied.util.DateUtil;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PropertyService 
{
	
	DAO dao;
	AvailabilityService availabilityService;
	PriceService priceService;
	PhotoService photoService;
	AccountService accountService;
	Integer LIMIT = Configuration.instance().getIntProperty("MAX_PROPERTY_SEARCH_RESULTS");
	
	public static final Logger log = Logger.getLogger(PropertyService.class);
	
    public List<Property> search(String countryName, String regionName, String locationName, Integer offset,
        Integer limit)
    {
    	log.debug("Search: " + countryName + " " + regionName + " " + locationName + " " + limit + " " + offset);
		String queryString = "from Property where " +
		    " location.country.name=:countryName ";
		if( regionName != null && regionName.trim().length() <=0 ) regionName = null;
		if( regionName != null )
		{
		    queryString += " and location.region.name=:regionName ";
		}
		if( locationName != null )
		{
		    queryString += " and location.name=:locationName";
		}
	    Query query = dao.getEntityManager().createQuery(queryString);
	    if( limit > LIMIT ) limit = LIMIT;
	    query.setMaxResults(limit);
	    query.setFirstResult(offset);
	    query.setParameter("countryName", countryName);
	    if( regionName != null ) query.setParameter("regionName", regionName);
	    if( locationName != null ) query.setParameter("locationName", locationName);
	    return query.getResultList();
    }
    
    public Long searchSize(String countryName, String regionName, String locationName)
        {
        	log.debug("Size: " + countryName + " " + regionName + " " + locationName);
    		String queryString = "select count(id) from Property where " +
    		    " location.country.name=:countryName ";
    		if( regionName != null )
    		{
    		    queryString += " and location.region.name=:regionName ";
    		}
    		if( locationName != null )
    		{
    		    queryString += " and location.name=:locationName";
    		}
    	    Query query = dao.getEntityManager().createQuery(queryString);
    	    query.setParameter("countryName", countryName);
    	    if( regionName != null ) query.setParameter("regionName", regionName);
    	    if( locationName != null ) query.setParameter("locationName", locationName);
    	    return (Long) query.getSingleResult();
        }
    
    public Property search(String countryName, String regionName, String locationName, String propertyName)
    {
    	log.debug("Search: " + countryName + " " + regionName + " " + locationName + " " + propertyName);
    	if( regionName != null && regionName.trim().length() <= 0 ) regionName = null;
		String queryString = "from Property where " +
		    " location.country.name=:countryName ";
		if( regionName != null )
		{
		    queryString += " and location.region.name=:regionName ";
		}
		queryString += " and location.name=:locationName";
		queryString += " and name=:propertyName";
	    Query query = dao.getEntityManager().createQuery(queryString);
	    query.setMaxResults(LIMIT);
	    query.setParameter("countryName", countryName);
	    if( regionName != null ) query.setParameter("regionName", regionName);
	    query.setParameter("locationName", locationName);
	    query.setParameter("propertyName",propertyName);
	    List list =  query.getResultList();
	    return (Property) list.get(0);
	    //if( list.size() > 0 ) return (Property) list.get(0);
	    //else return null;
    }
    
    public void availability(Property property, List<Calendar> period)
    {
    	availability(property, period, null);
    }
    
    public void availability(Property property, List<Calendar> period, 
    		OkapiedUserDetails user)
    {
        availability(property, period,user,false);
    }
    /**
     * Sets the infoList of the given property to its availability for
     * each day of the given period. If user is not null, all
     * bookings that are reservations (reservation=1) and have been created by the current
     * user do not count towards booking out the property.
     * @param property
     * @param period
     * @param skipMyReservations
     */
    public void availability(Property property, List<Calendar> period, 
    		OkapiedUserDetails user, boolean detailed)
    {
    	List<PropertyDay> infoList = new ArrayList<PropertyDay>();
        List<Matchable> avs;
        if( user != null && !detailed ) avs = availabilityService.getSkipCurrentUser(property, period,user);
        else avs = availabilityService.get(property, period);
        List<PriceInterface> priceList = priceService.get(property, period);
        for( Calendar day : period )
        {
        	PropertyDay propertyDay = new PropertyDay();
        	// check to see if booking is not in the past or too far in the future
        	if( availabilityService.isWithinValidPeriod(day,property,propertyDay,detailed) )
        	{
        		propertyDay.setOutOfPeriod(false);
	        	availabilityService.available(avs, day, propertyDay, detailed);
	        	propertyDay.price = priceService.price(priceList, day).getPrice();
	        	if( DateUtil.weekend(day) )
	        	{
	        		propertyDay.weekend = true;
	        	}
	        	if( propertyDay.price == null ) 
	        	{
	        		propertyDay.setAvailable(false);
	        	}
        	}
        	else
        	{
        		propertyDay.setOutOfPeriod(true);
        	}
        	infoList.add(propertyDay);
        }
        property.setInfoList(infoList);
    }
    
    public void availabilityMonth(Property property, Integer month, Integer year, OkapiedUserDetails user)
    {
    	availabilityMonth(property, month, year, user, false);
    }
    
    /**
     * @param month - from 0 to 11
     */
    public void availabilityMonth(Property property, Integer month, Integer year, OkapiedUserDetails user,
    		boolean lastDayOfPreviousMonth)
    {	
    	Calendar startDate = getCurrentDay(property);
    	startDate.set(Calendar.MONTH,month);
    	startDate.set(Calendar.YEAR, year);
    	startDate.set(Calendar.DAY_OF_MONTH,1);
    	
    	List<Calendar> period = new ArrayList<Calendar>();
    	if( lastDayOfPreviousMonth )
    	{
    		Calendar lastDay = getCurrentDay(property);
    		lastDay.set(Calendar.MONTH,month);
    		lastDay.set(Calendar.YEAR, year);
    		lastDay.set(Calendar.DAY_OF_MONTH,1);
    		lastDay.add(Calendar.DAY_OF_YEAR,-1);
    		period.add(lastDay);
    	}
    	for( int i=0; i < startDate.getActualMaximum(Calendar.DAY_OF_MONTH); ++i )
    	{
    	    Calendar date = getCurrentDay(property);
    	    date.setTime(startDate.getTime());
    	    date.add(Calendar.DAY_OF_MONTH,i);
    	    period.add(date);
    	}
    	availability(property,period,user);
    	if( lastDayOfPreviousMonth ) property.getInfoList().get(0).setExtraDay(true);
    }
    
    public void availabilityMonths(Property property, Integer month, Integer year, Integer numMonths,
            OkapiedUserDetails user)
    {
    	availabilityMonths(property,month,year,numMonths,user,false);
    }
    
    public void availabilityMonths(Property property, Integer month, Integer year, Integer numMonths,
        OkapiedUserDetails user, boolean detailed)
    {
    	Calendar startDate = getCurrentDay(property);
    	startDate.set(Calendar.MONTH,month);
    	startDate.set(Calendar.YEAR, year);
    	startDate.set(Calendar.DAY_OF_MONTH,1);
    	
    	Calendar endDate = getCurrentDay(property);
    	endDate.set(Calendar.MONTH,month+numMonths-1);
    	endDate.set(Calendar.YEAR, year);
    	endDate.set(Calendar.DAY_OF_MONTH,endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
    	
    	List<Calendar> period = new ArrayList<Calendar>();
    	boolean keepGoing = true;
    	for( int i=0; keepGoing; ++i )
    	{
    	    Calendar date = getCurrentDay(property);
    	    date.setTime(startDate.getTime());
    	    date.add(Calendar.DAY_OF_YEAR,i);
    	    if( date.getTimeInMillis() > endDate.getTimeInMillis() )
    	    {
    	    	keepGoing = false;
    	    }
    	    else
    	    {
    	        period.add(date);
    	    }
    	}
    	availability(property,period,user,detailed);
    }
    
    public Calendar getCurrentDay(Property property)
    {
    	return property.retrieveCurrentDay();
    }
    
    public Property getWithPhotos(Integer propertyId)
    {
    	String queryString = "from Property p INNER JOIN FETCH p.photos" +
    	    " WHERE p.id=:propertyId";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("propertyId",propertyId);
    	Property result = (Property) query.getSingleResult();
    	for(Photo photo : result.getPhotos() )
    	{
    		byte[] picture = photo.getPicture(); // trigger lazy loasding
    	}
    	return result;
    }
    
    public Property getWithDetails(Integer propertyId)
    {
    	String queryString = "from Property p INNER JOIN FETCH p.propertyDetails" +
    	    " WHERE p.id=:propertyId";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("propertyId",propertyId);
    	return (Property) query.getSingleResult();
    }
    
    public Property get(Integer propertyId)
    {
    	return (Property) dao.get(Property.class.getName(),"id", propertyId);
    }
    
    public Calendar getDate(String dateString, Property property)
    {
    	String yearString = dateString.substring(0,4);
    	String dayOfMonthString = dateString.substring(4,6);
    	String monthString = dateString.substring(6,8);
    	Calendar date = getCurrentDay(property);
    	date.set(Calendar.YEAR, new Integer(yearString) );
    	date.set(Calendar.MONTH, new Integer(monthString) - 1);
    	date.set(Calendar.DAY_OF_MONTH, new Integer(dayOfMonthString));
    	return date;
    }
    
    /**
     * Returns all properties that are owned by the given user, ordered by most recent first
     * @param ownerId
     * @return
     */
    public List<Property> getOwnerProperties(Integer ownerId, Integer limit, Integer offset)
    {
    	String queryString = "select new Property(id,name) from Property where owner.id=:ownerId order by id desc";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("ownerId",ownerId);
    	if( limit != null ) query.setMaxResults(limit);
    	if( offset != null ) query.setFirstResult(offset);
    	List<Property> properties = query.getResultList();
    	return properties;
    }
    
    public long getTotalProperties(Integer ownerId)
    {
    	String queryString = "select count(id) from Property where owner.id=:ownerId order by id desc";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("ownerId",ownerId);
    	return (Long) query.getSingleResult();
    }
    
    @Transactional(rollbackFor={InputValidationException.class})
    public Property saveProperty(Property property,Integer currencyCodeId,
        Integer locationId,Boolean alwaysAvailable,Float dailyRate,Integer ownerId,Integer propertyTypeId)
        throws InputValidationException
    {
    	return saveProperty(property, currencyCodeId, locationId, alwaysAvailable, dailyRate, ownerId,
    			propertyTypeId,false);
    }
    
    @Transactional(rollbackFor={InputValidationException.class})
    public Property saveProperty(Property property,Integer currencyCodeId,
        Integer locationId,Boolean alwaysAvailable,Float dailyRate,Integer ownerId,Integer propertyTypeId,
        boolean checkPayPalAccount)
        throws InputValidationException
    {
    	if( nameExists(property.getName(), locationId)) 
    		throw new InputValidationException("The name " + property.getName() + " is already taken");
    	
    	if(checkPayPalAccount)
        {
    	    if(!accountService.validPayPalAccount(LoginStatus.getUser()))
	    	{
	    		throw new InputValidationException("The first name, last name, and email of your account do not correspond to a valid PayPal account." +
	    	        " Your supplied email and name must correspond to a valid PalPal account to list a property on Okapied.");
	    	}
        }
        
        CurrencyCode currencyCode = (CurrencyCode) dao.get(CurrencyCode.class.getName(),"id",currencyCodeId);
        property.setCurrencyCode(currencyCode);
        Location location = (Location) dao.get(Location.class.getName(), "id",locationId);
        location.addAProperty();
        dao.update(location);
        property.setLocation(location);
        OkapiedUserDetails owner = 
            (OkapiedUserDetails) dao.get(OkapiedUserDetails.class.getName(),"id",ownerId);
        property.setOwner(owner);
        
        property.setDateListed(DateUtil.getCurrentCalendarCompareTZ().getTime());

        dao.persist(property);
        
        if( alwaysAvailable != null && alwaysAvailable )
        {
        	property.setDefaultAvailability(createDefaultAvailability(property));
        	dao.persist(property.getDefaultAvailability());
        	dao.update(property);
        }
    	if( dailyRate != null )
    	{
    	    property.setDefaultPrice(createDefaultPrice(property,dailyRate));
    	    dao.persist(property.getDefaultPrice());
    	    dao.update(property);
    	}
        if( propertyTypeId != null )
        {
            PropertyType propertyType = (PropertyType) dao.get(PropertyType.class.getName(),"id",propertyTypeId);
            PropertyDetails propertyDetails = property.getPropertyDetails();
            if( propertyDetails == null )
            {
            	propertyDetails = new PropertyDetails();
            	dao.persist(propertyDetails);
            }
            propertyDetails.setType(propertyType);
            dao.update(propertyDetails);
            property.setPropertyDetails(propertyDetails);
            dao.update(property);
        }
        
        return property;
    }
    
    public Availability createDefaultAvailability(Property property)
    {
    	Availability availability = new Availability();
    	availability.setAvailableBool(true);
    	availability.setDayOnlyBool(false);
    	availability.setProperty(property);
    	Calendar start = property.retrieveCurrentDay();
    	availability.setStartDate(start.getTime());
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR, Configuration.instance().getIntProperty("FORWARD_AVAILABILITY_WEEKS")*7);
    	availability.setEndDate(end.getTime());
    	return availability;
    }
    
    public Price createDefaultPrice(Property property, Float priceFloat)
    {
    	Price price = new Price();
    	price.setAvailableBool(true);
    	price.setDayOnlyBool(false);
    	price.setProperty(property);
    	price.setPrice(priceFloat);
    	Calendar start = property.retrieveCurrentDay();
    	price.setStartDate(start.getTime());
    	Calendar end = property.retrieveCurrentDay();
    	end.setTime(start.getTime());
    	end.add(Calendar.DAY_OF_YEAR, Configuration.instance().getIntProperty("FORWARD_AVAILABILITY_WEEKS")*7);
    	price.setEndDate(end.getTime());
    	return price;
    }
    
    @Transactional(rollbackFor={OkaSecurityException.class,InputValidationException.class})
    public Property editProperty(Property propertyChanges,Integer currencyCodeId,
            Integer locationId,Boolean alwaysAvailable,Float dailyRate,Integer ownerId,Integer propertyTypeId)
        throws OkaSecurityException, InputValidationException
    {
    	Property property = getWithDetails(propertyChanges.getId());
    	
    	if( propertyChanges.getName() != null && !propertyChanges.getName().equals(property.getName()) )
    	{
	    	Integer searchLocationId = locationId;
	    	if( searchLocationId == null ) searchLocationId = property.getLocation().getId();
	    	if( nameExists(propertyChanges.getName(), searchLocationId) )
	    		throw new InputValidationException("The name " + propertyChanges.getName() + " is already taken");
    	}
    	
    	if( propertyChanges.getName() != null ) property.setName(propertyChanges.getName());
    	if( propertyChanges.getRefundPolicy() != null ) property.setRefundPolicy(propertyChanges.getRefundPolicy());
    	if( propertyChanges.getPropertyDetails() != null )
    	{
    		PropertyDetails changesPD = propertyChanges.getPropertyDetails();
    		PropertyDetails pd = property.getPropertyDetails();
    		if( pd != null )
    		{
    			if( changesPD.getFullName() != null ) pd.setFullName(changesPD.getFullName());
    			if( changesPD.getSleeps() != null ) pd.setSleeps(changesPD.getSleeps());
    			if( changesPD.getBedrooms() != null ) pd.setBedrooms(changesPD.getBedrooms());
    			if( changesPD.getUnit() != null ) pd.setUnit(changesPD.getUnit());
    			if( changesPD.getStreetNumber() != null ) pd.setStreetNumber(changesPD.getStreetNumber());
    			if( changesPD.getStreet() != null ) pd.setStreet(changesPD.getStreet());
    			if( changesPD.getPostcode() != null ) pd.setPostcode(changesPD.getPostcode());
    		}
    	}
    	
    	if( !property.getOwner().getId().equals(ownerId) )
    	{
    		log.error("Attempted to edit property that user does not own, user id: " + ownerId);
    		throw new OkaSecurityException("You must be the owner of a property to edit it");
    	}
        
    	if( currencyCodeId != null )
    	{
            CurrencyCode currencyCode = (CurrencyCode) dao.get(CurrencyCode.class.getName(),"id",currencyCodeId);
            property.setCurrencyCode(currencyCode);
    	}
    	if( locationId != null )
    	{
            Location location = (Location) dao.get(Location.class.getName(), "id",locationId);
            property.getLocation().removeAProperty();
            dao.update(property.getLocation());
            location.addAProperty();
            dao.update(location);
            property.setLocation(location);
    	}
    	
    	
    	if( alwaysAvailable != null )
    	{
    		if( alwaysAvailable )
    		{
    			if( property.getDefaultAvailability() == null )
    			{
    				Availability av = createDefaultAvailability(property);
    				dao.persist(av);
    				property.setDefaultAvailability(av);
    			}
    			else
    			{
    				property.getDefaultAvailability().setAvailableBool(true);
    			}
    		}
    		else
    		{
    			Availability av = property.getDefaultAvailability();
    			if( av != null )
    			{
    			    property.setDefaultAvailability(null);
    			    dao.update(property);
    			    dao.delete(Availability.class.getName(),"id",av.getId());
    			}
    		}
    	}
    	if( dailyRate != null )
    	{
    		if( property.getDefaultPrice() == null )
    		{
    			Price price = createDefaultPrice(property, dailyRate);
    			dao.persist(price);
    			property.setDefaultPrice(price);
    		}
    		else
    		{
    			property.getDefaultPrice().setPrice(dailyRate);
    		}
    	}
    	
        if( propertyTypeId != null )
        {
            PropertyType propertyType = (PropertyType) dao.get(PropertyType.class.getName(),"id",propertyTypeId);
            if( property.getPropertyDetails() != null )
            	property.getPropertyDetails().setType(propertyType);
        }
        if( propertyChanges.getMinNights() != null )
        {
        	property.setMinNights(propertyChanges.getMinNights());
        }

        dao.update(property);
        
        return property;
    }
    
    @Transactional(rollbackFor={OkaSecurityException.class})
    public Property savePropertyDetails(Property propertyChanges,Integer flooringId, Integer ownerId,
        String kitchenOptions, String outdoorOptions, String bathroomOptions, String amOptions, String multiOptions)
        throws OkaSecurityException
    {
    	Property property = getWithDetails(propertyChanges.getId());
    	
    	if( !property.getOwner().getId().equals(ownerId) )
    	{
    		log.error("Attempted to edit property that user does not own, user id: " + ownerId);
    		throw new OkaSecurityException("You must be the owner of a property to edit it");
    	}
    	
    	if( property.getPropertyDetails() != null )
    	{
    		PropertyDetails changePD = propertyChanges.getPropertyDetails();
    		PropertyDetails pd = property.getPropertyDetails();
	    	if( flooringId != null )
	    	{
	    		Flooring flooring = (Flooring) dao.get(Flooring.class.getName(),"id", flooringId);
	    		pd.setFlooring(flooring);
	    	}
    		if( changePD != null )
    		{
    			pd.setDistanceToBeachMeters(pd.getDistanceToBeachMeters());
		    	if( changePD.getBedrooms() != null ) pd.setBedrooms(changePD.getBedrooms());
		    	if( changePD.getBathrooms() != null ) pd.setBathrooms(changePD.getBathrooms());
		    	if( changePD.getKingBeds() != null ) pd.setKingBeds(changePD.getKingBeds());
		    	if( changePD.getQueenBeds() != null ) pd.setQueenBeds(changePD.getQueenBeds());
		    	if( changePD.getDoubleBeds() != null ) pd.setDoubleBeds(changePD.getDoubleBeds());
		    	if( changePD.getSingleBeds() != null ) pd.setSingleBeds(changePD.getSingleBeds());
		    	if( changePD.getDistanceToBeachMeters() != null ) pd.setDistanceToBeachMeters(changePD.getDistanceToBeachMeters());
		    	if( changePD.getSleeps() != null ) pd.setSleeps(changePD.getSleeps());
    		}
	    	
	    	if( kitchenOptions != null ) pd.setKitchen(csvLineToSet(kitchenOptions));
	    	if( outdoorOptions != null ) pd.setOutdoor(csvLineToSet(outdoorOptions));
	    	if( bathroomOptions != null ) pd.setBathroomLaundry(csvLineToSet(bathroomOptions));
	    	if( amOptions != null ) pd.setAmmenities(csvLineToSet(amOptions));
	    	if( multiOptions != null ) pd.setMultimedia(csvLineToSet(multiOptions));
	    	
	    	dao.update(pd);
    	}
    	
        dao.update(property);
        
        return property;
    }
    
    @Transactional(rollbackFor={OkaSecurityException.class})
    public Property savePropertyDescription(Property propertyChanges, Integer ownerId) 
        throws OkaSecurityException
    {
    	Property property = getWithDetails(propertyChanges.getId());
    	if( !property.getOwner().getId().equals(ownerId) )
    	{
    		log.error("Attempted to edit property that user does not own, user id: " + ownerId);
    		throw new OkaSecurityException("You must be the owner of a property to edit it");
    	}
    	if( property.getPropertyDetails() != null )
    	{
    	    PropertyDetails changePD = propertyChanges.getPropertyDetails();
    	    if( changePD != null )
    	    {
    	    	if( changePD.getDescription() != null ) 
    	    		property.getPropertyDetails().setDescription(changePD.getDescription());
    	    	if( changePD.getCheckInInstructions() != null )
    	    		property.getPropertyDetails().setCheckInInstructions(changePD.getCheckInInstructions());
    	    	dao.update(property.getPropertyDetails());
    	    }
    	}
    	return property;
    }
    
    public Set<String> csvLineToSet(String string)
    {
    	String[] splitString = string.split("\\,");
    	Set<String> list = new HashSet<String>();
    	for( String value : splitString ) list.add(value);
    	return list;
    }
    
    /**
     * Returns true if the given property name already exists
     */
    public boolean nameExists(String propertyName, Integer locationId)
    {
    	String queryString = "from Property where name=:propertyName and location.id=:locationId";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("propertyName",propertyName);
    	query.setParameter("locationId",locationId);
    	if( query.getResultList().size() > 0 ) return true;
    	else return false;
    }
    
    public Property savePhotos(Property propertyChanges, List<File> files, List<String> fileNames,
        List<String> contentTypes, Integer userId) throws OkaSecurityException, IOException, InvalidInputException
    {
    	Property property = get(propertyChanges.getId());
    	if( !property.getOwner().getId().equals(userId) )
    	{
    		log.error("Attempted to edit property that user does not own, user id: " + userId);
    		throw new OkaSecurityException("You must be the owner of a property to edit it");
    	}
    	int numPhotos = property.getPhotos().size();
    	int i=0;
    	for( File file : files )
    	{
    		if( numPhotos >= Configuration.instance().getIntProperty("MAX_PHOTOS"))
    		{
    			String message = "Maximum number of photos exceeded: " + Configuration.instance().getIntProperty("MAX_PHOTOS") +
    			    ". Click on 'remove' on a photo to delete it before adding another.";
    			log.warn(message);
    			throw new InvalidInputException(message);
    		}
    		Photo photo = photoService.create(new FileInputStream(file));
    		photo.setName(fileNames.get(i));
    		photoService.resize(photo);
    		photo.setProperty(property);
    		//dao.persist(photo);
    		property.getPhotos().add(photo);
    		++i;
    		++numPhotos;
    	}
    	dao.update(property);
    	return property;
    }
    
    public void removePhoto(Integer propertyId, Integer photoId, Integer userId) 
        throws OkaSecurityException
    {
    	Property property = get(propertyId);
    	if( !property.getOwner().getId().equals(userId) )
    	{
    		log.error("Attempted to remove a photo from a property that user does not own, user id: " + userId);
    		throw new OkaSecurityException("You must be the owner of a property to remove a photo");
    	}
    	photoService.delete(photoId);
    }
    
	public void removeAllPhotos(Integer propertyId, Integer userId)
			throws OkaSecurityException 
	{
		Property property = get(propertyId);
		if (!property.getOwner().getId().equals(userId)) {
			log.error("Attempted to remove a photo from a property that user does not own, user id: "
					+ userId);
			throw new OkaSecurityException(
					"You must be the owner of a property to remove a photo");
		}
		String queryString = "delete from Photo where property.id=:propertyId";
		Query query = dao.getEntityManager().createQuery(queryString).setParameter("propertyId",propertyId);
		query.executeUpdate();
	}
	
	public void clearAndMakeAvailable(Property property, boolean available, Integer userId) throws OkaSecurityException
	{
		if (!property.getOwner().getId().equals(userId)) {
			log.error("Attempted to clear availability a property that user does not own, user id: "
					+ userId);
			throw new OkaSecurityException(
					"You must be the owner of a property to clear the availability");
		}
		
		property.setDefaultAvailability(null);
		dao.update(property);
		
		// delete all availabilities for the property not including price
		availabilityService.deleteAll(property,false);
		availabilityService.deleteAllWeekDay(property,false);
		
		if( available )
		{
			Availability av = createDefaultAvailability(property);
			av.setAvailableBool(true);
			dao.persist(av);
			property.setDefaultAvailability(av);
			dao.update(property);
		}
	}
	
	public void clearAndSetPrice(Property property, Integer userId, Float price) 
	    throws OkaSecurityException, PriceException
	{
		if (!property.getOwner().getId().equals(userId)) {
			log.error("Attempted to clear prices for a property that user does not own, user id: "
					+ userId);
			throw new OkaSecurityException(
					"You must be the owner of a property to clear the prices");
		}
		
		if(price == null)
		{
			if(property.getDefaultPrice() != null) price = property.getDefaultPrice().getPrice();
			else
			{
				log.error("No price provided to clear and set price");
				throw new PriceException("No price provided to clear and set price");
			}
		}
		property.setDefaultPrice(null);
		dao.update(property);
		
		// delete all availabilities for the property not including price
		availabilityService.deleteAll(property,true);
		availabilityService.deleteAllWeekDay(property,true);
		
		Price av = createDefaultPrice(property,price);
	    av.setAvailableBool(true);
		dao.persist(av);
		property.setDefaultPrice(av);
		dao.update(property);
	}
	
	
	public List<Property> getLatestListings()
	{
	    int max = Configuration.instance().getIntProperty("NUMBER_OF_LATEST_LISTINGS");
	    
	    String queryString = "from Property p where size(p.photos) > 0 order by p.id desc";
	    Query query = dao.getEntityManager().createQuery(queryString);
	    query.setMaxResults(max);
	    
	    return query.getResultList();
	}
    
	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public AvailabilityService getAvailabilityService() {
		return availabilityService;
	}

	public void setAvailabilityService(AvailabilityService availabilityService) {
		this.availabilityService = availabilityService;
	}

	public PriceService getPriceService() {
		return priceService;
	}

	public void setPriceService(PriceService priceService) {
		this.priceService = priceService;
	}

	public PhotoService getPhotoService() {
		return photoService;
	}

	public void setPhotoService(PhotoService photoService) {
		this.photoService = photoService;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public FeaturedProperty getFeaturedProperty() 
	{
		List<FeaturedProperty> fps = dao.getAll(FeaturedProperty.class.getName());
		if( fps.size() > 0 ) return fps.get(0);
		else return null;
	}
}
