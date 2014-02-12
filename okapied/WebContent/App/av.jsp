<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>

<s:include value="/App/list_header.jsp"></s:include>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/app.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
<script src="<%=request.getContextPath()%>/js/yahoo-dom-event.js"></script>
<script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>
<script src="<%=request.getContextPath()%>/js/connection_core-min.js"></script>
<script src="<%=request.getContextPath()%>/js/json-min.js"></script>
<script src="<%=request.getContextPath()%>/js/explorer.js"></script>
<script src="<%=request.getContextPath()%>/js/av.js"></script>

<div class="list-av">
<h2>Availability and Pricing</h2>
<p>
Okapied is an instant booking site. When you mark your property as available and set a price for a given period the
customer can instantly book and pay for a stay. Customers can only book 89 days in advance.
</p>
<p>
Click on dates, months, or week days to change the availability.
</p>
<div id="av-bar">
    <div id="clear-all">Make All:</div>
    <a id="available-button" href="javascript:void(0)">
        <img alt="Set as always available." title="Set as always available." 
            src="<%=request.getContextPath()%>/images/available_button.png"/>
    </a>
    <a id="unavailable-button" href="javascript:void(0)">
        <img alt="Set as always unavailable." title="Set as always unavailable." 
            src="<%=request.getContextPath()%>/images/unavailable_button.png"/>
    </a>
    <div id="base-price">Price:</div>
    <input id="base-price-text" type="text" />
    <div id="av-bar-currency"><s:property value="property.currencyCode.code" /></div>
    <input type="checkbox" id="set-price" name="set-price"/>
    <div id="set-price-label">Set Price on Click</div>
    <a id="clear-prices-button" href="javascript:void(0)">
        <img alt="Sets the default price and removes all price changes." title="Sets the default price and removes all price changes." 
            src="<%=request.getContextPath()%>/images/clear_prices_button.png"/>
    </a>
</div>
<div id="av-calendar"></div>
</div>
<s:include value="/App/list_footer.jsp"></s:include>
<script>
var explorer;
var av;
$(document).ready(function(){
	var userId = <% out.println(LoginStatus.getUser().getId()); %>
	explorer = new Explorer('explorer-container',userId,Explorer.PROPERTIES,'<%=request.getContextPath()%>');
	explorer.render();
	av = new Av('av-calendar','<s:property value="property.id" />','<%=request.getContextPath()%>');
	av.render();
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>