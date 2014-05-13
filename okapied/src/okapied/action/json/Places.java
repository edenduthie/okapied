package okapied.action.json;

import java.util.List;

import okapied.entity.Place;
import okapied.service.PlaceService;

import com.opensymphony.xwork2.ActionSupport;

public class Places extends ActionSupport
{
	static String LIST = "list";
	
    String searchString;
    List<Place> places;
    
    PlaceService placeService;
    
    public void nullServices()
    {
    	placeService = null;
    }
    
    public String list()
    {
    	places = placeService.search(searchString);
    	nullServices();
    	return LIST;
    }

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public PlaceService getPlaceService() {
		return placeService;
	}

	public void setPlaceService(PlaceService placeService) {
		this.placeService = placeService;
	}
}
