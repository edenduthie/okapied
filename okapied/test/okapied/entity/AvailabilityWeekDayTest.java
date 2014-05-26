package okapied.entity;

import java.util.Calendar;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AvailabilityWeekDayTest 
{
	@Test
    public void match()
    {
    	Calendar now = Calendar.getInstance();
    	AvailabilityWeekDay av = new AvailabilityWeekDay(now.get(Calendar.DAY_OF_WEEK),true);
    	Assert.assertTrue(av.match(now));
    }
	
	@Test
    public void matchFalse()
    {
    	Calendar now = Calendar.getInstance();
    	AvailabilityWeekDay av = new AvailabilityWeekDay(now.get(Calendar.DAY_OF_WEEK),false);
    	Assert.assertTrue(av.match(now));
    }
	
	@Test
    public void matchWrongDay()
    {
    	Calendar now = Calendar.getInstance();
    	AvailabilityWeekDay av = new AvailabilityWeekDay(now.get(Calendar.DAY_OF_WEEK),true);
    	now.add(Calendar.DAY_OF_WEEK,1);
    	Assert.assertFalse(av.match(now));
    }
}
