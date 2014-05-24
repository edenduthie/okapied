package okapied.service;

import java.util.List;

import junit.framework.Assert;
import okapied.BaseTest;
import okapied.dao.DAO;
import okapied.entity.Country;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class CountryServiceTest extends BaseTest
{
	
	@Autowired
	CountryService countryService;
	
	@Test
	public void getCountryByName()
	{
		DAO dao = countryService.getDao();
		Country country = Generator.country();
		country.setName("TestCountry");
		dao.persist(country);
		
		Country result = countryService.getCountryByName(country.getName());
		Assert.assertEquals(result.getId(),country.getId());
		
		dao.delete(Country.class.getName(), "id", country.getId());
	}
	
	@Test
	public void getAllCountriesWithPropertiesNoProperties()
	{
		DAO dao = countryService.getDao();
		Country country = Generator.country();
		country.setName("TestCountry");
		dao.persist(country);
		
		List<Country> countries = countryService.getAllCountriesWithProperties();
		Assert.assertEquals(countries.size(),0);
		
		dao.delete(Country.class.getName(), "id", country.getId());
	}
	
	@Test
	public void getAllCountriesWithProperties()
	{
		DAO dao = countryService.getDao();
		Country country = Generator.country();
		country.setName("TestCountry");
		country.setNumProperties(1);
		dao.persist(country);
		
		List<Country> countries = countryService.getAllCountriesWithProperties();
		Assert.assertEquals(countries.size(),1);
		
		dao.delete(Country.class.getName(), "id", country.getId());
	}
}
