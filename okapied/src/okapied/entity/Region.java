package okapied.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


/**
 * A State/Region
 */
@Entity
public class Region implements Place
{
	@Id @GeneratedValue public Integer id;
	public String name;
	public Integer numProperties = 0;
	public String code;
	@ManyToOne(optional=true,fetch=FetchType.EAGER) public Country country;
	
	public Region() {}
	
	public Region(Integer regionId, String regionName, Integer countryId, String countryName) 
	{
	    this.id = regionId;
	    this.name = regionName;
	    if( countryId != null ) this.country = new Country(countryId,countryName);
	    this.numProperties = null;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	@Override
	public Region getRegion() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getT()
	{
		return Place.REGION;
	}
	
	public void addAProperty()
	{
		++numProperties;
	}
	
	public void removeAProperty()
	{
		--numProperties;
	}
	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if( other instanceof Region )
		{
			Region otherRegion = (Region) other;
			if( otherRegion.getId().equals(getId()) ) return true;
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
