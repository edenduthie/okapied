package okapied.service;

import java.util.List;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Booking;
import okapied.entity.Feedback;
import okapied.entity.UserFeedback;
import okapied.exception.FeedbackException;
import okapied.exception.OkaSecurityException;
import okapied.util.DateUtil;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FeedbackService 
{
	DAO dao;
	BookingEntityService bookingEntityService;
	
	static Logger log = Logger.getLogger(FeedbackService.class);
	
    public List<Feedback> getFeedback(Integer propertyId, Boolean positive, Integer limit, Integer offset)
    {
    	String queryString = 
    	    "select new Feedback(id,positiveInt,text,dateLeft,rating,accuracy,cleanliness,valueForMoney) from Feedback where" +
    	    " property.id=:propertyId";
    	if( positive != null )
    	{
    		queryString += " and positiveInt=:positiveValue";
    	}
    	queryString += " order by dateLeft desc";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("propertyId", propertyId);
    	if( positive != null )
    	{
    		if( positive == true ) query.setParameter("positiveValue", 1);
    		else query.setParameter("positiveValue", 0);
    	}
    	if( limit != null ) query.setMaxResults(limit);
    	if( offset != null ) query.setFirstResult(offset);
    	return query.getResultList();
    }
    
    public Long getFeedbackSize(Integer propertyId, Boolean positive)
    {
    	String queryString = 
    	    "select count(*) from Feedback where" +
    	    " property.id=:propertyId";
    	if( positive != null )
    	{
    		queryString += " and positiveInt=:positiveValue";
    	}
    	queryString += " order by dateLeft desc";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("propertyId", propertyId);
    	if( positive != null )
    	{
    		if( positive == true ) query.setParameter("positiveValue", 1);
    		else query.setParameter("positiveValue", 0);
    	}
    	return (Long) query.getSingleResult();
    }
    
    public void leaveFeedbackUser(int bookingId, Feedback feedback, int userId)
       throws OkaSecurityException, FeedbackException
    {
    	Booking booking = bookingEntityService.get(bookingId);
    	
        // check to see if the user is the booking's user
    	booking.checkIsBookingUser(booking, userId);
    	
    	// check to see if ready to leave feedback and no existing feedback
    	if( !booking.readyToLeaveFeedbackUser() )
    	{
    		log.error("Booking not ready for feedback: " + bookingId);
    		throw new FeedbackException("The booking is not ready for feedback yet");
    	}
    	
    	feedback.setBooking(booking);
    	feedback.setDateLeft(DateUtil.getCurrentCalendarCompareTZ().getTime());
    	feedback.setProperty(booking.getProperty());
    	dao.persist(feedback);
    	
    	booking.setUserFeedback(feedback);
    	dao.update(booking);
    	
    	if( feedback.getPositive() ) feedback.getProperty().incrementPositiveFeedback();
    	else feedback.getProperty().incrementNegativeFeedback();
    	feedback.getProperty().updateCleanliness(feedback.getCleanliness());
    	feedback.getProperty().updateAccuracy(feedback.getAccuracy());
    	feedback.getProperty().updateRating(feedback.getRating());
    	feedback.getProperty().updateValueForMoney(feedback.getValueForMoney());
    	dao.update(feedback.getProperty());
    }
    
	public void leaveFeedbackOwner(int bookingId, UserFeedback feedback, int userId)
			throws OkaSecurityException, FeedbackException 
	{
		Booking booking = bookingEntityService.get(bookingId);

		// check to see if the user is the booking's user
		booking.checkIsBookingOwner(booking, userId);

		// check to see if ready to leave feedback and no existing feedback
		if (!booking.readyToLeaveFeedbackOwner()) {
			log.error("Booking not ready for feedback: " + bookingId);
			throw new FeedbackException(
					"The booking is not ready for feedback yet");
		}

		feedback.setBooking(booking);
		feedback.setUser(booking.getUser());
		feedback.setDateLeft(DateUtil.getCurrentCalendarCompareTZ().getTime());
		dao.persist(feedback);

		booking.setOwnerFeedback(feedback);
		dao.update(booking);
	}
    
    public Feedback get(int id)
    {
    	return (Feedback) dao.get(Feedback.class.getName(),"id",id);
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
}
