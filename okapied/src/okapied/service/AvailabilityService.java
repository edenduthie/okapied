package okapied.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Availability;
import okapied.entity.AvailabilityWeekDay;
import okapied.entity.Booking;
import okapied.entity.Matchable;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Price;
import okapied.entity.PriceWeekDay;
import okapied.entity.Property;
import okapied.entity.PropertyDay;
import okapied.exception.AlreadyBookedException;
import okapied.exception.OkaSecurityException;
import okapied.util.Configuration;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AvailabilityService 
{
	DAO dao;
	BookingEntityService bookingEntityService;
	
	public static int LARGEST_PRECEDENCE = 4;
	
	private static final Logger log = Logger.getLogger(AvailabilityService.class);
	
	public List<Matchable> get(Property property, List<Calendar> period)
	{
    	List<Matchable> matchables = getAvailability(property,period);
		matchables.addAll(getAvailabilityWeekDay(property));	
		matchables.addAll(getBookings(property, period));
		log.debug("Loaded " + matchables.size() + " matchables for property " + property.getId());
		return matchables;
	}
	
	public List<Matchable> getSkipCurrentUser(Property property, List<Calendar> period, OkapiedUserDetails user)
	{
    	List<Matchable> matchables = getAvailability(property,period);
		matchables.addAll(getAvailabilityWeekDay(property));	
		matchables.addAll(getBookingsSkipCurrentUser(property, period,user));
		log.debug("Loaded " + matchables.size() + " matchables for property " + property.getId());
		return matchables;
	}
	
	public List<Matchable> getAvailability(Property property, List<Calendar> period)
	{
    	String queryString = "from Availability a where " +
	    " endDate >= ?1 and startDate <= ?2 " +
	    " and property.id = ?3";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter(1,period.get(0).getTime());
		query.setParameter(2,period.get(period.size()-1).getTime());
		query.setParameter(3,property.getId());
        List<Object> avs = query.getResultList();	
		List<Matchable> matchables = new ArrayList<Matchable>();
		for( Object o : avs ) 
		{ 
			if( !(o instanceof Price) ) matchables.add((Matchable) o);
		}
		return matchables;
	}
	
	public List<Matchable> getAvailabilityWeekDay(Property property)
	{
		String queryString = "from AvailabilityWeekDay where "
				+ " property.id=:propertyId";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("propertyId", property.getId());
		List<Matchable> results =  query.getResultList();
		List<Matchable> matchables = new ArrayList<Matchable>();
		for( Object o : results ) 
		{ 
			if( !(o instanceof PriceWeekDay) ) matchables.add((Matchable) o);
		}
		return matchables;
	}
	
	public List<Matchable> getBookings(Property property, List<Calendar> period)
	{
		String queryString = "from Booking where " +
	    " property.id=:propertyId";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("propertyId", property.getId());
	    return query.getResultList();		
	}
	
	public List<Matchable> getBookingsSkipCurrentUser(Property property, 
        List<Calendar> period, OkapiedUserDetails user)
	{
		String queryString = "from Booking where " +
	    " property.id=:propertyId " +
	    " and ((user.id!=:userId) or (user.id=:userId and reserved=:reserved))";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("propertyId", property.getId());
		query.setParameter("userId",user.getId());
		query.setParameter("reserved",0);
	    return  query.getResultList();		
	}
	
	public boolean available(List<Matchable> list, Calendar day)
	{
		return available(list,day,new PropertyDay());
	}
	
	public boolean available(List<Matchable> list, Calendar day, PropertyDay result, boolean detailed)
	{
		if( detailed ) return availableDetailed(list,day,result);
		else return available(list,day,result);
	}
	
	public boolean available(List<Matchable> list, Calendar day, PropertyDay result)
	{
		boolean match = false;
		boolean isAvailable = false;
		boolean isReserved = false;
		int currentPrecedence = LARGEST_PRECEDENCE;
		result.setFirstDayOfBooking(false);
		result.setLastDayOfBooking(false);
		for( Matchable av : list )
		{
			if( !match || av.precedence() < currentPrecedence )
			{
				if( av.match(day) )
				{
					match = true;
					if( av.getAvailableBool() ) isAvailable = true;
					else isAvailable = false;
					currentPrecedence = av.precedence();
				}
				if( av.getMatchedReservedBool(day) ) isReserved = true;
			}
			if( av.getBookingId() != null )
			{
				Booking booking = (Booking) av;
				if(booking.retrieveFirstDayOfBooking(day) && booking.retrieveIsStillValid())
				{
					result.setFirstDayOfBooking(true);
				}
				if(booking.retrieveLastDayOfBooking(day) && booking.retrieveIsStillValid())
				{
					result.setLastDayOfBooking(true);
					if( booking.getReservedBool() ) result.setLastReservedDay(true);
				}
				if( booking.match(day)) result.setBookingDay(true);
			}
		}
        result.setAvailable(isAvailable);
        result.setReserved(isReserved);
        return isAvailable;
	}
	
	public boolean availableDetailed(List<Matchable> list, Calendar day, PropertyDay result)
	{
		boolean match = false;
		boolean isAvailable = false;
		boolean isReserved = false;
		int currentPrecedence = LARGEST_PRECEDENCE;
		for( Matchable av : list )
		{
			if( !match || av.precedence() < currentPrecedence )
			{
				if( av.match(day) )
				{
					match = true;
					if( av.getAvailableBool() ) isAvailable = true;
					else isAvailable = false;
					currentPrecedence = av.precedence();
					if( av.getBookingId() != null ) result.setBookingId(av.getBookingId());
				}
				if( av.getMatchedReservedBool(day) ) 
				{
					isReserved = true;
					isAvailable = av.getAvailableBool();
					if( av.getBookingId() != null ) result.setBookingId(av.getBookingId());
				}
			}
		}
        result.setAvailable(isAvailable);
        result.setReserved(isReserved);
        return isAvailable;
	}
	
	public boolean isWithinValidPeriod(Calendar day, Property property, PropertyDay result)
	{
		return isWithinValidPeriod(day,property,result,false);
	}
		
	public boolean isWithinValidPeriod(Calendar day, Property property, PropertyDay result, boolean detailed)
	{
		Calendar today = property.retrieveCurrentDay();
		if( today.compareTo(day) > 0 ) 
		{
			result.setAvailable(false);
			result.setReserved(false);
			return false;
		}
		if( !detailed )
		{
			today.add(Calendar.DAY_OF_YEAR, Configuration.instance().getIntProperty("MAX_FORWARD_BOOKING_DAYS"));
			if( today.compareTo(day) <=0 ) 
			{
				result.setAvailable(false);
				result.setReserved(false);
				return false;
			}
		}
		return true;
	}
	
	public void makeAvailableDayOnly(Property property, Integer dayOfMonth, Integer month, Integer year,
	    Boolean availability, Integer userId, Float price) throws AlreadyBookedException, OkaSecurityException
	{
		Calendar day = property.retrieveCurrentDay();
		day.set(Calendar.DAY_OF_MONTH,dayOfMonth);
		day.set(Calendar.MONTH,month);
		day.set(Calendar.YEAR,year);
		makeAvailableDayOnly(property,day,availability,userId,price);
	}

	public void makeAvailableDayOnly(Property property, Calendar day, Boolean availability,
		    Integer userId) throws AlreadyBookedException, OkaSecurityException
	{
		makeAvailableDayOnly(property,day,availability,userId,null);
	}
	
	public void makeAvailableDayOnly(Property property, Calendar day, Boolean availability,
	    Integer userId, Float price) throws AlreadyBookedException, OkaSecurityException
	{
		if (!property.getOwner().getId().equals(userId)) {
			log.error("Attempted to change availability of a property that user does not own, user id: "
					+ userId);
			throw new OkaSecurityException(
					"You must be the owner of a property to change the availability");
		}
		
		// make sure there are no Bookings on the given day
		List<Booking> bookings = bookingEntityService.getBookingsForDay(property,day);
		if( bookings.size() > 0 )
		{
			throw new AlreadyBookedException("A booking has been made for the selected day" +
			    ", the availability cannot be changed");
		}
		
		// search for any single availabilities on the given day if there are any
		/// just set their availabilities and save
		List<Availability> currentAv = getAvailabilitiesDayOnly(property,day,price);
		if( currentAv.size() > 0 )
		{
			for( Availability av : currentAv )
			{
				av.setAvailableBool(availability);
				if( price != null )
				{
					Price priceObject = (Price) av;
					priceObject.setPrice(price);
				}
				dao.update(av);
			}
		}
		// otherwise create a new one and save
		else
		{
			Availability av = new Availability();
			if( price != null )
			{
				Price priceObject = new Price();
				priceObject.setPrice(price);
				av = priceObject;
			}
			av.setAvailableBool(availability);
			av.setDayOnlyBool(true);
			av.setStartDate(day.getTime());
			Calendar endDate = property.retrieveCurrentDay();
			endDate.setTime(day.getTime());
			endDate.add(Calendar.DAY_OF_YEAR,1);
			av.setEndDate(endDate.getTime());
			av.setProperty(property);
			dao.persist(av);
		}
	}
	
	public List<Availability> getAvailabilitiesDayOnly(Property property, Calendar date)
	{
	    return getAvailabilitiesDayOnly(property,date,null);
	}
	
	public List<Availability> getAvailabilitiesDayOnly(Property property, Calendar date, Float price)
	{
		String table = "Availability";
		if( price != null ) table = "Price";
		String queryString = "from "+table+" where precedenceNum=:precedence and startDate=:date " +
		    " and property.id=:propertyId";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("date",date.getTime());
		query.setParameter("propertyId",property.getId());
		query.setParameter("precedence",Availability.PRECEDENCE_DAY_ONLY);
		List<Availability> results =  query.getResultList();
		List<Availability> filteredResults = new ArrayList<Availability>();
		
		if( price != null ) return results;
		// if we are looking for Availability only remove all Prices as we can't filter by superclass
		for( Availability av : results )
		{
			if(!(av instanceof Price)) filteredResults.add(av);
		}
		return filteredResults;
	}
	
	public List<Availability> getAvailabilitiesPeriod(Property property, Calendar start, Calendar end, Float price)
	{
		String table = "Availability";
		if( price != null ) table = "Price";
		String queryString = "from "+table+" where precedenceNum!=:precedence and startDate=:start and endDate=:end" +
		    " and property.id=:propertyId";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("start",start.getTime());
		query.setParameter("end",end.getTime());
		query.setParameter("propertyId",property.getId());
		query.setParameter("precedence",Availability.PRECEDENCE_DAY_ONLY);
		List<Availability> results =  query.getResultList();
		if( price != null ) return results;
		
		// filter out prices as we can't search by superclass
		List<Availability> returnList = new ArrayList<Availability>();
		for( Availability av : results )
		{
			if( !(av instanceof Price) ) returnList.add(av);
		}
		return returnList;
	}
	
	public void makeAvailablePeriod(Property property, Calendar start, Calendar end,
		    Integer userId, Float price) throws AlreadyBookedException, OkaSecurityException
	{
		if (!property.getOwner().getId().equals(userId)) {
			log.error("Attempted to change availability of a property that user does not own, user id: "
					+ userId);
			throw new OkaSecurityException(
					"You must be the owner of a property to change the availability");
		}
		
		// make sure there are no Bookings on the given day
		List<Booking> bookings = bookingEntityService.getBookingsForPeriod(property,start,end);
		if( bookings.size() > 0 )
		{
			throw new AlreadyBookedException("A booking has been made for the selected day" +
			    ", the availability cannot be changed");
		}
		
		// search for any single availabilities on the given day if there are any
		/// just set their availabilities and save
		List<Availability> currentAv = getAvailabilitiesPeriod(property,start,end,price);
		if( currentAv.size() > 0 )
		{
			for( Availability av : currentAv )
			{
				if( price != null )
				{
					Price priceObject = (Price) av;
					priceObject.setPrice(price);
				}
				else
				{
					av.setAvailableBool(!av.getAvailableBool());
				}
				av.setPrecedenceNum(Availability.PRECEDENCE_SET_PERIOD);
				dao.update(av);
			}
		}
		// otherwise create a new one and save
		else
		{
			Availability av = new Availability();
			av.setAvailableBool(false);
			if( price != null )
			{
				Price priceObject = new Price();
				priceObject.setPrice(price);
				av = priceObject;
				av.setAvailableBool(true);
			}
			av.setDayOnlyBool(false);
			av.setStartDate(start.getTime());
			av.setEndDate(end.getTime());
			av.setProperty(property);
			av.setPrecedenceNum(Availability.PRECEDENCE_SET_PERIOD);
			dao.persist(av);
		}
		
		// then we delete any day only availabilities / prices within the period
		deleteAllDayOnly(property,start,end,price);
	}
	
	public void deleteAllDayOnly(Property property, Calendar start, Calendar end, Float price)
	{
		String table = "Availability";
		if( price != null ) table = "Price";
		String queryString = "from " + table +
		    " where property.id=:propertyId" +
		    " and startDate >= :start" +
		    " and endDate < :end" +
		    " and precedenceNum =:precedence";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("start",start.getTime());
		query.setParameter("end",end.getTime());
		query.setParameter("precedence", Availability.PRECEDENCE_DAY_ONLY);
		query.setParameter("propertyId",property.getId());
		List<Availability> results = query.getResultList();
		List<Availability> toDelete = new ArrayList<Availability>();
		// we have to groom for Availability as we cannot do a select for the superclass only
		if( price != null ) toDelete = results;
		else
		{
			for( Availability av : results )
			{
				if( !(av instanceof Price) ) toDelete.add(av);
			}
		}
		for( Availability av : toDelete )
		{
			dao.delete(Availability.class.getName(),"id",av.getId());
		}
	}
	
	/**
	 * For a given month and day, returns a period covering the whole month.
	 * @return
	 */
	public Calendar[] monthYearToPeriod(Property property, Integer month, Integer year)
	{		
	    Calendar start = property.retrieveCurrentDay();
	    start.set(Calendar.DAY_OF_MONTH,1);
	    start.set(Calendar.MONTH,month);
	    start.set(Calendar.YEAR,year);
	    Calendar end = property.retrieveCurrentDay();
	    end.setTime(start.getTime());
	    int startMonth = start.get(Calendar.MONTH);
	    if( startMonth == 11 ) end.set(Calendar.MONTH,0);
	    else end.set(Calendar.MONTH,startMonth+1);
	    Calendar[] period = new Calendar[2];
	    period[0] = start;
	    period[1] = end;
	    return period;
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public BookingEntityService getBookingEntityService() {
		return bookingEntityService;
	}

	public void setBookingEntityService(BookingEntityService bookingEntityService) {
		this.bookingEntityService = bookingEntityService;
	}
	
	public void deleteAll(Property property, boolean price)
	{	
		String table = "Availability";
		if( price ) table = "Price";
		String queryString = "from " + table +
		    " where property.id=:propertyId";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("propertyId",property.getId());
		List<Availability> results = query.getResultList();
		List<Availability> toDelete = new ArrayList<Availability>();
        if( price ) toDelete = results;
        else
        {
			// we have to groom for Availability as we cannot do a select for the superclass only
			for( Availability av : results )
			{
				if( !(av instanceof Price) ) toDelete.add(av);
			}
        }
		for( Availability av : toDelete )
		{
			dao.delete(Availability.class.getName(),"id",av.getId());
		}
	}
	
	public void deleteAllWeekDay(Property property, boolean price)
	{	
		String table = "AvailabilityWeekDay";
		if( price ) table = "PriceWeekDay";
		String queryString = "from " + table +
		    " where property.id=:propertyId";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("propertyId",property.getId());
		List<AvailabilityWeekDay> results = query.getResultList();
		List<AvailabilityWeekDay> toDelete = new ArrayList<AvailabilityWeekDay>();
        if( price ) toDelete = results;
        else
        {
			// we have to groom for Availability as we cannot do a select for the superclass only
			for( AvailabilityWeekDay av : results )
			{
				if( !(av instanceof PriceWeekDay) ) toDelete.add(av);
			}
        }
		for( AvailabilityWeekDay av : toDelete )
		{
			dao.delete(table,"id",av.getId());
		}
	}
	
	public void makeAvailableWeekDay(Property property,int weekDay, Integer userId, Float price) 
	     throws AlreadyBookedException, OkaSecurityException
	{
		if (!property.getOwner().getId().equals(userId)) {
			log.error("Attempted to change availability of a property that user does not own, user id: "
					+ userId);
			throw new OkaSecurityException(
					"You must be the owner of a property to change the availability");
		}
		
		// make sure there are no Bookings on the given day
		List<Booking> bookings = bookingEntityService.getActiveBookings(property);
		if( bookings.size() > 0 )
		{
			throw new AlreadyBookedException("There are active bookings for the property" +
			    ", the availability for a day of the week cannot be changed");
		}
		
		// search for any single availabilities on the given day if there are any
		/// just set their availabilities and save
		List<AvailabilityWeekDay> currentAv = getAvailabilitiesWeekDay(property,weekDay,price);
		if( currentAv.size() > 0 )
		{
			for( AvailabilityWeekDay av : currentAv )
			{
				if( price != null )
				{
					PriceWeekDay priceObject = (PriceWeekDay) av;
					priceObject.setPrice(price);
				}
				else
				{
					av.setAvailableBool(!av.getAvailableBool());
				}
				av.setPrecedenceNum(Availability.PRECEDENCE_SET_PERIOD);
				dao.update(av);
			}
		}
		// otherwise create a new one and save
		else
		{
			AvailabilityWeekDay av = new AvailabilityWeekDay();
			av.setAvailableBool(false);
			if( price != null )
			{
				PriceWeekDay priceObject = new PriceWeekDay();
				priceObject.setPrice(price);
				av = priceObject;
				av.setAvailableBool(true);
			}
			av.setDayOfWeek(weekDay);
			av.setProperty(property);
			av.setPrecedenceNum(Availability.PRECEDENCE_SET_PERIOD);
			dao.persist(av);
		}
	}
	
	public List<AvailabilityWeekDay> getAvailabilitiesWeekDay(Property property, int weekDay, Float price)
	{
		String table = "AvailabilityWeekDay";
		if( price != null ) table = "PriceWeekDay";
		String queryString = "from "+table+" where dayOfWeek =:weekDay" +
		    " and property.id=:propertyId";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("weekDay",weekDay);
		query.setParameter("propertyId",property.getId());
		List<AvailabilityWeekDay> results =  query.getResultList();
		if( price != null ) return results;
		
		// filter out prices as we can't search by superclass
		List<AvailabilityWeekDay> returnList = new ArrayList<AvailabilityWeekDay>();
		for( AvailabilityWeekDay av : results )
		{
			if( !(av instanceof PriceWeekDay) ) returnList.add(av);
		}
		return returnList;
	}
}
