package okapied.service;

import java.util.List;

import okapied.BaseTest;
import okapied.dao.DAO;
import okapied.entity.Country;
import okapied.entity.Location;
import okapied.entity.Region;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocationServiceTest extends BaseTest
{
    @Autowired
    LocationService locationService;
    
    @Test
    public void testGetLocationsByRegionNoProperties()
    {
    	Location location = Generator.location();
    	persist(location);
    	
    	List<Location> locations = locationService.getLocationsByRegion(location.getRegion().getId());
    	Assert.assertEquals(locations.size(),0);
    	
    	DAO dao = locationService.getDao();
    	dao.delete(Location.class.getName(), "id", location.getId());
    	dao.delete(Region.class.getName(),"id",location.getRegion().getId());
    	dao.delete(Country.class.getName(), "id", location.getCountry().getId());
    }
    
    @Test
    public void testGetLocationsByRegion()
    {
    	Location location = Generator.location();
    	location.setNumProperties(1);
    	persist(location);
    	
    	List<Location> locations = locationService.getLocationsByRegion(location.getRegion().getId());
    	Assert.assertEquals(1,locations.size());
    	Assert.assertEquals(location.getId(),locations.get(0).getId());
    	
    	DAO dao = locationService.getDao();
    	dao.delete(Location.class.getName(), "id", location.getId());
    	dao.delete(Region.class.getName(),"id",location.getRegion().getId());
    	dao.delete(Country.class.getName(), "id", location.getCountry().getId());
    }
    
    public void persist(Location location)
    {
    	DAO dao = locationService.getDao();
    	dao.persist(location.getCountry());
    	dao.persist(location.getRegion());
    	dao.persist(location);
    }
}
