function Booking(propertyId,elementId,contextRoot)
{
    this.propertyId = propertyId;
    this.elementId = elementId;
    this.calendars = new Array();
    this.pageDates = new Array();
    this.pageDates[0] = null;
    this.pageDates[1] = null;
    this.selectedDates = new Array();
    this.selectedDates[0] = null;
    this.selectedDates[1] = null;
    this.maxIntervalMillis = 1000*60*60*24*60;
    this.maxIntervalDays = 60;
    this.pollingInterval = 60000;
    this.numWaits = 0;
    this.reservationTimeoutSeconds = 300;
    this.currentSec = -1;
    this.prices = new Array();
    this.prices[0] = null;
    this.prices[1] = null;
    this.contextRoot = contextRoot
}

Booking.prototype.render = function()
{
	currentDate = new Date();
	this.renderCal(0,currentDate.getMonth(),currentDate.getFullYear());
	this.renderCal(1,currentDate.getMonth(),currentDate.getFullYear());
	this.setUpDateInput(0);
	this.setUpDateInput(1);
};

Booking.prototype.setUpDateInput = function(index)
{
	that = this;
	$("input#date-"+index).val('');
	$("input#date-"+index).keypress(function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 13) {
		   that.dateSelectedDisplayString($("input#date-"+index).val(), index);
		}
	});
};

Booking.prototype.retrieveAvailability = function(month,year,index)
{
	that = this;
    var url = this.contextRoot + '/JSON/Availability/list/?propertyId=' +
        this.propertyId + 
	    '&month=' + month +
	    '&year=' + year;
	this.showWait();
	var responseSuccess = function(data) {
	    that.showAvailability(data,index);
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

Booking.prototype.unavailableCellRenderer = function(workingDate, cell)
{
	content = '<div class="unavailable-cal">' + workingDate.getDate() + '</div>';
    $(cell).html(content);
    return YAHOO.widget.Calendar.STOP_RENDER;
};

Booking.prototype.reservedCellRenderer = function(workingDate, cell)
{
	content = '<div class="reserved-cal">' + workingDate.getDate() + '</div>';
    $(cell).html(content);
    return YAHOO.widget.Calendar.STOP_RENDER;
};

Booking.prototype.firstDateCellRenderer = function(workingDate, cell)
{
	price = '';
	
	displayMonth = workingDate.getMonth();
	displayMonth += 1;
	dayOfMonth = workingDate.getDate();
	displayYear = workingDate.getFullYear();
    var dateString = displayMonth + "/" + dayOfMonth + "/" + displayYear;
    
    if( this.prices != null ) price = this.prices[dateString];
	
	var content = '<a href="#" alt="'+price+'" title="'+price+'" class="first-day-cal">' 
	    + this.buildDayLabel(workingDate) + '</a>';
	
	$(cell).html(content);
	
	return YAHOO.widget.Calendar.STOP_RENDER;
};

Booking.prototype.showAvailability = function(data,index)
{	
	data = YAHOO.lang.JSON.parse(data.responseText);
	this.pageDates[index] = data;
	var displayMonth = data.month + 1;
	var displayYear = data.year;
	var allDates = "";
	var reservedDates = "";
	var firstDates = "";
	var lastDates = "";
	var firstReservedDates = "";
	var lastReservedDates = "";
	var show = "";
	this.prices[index] = new Array();
	for( i=0; i < data.days.length; ++i  )
	{
		var day = data.days[i];
		var dayOfMonth;
		var dateString;
		var theDate;
		if( day.extraDay )
		{
			// this is the last day of the previous month
			theDate = new Date(displayYear,displayMonth-1,1);
			var firstDayTime = theDate.getTime();
			var millisInADay = 1000*60*60*24;
			var lastDayTime = firstDayTime - millisInADay;
			theDate = new Date(lastDayTime);
			dayOfMonth = theDate.getDate();
			dateString = (theDate.getMonth()+1) + "/" + dayOfMonth + "/" + theDate.getFullYear();
		}
		else
		{
		    dayOfMonth = i;
		    dateString = displayMonth + "/" + dayOfMonth + "/" + displayYear;
		    theDate = new Date(displayYear,displayMonth,dayOfMonth);
		}
	    if( day.firstDayOfBooking || (day.lastDayOfBooking&&!day.extraDay) )
	    {
	    	if( day.firstDayOfBooking && day.lastDayOfBooking )
	    	{
	    		if( day.reserved ) 
	    			reservedDates = this.addString(reservedDates,dateString);
	    		else
	    			allDates = this.addString(allDates,dateString);
	    	}
	    	if( day.firstDayOfBooking )
	    	{
	    		if( day.reserved ) 
	    			firstReservedDates = this.addString(firstReservedDates,dateString);
	    		else
		            firstDates = this.addString(firstDates,dateString);
	    	}
	    	if( day.lastDayOfBooking )
	    	{
	    		if( day.lastReservedDay ) 
	    		    lastReservedDates = this.addString(lastReservedDates,dateString);
	    		else
	    			lastDates = this.addString(lastDates,dateString);
	    		
	    		if( !day.available )
	    		{
	    		    allDates = this.incrementDateIfRequired(index,day,theDate,allDates,dateString);	
	    		}
	    	}
	    }
	    else if( !day.available )
		{
		    if( allDates.length > 0  ) allDates += ",";
	    	allDates = this.incrementDateIfRequired(index,day,theDate,allDates,dateString);
		}
	    else if( day.reserved )
		{
		    if( reservedDates.length > 0  ) reservedDates += ",";
		    reservedDates += dateString;
		}
		if( day.price === undefined ) {}
		else
		{
		    this.prices[index][dateString] = day.price;
		}
	}
	calendar = this.calendars[index];
	calendar.removeRenderers();
    calendar.addRenderer(allDates, this.unavailableCellRenderer);
    calendar.addRenderer(reservedDates, this.reservedCellRenderer);
    if( index == 1 )
    {
        //calendar.addRenderer(firstDates, this.firstDateCellRenderer);
    	calendar.addRenderer(lastDates, this.unavailableCellRenderer);
    	calendar.addRenderer(lastReservedDates, this.reservedCellRenderer);
    }
    else
    {
    	calendar.addRenderer(firstDates, this.unavailableCellRenderer);
    	calendar.addRenderer(firstReservedDates, this.reservedCellRenderer);
    }
    calendar.prices = this.prices[index];
	calendar.render();
	this.hideWait();
};

Booking.prototype.incrementDateIfRequired = function(index,day,theDate,allDates,dateString)
{
	if( index == 1 && !day.outOfPeriod && !day.bookingDay )
	{
	    var incrementedDate = this.incrementDate(theDate);
	    var formatedDate = this.formatDateForCalendar(incrementedDate);
	    if( day.extraDay ) // for some reason this needs to be formatted differently
	    {
	    	formatedDate = this.formatDateForCalendarIncrementMonth(incrementedDate);
	    }
	    return this.addString(allDates,formatedDate);
	}
	else
	{
		return this.addString(allDates,dateString);
	}
}

Booking.prototype.incrementDate = function(date)
{
	var time = date.getTime();
	var millisInADay = 1000*60*60*24;
	time += millisInADay;
	var incrementDate = new Date(time);
	return incrementDate;
}

Booking.prototype.addString = function(string,add)
{
	if( string.length > 0  ) string += ",";
	string += add;
	return string;
};

Booking.prototype.dateSelected = function(dates,index)
{
	date = dates[0].toString();
	splitString = date.split(",");
	if( splitString.length = 3 )
	{
		year = splitString[0];
	    month = splitString[1];
	    day = splitString[2];
	}
	displayString = day + "/" + month + "/" + year;
	$("input#date-"+index).val(displayString);
	this.selectedDates[index] = new Date(month + "/" + day + "/" + year);
	try
	{
	    this.makeReservation();
	}
	catch(err)
	{
		$("input#date-"+index).val('');
		this.selectedDates[index] = null;
		alert(err);
	}
};

Booking.prototype.dateSelectedDisplayString = function(displayString, index)
{
	splitString = displayString.split("/");
	if( splitString.length = 3 )
	{
		day = splitString[0];
	    month = splitString[1];
	    year = splitString[2];
		this.selectedDates[index] = new Date(month + "/" + day + "/" + year);
		try
		{
		    this.makeReservation();
		}
		catch(err)
		{
			$("input#date-"+index).val('');
			this.selectedDates[index] = null;
			alert(err);
		}
	}
}

Booking.prototype.makeReservation = function()
{
    if( this.selectedDates[0] != null && this.selectedDates[1] != null )
    {
        this.validateDates();
        var url = this.contextRoot + '/JSON/Booking/reservation/?propertyId=' +
            this.propertyId + 
	        '&start=' + this.formatDate(this.selectedDates[0]) +
	        '&end=' + this.formatDate(this.selectedDates[1]) +
	        '&people=2';
	    this.showWait();
    	var responseSuccess = function(data) {
    		try
    		{
    		    that.showReservation(YAHOO.lang.JSON.parse(data.responseText));
    		}
    		catch (e) 
    		{
    			alert("Darn, there has been an error");
        	    that.hideWait();
        	    that.clearReservation();
    		}
    	};
    	var responseFailure = function(data) {
    		alert("Darn, there has been an error");
    	    that.hideWait();
    	    that.clearReservation();
    	};
    	var callback = {
    	    success:responseSuccess,
    	    failure:responseFailure
    	};
    	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
    }
};

Booking.prototype.showReservation = function(data)
{
	if( data.message != null ) 
	{
	    alert(data.message);	
	    this.hideWait();
	    this.clearReservation();
	    return;
	}
	$('#booking-id').val(data.booking.id);
	
	var html = 
		"<p>Period Starting: " + data.start + "</p>"
	    + "<p>Period Ending: " + data.end + "</p>"
	    + "<p>Total Cost for "+ data.period +" nights: <strong>" + data.booking.total + 
	    "</strong> "+data.currency+"</p>"
	    + '<p class="powered-by">Powered by PayPal</p>';
	$('#reservation-details').html(html);
	$('#book-button').show();
	
	$("#reservation-message .message-content").show();
	if( this.currentSec < 0 )
    {
		this.currentSec = this.reservationTimeoutSeconds;
	    this.countdown();
    }
	else
	{
		this.currentSec = this.reservationTimeoutSeconds;
	}
	
	this.hideWait();
};

function calcage(secs, num1, num2) {
	  s = ((Math.floor(secs/num1))%num2).toString();
	  if (s.length < 2)
	    s = "0" + s;
	  return "<b>" + s + "</b>";
	}

Booking.prototype.clearReservation = function()
{
	$("#reservation-message .message-content").hide();
	$('#reservation-details').html('');
	$('#book-button').hide();
}

Booking.prototype.countdown = function()
{
	secs = this.currentSec;
	if( secs < 0 )
	{
		this.clearReservation();
		return;
	}
	that = this;
	CountStepper = -1;
	var SetTimeOutPeriod = (Math.abs(CountStepper)-1)*1000 + 990;
	DisplayFormat = "%%M%% Min, %%S%% Sec.";
    DisplayStr = DisplayFormat.replace(/%%M%%/g, calcage(secs,60,60));
    DisplayStr = DisplayStr.replace(/%%S%%/g, calcage(secs,1,60));
    $("#countdown").html(DisplayStr);
    this.currentSec = secs+CountStepper;
    setTimeout(function(){
        that.countdown();
    }, SetTimeOutPeriod);
};

Booking.prototype.formatDate = function(date)
{
	stringDate = '';
	stringDate += date.getFullYear();
	var day = date.getDate();
	if( day < 10) stringDate += "0";
	stringDate += day;
	var month = date.getMonth() + 1;
	if( month < 10 ) stringDate += "0";
	stringDate += month;
	return stringDate;
};

Booking.prototype.formatDateForCalendar = function(date)
{
	stringDate = '';
	var day = date.getDate();
	//if( day < 10) stringDate += "0";
	var month = date.getMonth();
	//if( month < 10 ) stringDate += "0";
	stringDate += month + "/";
	stringDate += day + "/";
	stringDate += date.getFullYear();
	return stringDate;
};

Booking.prototype.formatDateForCalendarIncrementMonth = function(date)
{
	stringDate = '';
	var day = date.getDate();
	//if( day < 10) stringDate += "0";
	var month = date.getMonth()+1;
	//if( month < 10 ) stringDate += "0";
	stringDate += month + "/";
	stringDate += day + "/";
	stringDate += date.getFullYear();
	return stringDate;
};

Booking.prototype.validateDates = function()
{
	if(this.selectedDates[0].getTime() >= this.selectedDates[1].getTime())
	{
	    throw "End date must be after start date";
	}
	difference = this.selectedDates[1].getTime() - this.selectedDates[0].getTime();
	if( difference > this.maxIntervalMillis )
	{
	    throw "Maximum booking period is " + this.maxIntervalDays + " days";	
	}
};

Booking.prototype.renderCal = function(index,month,year)
{	
	that = this;
	elementId = "booking-cal-"+index;
    calendar = new YAHOO.widget.Calendar(elementId,{pagedate: (month+1) + "/" + year});
    calendar.cfg.queueProperty("navigator",true,false);
    calendar.changePageEvent.subscribe(function(event,dates){
	    month = dates[1].getMonth();
	    year = dates[1].getFullYear();
	    that.retrieveAvailability(month,year,index);
	});
    calendar.selectEvent.subscribe(function(event,dates) {
    	that.dateSelected(dates,index);
    });
    this.calendars[index] = calendar;
    this.pageDates[index] = new Object();
    this.pageDates[index].month = month;
    this.pageDates[index].year = year;
    this.retrieveAvailability(month,year,index);
    this.startPollingLoop(index);
};

Booking.prototype.startPollingLoop = function(index)
{
	that = this;
	setTimeout(function() {that.retrieveCurrentAvailability(index);},this.pollingInterval);
}

Booking.prototype.retrieveCurrentAvailability = function(index)
{
	date = this.pageDates[index];
	if( date != null )
	{
	    this.retrieveAvailability(date.month, date.year, index);
		that = this;
		setTimeout(function() {that.retrieveCurrentAvailability(index);},this.pollingInterval);
	}
}

Booking.prototype.showWait = function()
{
	++this.numWaits;
	if( this.numWaits == 1 )
    {
	    $('.app-canvas-content .wait img').show();
    }
}

Booking.prototype.hideWait = function()
{
	--this.numWaits;
	if( this.numWaits == 0 )
    {
	    $('.app-canvas-content .wait img').hide();
    }
}