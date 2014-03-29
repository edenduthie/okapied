Explorer.BOOKINGS = 'bookings';
Explorer.PROPERTIES = 'properties';
Explorer.PROPERTY_BOOKINGS = 'property_bookings';
Explorer.MY_ACCOUNT = 'me';

function Explorer(divId,userId,view,contextRoot)
{
    this.divId = divId;
    this.userId = userId;
    this.view = view;
    this.numWaits = 0;
    this.limit = 20;
    this.offset = 0;
    this.contextRoot = contextRoot;
}

Explorer.prototype.render = function()
{
	var that = this;
    $('#explorer-previous').click(function(){
    	that.previous();
    });
    $('#explorer-next').click(function(){
    	that.next();
    });
	this.renderContent();
};

Explorer.prototype.renderContent = function()
{
	var that = this;
	var contentText = 
		'<div id="explorer-title">' +
		'<div id="explorer-title-text">' +
	    '<a title="Bookings I have made." class="explorer-button" id="explorer-bookings-button" href="javascript:void(0)">My<br>Bookings</a>' +
	    '<div class="explorer-title-line"></div>' + 
	    '<a title="Bookings on my properties." class="explorer-button" id="explorer-property-booking-button" href="javascript:void(0)">Property<br>Bookings</a>' +
	    '<div class="explorer-title-line"></div>' + 
	    '<a title="Properties I have listed." class="explorer-button" id="explorer-property-button" href="javascript:void(0)">My<br>Properties</a>' +
	    '</div>' +
	    '</div>' +
        '<div id="explorer-list"></div>';
	$('#'+this.divId).html(contentText);
	$('#explorer-property-button').click(function(){
		that.offset = 0;
		that.view = Explorer.PROPERTIES;
		that.loadProperties();
	});
	$('#explorer-bookings-button').click(function(){
		that.offset = 0;
		that.view = Explorer.BOOKINGS;
		that.loadBookings();
	});	
	$('#explorer-property-booking-button').click(function(){
		that.offset = 0;
		that.view = Explorer.PROPERTY_BOOKINGS;
		that.loadPropertyBookings();
	});	
	if( this.view == Explorer.PROPERTIES ) this.loadProperties();
	if( this.view == Explorer.BOOKINGS ) this.loadBookings();
	if( this.view == Explorer.PROPERTY_BOOKINGS ) this.loadPropertyBookings();
};

Explorer.prototype.loadProperties = function()
{
	var that = this;
    var url = this.contextRoot + '/JSON/Properties/list?ownerId=' +
        this.userId +
        '&limit=' + this.limit +
        '&offset=' + this.offset;
	this.showWait();
	var responseSuccess = function(data) {
	    that.showProperties(data);
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

Explorer.prototype.showProperties = function(data)
{
	this.hideWait();
	data = YAHOO.lang.JSON.parse(data.responseText);
	var text = '';
	text += '<ul>';
	var j=0;
    for( var i in data.properties)
    {
    	++j;
    	var property = data.properties[i];
    	text += '<li ';
    	if(j%2 == 1) text += 'class="odd-explorer-li"';
    	text += '>' + 
    	'<a href="'+this.contextRoot+'/App/Publish/list?propertyId='+property.id+'">' +
    	property.name + 
    	'</a>'
    	'</li>';
    }
    text += '</ul>';
    $('#explorer-list').html(text);
    this.bold(Explorer.PROPERTIES);
    
    if( this.offset <= 0 ) $('#explorer-previous').hide();
    else $('#explorer-previous').show();
    if( (this.offset + this.limit ) >= data.size ) $('#explorer-next').hide();
    else $('#explorer-next').show();
};

Explorer.prototype.loadBookings = function()
{
	var that = this;
    var url = this.contextRoot+'/JSON/Bookings/list?userId=' +
        this.userId +
        '&limit=' + this.limit +
        '&offset=' + this.offset;
	this.showWait();
	var responseSuccess = function(data) {
	    that.showBookings(data);
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

Explorer.prototype.showBookings = function(data)
{
	this.hideWait();
	data = YAHOO.lang.JSON.parse(data.responseText);
	var text = '';
	text += '<ul>';
	var j=0;
    for( var i in data.bookings)
    {
    	++j;
    	var booking = data.bookings[i];
    	text += '<li ';
    	if(j%2 == 1) text += 'class="odd-explorer-li"';
    	text += '>' + 
    	'<a href="'+this.contextRoot+'/App/ViewBooking/list?id='+booking.id+'">' +
    	'<div class="property-name">' + booking.property.name + '</div>' +
    	'<span class="start-date">' + this.formatDate(booking.startDate) + ' to </span>' +
    	'<span class="end-date">' + this.formatDate(booking.endDate) + '</span>' +
    	'</a>'
    	'</li>';
    }
    text += '</ul>';
    $('#explorer-list').html(text);
    this.bold(Explorer.BOOKINGS);
    
    if( this.offset <= 0 ) $('#explorer-previous').hide();
    else $('#explorer-previous').show();
    if( (this.offset + this.limit ) >= data.size ) $('#explorer-next').hide();
    else $('#explorer-next').show();
};

Explorer.prototype.loadPropertyBookings = function()
{
	var that = this;
    var url = this.contextRoot+'/JSON/PropertyBookings/list?userId=' +
        this.userId +
        '&limit=' + this.limit +
        '&offset=' + this.offset;
	this.showWait();
	var responseSuccess = function(data) {
	    that.showPropertyBookings(data);
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

Explorer.prototype.showPropertyBookings = function(data)
{
	this.hideWait();
	data = YAHOO.lang.JSON.parse(data.responseText);
	var text = '';
	text += '<ul>';
	var j=0;
    for( var i in data.bookings)
    {
    	++j;
    	var booking = data.bookings[i];
    	text += '<li ';
    	if(j%2 == 1) text += 'class="odd-explorer-li"';
    	text += '>' + 
    	'<a href="'+this.contextRoot+'/App/ViewBookingOwner/list?id='+booking.id+'">' +
    	'<div class="property-name">' + booking.property.name + '</div>' +
    	'<span class="start-date">' + this.formatDate(booking.startDate) + ' to </span>' +
    	'<span class="end-date">' + this.formatDate(booking.endDate) + '</span>' +
    	'</a>'
    	'</li>';
    }
    text += '</ul>';
    $('#explorer-list').html(text);
    this.bold(Explorer.PROPERTY_BOOKINGS);
    
    if( this.offset <= 0 ) $('#explorer-previous').hide();
    else $('#explorer-previous').show();
    if( (this.offset + this.limit ) >= data.size ) $('#explorer-next').hide();
    else $('#explorer-next').show();
};

Explorer.prototype.formatDate = function(javaDate)
{
	var year = javaDate.substring(0,4);
    var month = javaDate.substring(5,7);
    var day = javaDate.substring(8,10);
    month = parseInt(month,10);
	//month += 1;
	if( month < 10 ) month = '0' + month;
	year = year.substring(2,4);
	var dateString = day + "/" + month + "/" + year;
	return dateString;
};

Explorer.prototype.next = function()
{
	this.offset += this.limit;
	this.renderContent();
};

Explorer.prototype.previous = function()
{
	this.offset -= this.limit;
	if( this.offset <= 0 ) this.offset = 0;
	this.renderContent();
};

Explorer.prototype.bold = function(key)
{
	$('#explorer-property-button').css('font-weight','normal');
	$('#explorer-bookings-button').css('font-weight','normal');
	$('#explorer-property-booking-button').css('font-weight','normal');
    if( key == Explorer.PROPERTIES ) $('#explorer-property-button').css('font-weight','bold');	
    if( key == Explorer.BOOKINGS ) $('#explorer-bookings-button').css('font-weight','bold');
    if( key == Explorer.PROPERTY_BOOKINGS ) $('#explorer-property-booking-button').css('font-weight','bold');
};

Explorer.prototype.showWait = function()
{
	++this.numWaits;
	if( this.numWaits == 1 )
    {
	    //$('.app-canvas-content .wait img').show();
    }
};

Explorer.prototype.hideWait = function()
{
	--this.numWaits;
	if( this.numWaits == 0 )
    {
	    //$('.app-canvas-content .wait img').hide();
    }
};