package okapied.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileFormatScriptCities {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Map<String,Integer> countries = new HashMap<String,Integer>();
		Map<String,Set<String>> cities = new HashMap<String,Set<String>>();
		
        try {
        	File file = new File("D:\\code\\okapied\\okapied\\scripts\\postgres\\countryInfo_formatted.txt");
        	FileInputStream fis = new FileInputStream(file);
        	InputStreamReader in = new InputStreamReader(fis, "UTF-8"); 
        	BufferedReader br = new BufferedReader(in);
			String line;
			int i = 0;
			while( (line = br.readLine()) != null )
			{
				++i;
				String[] splitLine = line.split(",");
				countries.put(splitLine[0], i);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
        	File file = new File("D:\\code\\okapied\\okapied\\scripts\\postgres\\cities.txt");
        	FileInputStream fis = new FileInputStream(file);
        	InputStreamReader in = new InputStreamReader(fis, "UTF-8"); 
        	BufferedReader br = new BufferedReader(in);
			File out = new File("D:\\code\\okapied\\okapied\\scripts\\postgres\\cities.sql");
			FileOutputStream fos = new FileOutputStream(out);
			OutputStreamWriter fosw = new OutputStreamWriter(fos, "UTF-8");
			PrintWriter bw = new PrintWriter(fosw);
			String line;
			int i = 0;
			while( (line = br.readLine()) != null )
			{
				String[] splitLine = line.split(",");
				Set<String> countryCities = cities.get(splitLine[0]);
				if( cities.get(splitLine[0]) == null )
				{
					countryCities = new HashSet<String>();
					cities.put(splitLine[0], countryCities);
				}
				if( !countryCities.contains(splitLine[1]) )
				{
					countryCities.add(splitLine[1]);
				    String escapedName = splitLine[1].replaceAll("'", "''");
				    String outline = "INSERT INTO region(id,name,country_id,type_id) VALUES("+ i + ",'" +
				    escapedName + "'," + countries.get(splitLine[0]) + ",1);";
				    bw.println(outline);
				    bw.flush();
				    ++i;
				}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
