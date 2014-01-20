<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="/header.jsp"></s:include>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
<script src="<%=request.getContextPath()%>/js/yahoo-dom-event.js"></script>
<script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>

<div id="content">
<s:set name="weekends" value="weekends" scope="request"/>
<jsp:useBean id="weekends" class="java.util.ArrayList" scope="request"/>
<s:set name="periodDisplay" value="periodDisplay" scope="request"/>
<jsp:useBean id="periodDisplay" class="java.util.ArrayList" scope="request"/>
    <s:if test="properties.size > 0" >
    <div id="cal1Container"></div>
    <div id="property-table" class="content-container">
        <div id="property-table-header">
            <div class='property-table-first-header-col property-table-header-col'>&nbsp;</div>
            <div id="calendar-icon">
                <a href="javascript:void(0);" title="Jump To Date" alt="Jump To Date"></a>
            </div>
            <div id="arrow-back">
                <a href="javascript:void(0);" title="Previous 2 weeks"></a>
            </div>
            <% 
                int i=0; for( Object display : periodDisplay ) {
            %>
                <div class='property-table-header-col <% if((Boolean)weekends.get(i)) out.println("weekend-date"); %>'>
                    <% out.println(display); %>
                </div>
            <% ++i; } %>
            <div id="arrow-right">
                <a href="javascript:void(0);" title="Next 2 weeks"></a>
            </div>
        </div>
        <s:iterator value="properties">
            <s:set name="currency" value="currencyCode.name" />
            <div class='property-table-row'>
            <input class="property-name-input" type="hidden" value="<s:property value="name" escape="false" />"></input>
            <input class="country-name-input" type="hidden" value="<s:property value="location.country.name" escape="false" />"></input>
            <input class="region-name-input" type="hidden" value="<s:property value="location.region.name" escape="false" />"></input>
            <input class="location-name-input" type="hidden" value="<s:property value="location.name" escape="false" />"></input>
            <div class="property-label">
                <div class="property-name"><s:property value="name" /></div>
                <div class="percentage-pos">
                    <s:property value="percentagePositive" />%
                </div>
                <div class="feedback-bar">
                    <div style='width: <s:property value="percentagePositive" />%;' class="feedback-bar-fill"
                    >&nbsp;</div>
                </div>
            </div>
            <s:iterator value="infoList">
                <div title="Currency: <s:property value="#currency" />" class='property-table-col 
                    <s:if test="available">
                        <s:if test="weekend">
                            weekend-box
                        </s:if>
                        available-box
                    </s:if>
                    <s:else>not-available-box
                    </s:else>'>
                    <s:if test="available == true">
                        <s:property value="retrievePriceRounded()" />
                    </s:if>
                    <s:else>
                        <img alt="Not Available" title="Not Available" src="<%=request.getContextPath()%>/images/not-available.png"></img>
                    </s:else>
                </div>
            </s:iterator>
            </div><!--property-table-row--->
        </s:iterator>
    </div>
    <div id="list-footer">
        <div id="pagination-bar">
            <div id="navigation-right">
            <img style="display:hidden" id="next-pages" src="<%=request.getContextPath()%>/images/dots.png" alt="More pages" title="More pages"/>
            <s:if test="!lastPage">
                <img id="pagination-right" src="<%=request.getContextPath()%>/images/pagination_right.png" alt="Next page of properties" title="Next page of properties"/>
            </s:if>
            </div>
            <div id="navigation-left">
            <s:if test="offset > 0">
                <img id="pagination-left" src="<%=request.getContextPath()%>/images/pagination_left.png" alt="Previous page of properties" title="Previous page of properties"/>
            </s:if>
            <img style="display:hidden" id="previous-pages" src="<%=request.getContextPath()%>/images/dots.png" alt="Previous pages" title="Previous pages"/>
            </div>
            <div id="pages"></div>
        </div>
    </div>
    </s:if>
    <s:else>
        <s:include value="/front.jsp"></s:include>
    </s:else>
    <s:include value="/footer.jsp"></s:include>
</div><!--content-->
<s:if test="properties.size > 0" >
<script type="text/javascript">

var millisIn2Weeks = 1000*60*60*24*14;
var millisIn1Day = 1000*60*60*24;
var cal1;

function calendarSelected(type,args,obj) { 
	$("#cal1Container").hide();
	cal1 = null;
    var selected = args[0]; 
    var temp = selected+"";
    var splitString = temp.split(",");
    var date = splitString[0]+padInt(splitString[1])+padInt(splitString[2]);
    var url = okapied.prepareUrl();
	if( okapied.hasAtLeastOneParam ) url += "&";
	else url += "?";
	url += "date=" + date;
    window.location.href = url;
}; 

function padInt(theInt)
{
	if( theInt < 10 ) return "0"+theInt;
	else return theInt;
}

function showCal1()
{
	if( cal1 == null )
	{
	    cal1 = new YAHOO.widget.Calendar("cal1Container");
	    cal1.selectEvent.subscribe(calendarSelected,cal1,true); 
	    cal1.render();
	    $("#cal1Container").show();
	}	
	else
	{
		cal1 = null;
		$("#cal1Container").hide();
	}
}

var firstPageOfList = 1;
var numPagesPerList = 9;

$(document).ready(function() {
	$("#arrow-right").click(function(){
		var url = okapied.prepareUrl();
		if( okapied.hasAtLeastOneParam ) url += "&";
		else url += "?";
		url += "time=" + <s:property value="period[period.size-1].timeInMillis" />;
	    window.location.href = url;
	});
	$("#arrow-back").click(function(){
		var time = <s:property value="period[0].timeInMillis" />;
		time -= millisIn1Day;
		time -= millisIn2Weeks;
		var url = okapied.prepareUrl();
		if( okapied.hasAtLeastOneParam ) url += "&";
		else url += "?";
		url += "time=" + time;
	    window.location.href = url;
	});
	$("#calendar-icon").click(function(){
		showCal1();
	});
	$(".property-table-row").mouseenter(function(){
		$(this).find(".available-box").css('background-color','rgb(3,140,219)');
		$(this).find(".weekend-box").css('background-color','rgb(254,137,51)');
		$(this).find(".not-available-box").css('background-color','rgb(222,222,222)');
		//$(this).find(".property-label").css('background-color','rgb(32,78,209)');
	});
	$(".property-table-row").mouseleave(function(){
		$(this).find(".property-table-col").css('background-color','rgb(3,163,255)');
		$(this).find(".weekend-box").css('background-color','rgb(254,170,51)');
		$(this).find(".not-available-box").css('background-color','white');
		//$(this).find(".property-label").css('background-color','');
	});
	$(".property-table-row").click(function(){
		okapied.limit = null;
		okapied.offset = null;
		okapied.place['region.name'] = $(this).find(".region-name-input").val();
		okapied.place['country.name'] = $(this).find(".country-name-input").val();
		okapied.place['name'] = $(this).find(".location-name-input").val();
		okapied.place['t'] = "L";
		var url = okapied.prepareUrl() + "/" + encodeURI($(this).find(".property-name-input").val());
	    window.location.href = url;
	});

	okapied.offset = <s:property value="offset" />;
	okapied.limit = <s:property value="limit" />;
	okapied.size = <s:property value="size" />;
	okapied.firstPage = <s:property value="firstPage" />;

	$('#pagination-right').click(function(){
		var newOffset = okapied.offset + okapied.limit;
		if( newOffset < okapied.size ) okapied.offset += okapied.limit;
		var url = okapied.prepareUrl();
		window.location.href = url;
	});
	$('#pagination-left').click(function(){
		var newOffset = okapied.offset - okapied.limit;
		if( newOffset >= 0 ) okapied.offset = newOffset;
		var url = okapied.prepareUrl();
		window.location.href = url;
	});
	$('#next-pages').click(function(){
		firstPageOfList += numPagesPerList;
		showPageList(okapied.offset,okapied.limit,okapied.size);
	});
	$('#previous-pages').click(function(){
		firstPageOfList -= numPagesPerList;
		showPageList(okapied.offset,okapied.limit,okapied.size);
	});
	firstPageOfList = okapied.firstPage;
	showPageList(okapied.offset,okapied.limit,okapied.size);
});

function showPageList(offset,limit,size)
{
	okapied.firstPage = firstPageOfList;
	var numPages = Math.ceil(size/limit);
	var currentPage = (offset/limit) + 1;
	
	if( firstPageOfList <= 1 )
	{
		$('#previous-pages').hide();
	}
	else
	{
		$('#previous-pages').show();
	}

	if( (firstPageOfList + numPagesPerList) >= numPages )
	{
		$('#next-pages').hide();
	}
	else
	{
		$('#next-pages').show();
	}
	
	if( numPages > 0 )
	{
		var allPages = '';
		for( i=firstPageOfList; i < (firstPageOfList + numPagesPerList) && i <= numPages; ++i )
		{
			var page = '<a';
			page +=
			    ' href="javascript:void()" '
				+ 'onclick="'
				+ 'goToPage(' + i + ');'
				+ '"'
				+ ' >' 
			    + i + '</a>';
			if( currentPage == i )
			{
				page = '<span';
				page += ' >'+i+'</span>';
			}
			allPages += page;
		}
		$('#pages').html(allPages);
	}
}

function goToPage(pageNum)
{
	var newOffset = (pageNum-1)*okapied.limit;
	okapied.offset = newOffset;
	var url = okapied.prepareUrl();
	window.location.href = url;  
}
</script>
</s:if>
</body>
</html>