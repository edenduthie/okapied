package okapied.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Booking;
import okapied.entity.Property;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BookingEntityService 
{
	DAO dao;
	
	public List<Booking> getBookingsForDay(Property property, Calendar date)
	{
		String queryString = "from Booking where " +
	    " property.id=:propertyId" +
	    " and startDate <= :date" +
	    " and endDate > :date";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("propertyId", property.getId());
		query.setParameter("date",date.getTime());
	    List<Booking> bookings = query.getResultList();
	    
	    List<Booking> bookingResults = new ArrayList<Booking>();
	    for( Booking booking : bookings )
	    {
            if( !booking.getReservedBool() && booking.getBookingStatus() != null && 
                !booking.getBookingStatus().equals(Booking.BOOKING_STATUS_CONFIRMED) )
            {
            	// don't add
            }
            else
            {
            	bookingResults.add(booking);
            }
	    }
	    return bookingResults;
	}
	
	public List<Booking> getBookingsForPeriod(Property property, Calendar start, Calendar end)
	{
		String queryString = "from Booking where " +
	    " property.id=:propertyId" +
	    " and startDate < :end" +
	    " and endDate > :start";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("propertyId", property.getId());
		query.setParameter("start",start.getTime());
		query.setParameter("end",end.getTime());
	    List<Booking> bookings = query.getResultList();
	    
	    List<Booking> bookingResults = new ArrayList<Booking>();
	    for( Booking booking : bookings )
	    {
            if( !booking.getReservedBool() && booking.getBookingStatus() != null && 
                !booking.getBookingStatus().equals(Booking.BOOKING_STATUS_CONFIRMED) )
            {
            	// don't add
            }
            else
            {
            	bookingResults.add(booking);
            }
	    }
	    return bookingResults;
	}
	
	public List<Booking> getActiveBookings(Property property)
	{
		String queryString = "from Booking where " +
	    " property.id=:propertyId" +
	    " and endDate > :currentDate";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("propertyId", property.getId());
		query.setParameter("currentDate",property.retrieveCurrentDay().getTime());
	    List<Booking> bookings = query.getResultList();
	    
	    List<Booking> bookingResults = new ArrayList<Booking>();
	    for( Booking booking : bookings )
	    {
            if( !booking.getReservedBool() && booking.getBookingStatus() != null && 
                !booking.getBookingStatus().equals(Booking.BOOKING_STATUS_CONFIRMED) )
            {
            	// don't add
            }
            else
            {
            	bookingResults.add(booking);
            }
	    }
	    return bookingResults;
	}
	
	public List<Booking> getMyBookings(Integer userId, Integer limit, Integer offset)
	{
		String queryString = "select new Booking(id,startDate,endDate,property.name) from Booking where user.id=:userId" +
		//" and reserved != 1" +
		" and bookingStatus != :bookingStatus" +
		" order by id desc";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("userId",userId);
		query.setMaxResults(limit);
		query.setFirstResult(offset);
		query.setParameter("bookingStatus", Booking.BOOKING_STATUS_RESERVATION);
		return query.getResultList();
	}
	
	public long getMyBookingsSize(Integer userId)
	{
		String queryString = "select count(id) from Booking where user.id=:userId" +
		//" and reserved != 1" +
		" and bookingStatus != :bookingStatus";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("userId",userId);
		query.setParameter("bookingStatus", Booking.BOOKING_STATUS_RESERVATION);
		return (Long) query.getSingleResult();
	}
	
	/**
	 * @TODO I may need to implement pagination for this in the future
	 * @return
	 */
	public List<Booking> getConfirmedBookingsNoReminderDayBeforeStart()
	{
		String queryString = "from Booking" +
		    " where bookingStatus=:bookingStatus" +
		    " and reminderEmail=0";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("bookingStatus", Booking.BOOKING_STATUS_CONFIRMED);
		List<Booking> bookings = query.getResultList();
		
		List<Booking> results = new ArrayList<Booking>();
		for( Booking booking : bookings )
		{
			Calendar tomorrow = booking.getProperty().retrieveCurrentTime();
			tomorrow.add(Calendar.DAY_OF_YEAR,1);
			Calendar startDate = booking.getProperty().retrieveCurrentDay();
			startDate.setTime(booking.getStartDate());
			if( startDate.getTimeInMillis() <= tomorrow.getTimeInMillis() )
			{
				results.add(booking);
			}
		}
		return results;
	}
	
	/**
	 * @TODO I may need to implement pagination for this in the future
	 * @return
	 */
	public List<Booking> getConfirmedBookingsNoFeedbackDayAfterEnd()
	{
		String queryString = "from Booking" +
		    " where bookingStatus=:bookingStatus" +
		    " and feedbackEmail=0" +
		    " and userFeedback IS NULL";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("bookingStatus", Booking.BOOKING_STATUS_CONFIRMED);
		List<Booking> bookings = query.getResultList();
		
		List<Booking> results = new ArrayList<Booking>();
		for( Booking booking : bookings )
		{
			Calendar today = booking.getProperty().retrieveCurrentTime();
			Calendar endDate = booking.getProperty().retrieveCurrentDay();
			endDate.setTime(booking.getEndDate());
			endDate.add(Calendar.DAY_OF_YEAR,1);
			if( today.getTimeInMillis() >= endDate.getTimeInMillis() )
			{
				results.add(booking);
			}
		}
		return results;
	}
	
	public Booking get(int id)
	{
		return (Booking) dao.get(Booking.class.getName(),"id", id);
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	/**
	 * Retrieves all bookings on all properties that the given user owns.
	 */
	public List<Booking> getPropertyBookings(Integer id, Integer limit,
			Integer offset) 
	{
		String queryString = "select new Booking(id,startDate,endDate,property.name) from Booking where property.owner.id=:ownerId order by startDate desc, id desc and reserved != 1";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("ownerId",id);
		query.setMaxResults(limit);
		query.setFirstResult(offset);
		return query.getResultList();
	}

	public Long getPropertyBookingsSize(Integer id) {
		String queryString = "select count(id) from Booking where property.owner.id=:ownerId order by startDate desc, id desc and reserved != 1";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("ownerId",id);
		return (Long) query.getSingleResult();
		
	}
}
