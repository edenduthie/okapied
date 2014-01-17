<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/header.jsp"></s:include>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/container.css">

<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<!--
<script src="<%=request.getContextPath()%>/galleria/src/galleria.js"></script>
<script src="<%=request.getContextPath()%>/galleria/src/themes/classic/galleria.classic.js"></script>
-->
<script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>
<script src="<%=request.getContextPath()%>/js/panels.js"></script>
<script src="<%=request.getContextPath()%>/js/dragdrop-min.js"></script>
<script src="<%=request.getContextPath()%>/js/container-min.js"></script>

<div class="content">
<div id="refundPolicyPanel">
    <div class="hd"></div>
    <div class="bd"></div>
    <div class="ft"></div>
</div>
<div id="property-top-feedback-bar">
	    <div id="property-feedback-bar" "class="feedback-bar">
	        <div style='width: <s:property value="property.percentagePositive" />%;' class="feedback-bar-fill">&nbsp;</div>
	    </div>
	    <div class="percentage-pos"><s:property value="property.percentagePositive" />%</div>
    </div>
    <div id="property-title" class="left-margin text-color"><s:property value="propertyName" /></div>
    <div id="property-full-name" class="left-margin text-color"><s:property value="property.propertyDetails.fullName"/></div>
    <div id="first-row">
    <div class="left-column">
        <div id="summary-info" class="left-margin">
            <div id="book-button">
                <s:if test="!property.retrieveFrozen()" >
                    <a href='<%=request.getContextPath()%>/App/Booking/book?propertyId=<s:property value="property.id"/>'><img src="<%=request.getContextPath()%>/images/book_button.png" alt="Book Now" title="Book Now"></img></a>
                </s:if>
                <s:else>
                    Bookings temporaraly unavailable
                </s:else>
            <s:if test="ownerOfProperty">
                <div id="owner-edit-button">
                    <a href='<%=request.getContextPath()%>/App/Publish/list?propertyId=<s:property value="property.id"/>'>
                        <img src="<%=request.getContextPath()%>/images/edit_button.png" alt="Edit Property" title="Edit Property"></img>
                    </a>
                </div>
            </s:if>
            </div>
            <div>
            <div class="icon-with-label">
                <s:if test="property.propertyDetails.type.id == 1" >
                    <div class="house-icon"></div>
                </s:if>
                <s:elseif test="property.propertyDetails.type.id == 2" >
                    <div class="apartment-icon"></div>
                </s:elseif>
                <s:elseif test="property.propertyDetails.type.id == 3" >
                    <div class="room-icon"></div>
                </s:elseif>
                <s:elseif test="property.propertyDetails.type.id == 4" >
                    <div class="bnb-icon"></div>
                </s:elseif>
                <div class="icon-label text-color">Type: <s:property value="property.propertyDetails.type.name" /></div>
            </div>
            <div class="icon-with-label"><div class="share-icon"></div><div class="icon-label text-color">Sleeps: <s:property value="property.propertyDetails.sleeps" /></div></div>
            <div class="icon-with-label"><div class="beds-icon"></div><div class="icon-label text-color">Bedrooms: <s:property value="bedrooms" /> </div></div>
            <div class="icon-with-label"><div class="moon-icon"></div><div class="icon-label text-color">Min Nights: <s:property value="property.minNights" /> </div></div>
            </div>
        </div>
        <div id="feedback-sum">
            <div id="feedback-top">
                <div class="top-right"></div>
                <div class="top-left"></div>
                <div class="top-center"></div>
            </div>
            <div class="feedback-content">
                <div class="feedback-text">
                    <div id="overall-feedback-graph">
                        Cleanliness: <div class="percentage-pos"><s:property value="property.cleanlinessPercentage()" />%</div><br>
                        <div class="feedback-bar"><div style='width: <s:property value="property.cleanlinessPercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
                        As Described: <div class="percentage-pos"><s:property value="property.accuracyPercentage()" />%</div><br>
                        <div class="feedback-bar"><div style='width: <s:property value="property.accuracyPercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
                        Value: <div class="percentage-pos"><s:property value="property.valuePercentage()" />%</div><br>
                        <div class="feedback-bar"><div style='width: <s:property value="property.valuePercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
                        Overall: <div class="percentage-pos"><s:property value="property.ratingPercentage()" />%</div><br>
                        <div class="feedback-bar"><div style='width: <s:property value="property.ratingPercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
                    </div>
                    <s:iterator value="latestFeedback">
                        <div class="feedback-text-row">
                            <div class="pos-neg">
                                <s:if test="positive">+</s:if>
                                <s:else>-</s:else>
                            </div>
                            <div class="feedback-text-content">
                                <s:property value='retrieveTextCutoff()' />
                            </div>
                        </div>
                    </s:iterator>
                </div>
                <div id="feedback-footer">
                    Latest Reviews | <a href="#all-reviews" onclick="filter(null)">View All</a>
                </div>
            </div>
            <div id="feedback-bottom">
                <div class="bottom-right"></div>
                <div class="bottom-left"></div>
                <div class="bottom-center"></div>
            </div>
        </div>
    </div>
    <div class="right-column">
        <div id="photos">
            <div id="images-top">
                <div class="grey-top-right"></div>
                <div class="grey-top-left"></div>
                <div class="grey-top-center"></div>
            </div>
            <div class="images">
             <s:iterator value="property.photos">
                 <img src='<%=request.getContextPath()%>/File/Photo_list.action?photoId=<s:property value="id" />'></img>
             </s:iterator>
             <s:if test="property.photos.size() <= 0">
                 <img src='<%=request.getContextPath()%>/images/default_property_image.jpg'></img>
             </s:if>
            </div>
            <div id="images-bottom">
                <div class="grey-bottom-right"></div>
                <div class="grey-bottom-left"></div>
                <div class="grey-bottom-center"></div>
            </div>
        </div>
    </div>
    </div><!--first-row-->
    <div id="second-row">
        <div class="left-column">
            <div id="property-features">
            <div id="property-features-top">
                <div class="grey-top-right"></div>
                <div class="grey-top-left"></div>
                <div class="grey-top-center"></div>
            </div>
            <div id="property-features-content">
                <div class="property-features-text">
                    <h2>Property Features</h2>
                    <div class="right-small-column">
                        <h3>Bathroom/Laundry</h3>
                            <p>Bathrooms: <s:property value="property.propertyDetails.bathrooms" /></p>
                            <s:iterator value="property.propertyDetails.bathroomLaundry">
                                <p><s:property/></p>
                            </s:iterator>
                        <h3>Ammenities</h3>
                            <s:iterator value="property.propertyDetails.ammenities">
                                <p><s:property/></p>
                            </s:iterator>
                            <p>Flooring: <s:property value="property.propertyDetails.flooring.name"/></p>
                        <h3>Multimedia</h3>
                            <s:iterator value="property.propertyDetails.multimedia">
                                <p><s:property/></p>
                            </s:iterator>
                    </div>
                    <div class="left-small-column">
                        <h3>Bedroom</h3>
                            <p>Bedrooms: <s:property value="property.propertyDetails.bedrooms" /></p>
                            <p>Sleeps: <s:property value="property.propertyDetails.sleeps" /></p>
                            <s:if test="property.propertyDetails.kingBeds > 0" ><p>King: <s:property value="property.propertyDetails.kingBeds" /></p></s:if>
                            <s:if test="property.propertyDetails.queenBeds > 0" ><p>Queen: <s:property value="property.propertyDetails.queenBeds" /></p></s:if>
                            <s:if test="property.propertyDetails.doubleBeds > 0" ><p>Double: <s:property value="property.propertyDetails.doubleBeds" /></p></s:if>
                            <s:if test="property.propertyDetails.singleBeds > 0" ><p>Single: <s:property value="property.propertyDetails.singleBeds" /></p></s:if>
                        <h3>Kitchen</h3>
                            <s:iterator value="property.propertyDetails.kitchen">
                                <p><s:property/></p>
                            </s:iterator>
                        <h3>Outdoor</h3>
                            <s:iterator value="property.propertyDetails.outdoor">
                                <p><s:property/></p>
                            </s:iterator>
                            <s:if test="property.propertyDetails.distanceToBeachMeters >=0">
                                <p>Distance to Beach: <s:property value="property.propertyDetails.distanceToBeachMeters" />m</p>
                            </s:if>
                    </div>
                    <div class="clearfloat" ></div>
                </div>
            </div>
            <div id="property-features-bottom">
                <div class="grey-bottom-right"></div>
                <div class="grey-bottom-left"></div>
                <div class="grey-bottom-center"></div>
            </div>
            </div><!-- property-features -->
        </div>
        <div class="right-column">
            <div id="description-cal">
                <div id="description-cal-top">
                    <div class="top-right"></div>
                    <div class="top-left"></div>
                    <div class="top-center"></div>
                </div>
	            <div id="description-cal-content">
	                <div class="description-cal-text">
	                    <div id="description-left">
		                    <h2>Description</h2>
		                    <div id="description-text">
		                        <s:property value='propertyDescription' escape='false'/>
		                    </div>
	                    </div>
	                    <div id="calendar-right">
	                        <div id="calendar-wait"><img src="<%=request.getContextPath()%>/images/wait.gif"></img></div>
	                        <div id="calendar"></div>
	                        <div id="property-address">
                            <h3>Address</h3>
                            <p>
							<s:property value="property.propertyDetails.street" />, <s:property value="property.location.name" />, <s:property value="property.location.region.name" />, <s:property value="property.location.country.name" /> <s:property value="property.propertyDetails.postcode" />
                            </p>
                            <h3><span id="refund-title">Refund Policy</span> <div class="question"><a href="javascript:void(0)" onclick="refundPolicyPanel('refundPolicyPanel','<%=request.getContextPath()%>')"><img alt="See details of our refund policies." title="See details of our refund policies" src='<%=request.getContextPath()%>/images/question.png'></img></a></div></h3>
                            <p>
                                <s:property value="property.retrieveRefundPolicyText()" />
                            </p>
	                        </div>
	                    </div>
	                </div>
	            </div>
	            <div id="description-cal-bottom">
	                <div class="bottom-right"></div>
	                <div class="bottom-left"></div>
	                <div class="bottom-center"></div>
	            </div>
            </div>
        </div>
    </div><!-- second-row -->
    <div id="third-row">
        <div class="left-column">
        </div>
        <div class="right-column">
            <div id="reviews">
	            <div id="reviews-top">
	                <div class="top-right"></div>
	                <div class="top-left"></div>
	                <div class="top-center"></div>
	            </div>
	            <div id="reviews-content">
	                <div id="reviews-text">
	                <div id="reviews-heading">
	                    <div id="reviews-wait"><img src="<%=request.getContextPath()%>/images/wait.gif"></img></div>
	                    <a name="all-reviews" id="all-reviews" class="first not-selected-heading" href="javascript:void(0)" onclick='filter(null)'>All Reviews</a>|
	                    <a id="positive" class="rest not-selected-heading" href="javascript:void(0)" onclick='filter(true)'>Positive</a>|
	                    <a id="negative" class="rest not-selected-heading" href="javascript:void(0)" onclick='filter(false)'>Negative</a>
	                </div>
	                <div id="reviews-body">
                        <div id="loading-reviews">The feedback is taking quite a while to load don't you think?'</div>
	                </div>
	                </div>
	                <div id="reviews-footer" />
	                    <a id="next-reviews" href="javascript:void(0)" onclick="nextPage();">
	                        <img src="<%=request.getContextPath()%>/images/arrow-right.png" />
	                    </a>
	                    <a id="previous-reviews" href="javascript:void(0)" onclick="previousPage()">
	                        <img src="<%=request.getContextPath()%>/images/arrow-back.png" />
	                    </a>
	                    <div id="review-pages">
	                    </div>
	                    <div class="clearfloat"></div>
	                </div>
	            </div>
	            <div id="reviews-bottom">
	                <div class="bottom-right"></div>
	                <div class="bottom-left"></div>
	                <div class="bottom-center"></div>
	            </div>
            </div>
        </div>
    </div>
</div>
<script>

var calendar;

var positive = null;
var limit = 10;
var mainOffset = 0;
var size = 0;
var contextRoot = '<%=request.getContextPath()%>';

$(document).ready(function() {
	//Galleria.loadTheme('<%=request.getContextPath()%>/galleria/src/themes/classic/galleria.classic.js');
	// picture galery
	var options = {
	    width: 650,
	    height: 500
    };
	var gallery = new Galleria();
    gallery.init( $('.images'), options );
    
    // calendar
    calendar = new YAHOO.widget.Calendar("calendar");
    calendar.disableClick = true;
    calendar.cfg.queueProperty("navigator",true,false);

    
    var startYear = calendar.cfg.getProperty("pagedate").getFullYear();
    var startMonth = calendar.cfg.getProperty("pagedate").getMonth();
    retrieveAvailability(startMonth,startYear);
    
    calendar.changePageEvent.subscribe(function(event,dates){
        var month = dates[1].getMonth();
        var year = dates[1].getFullYear();
        retrieveAvailability(month,year);
    });
    
    calendar.cfg.fireQueue();
    calendar.render();

    //retrieveFeedback(positive,limit,mainOffset);
    filter(positive);
});

function retrieveAvailability(month,year)
{
    var url = contextRoot+'/JSON/Availability/list/?propertyId=' +
	    <s:property value="property.id" /> +
	    '&month=' + month +
	    '&year=' + year;
	$('#calendar-wait img').show();
	$.getJSON(url, function(data) {
		showAvailability(data);
	});
}

function showAvailability(data)
{
	var displayMonth = data.month + 1;
	var displayYear = data.year;
	var allDates = "";
	var prices = new Array();
	for( i=0; i < data.days.length; ++i  )
	{
		day = data.days[i];
		if( day.available )
		{
		    var dayOfMonth = i;
		    var dateString = displayMonth + "/" + dayOfMonth + "/" + displayYear;
		    if( allDates.length > 0  ) allDates += ",";
		    allDates += dateString;
		    if( day.price === undefined ) {}
			else
			{
			    prices[dateString] = day.price;
			}
		}
	}
	calendar.cfg.queueProperty("selected",allDates,false);
	calendar.cfg.fireQueue();
	calendar.prices = prices;
	calendar.render();
	$('#calendar-wait img').hide();
}

function retrieveFeedback(positive,limit,offset)
{
	mainOffset = offset;
    var url = contextRoot+'/JSON/Feedback/list/?propertyId=' +
	    <s:property value="property.id" /> +
	    '&limit=' + limit +
	    '&offset=' + offset;
	if(positive != null) 
	{
        url += "&positive="+positive;
	}
	$("#reviews-wait").show();
	$.getJSON(url, function(data) {
		showFeedback(data);
	});	
}

var m_names = new Array("Jan", "Feb", "Mar", 
		"Apr", "May", "Jun", "Jul", "Aug", "Sep", 
		"Oct", "Nov", "Dec");

function showFeedback(data)
{
	var text = "";
	for( i=0; i < data.feedback.length; ++i )
	{
		var item = data.feedback[i];
		var dateLeft = new Date(item.dateLeft);
		var s = item.dateLeft;
		var month = parseInt(s.substring(5,7));
		month = month-1;
		var dateLeft = new Date(s.substring(0,4),month,s.substring(8,10),parseInt(s.substring(11,13)),
				parseInt(s.substring(14,16)),parseInt(s.substring(17)),0);
		text += '<div class="';
		if( i%2 != 0 ) text += 'reviews-content-white';
		else text += 'reviews-content-grey';
		text += '">';
		text += '<div class="review-left-column">';
		text += '<div class="sign">';
		if( item.positive ) text += '+';
		else text += '-';
		text += '</div>';
		text += '<div class="review-date">';
		text += dateLeft.getDate() + ' ' + m_names[dateLeft.getMonth()] + ' ' + dateLeft.getFullYear() + "<br>";
		text += '</div>';
		text += '<div class="review-data">';
		text += getFeedbackBarString(item.cleanliness,'Cleanliness');
		text += getFeedbackBarString(item.accuracy,'As Described');
		text += getFeedbackBarString(item.valueForMoney,'Value');
		text += getFeedbackBarString(item.rating,'Overall');
		text += '</div>';
		text += '</div><!--review-left-column-->';
		text += '<div class="review-body';
		if( !item.positive ) text += ' bold-review';
		text += '">';
		if( item.text == null || item.text.length <= 0 ) text += '&nbsp;';
		else text += item.text;
		text += '</div>';
		text += "</div><!--reviews-content-->";
+item.text+'</div>';
	}
	$("#reviews-body").html(text);
	$("#reviews-wait").hide();

	size = data.size;
	if( mainOffset <= 0 ) $("#previous-reviews").hide();
	else $("#previous-reviews").show();
	if( mainOffset + limit >= size ) $("#next-reviews").hide();
    else $("#next-reviews").show();

    buildReviewPages();
}

function getFeedbackBarString(rating,label)
{
	var percentage = (rating*100)/5;
	text = label + ": " 
	    + '<div class="percentage-pos">'+percentage+'%</div>'
	    + "<br>";
	text +=
    '<div "class="feedback-bar">' +
        '<div style="width: '+percentage+'%;" class="feedback-bar-fill">&nbsp;</div>' +
    '</div>';	
    return text;
}

function nextPage()
{
    mainOffset += limit;
    retrieveFeedback(positive,limit,mainOffset);
}
function previousPage()
{
    mainOffset -= limit;
    retrieveFeedback(positive,limit,mainOffset);
}

function buildReviewPages()
{
	var text = "";
	numPages = size/limit;
	for( i=0; i < numPages; ++i)
	{
		pageNumber = i+1;
		pageOffset = i*limit;
		text += '<a href="javascript:void(0)" onclick=retrieveFeedback(' +
		    positive + "," +
		    limit + "," +
		    pageOffset +
		    ')';
		if( mainOffset == pageOffset ) text += ' class="page-selected" ';
		else  text += ' class="page-not-selected" ';
		text += '>'+
		    pageNumber
		    '</a>';
	}
	$('#review-pages').html(text);
}

function filter(isPositive)
{
	mainOffset = 0;
	positive = isPositive;
	retrieveFeedback(positive,limit,mainOffset);
	if( positive == null )
	{
		// old color #011d6d
		$('#all-reviews').css('color','rgb(118,118,118)');
		$('#positive').css('color','rgb(3,163,255)');
		$('#negative').css('color','rgb(3,163,255)');
	}
	if( positive == true )
	{
		$('#all-reviews').css('color','rgb(3,163,255)');
		$('#positive').css('color','rgb(118,118,118)');
		$('#negative').css('color','rgb(3,163,255)');
	}
	if( positive == false )
	{
		$('#all-reviews').css('color','rgb(3,163,255)');
		$('#positive').css('color','rgb(3,163,255)');
		$('#negative').css('color','rgb(118,118,118)');
	}
}

var hoverNext = false;
var hoverBack = false;

$(document).ready(function(){
	$('#next-reviews').hover(function(){
		if( !hoverNext )
		{
		    $(this).children("img").attr("src",'<%=request.getContextPath()%>/images/arrow-right-hover.png');
            hoverNext = true;
		}
		else
		{
			$(this).children("img").attr("src",'<%=request.getContextPath()%>/images/arrow-right.png');
            hoverNext = false;
		}
	});
	$('#previous-reviews').hover(function(){
		if( !hoverBack )
		{
		    $(this).children("img").attr("src",'<%=request.getContextPath()%>/images/arrow-back-hover.png');
            hoverBack = true;
		}
		else
		{
			$(this).children("img").attr("src",'<%=request.getContextPath()%>/images/arrow-back.png');
            hoverBack = false;
		}
	});
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>