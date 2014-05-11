package okapied.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Location is either a populated place (featureCode=P) or administrative region featureCode=A.
 * It corresponds to a city or suburb
 */
@Entity
public class Location implements Place
{
	@Id @GeneratedValue public Integer id;
	public String name;
	public Integer numProperties = 0;
	@ManyToOne(optional=true,fetch=FetchType.EAGER) public Region region;
	@ManyToOne(optional=true,fetch=FetchType.EAGER) public Country country;
	String tz;
	Float lat;
	Float lon;
	Long geonameId;
	String featureCode;
	String featureName;
	
	public Location() {}
	
	public Location(Integer locationId, String locationName, Integer countryId,
			String countryName, Integer regionId, String regionName) 
	{
		this.id = locationId;
		this.name = locationName;
		if( countryId != null ) this.country = new Country(countryId,countryName);
		if( regionId != null ) this.region = new Region(regionId,regionName,countryId,countryName);
		this.numProperties = null;
	}
	
	public Location(Integer locationId, String locationName, Integer countryId,
			String countryName) 
	{
		this.id = locationId;
		this.name = locationName;
		if( countryId != null ) this.country = new Country(countryId,countryName);
		this.region = null;
		this.numProperties = null;
	}
	
	public String getTz() {
		return tz;
	}
	public void setTz(String tz) {
		this.tz = tz;
	}
	public Float getLat() {
		return lat;
	}
	public void setLat(Float lat) {
		this.lat = lat;
	}
	public Float getLon() {
		return lon;
	}
	public void setLon(Float lon) {
		this.lon = lon;
	}
	public Long getGeonameId() {
		return geonameId;
	}
	public void setGeonameId(Long geonameId) {
		this.geonameId = geonameId;
	}
	public String getFeatureCode() {
		return featureCode;
	}
	public void setFeatureCode(String featureCode) {
		this.featureCode = featureCode;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getNumProperties() {
		return numProperties;
	}
	public void setNumProperties(Integer numProperties) {
		this.numProperties = numProperties;
	}
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public String getT()
	{
		return Place.LOCATION;
	}
	
	public void addAProperty()
	{
		++numProperties;
		if( region != null ) region.addAProperty();
		if( country != null ) country.addAProperty();
	}
	
	public void removeAProperty()
	{
		--numProperties;
		if( region != null ) region.removeAProperty();
		if( country != null ) country.removeAProperty();
	}
	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof Location)
		{
			Location otherLocation = (Location) other;
			if( otherLocation.getId().equals(getId())) return true;
			else return false;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
}
