package okapied.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class DateUtil 
{
	
    public static boolean weekend(Calendar calendar)
    {
    	int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    	if( dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY ) return true;
    	else return false;
    }
    
    public static Calendar clearTime(Calendar cal)
    {
    	cal.set(Calendar.HOUR_OF_DAY, 12);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal;
    }
    
    public static Calendar getCurrentCalendarCompareTZ()
    {
        return new GregorianCalendar(TimeZone.getTimeZone(Configuration.instance().getStringProperty("COMPARE_TIME_ZONE")));
    }
    
    public static List<Calendar> getPeriod(Calendar startDate, Calendar endDate)
    {	
    	List<Calendar> period = new ArrayList<Calendar>();
    	clearTime(startDate);
    	clearTime(endDate);
    	
    	Calendar current = (Calendar) startDate.clone();
    	for( int i=0; current.compareTo(endDate) < 0; ++i )
    	{
    		current = (Calendar) startDate.clone();
    		current.add(Calendar.DAY_OF_YEAR, i);
    		period.add(current);
    	}
    	return period;
    }
    
    public static List<Calendar> getPeriodExcludingEnd(Calendar startDate, Calendar endDate)
    {
    	List<Calendar> list = getPeriod(startDate,endDate);
    	list.remove(list.size()-1);
    	return list;
    }
    
    public static Integer differenceInDays(Calendar startDate, Calendar endDate)
    {
    	long difference = endDate.getTimeInMillis() - startDate.getTimeInMillis();
    	double millisInADay = 1000*60*60*24;
    	double differenceInDays = ((double) difference)/millisInADay;
    	return new Double(differenceInDays).intValue();
    }
}
