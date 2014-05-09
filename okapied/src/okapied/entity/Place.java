package okapied.entity;


public interface Place 
{   
	static String COUNTRY = "C";
	static String REGION = "R";
	static String LOCATION = "L";
	static String PROPERTY = "P";
	
	public Integer getId() ;
    public Integer getNumProperties();
    public String getName();
    public Country getCountry();
    public Region getRegion();
    public String getT();
    public Location getLocation();
}
