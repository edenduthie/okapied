package okapied.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Country;
import okapied.entity.Location;
import okapied.entity.Place;
import okapied.entity.Region;
import okapied.util.Configuration;

import org.apache.log4j.Logger;

public class PlaceService 
{
    DAO dao;
    static Logger logger = Logger.getLogger(PlaceService.class);
    
    public List<Place> search(String searchString)
    {
    	int limit = Configuration.instance().getIntProperty("MAX_PLACE_SEARCH_RESULTS");

    	List<Place> countries = searchCountries(searchString,limit);
    	List<Place> regions = searchRegions(searchString,limit);
    	List<Place> locations = searchLocations(searchString,limit);
    	List<Place> properties = searchProperties(searchString,limit);
    	
    	List<Place> results = new ArrayList<Place>();
    	results.addAll(countries);
    	results.addAll(regions);
    	results.addAll(locations);
    	results.addAll(properties);
    	
    	return results;
    }
    
    public List<Place> searchCountries(String searchString, int max)
    {
    	String queryString = "select new Country(id,name) from Country where name LIKE :searchString" +
    	    " and numProperties > 0";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("searchString",searchString + "%");
    	query.setMaxResults(max);
    	return query.getResultList();
    }
    
    public List<Place> searchRegions(String searchString, int max)
    {
    	String queryString = "select new Region(id,name,country.id,country.name)" 
    		+ " from Region where name LIKE :searchString" 
    		+ " and numProperties > 0";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("searchString",searchString + "%");
    	query.setMaxResults(max);
    	return query.getResultList();
    }
    
    public List<Place> searchLocations(String searchString, int max)
    {
    	String queryString = 
    		"select new Location(id,name,country.id,country.name"
    		+ ",region.id,region.name)" 
    	    + " from Location where name LIKE :searchString"
    	    + " and numProperties > 0";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("searchString",searchString + "%");
    	query.setMaxResults(max);
    	List<Place> resultsWithRegion = query.getResultList();

    	List<Place> resultsWithoutRegion = null;
    	if( resultsWithRegion.size() < max )
    	{
	    	queryString = 
	    		"select new Location(id,name,country.id,country.name)" 
	    	    + " from Location where name LIKE :searchString"
	    	    + " and region = null and numProperties > 0";
	    	query = dao.getEntityManager().createQuery(queryString);
	    	query.setParameter("searchString",searchString + "%");
	    	query.setMaxResults(max);
	    	resultsWithoutRegion = query.getResultList();
    	}

    	List<Place> results = new ArrayList<Place>();
    	results.addAll(resultsWithRegion);
    	if( resultsWithoutRegion != null ) results.addAll(resultsWithoutRegion);
    	
    	return results;
    }
    
    public List<Place> searchProperties(String searchString, int max)
    {
    	String queryString = 
    		"select new Property(id,name,location.id,location.name"
    		+ ",location.region.id,location.region.name"
    		+ ",location.country.id,location.country.name)" 
    	    + " from Property where name LIKE :searchString";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("searchString",searchString + "%");
    	query.setMaxResults(max);
    	List<Place> resultsWithRegion = query.getResultList();
    	
    	List<Place> resultsWithoutRegion = null;
    	if( resultsWithRegion.size() < max )
    	{
	    	queryString = 
	    		"select new Property(id,name,location.id,location.name"
	    		+ ",location.country.id,location.country.name)" 
	    	    + " from Property where name LIKE :searchString"
	    	    + " and location.region = null";
	    	query = dao.getEntityManager().createQuery(queryString);
	    	query.setParameter("searchString",searchString + "%");
	    	query.setMaxResults(max);
	    	resultsWithoutRegion = query.getResultList();
    	}
    	
    	List<Place> results = new ArrayList<Place>();
    	results.addAll(resultsWithRegion);
    	if( resultsWithoutRegion != null ) results.addAll(resultsWithoutRegion);
    	
    	return results;
    }
    
    public List<Place> searchIncludingNoProperties(String searchString, int max, String table)
    {
    	String queryString = "from " + table + " where name LIKE :searchString";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("searchString",searchString + "%");
    	query.setMaxResults(max);
    	return query.getResultList();
    }
    
    public List<Place> searchIncludingNoPropertiesOrdered(String searchString, int max, String table)
    {
    	String queryString = "from " + table + " where name LIKE :searchString order by population desc";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("searchString",searchString + "%");
    	query.setMaxResults(max);
    	return query.getResultList();
    }

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public List<Place> searchLocations(String searchString) {
    	int limit = Configuration.instance().getIntProperty("MAX_LOCATIONS_SEARCH_RESULTS");
    	List<Place> locations = searchIncludingNoProperties(searchString,limit,Location.class.getName());
    	return locations;
	}
}
