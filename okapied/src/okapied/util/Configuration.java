package okapied.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class Configuration {
	
	Properties configFile;
	private static Configuration instance;
	private static final Logger log = Logger.getLogger(Configuration.class);
	
	Set<String> namespaces;
	Set<String> staticNamespaces;
	Set<String> actions;
	Set<String> commands;
	
	private Configuration()
	{
		try
		{
		    configFile = new Properties();
		    configFile.load(this.getClass().getClassLoader().getResourceAsStream("okapied.properties"));
		    namespaces = parseList(getStringProperty("NAMESPACES"));
		    staticNamespaces = parseList(getStringProperty("STATIC_NAMESPACES"));
		    actions = parseList(getStringProperty("ACTIONS"));
		    commands = parseList(getStringProperty("COMMANDS"));
		}
		catch( IOException ioe )
		{
			log.error(ioe.getMessage());
		}
	}
	
	
	public static Configuration instance() {
		if( instance == null )
		{
		    instance = new Configuration();
		}
		return instance;
	}
	
	public String getStringProperty(String key)
	{
		return configFile.getProperty(key);
	}
	
	public Integer getIntProperty(String key)
	{
		return new Integer(configFile.getProperty(key));
	}
	
	public BigDecimal getBigDecimalProperty(String key)
	{
		return new BigDecimal(configFile.getProperty(key));
	}
	
	public Set<String> parseList(String list)
	{
		String[] splitString = list.split("\\,");
		Set<String> set = new HashSet<String>();
		for( String item : splitString )
		{
			set.add(item);
		}
		return set;
	}
	
	public boolean validNamespace(String namespace)
	{
		if( namespaces.contains(namespace) ) return true;
		else return false;
	}
	
	public boolean validStaticNamespace(String namespace)
	{
		if( staticNamespaces.contains(namespace) ) return true;
		else return false;
	}
	
	public boolean validAction(String action)
	{
		if( actions.contains(action) ) return true;
		else return false;
	}
	
	public boolean validMethod(String command)
	{
		if( commands.contains(command) ) return true;
		else return false;
	}


	public Properties getConfigFile() {
		return configFile;
	}


	public void setConfigFile(Properties configFile) {
		this.configFile = configFile;
	}


	public double getDoubleProperty(String key) {
		return new Double(configFile.getProperty(key));
	}
	
	public float getFloatProperty(String key) {
		return new Float(configFile.getProperty(key));
	}
}
