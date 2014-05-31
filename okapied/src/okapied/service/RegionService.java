package okapied.service;

import java.util.List;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Region;

public class RegionService {

	DAO dao;
	static final int LIMIT = 10;
	
	public List<Region> getRegionListByCountry(Integer countryId)
	{
		String queryString = "from Region where country.id=:countryId";
		queryString += " and numProperties > 0";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("countryId", countryId);
		return query.getResultList();
	}
	
	public List<Region> searchRegions(Integer countryId, String searchString)
	{
		String queryString = "from Region where country.id=:countryId and name LIKE '" +
		    searchString + "%'";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setMaxResults(LIMIT);
		query.setParameter("countryId", countryId);
		return query.getResultList();
	}
	
	public Region get(Integer id)
	{
		return (Region) dao.get(Region.class.getName(),"id", id);
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
}
