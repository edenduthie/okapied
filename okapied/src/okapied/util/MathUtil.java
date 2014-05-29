package okapied.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

public class MathUtil 
{	
	public static float rollingAverage(int n, float oldAverage, float nextValue) 
	{
		float oldTotal = oldAverage*(n-1);
		float newTotal = oldTotal + nextValue;
		return newTotal/n;
	}
	
	public static float average(float[] values)
	{
		float total = 0f;
		for( Float f : values )
		{
			total += f;
		}
		return total/values.length;
	}
	
    public static BigDecimal roundBigDecimal(double value)
    {
    	return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    public static BigDecimal roundBigDecimal(float value)
    {
    	return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
	public static String generateTrackingKey(Integer bookingId)
	{
		String authString =
		     Calendar.getInstance().getTime().toString() +
		     Math.random() + bookingId;
		String authHex = new Md5PasswordEncoder().encodePassword(authString,null);
		return authHex;
	}
}
