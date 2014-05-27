package okapied.datagenerator;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import okapied.BaseTest;
import okapied.dao.DAO;
import okapied.entity.Availability;
import okapied.entity.CurrencyCode;
import okapied.entity.Flooring;
import okapied.entity.Location;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Price;
import okapied.entity.Property;
import okapied.entity.PropertyType;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class GenerateProperties extends BaseTest
{
	@Autowired 
	DAO dao;
	
	@Test
    public void generate()
    {
		String queryString = "from Location where country.id=1";
		Query query = dao.getEntityManager().createQuery(queryString);
		List<Location> locations = query.getResultList();
		
		for( Location location : locations )
		{
            System.out.println("Adding 100 properties to location: " + location.getName());
			for( int i=0; i < 100; ++i )
			{
		    	Property property = Generator.property();
		    	property.setLocation(location);
		    	//property.updateTz("Europe/Andorra");
		    	property.setName("jhudjskekdmsnemsndm"+i);
		    	property.setNegativeFeedback(new Double(Math.random()*100).intValue());
		    	property.setPositiveFeedback(new Double(Math.random()*100).intValue());
		    	property.setCurrencyCode((CurrencyCode)dao.get("CurrencyCode","id",22));
		    	property.setOwner((OkapiedUserDetails)dao.get("OkapiedUserDetails","id",1));
		    	property.getPropertyDetails().setFlooring((Flooring)dao.get("Flooring","id",1));
		    	property.getPropertyDetails().setType((PropertyType)dao.get("PropertyType","id",1));
		    	
		    	dao.persist(property);
		    	
		    	for( int j=0; j < 20; ++j )
		    	{
		    		Availability av = new Availability();
		    		Price price = new Price();
		    		av.setAvailableBool(true);
		    		av.setProperty(property);
		    		price.setProperty(property);
		    		Calendar now = Calendar.getInstance();
		    		now.add(Calendar.DAY_OF_YEAR, j*2);
		    		av.setStartDate(now.getTime());
		    		price.setStartDate(now.getTime());
		    		now.add(Calendar.DAY_OF_YEAR, 4);
		    		av.setEndDate(now.getTime());
		    		price.setEndDate(now.getTime());
		    		av.setDayOnlyBool(true);
		    		price.setDayOnlyBool(true);
		    		price.setPrice((float)j);
		    		dao.persist(av);
		    		dao.persist(price);
		    	}
			}
		}
    }
}
