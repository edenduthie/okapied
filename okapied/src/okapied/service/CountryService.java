package okapied.service;

import java.util.List;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Country;

import org.apache.log4j.Logger;

public class CountryService
{
	DAO dao;
	
	private static final Logger log = Logger.getLogger(CountryService.class);
	
	public Country getCountryByName(String name)
	{
		log.debug("Loading countrry: " + name);
		String queryString = "from Country where name=:countryName ";
	    Query query = dao.getEntityManager().createQuery(queryString);
	    query.setParameter("countryName", name);
	    return (Country) query.getSingleResult();
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public List<Country> getAllCountriesWithProperties() 
	{
		String queryString = "from Country where numProperties > 0";
		Query query = dao.getEntityManager().createQuery(queryString);
		return query.getResultList();
	}
	
	public Country get(Integer id)
	{
		return (Country) dao.get(Country.class.getName(),"id", id);
	}
}
