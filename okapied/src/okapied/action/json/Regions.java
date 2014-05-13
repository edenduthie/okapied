package okapied.action.json;

import java.util.List;

import okapied.entity.Region;
import okapied.service.RegionService;

public class Regions
{
    private List<Region> regions;
	private String LIST = "list";
    RegionService regionService;
    Integer countryId;
    String searchString;
    
    public void nullServices()
    {
    	regionService = null;
    }
    
    public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public String list()
    {
		if( countryId != null && countryId > 0 && searchString != null && searchString.length() > 0 )
		{
    	    regions = regionService.searchRegions(countryId,searchString);
		}
		nullServices();
    	return LIST;
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
}
