package okapied.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import okapied.entity.Country;
import okapied.entity.Location;
import okapied.entity.Region;

public class FileFormatScriptFullCities {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Map<String,Country> countriesMap = new HashMap<String,Country>();
		List<Country> countries = new ArrayList<Country>();
		
		Map<String,Region> regionsMap = new HashMap<String,Region>();
		List<Region> regions = new ArrayList<Region>();
		
		// load countries
        try {
        	File file = new File("D:\\code\\okapied\\okapied\\scripts\\data\\countryInfo_formatted.txt");
        	FileInputStream fis = new FileInputStream(file);
        	InputStreamReader in = new InputStreamReader(fis, "UTF-8"); 
        	BufferedReader br = new BufferedReader(in);
			String line;
			int i = 0;
			while( (line = br.readLine()) != null )
			{
				++i;
				String[] splitLine = line.split(",");
				Country country = new Country();
				country.setCode(splitLine[0]);
				country.setName(splitLine[1]);
				country.setNumProperties(0);
				country.setId(i);
				countries.add(country);
				countriesMap.put(country.code,country);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			File fileOut = new File("D:\\code\\okapied\\okapied\\scripts\\mysql\\countries.sql");
			FileOutputStream fos = new FileOutputStream(fileOut);
			OutputStreamWriter fosw = new OutputStreamWriter(fos, "UTF-8");
			PrintWriter bw = new PrintWriter(fosw);
			
			int i=0;
			for( Country country : countries )
			{
				++i;
				String outline = "INSERT INTO Country(id,code,name,numProperties) values("
				    + i + ","
				    + "'" + country.getCode() + "'," 
				    + "'" + country.getName() + "',"
				    + "0"
				    + ");";
				 byte[] out = outline.getBytes("UTF-8");
				 fos.write(out);
				 fos.flush();
			}
			fos.close();
		}
		catch(IOException e)
		{
			System.out.println("Error with countries file");
		}
		
		//load regions
       try {
        	File file = new File("D:\\code\\okapied\\okapied\\scripts\\data\\admin1Codes.txt");
        	FileInputStream fis = new FileInputStream(file);
        	InputStreamReader in = new InputStreamReader(fis, "UTF-8"); 
        	BufferedReader br = new BufferedReader(in);
			String line;
			int i = 1;
			while( (line = br.readLine()) != null )
			{
				if( i == 1 ) line = "AD.00	Andorra (general)";
				String[] splitLine = line.split("\t");
				String countryCode = splitLine[0].substring(0,2);
				String regionCode = splitLine[0].substring(3,splitLine[0].length());
				Country country = countriesMap.get(countryCode);
				if( country != null )
				{
					String regionName;
					if( splitLine.length > 1 ) regionName = splitLine[1];
					else regionName = country.getName();
					Region region = new Region();
					region.setCode(regionCode);
					region.setName(regionName.replaceAll("'", "''"));
					region.setCountry(country);
					region.setId(i);
					region.setCountry(country);
					regions.add(region);
					regionsMap.put(splitLine[0],region);
					++i;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			File fileOut = new File("D:\\code\\okapied\\okapied\\scripts\\mysql\\regions.sql");
			FileOutputStream fos = new FileOutputStream(fileOut);
			OutputStreamWriter fosw = new OutputStreamWriter(fos, "UTF-8");
			PrintWriter bw = new PrintWriter(fosw);
			
			int i=0;
			for( Region region : regions )
			{
				++i;
				String outline = "INSERT INTO Region(id,code,name,numProperties,country_id) values("
				    + region.getId() + ","
				    + "'" + region.getCode() + "'," 
				    + "'" + region.getName() + "',"
				    + "0,"
				    + region.getCountry().getId()
				    + ");";
				 byte[] out = outline.getBytes("UTF-8");
				 fos.write(out);
				 fos.flush();
			}
			fos.close();
		}
		catch(IOException e)
		{
			System.out.println("Error with regions file");
		}
		
		PrintWriter bw;
		File fileOut;
		FileOutputStream fos;
		
		try
		{
			fileOut = new File("D:\\code\\okapied\\okapied\\scripts\\mysql\\locations.sql");
			fos = new FileOutputStream(fileOut);
//			OutputStreamWriter fosw = new OutputStreamWriter(fos, "UTF-8");
//			bw = new PrintWriter(fosw);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
        int i=1;
		for( Country country : countries )
        {
			//if( !country.code.equals("AU") ) continue;
			
			Set<String> cities = new HashSet<String>();
			Map<Integer,Set<String>> locations = new HashMap<Integer,Set<String>>();
			
			//System.out.println("Loading country: " + country.getCode());
			try
			{
			    String filename = "D:\\code\\okapied\\okapied\\scripts\\data\\"+country.getCode()+".zip";
			    File file = new File(filename);
			    if( !file.exists() )
			    {
			    	System.out.println("Downloading file");
				    URL url = new URL("http://download.geonames.org/export/dump/" + country.getCode() + ".zip");
				    URLConnection uc = url.openConnection();
				    uc.connect();
				    System.out.println(uc.getContentLength());
				    InputStream is = uc.getInputStream();
				    FileOutputStream out = new FileOutputStream(filename);
				    
				    byte[] buf = new byte[1024];
				    int len;
				    while ((len = is.read(buf)) > 0) {
				        out.write(buf, 0, len);
				    }
				    is.close();
				    out.close();
			    }
			    System.out.println("Reading zip file");
			    ZipFile zip = new ZipFile(filename);
			    Enumeration entries = zip.entries();
			    while( entries.hasMoreElements() )
			    {
			    	ZipEntry entry = (ZipEntry) entries.nextElement();
			    	if( entry.getName().equals(country.getCode()+".txt"))
			    	{
			    		System.out.println("Reading entry: " + entry.getName());
			    		BufferedReader br = new BufferedReader(
			                    new InputStreamReader(zip.getInputStream(entry),"UTF-8"));
			    		String line;
			    		while( (line = br.readLine()) != null )
			    		{
			    			String[] splitString = line.split("\t");
                            String city = splitString[1];
                            Float lat = new Float(splitString[4]);
                            Float lon = new Float(splitString[5]);
                            String tz = splitString[17];
                            Integer geonameId = new Integer(splitString[0]);
                            String featureCode = splitString[6];
                            String featureName = splitString[7];
                            String regionCode = country.getCode() + "." + splitString[10];
                            Region region = regionsMap.get(regionCode);
			    			if( featureCode.equals("P") || featureCode.equals("A") )
			    			{	
			    			    Set<String> result = null;
			    			    if( region != null ) result = locations.get(region.getId());
			    			    boolean duplicate = false;

			    			    if( result != null )
			    			    {
			    			    	
			    			    	if( result.contains(city) )
			    			    	{
			    			    		duplicate = true;
			    			    	}
			    			    }
			    				
			    			    if( !duplicate )
			    			    {
			    			    	if( region != null && result == null ) 
			    			        {
			    			    		result = new HashSet<String>();
			    			    	    locations.put(region.getId(),result);
			    			        }
			    			    	if( result != null ) result.add(city);
			    			    	
				    				cities.add(city);
								    String escapedName = city.replaceAll("'", "''");
								    String outline = "INSERT INTO Location(id,featureCode,featureName,geonameId,lat,lon,name,numProperties,tz,country_id";
								    if( region != null ) outline += ",region_id";
								    outline += ") VALUES("
								    + i + ","
								    + "'" + featureCode + "',"
								    + "'" + featureName + "',"
								    + geonameId + ","
								    + lat + ","
								    + lon + ","
								    + "'" + escapedName + "'," 
								    + "0,"
								    + "'" + tz + "',"
								    + country.getId();
								    if( region != null )
								    {
								    	outline += "," + region.getId();
								    }
								    outline +=  ");";
								    //bw.println(outline);
								    //bw.flush();
								    byte[] out = outline.getBytes("UTF-8");
								    fos.write(out);
								    fos.flush();
								    ++i;
			    			    }
			    			}
			    		}
			    	}
			    }
            }
			catch (Exception e) {
				System.out.println("Failed country: " + country.getCode());
			}
		}
	}
}

