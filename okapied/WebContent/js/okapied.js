/*
function removeContextRootPrefix(elementId,contextRoot)
{
	if( $('#'+elementId).length == 1 )
	{
	    var newVal = $('#'+elementId).val();
		newVal = newVal.substring(contextRoot.length,newVal.length);
		$('#'+elementId).val(newVal);
	}
}
*/

$(document).ready(function() {
	$("form br").each(function(){
		$(this).remove();
	});
});

function Okapied(offset,limit,contextRoot)
{
	this.place = new Array();
	this.textId = "search_form_placeText";
	this.autoId = "region-empty-results-container";
	this.offset = offset;
	this.limit = limit;
	this.hasAtLeastOneParam = false;
	this.size = null;
	this.contextRoot = contextRoot;
	this.firstPage = 1;
};
function gotoURL(url)
{
	alert('going');
	window.location = url;
}

Okapied.prototype.showBrowse = function()
{
	var url = window.location.protocol + "//" + location.host + this.contextRoot + "/Browse/Browse/list";
	window.location.href = url;
	return false;
}


Okapied.prototype.render = function()
{
	this.displayPlaces();
	
	var that = this;
	$("#top-search-button").click(function(){
		if( that.place.name == null || (that.place.name.length<=0))
		{
			//alert("No matches found");
            that.showBrowse();
			return false;
		}
		else 
		{
			var url = that.prepareUrl();
			window.location.href = url;
		}
	});
	$('.input-region div .wwctrl input').keypress(function(event) {
        //if (event.which == '13') $("#top-search-button").click();
		if( event.which == '13' ) 
		{
			if( that.place.name == null || (that.place.name.length<=0))
			{
				//alert("No matches found");
				that.showBrowse();
				return false;
			}
			else 
			{
				var url = that.prepareUrl();
				window.location.href = url;
			}
			return false;
		}
    });
};

Okapied.prototype.prepareUrl = function()
{
	var searchString = '';
	if( this.place.name == null )
	{
		alert("Please select where to search");
		return;
	}
	else if( this.place['t'] == 'C' )
	{
	    searchString = encodeURIComponent(this.place['name']);
	}
	else if( this.place['t'] == 'R' )
	{
		searchString = encodeURIComponent(this.place['country.name']) + "/" + encodeURIComponent(this.place['name']);
	}
	else if( this.place['t'] == 'L' )
	{
		searchString = encodeURIComponent(this.place['country.name']) + "/";
		if( typeof this.place['region.name'] != 'undefined' )
		    searchString += encodeURIComponent(this.place['region.name']);
		searchString += "/" + encodeURIComponent(this.place['name']);
	}
	else if( this.place['t'] == 'P' )
	{
		searchString = encodeURIComponent(this.place['country.name']) + "/";
		if( typeof this.place['region.name'] != 'undefined' )
		    searchString += encodeURIComponent(this.place['region.name']);
		searchString += "/" + encodeURIComponent(this.place['location.name']) + "/" + encodeURIComponent(this.place['name']);
	}
	if( this.offset != null && this.limit != null)
	{
	    searchString += "?offset=" + this.offset + "&limit=" + this.limit + "&firstPage=" + this.firstPage;
	    this.hasAtLeastOneParam = true;
	}
    var url = window.location.protocol + "//" + location.host + this.contextRoot + "/" + searchString;
    return url;
};

Okapied.prototype.setCorrectPlaceName = function()
{
	if( this.place['t'] == 'C' )
	{
		this.place['name'] = this.place['country.name'];
	}
	if( okapied.place['t'] == 'R' )
	{
		this.place['name'] = this.place['region.name'];
	}
};

Okapied.prototype.displayPlaces = function()
{
	var that = this;
	YAHOO.example.ItemSelectHandler = function() {
			// Use a LocalDataSource
			var oDS = new YAHOO.util.XHRDataSource(that.contextRoot+'/JSON/Places/list?');
			oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;

			//oDS.responseSchema = {fields : ["name", "id", "type.name"]};

			oDS.responseSchema = {
				resultsList : "places", // String pointer to result data
				// Field order doesn't matter and not all data is required to have a field
				fields : [ {
					key : "name"
				}, {
					key : "id"
				}, {
					key : "t"
				}, {
					key : "country.name"
				}, {
					key : "region.name"
				}, {
					key : "location.name"
				} ]
			};

			oDS.maxCacheEntries = 100; 

			// Instantiate the AutoComplete
			var oAC = new YAHOO.widget.AutoComplete(that.textId,
					that.autoId, oDS);
			oAC.resultTypeList = false;

			oAC.forceSelection = true;
			//oAC.typeAhead = true;
		    oAC.queryDelay = 0.1;
		    oAC.minQueryLength = 1;
		    oAC.maxResultsDisplayed = 10;

			// enable use of the text box again
			$('#'+that.textId).removeAttr('disabled');
			
			oAC.generateRequest = function(sQuery) { 
	            return "searchString="+sQuery;
	        }; 

			oAC.formatResult = function(oResultData, sQuery, sResultMatch) {
				var result;
				if( oResultData['t'] == 'P' )
				{
				    result = sResultMatch + " (" + oResultData['location.name'] + ", ";
				    if( typeof oResultData['region.name'] != 'undefined')
				        result += oResultData['region.name'] + ", ";
				    result += oResultData['country.name'] + ")";
				}
				else if( oResultData['t'] == 'L' )
				{
				    result = sResultMatch + " (";
				    if( typeof oResultData['region.name'] != 'undefined')
				        result += oResultData['region.name'] + ", ";
				    result += oResultData['country.name'] + ")";
				}
				else if( oResultData['t'] == 'R' )
				{
					result = sResultMatch + " (" + oResultData['country.name'] + ")";
				}
				else
				{
					result = sResultMatch;
				}
				return result;
			};

	        var regionHandler = function(sType, aArgs) {
	            var myAC = aArgs[0]; // reference back to the AC instance
	            var elLI = aArgs[1]; // reference to the selected LI element
	            var oData = aArgs[2]; // object literal of selected item's result data
	            
	            that.place = oData;
	        };
	        oAC.itemSelectEvent = new YAHOO.util.CustomEvent("itemSelect", oAC);
	        oAC.itemSelectEvent.subscribe(regionHandler);

			return {
				oDS : oDS,
				oAC : oAC
			};
		}();
}