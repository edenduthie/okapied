package okapied.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Country implements Place
{
	@Id @GeneratedValue public Integer id;
	public String name;
	public Integer numProperties = 0;
	public String code;
	
	public Country() {}
	
	public Country(Integer countryId, String countryName) 
	{
		this.id = countryId;
		this.name = countryName;
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
	@Override
	public Country getCountry() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Region getRegion() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getT()
	{
		return Place.COUNTRY;
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
}
