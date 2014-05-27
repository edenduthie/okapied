package okapied.datagenerator;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import okapied.BaseTest;
import okapied.dao.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class PerformanceTest extends BaseTest
{
     @Autowired
     DAO dao;
     
     @Test
     public void performance()
     {
//    	 String queryString = "from Availability where property.id=:propertyId";
//    	 Query query = dao.getEntityManager().createQuery(queryString);
//    	 query.setParameter("propertyId",294);
//    	 List<Object> results = query.getResultList();
//    	 Calendar before = Calendar.getInstance();
//    	 query = dao.getEntityManager().createQuery(queryString);
//    	 query.setParameter("propertyId",294);
//    	 results = query.getResultList();
//    	 Calendar after = Calendar.getInstance();
//    	 Long diff = after.getTimeInMillis() - before.getTimeInMillis();
//    	 System.out.println("EXECUTION1: " + diff);	
     }
}
