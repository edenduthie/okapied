function Av(calendarId,propertyId,contextRoot)
{
	this.calendarId = calendarId;
	this.startMonth = null;
	this.startYear = null;
	this.numPageChanges = 0;
	this.maxPageChanges = 6;
	this.numWaits = 0;
	this.propertyId = propertyId;
	this.pageDates = null;
	this.prices = null;
	this.calendar = null;
	this.bookingIds = null;
	this.pollingInterval = 60000;
	this.allDates = new Array();
	this.avDates = new Array();
	this.reservedDates = new Array();
	this.contextRoot = contextRoot;
	this.outOfDates;
};

Av.prototype.render = function()
{	
	this.renderCal();
	var that = this;
	// set the clear all button actions
	$('#available-button img').click(function(){
		that.clearAv('1');
	});
	$('#unavailable-button img').click(function(){
		that.clearAv('0');
	});
	$('#clear-prices-button img').click(function(){
		that.clearAv('1',true);
	});
};

Av.prototype.renderCal = function()
{	
	that = this;
    var calendar = new YAHOO.widget.CalendarGroup("cal1",this.calendarId, {PAGES:this.maxPageChanges,MULTI_SELECT:true}); 
    this.calendar = calendar;
    calendar.render();
    calendar.cfg.queueProperty("navigator",true,false);
    calendar.changePageEvent.subscribe(function(event,dates){
	    month = dates[1].getMonth();
	    year = dates[1].getFullYear();
	    that.retrieveAvailability(month,year);
	});
//    calendar.selectEvent.subscribe(function(event,dates) {
//    	that.dateSelected(dates,index);
//    });
    currentDate = new Date();
    this.pageDates = new Object();
    this.pageDates.month = currentDate.getMonth();
    this.pageDates.year = currentDate.getFullYear();
    this.retrieveAvailabilityFinal(currentDate.getMonth(),currentDate.getFullYear());
    this.startPollingLoop();
};

Av.prototype.setStartDate = function(month,year)
{
	if( this.startYear == null )
	{
		this.startYear = year;
		this.startMonth = month;
	}
    else if( this.startYear > year )
	{
		this.startYear = year;
		this.startMonth = month;
	}
	else if( this.startYear == year )
	{
	    if( this.startMonth > month )
	    {
	    	this.startYear = year;
	    	this.startMonth = month;
	    }
	}
};

Av.prototype.clearPages = function()
{
    this.startYear = null;
    this.startMonth = null;
    this.numPageChanges = 0;
}

Av.prototype.retrieveAvailability = function(month,year)
{
	++this.numPageChanges;
	this.setStartDate(month,year);
	if( this.numPageChanges == this.maxPageChanges )
	{
		this.retrieveAvailabilityFinal(this.startMonth,this.startYear);
		this.clearPages();   
	}
};

Av.prototype.retrieveAvailabilityFinal = function(month,year)
{
	that = this;
    var url = this.contextRoot + '/JSON/Availability/list/?propertyId=' +
        this.propertyId + 
	    '&month=' + month +
	    '&year=' + year +
	    '&numMonths=' + this.maxPageChanges +
	    '&detailed=1';
	this.showWait();
	var responseSuccess = function(data) {
	    that.showAvailability(data);
	};
	var responseFailure = function(data) {
	    that.hideWait();
	};
	var callback = {
	    success:responseSuccess,
	    failure:responseFailure,
	    cache:false
	};
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
};

Av.prototype.showAvailability = function(argument)
{	
	var data = YAHOO.lang.JSON.parse(argument.responseText);
	this.pageDates = data;
	var currentDate = new Date(data.year,data.month,1,12,0,0,0);
	this.allDates = new Array();
	this.avDates = new Array();
	this.reservedDates = new Array();
	var show = "";
	this.prices = new Array();
	this.bookingIds = new Array();
	this.outOfDates = new Array();
	for( i=0; i < data.days.length; ++i  )
	{
		day = data.days[i];
		var displayMonth = currentDate.getMonth()+1;
	    var dateString = displayMonth + "/" + currentDate.getDate() + "/" + currentDate.getFullYear();
	    if( day.outOfPeriod )
	    {
	    	this.outOfDates[dateString] = true;
	    }
	    else
	    {
	    	this.outOfDates[dateString] = false;
	    }
		if( !day.available && day.bookingId == null)
		{
		    this.allDates.push(new Date(currentDate.getTime())); 
		}
		else if( day.bookingId != null )
		{
		    this.reservedDates.push(new Date(currentDate.getTime())); 
		    this.bookingIds[dateString] = day.bookingId;
		}
		if( day.price === undefined ) {}
		else
		{
		    this.prices[dateString] = day.price;
		}
		if( day.available )
		{
			this.avDates.push(new Date(currentDate.getTime()));
		}
		
		var oneDayInMillis = 1000*60*60*24;
		var newTime = currentDate.getTime() + oneDayInMillis;
		currentDate.setTime(newTime);
	}
	var calendar = this.calendar;
    this.addRenderers();
    calendar.prices = this.prices;
    calendar.outOfDates = this.outOfDates;
	calendar.render();
	this.hideWait();
};

Av.prototype.addRenderers = function()
{
	var calendar = this.calendar;
	calendar.bookingIds = this.bookingIds;
	calendar.removeRenderers();
    calendar.addRenderer(this.arrayToCSV(this.allDates), this.unavailableCellRenderer);
    calendar.addRenderer(this.arrayToCSV(this.reservedDates), this.bookedCellRenderer);
    calendar.addRenderer(this.arrayToCSV(this.avDates), this.avCellRenderer);
};

Av.prototype.arrayToCSV = function(param)
{
	var csv = '';
	for( var i=0; i < param.length; ++i )
	{
		var date = param[i];
		var displayMonth = date.getMonth()+1;
	    var dateString = displayMonth + "/" + date.getDate() + "/" + date.getFullYear();
	    if( csv.length > 0 ) csv += ",";
	    csv += dateString;
	}
	return csv;
	
};

Av.prototype.unavailableCellRenderer = function(workingDate, cell)
{
	var displayString = dateToDisplayString(workingDate);
	var content;
	content = '<div class="unavailable-cal" ';
	if( !this.outOfDates[displayString] )
	{
		content +=
	    ' onclick="av.toggleAv('
		+ workingDate.getTime() + ",1"
		+ ')';
	}
	content +=
		'"><a class="unavail-a" href="javascript:void()">' 
	    + workingDate.getDate() + '</a></div>';
    $(cell).html(content);
    $(cell).html(content);
    return YAHOO.widget.Calendar.STOP_RENDER;
};

Av.prototype.bookedCellRenderer = function(workingDate, cell)
{
	var displayString = dateToDisplayString(workingDate);
	var bookingId = this.bookingIds[displayString];
	content = '<div class="reserved-cal">';
	if( bookingId != null ) content += '<a title="View Booking Details" href="../ViewBookingOwner/list?id=' + bookingId + '" >';
	content += workingDate.getDate();
	if( bookingId != null ) content += "</a>";
	content += '</div>';
    $(cell).html(content);
    return YAHOO.widget.Calendar.STOP_RENDER;
};

dateToDisplayString = function(date)
{
	var displayMonth = date.getMonth()+1;
    return dateString = displayMonth + "/" + date.getDate() + "/" + date.getFullYear();
};

Av.prototype.avCellRenderer = function(workingDate, cell)
{
	var content = '<div class="av-cal" onclick="av.toggleAv('
		+ workingDate.getTime() + ",0"
		+ ')"><a class="date-cell" href="javascript:void()">' 
	    + workingDate.getDate() + '</a>'
	    + '<a class="price-cell" href="javascript:void(0)">'
	    + this.prices[dateToDisplayString(workingDate)]
	    + '</a>'
	    + '</div>';
	
    $(cell).html(content);
    return YAHOO.widget.Calendar.STOP_RENDER;
};

Av.prototype.validatePrice = function(price)
{
	if( price == null || price.length <= 0) 
	{
		alert("Enter a price in the text box before clicking on a date to set the price.");
		return false;
	}
	if (/^\d*$/.test(price))
	{
	    return true;
	}
	else
	{
		alert("The price must be a whole number.");
		return false;
	}
}

Av.prototype.toggleAv = function(time,isAvailable)
{
    var date = new Date(time);
    var price = null;
    if( $('#set-price').attr('checked') )
    {
    	price = $('#base-price-text').val();
        if( !this.validatePrice(price) ) return;
    	isAvailable = true;
    }
    that = this;
    var url = this.contextRoot + '/JSON/SaveAvailability/list/?propertyId=' +
        this.propertyId +
        '&day=' + date.getDate() +
	    '&month=' + date.getMonth() +
	    '&year=' + date.getFullYear() +
	    '&av=';
    if( isAvailable ) url += "1";
    else url += "0";
    if( price != null && price.length > 0 ) url += "&price=" + price; 
	this.showWait();
	var responseSuccess = function(data) {
		that.hideWait();
		data = YAHOO.lang.JSON.parse(data.responseText);
		if( data.message != 'success' )
		{
			alert(data.message);
			that.retrieveCurrentAvailability();
		}
	};
	var responseFailure = function(data) {
		alert('There was an error, please try again');
	    that.hideWait();
	    that.retrieveCurrentAvailability();
	};
	var callback = {
	    success:responseSuccess,
	    failure:responseFailure,
	    cache:false
	};
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
	var data = date;
	data.av =  isAvailable;
	if( price == null || price.length <= 0 )
	{
	    this.changeDateDisplay(data);
	}
	else
	{
		this.changePriceDisplay(date,price);
	}
};

Av.prototype.changeDateDisplay = function(data)
{
	if( data.av == 1 )
    {
		this.remove(this.allDates,data);
		this.add(this.avDates,data);
    }
	else
	{
	    this.remove(this.avDates,data);
	    this.add(this.allDates,data);
	}
	this.addRenderers();
	this.calendar.render();
};

Av.prototype.changePriceDisplay = function(date,price)
{
    this.prices[dateToDisplayString(date)] = price;
	this.addRenderers();
	this.calendar.render();
};

Av.prototype.remove = function(theArray,data)
{
	for( var i=0; i < theArray.length; ++i )
	{
		var date = theArray[i];
		if( date.getMonth() == data.getMonth() &&
			date.getFullYear() == data.getFullYear() &&
			date.getDate() == data.getDate() )
		{
			theArray.splice(i,1);
		}
	}
};

Av.prototype.add = function (theArray,data)
{
	//var date = new Date(data.year,data.month,data.day,12,0,0,0);
	theArray.push(data);
};

Av.prototype.showWait = function()
{
	++this.numWaits;
	if( this.numWaits == 1 )
    {
	    $('.app-canvas-content .wait img').show();
    }
};

Av.prototype.hideWait = function()
{
	--this.numWaits;
	if( this.numWaits == 0 )
    {
	    $('.app-canvas-content .wait img').hide();
    }
};

Av.prototype.startPollingLoop = function()
{
	var that = this;
	setTimeout(function() {that.retrieveCurrentAvailabilityPoll();},this.pollingInterval);
};

Av.prototype.retrieveCurrentAvailabilityPoll = function()
{
	var date = this.pageDates;
	if( date != null )
	{
	    this.retrieveAvailabilityFinal(date.month, date.year);
		var that = this;
		setTimeout(function() {that.retrieveCurrentAvailability();},this.pollingInterval);
	}
};

Av.prototype.retrieveCurrentAvailability = function()
{
	var date = this.pageDates;
	if( date != null ) this.retrieveAvailabilityFinal(date.month, date.year);
};

Av.prototype.getMonthClick = function(date)
{
	var clickVal = ' onclick="av.toggleAvMonth('+date.getTime()+')" ';
	return clickVal;
};

Av.prototype.toggleAvMonth = function(time)
{
	var date = new Date(time);
    
    var price = null;
    if( $('#set-price').attr('checked') )
    {
    	price = $('#base-price-text').val();
    	if( price == null || price.length <= 0) 
    	{
    		alert("Enter a price in the text box before clicking on a date to set the price.");
    		return;
    	}
    }
    that = this;
    var url = this.contextRoot + '/JSON/SaveAvailability/list/?propertyId=' +
        this.propertyId +
	    '&month=' + date.getMonth() +
	    '&year=' + date.getFullYear();
    if( price != null && price.length > 0 ) url += "&price=" + price; 
	this.showWait();
	var responseSuccess = function(data) {
		that.hideWait();
		var data = YAHOO.lang.JSON.parse(data.responseText);
		if( data.message == null || data.message.length <= 0 || data.message != 'success' )
		{
			alert(data.message);
		}
	    that.retrieveCurrentAvailability();
	};
	var responseFailure = function(data) {
		alert('There was an error, please try again');
	    that.hideWait();
	    that.retrieveCurrentAvailability();
	};
	var callback = {
	    success:responseSuccess,
	    failure:responseFailure,
	    cache:false
	};
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
};

Av.prototype.clearAv = function(available,price)
{	
	if( price )
	{
		price = $("#base-price-text").val();
		if( price == null || price.length <= 0 ) price = -1;
	}
	that = this;
	var url = this.contextRoot + '/JSON/SaveAvailability/clear/?propertyId=' +
	    this.propertyId +
	    '&av=' + available; 
	if( price ) url += "&price=" + price;
	this.showWait();
	var responseSuccess = function(data) {
		that.hideWait();
		var data = YAHOO.lang.JSON.parse(data.responseText);
		if( data.message == null || data.message.length <= 0 || data.message != 'success' )
		{
			alert(data.message);
		}
	    that.retrieveCurrentAvailability();
	};
	var responseFailure = function(data) {
		alert('There was an error, please try again');
	    that.hideWait();
	    //that.retrieveCurrentAvailability();
	};
	var callback = {
	    success:responseSuccess,
	    failure:responseFailure,
	    cache:false
	};
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
};

Av.prototype.toggleAvWeekDay = function(dayCode)
{
    dayCode = dayCode + 1;
    var price = null;
    if( $('#set-price').attr('checked') )
    {
    	price = $('#base-price-text').val();
    	if( price == null || price.length <= 0) 
    	{
    		alert("Enter a price in the text box before clicking on a date to set the price.");
    		return;
    	}
    }
    that = this;
    var url = this.contextRoot + '/JSON/SaveAvailability/list/?propertyId=' +
        this.propertyId +
	    '&dayOfWeek=' + dayCode;
    if( price != null && price.length > 0 ) url += "&price=" + price; 
	this.showWait();
	var responseSuccess = function(data) {
		that.hideWait();
		var data = YAHOO.lang.JSON.parse(data.responseText);
		if( data.message == null || data.message.length <= 0 || data.message != 'success' )
		{
			alert(data.message);
		}
	    that.retrieveCurrentAvailability();
	};
	var responseFailure = function(data) {
		alert('There was an error, please try again');
	    that.hideWait();
	    //that.retrieveCurrentAvailability();
	};
	var callback = {
	    success:responseSuccess,
	    failure:responseFailure,
	    cache:false
	};
	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
};
