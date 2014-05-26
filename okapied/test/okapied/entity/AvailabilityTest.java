package okapied.entity;

import java.util.Calendar;

import okapied.util.Generator;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AvailabilityTest
{
    @Test
    public void match()
    {
    	Availability av = Generator.availability();
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 5);
    	Assert.assertTrue(av.match(testDate));
    }
    
    @Test
    public void matchNoMatchBelow()
    {
    	Availability av = Generator.availability();
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, -5);
    	Assert.assertFalse(av.match(testDate));
    }
    
    @Test
    public void matchNoMatchAbove()
    {
    	Availability av = Generator.availability();
    	Calendar testDate = Calendar.getInstance();
    	Generator.clear(testDate);
    	testDate.add(Calendar.DAY_OF_YEAR, 100);
    	Assert.assertFalse(av.match(testDate));
    }
}
