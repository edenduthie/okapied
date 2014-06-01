package okapied.service;

import java.util.List;

import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.Location;

public class LocationService 
{
    DAO dao;
    
    public List<Location> getLocationsByRegion(Integer regionId)
    {
    	String queryString = "from Location where region.id=:regionId";
    	queryString += " and numProperties > 0";
    	Query query = dao.getEntityManager().createQuery(queryString);
    	query.setParameter("regionId",regionId);
    	return query.getResultList();
    }

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
}
