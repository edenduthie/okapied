package okapied.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.swing.text.html.HTMLEditorKit;

public class StayzEnquiry {
	
	String nextURL = "http://www.stayz.com.au/accommodation";
	String lastURL = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StayzEnquiry enquiry = new StayzEnquiry();
		try
		{ 
			enquiry.readNumbersAndPost();
		}
		catch( IOException ioe )
		{
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	public void readNumbersAndPost() throws IOException
	{
		BufferedWriter processed = new BufferedWriter(new FileWriter("Processed.txt",true));
		
		BufferedReader reader = new BufferedReader(new FileReader("StayzPropertyNumbers.txt"));
		String line = null;
		while( (line = reader.readLine()) != null)
		{
			String number = line.trim();
			if( number.length() > 0 )
			{
				processed.append(number);
				processed.newLine();
				processed.flush();
				post(number);
			}
		}
		
		reader.close();
		processed.close();
	}
	
	public void post(String propertyId) throws IOException
	{
		System.out.println("Posting to property: " + propertyId);
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		String encoding = "UTF-8";
		
		map.put("numNights","10");
		map.put("Seriousness","General");
		map.put("calendarType","local");
		map.put("startDate","16/08/2011");
		map.put("endDate","26/08/2011");
		map.put("numGuestsAdults","3");
		map.put("numGuestsChildren","0");
		map.put("firstName","Sandra");
		map.put("lastName","Peterson");
		map.put("email","speterson@okapied.com");
		map.put("confirmEmail","speterson@okapied.com");
		map.put("phone","0398765423");
		map.put("countryId","6");
		map.put("postcode", "3087");
		map.put("ageRange","35 to 49");
		map.put("foundStayzId","9");
		map.put("comments","Hi,\n\nWhat is your availability for September? Do you offer discounts for longer periods?\n\nSandra Peters\nwww.okapied.com\nOkapied - Instant Booking of Holiday Accommodation and Free Listing");
		map.put("agreeTc","true");
		map.put("agreeDealsNotification","true");
		map.put("honey","towels");
		
		// Construct data
	    String data = URLEncoder.encode("propertyId", encoding) + "=" + URLEncoder.encode(propertyId,encoding);
	    
	    for( String key : map.keySet())
	    {
	    	data += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(map.get(key),encoding);
	    }

//	    // Send data
	    URL url = new URL("http://www.stayz.com.au:80/booking.action");
	    URLConnection conn = url.openConnection();
	    System.setProperty("http.agent", "");
	    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	    conn.setDoOutput(true);
	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	    wr.write(data);
	    wr.flush();

	    // Get the response
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    BufferedWriter writer = new BufferedWriter(new FileWriter("results/" + propertyId + ".html"));
	    String line;
	    while ((line = rd.readLine()) != null) {
	        writer.append(line);
	        writer.newLine();
	    }
	    writer.close();
	    wr.close();
	    rd.close();
	}
	
	public void getNumbers() throws IOException
	{
		FileWriter fstream = new FileWriter("StayzPropertyNumbers.txt",true);
		BufferedWriter out = new BufferedWriter(fstream);
		
		for( int i=1; i <= 1197; ++i )
		{
			String urlString = "http://www.stayz.com.au/accommodation/"+i+"?view=list";
			if( i == 1 )
			{
				urlString = "http://www.stayz.com.au/accommodation";
			}
			System.out.println("Processing url: " + urlString);
		    URL url = new URL(urlString);
		    InputStreamReader reader = new InputStreamReader(url.openStream());
			HTMLEditorKit.Parser parse = new HTMLParse().getParser();
			parse.parse(reader,new StayzParserCallback(out,this),true);
			out.flush();
		}
		
		out.close();
	}

	public String getNextURL() {
		return nextURL;
	}

	public void setNextURL(String nextURL) {
		this.lastURL = this.nextURL;
		this.nextURL = nextURL;
	}
}
