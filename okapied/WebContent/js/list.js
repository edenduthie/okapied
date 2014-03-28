function List(methodName,locationName,textId,autoId,contextRoot)
{
	this.numWaits = 0;
	
	this.location = null;
	this.locationName = locationName;
	this.locationId = null;
	this.tz = null;
	this.methodName = methodName;
	this.textId = textId;
	this.autoId = autoId;
	this.locationIdHidden = 'location-id-hidden';
	this.contextRoot = contextRoot;
};

List.prototype.render = function()
{
	this.displayLocations();
	this.setupEvents();
};

List.prototype.setupEvents = function()
{
    $('select[name="propertyTypeId"]').change(function(){
    	if( $(this).val() == 3 || $(this).val() == 4)
    	{
    		$('select[name="bedrooms"]').val(1);
    		$('#bedrooms-select').hide();
    	}
    	else
    	{
    		$('#bedrooms-select').show();
    	}
    });
};

List.prototype.showWait = function()
{
	++this.numWaits;
	if( this.numWaits == 1 )
    {
	    $('.app-canvas-content .wait img').show();
    }
};

List.prototype.hideWait = function()
{
	--this.numWaits;
	if( this.numWaits == 0 )
    {
	    $('.app-canvas-content .wait img').hide();
    }
};

List.prototype.displayLocations = function()
{
	var that = this;
	YAHOO.example.ItemSelectHandler = function() {
			// Use a LocalDataSource
			var oDS = new YAHOO.util.XHRDataSource(that.contextRoot+'/JSON/Locations/list?');
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
				} ]
			};

			oDS.maxCacheEntries = 100; 

			// Instantiate the AutoComplete
			var oAC = new YAHOO.widget.AutoComplete(that.method+that.textId,
					that.autoId, oDS);
			oAC.resultTypeList = false;

			oAC.forceSelection = true;
			//oAC.typeAhead = true;
		    oAC.queryDelay = 0.1;
		    oAC.minQueryLength = 1;
		    oAC.maxResultsDisplayed = 100;

			// enable use of the text box again
			$('#'+that.method+that.textId).removeAttr('disabled');
			
			oAC.generateRequest = function(sQuery) { 
	            return "searchString="+sQuery;
	        }; 

			oAC.formatResult = function(oResultData, sQuery, sResultMatch) {
				var result = '';
				if( oResultData['t'] == 'L' )
				{
				    result = sResultMatch + " (";
				    if( typeof oResultData['region.name'] != 'undefined' )
				        result += oResultData['region.name'] + ",";
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
	            that.location = oData;
	            $('#'+that.locationIdHidden).val(that.location.id);
	        };
	        oAC.itemSelectEvent = new YAHOO.util.CustomEvent("itemSelect", oAC);
	        oAC.itemSelectEvent.subscribe(regionHandler);
	        
	        $('#'+that.method+that.textId).val(that.locationName);

			return {
				oDS : oDS,
				oAC : oAC
			};
		}();
};

List.prototype.listNow = function()
{
    $('#essential-list-form form').submit();	
};