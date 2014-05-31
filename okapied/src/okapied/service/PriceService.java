package okapied.service;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Matchable;
import okapied.entity.Price;
import okapied.entity.PriceInterface;
import okapied.entity.Property;

import org.apache.log4j.Logger;

public class PriceService 
{
	DAO dao;
	BookingEntityService bookingEntityService;
	
	public static int LARGEST_PRECEDENCE = 10000;
	
	public static final Logger log = Logger.getLogger(PriceService.class);
	
	public List<PriceInterface> get(Property property, List<Calendar> period)
	{
    	List<PriceInterface> periodList = getPeriod(property,period);
    	List<PriceInterface> weekDayList = getWeekDay(property);
    	for( PriceInterface m : weekDayList ) periodList.add(m);
    	return periodList;
	}
	
	public List<PriceInterface> getPeriod(Property property, List<Calendar> period)
	{
    	String queryString = "from Price where " + "" +
	    " endDate >= ?1 and startDate <= ?2 " +
	    " and property.id = ?3";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter(1,period.get(0).getTime());
		query.setParameter(2,period.get(period.size()-1).getTime());
		query.setParameter(3,property.getId());
		return query.getResultList();
	}
	
	public List<PriceInterface> getWeekDay(Property property)
	{
		String queryString = "from PriceWeekDay where "
				+ " property.id=:propertyId";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("propertyId", property.getId());
		return query.getResultList();
	}
	
	public PriceInterface price(List<PriceInterface> list, Calendar day)
	{
		boolean match = false;
		PriceInterface currentPrice = new Price();
		int currentPrecedence = LARGEST_PRECEDENCE;
		for( PriceInterface price : list )
		{
			if( !match || price.precedence() < currentPrecedence )
			{
				if( price.match(day) )
				{
					match = true;
					currentPrice = price;
					currentPrecedence = price.precedence();
				}
			}
		}
        return currentPrice;
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
