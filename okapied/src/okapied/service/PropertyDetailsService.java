package okapied.service;

import java.util.List;
import java.util.Set;

import okapied.entity.OkaOption;
import okapied.entity.PropertyDetails;

public class PropertyDetailsService 
{
	/**
	 * Goes through all the option types of the PropertyDetails (kitchen, bathroom, etc..). If these
	 * options are present they are set as selected in the list of options, otherwise they are marked
	 * as not selected.
	 */
    public void parseAllOptions(PropertyDetails pd, List<OkaOption> options)
    {
    	if( pd == null ) return;
    	parseOptionType(pd.getKitchen(),options,OkaOption.OPTION_TYPE_KITCHEN);
    	parseOptionType(pd.getOutdoor(),options,OkaOption.OPTION_TYPE_OUTDOOR);
    	parseOptionType(pd.getBathroomLaundry(),options,OkaOption.OPTION_TYPE_BATHROOM);
    	parseOptionType(pd.getAmmenities(),options,OkaOption.OPTION_TYPE_AM);
    	parseOptionType(pd.getMultimedia(),options,OkaOption.OPTION_TYPE_MULTIMEDIA);
    }
    
    public void parseOptionType(Set<String> stringOptions, List<OkaOption> options, Integer optionType)
    {
    	if( stringOptions == null ) return;
    	for( OkaOption option : options )
    	{
    		if( option.getOptionType().equals(optionType) )
    		{
	    		if( stringOptions.contains(option.getName()) ) option.setIsSelected(true);
	    		else option.setIsSelected(false);
    		}
    	}
    }
}
