package okapied;


import java.util.Collection;

import okapied.dao.DAO;
import okapied.entity.Availability;
import okapied.entity.AvailabilityWeekDay;
import okapied.entity.Booking;
import okapied.entity.Country;
import okapied.entity.CurrencyCode;
import okapied.entity.Flooring;
import okapied.entity.Location;
import okapied.entity.OkapiedGrantedAuthority;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Price;
import okapied.entity.PriceWeekDay;
import okapied.entity.Property;
import okapied.entity.PropertyType;
import okapied.entity.Region;
import okapied.service.AccountService;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * @author eduthie
 */
@ContextConfiguration(locations={"file:test/applicationContext.xml"})
public class BaseTest extends AbstractTestNGSpringContextTests
{
	@Autowired protected DAO dao;
	
	public static final String TEST_DATA_DIR = "testdata";
	
	protected Property property;
	protected OkapiedUserDetails user;
	protected Availability av;
	protected Booking booking;
	protected AvailabilityWeekDay avWd;
	protected Price price;
	protected PriceWeekDay priceWd;
	
	@Autowired protected AccountService accountService;
	
	public void saveAvailability()
	{
		property = av.getProperty();
    	saveProperty();
    	dao.persist(av);
	}
	
	public void savePrice()
	{
		property = price.getProperty();
    	saveProperty();
    	dao.persist(price);
	}
	
	public void saveAvailabilityWD()
	{
		property = avWd.getProperty();
    	saveProperty();
    	dao.persist(avWd);
	}
	
	public void saveBooking()
	{
    	property = booking.getProperty();
    	saveProperty();
    	booking.setUser(property.getOwner());
    	dao.persist(booking);
	}
	
	public void saveProperty()
	{
		saveProperty(property);
	}
	
	public void saveProperty(Property property)
	{
		saveLocations(property);
		dao.persist(property.getCurrencyCode());
    	dao.persist(property.getPropertyDetails().getFlooring());
    	dao.persist(property.getPropertyDetails().getType());
    	Collection<GrantedAuthority> auths = property.getOwner().getAuthorities();
    	for( GrantedAuthority auth : auths )
    	{
    		dao.persist((OkapiedGrantedAuthority)auth);
    	}
    	dao.persist(property.getOwner());
    	user = property.getOwner();
		dao.persist(property);
	}
	
	public Property copyProperty(Property property)
	{
    	Property property2 = Generator.property();
    	property2.setLocation(property.getLocation());
    	property2.setOwner(property.getOwner());
    	property2.getPropertyDetails().setFlooring(property.getPropertyDetails().getFlooring());
    	property2.getPropertyDetails().setType(property.getPropertyDetails().getType());
    	property2.setCurrencyCode(property.getCurrencyCode());
    	dao.persist(property2);
    	return property2;
	}
	
	public void saveLocations()
	{
		saveLocations(property);
	}
	
	public void saveLocations(Property property)
	{
		dao.persist(property.getLocation().getCountry());
		if( property.getLocation().getRegion() != null ) dao.persist(property.getLocation().getRegion());
		dao.persist(property.getLocation());
	}
	
	public void removeAvailability()
	{
		dao.delete(Availability.class.getName(), "id", av.getId());
		removeProperty();
	}
	
	public void removeAvailabilityWD()
	{
		dao.delete(AvailabilityWeekDay.class.getName(), "id", avWd.getId());
		removeProperty();
	}
	
	public void removePrice()
	{
		dao.delete(Price.class.getName(), "id", price.getId());
		removeProperty();
	}
	
	public void removePriceAndAvailability()
	{
		dao.delete(Price.class.getName(), "id", price.getId());
		dao.delete(Availability.class.getName(), "id", av.getId());
		removeProperty();
	}
	
	public void removePriceWD()
	{
		dao.delete(PriceWeekDay.class.getName(), "id", priceWd.getId());
		removeProperty();
	}
	
	public void removeBooking()
	{
		dao.delete(Booking.class.getName(), "id", booking.getId());
		removeProperty();
	}
	
	public void removeProperty()
	{
		removeProperty(property);
	}
	
	public void removeProperty(Property property)
	{
    	dao.delete(Property.class.getName(), "id", property.getId());
    	dao.deleteAll(Flooring.class.getName());
    	dao.deleteAll(PropertyType.class.getName());
    	dao.deleteAll(CurrencyCode.class.getName());
        accountService.remove(property.getOwner());
        dao.deleteAll(OkapiedGrantedAuthority.class.getName());
        removeLocations(property);
	}
	
	public void removePropertyNoSubData(Property property)
	{
		dao.delete(Property.class.getName(), "id", property.getId());
	}
	
	public void removeLocations()
	{
		removeLocations(property);
	}
	
	public void removeLocations(Property property)
	{
        int countryId = property.getLocation().getCountry().getId();
    	dao.delete(Location.class.getName(), "id", property.getLocation().getId());
    	if( property.getLocation().getRegion() != null ) dao.delete(Region.class.getName(), "id",property.getLocation().getRegion().getId());
    	dao.delete(Country.class.getName(),"id",countryId);
	}
}
