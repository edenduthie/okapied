package okapied.service;

import java.util.ArrayList;
import java.util.List;

import okapied.BaseTest;
import okapied.dao.DAO;
import okapied.entity.Country;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DAOTest extends BaseTest
{
	@Autowired DAO dao;
	
    @Test
    public void testGetAllCountries()
    {
    	List<Country> setCountries = new ArrayList<Country>();
    	for( int i=0; i < 10; ++i  )
    	{
    		Country country = Generator.country();
    		dao.persist(country);
    		setCountries.add(country);
    	}
    	
    	List<Country> countries = dao.getAll(Country.class.getName());
    	Assert.assertEquals(countries.size(),10);
    	int i=0;
    	for( Country result : countries )
    	{
    		Assert.assertEquals(result.getName(),setCountries.get(i).getName());
    		Assert.assertEquals(result.getId(),setCountries.get(i).getId());
    		++i;
    	}
    	
    	dao.deleteAll(Country.class.getName());
    }
}
