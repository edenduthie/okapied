package okapied.action.json;

import java.util.List;

import okapied.dao.DAO;
import okapied.entity.Country;

public class Countries 
{
    private List<Country> countries;
    private String LIST = "list";
    
    DAO dao;

    public void nullServices()
    {
    	dao = null;
    }
    
	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}
    
    public String list()
    {
    	countries = dao.getAll(Country.class.getName());
    	nullServices();
    	return LIST;
    }
}
