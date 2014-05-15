package okapied.action.browse;

import java.util.List;

import okapied.action.BaseAction;
import okapied.dao.DAO;
import okapied.entity.Country;
import okapied.entity.Location;
import okapied.entity.Region;
import okapied.service.CountryService;
import okapied.service.LocationService;
import okapied.service.RegionService;

public class Browse extends BaseAction
{	
    private List<Country> countries;
    private List<Region> regions;
    private List<Location> locations;
    private String LIST = "list";
    private String REGION = "region";
    private String LOCATION = "location";
    DAO dao;
    Integer countryId;
    Integer regionId;
    RegionService regionService;
    LocationService locationService;
    CountryService countryService;
    Country country;
    Region region;
    
    public String list()
    {
    	countries = countryService.getAllCountriesWithProperties();
    	return LIST;
    }
    
    public String region()
    {
    	regions = regionService.getRegionListByCountry(countryId);
    	country = countryService.get(countryId);
    	return REGION;
    }
    
    public String location()
    {
    	locations = locationService.getLocationsByRegion(regionId);
    	if( locations.size() > 0 )
    	{
    		country = locations.get(0).getCountry();
    		region = locations.get(0).getRegion();
    	}
    	return LOCATION;
    }

	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}
	
	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}

	public RegionService getRegionService() {
		return regionService;
	}

	public void setRegionService(RegionService regionService) {
		this.regionService = regionService;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public Integer getRegionId() {
		return regionId;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}

	public LocationService getLocationService() {
		return locationService;
	}

	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}

	public CountryService getCountryService() {
		return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}
	
	public String getTitle()
	{
		String result =  " - Okapied Holiday Accommodation / Vacation Rentals";
		
		if( countryId == null && regionId == null )
		{
			result = "Country List" + result;
		}
		else if(countryId != null)
		{
			Country country = countryService.get(countryId);
			result = country.getName() + result;
		}
		else if( regionId != null )
		{
			Region region = regionService.get(regionId);
			result = region.getName() + result;
		}
		else
		{
			result = "Browse" + result;
		}
		return result;
	}
	
	public String getDescription()
	{
		return  getTitle() + ". Browse Okapied's listings.";
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}
}
