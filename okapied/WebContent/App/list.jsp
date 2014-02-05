<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
"http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>
<s:include value="/App/list_header.jsp"></s:include>

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/app.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/container.css">
    
    <script src="<%=request.getContextPath()%>/js/yahoo-dom-event.js"></script>
    <script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/connection_core-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/json-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/explorer.js"></script>
    <script src="<%=request.getContextPath()%>/js/list.js"></script>
    <script src="<%=request.getContextPath()%>/js/panels.js"></script>
    <script src="<%=request.getContextPath()%>/js/dragdrop-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/container-min.js"></script>
<div class="list-essential">
<s:if test="validPayPal">
<s:if test="property != null && property.id > 0">
    <h2>Edit Essential Property Details</h2>
    <p>
    You can update the following essential information about your property. Click on ‘List Now’ to immediately save the changes.
    </p>
</s:if>
<s:else>
    <h2>Get Your Property Up There</h2>
    <p>
    Please provide the following essential information about your property. Click on ‘List Now’ to immediately add it to the listings.
    </p>
</s:else>
<div id="essential-list-form">
<s:form action="/App/Publish/%{methodName}submit" method="post">
    <s:textfield name="property.name" label="Name"></s:textfield>
    <s:textfield name="property.propertyDetails.fullName" label="Short Description"></s:textfield>
    <s:select name="propertyTypeId" label="Type" list="propertyTypes" listKey="id" listValue="name"></s:select>
    <s:select name="property.propertyDetails.sleeps" label="Sleeps" list="sleepsOptions"></s:select>
    <div id="min-booking-nights-input"><s:textfield name="property.minNights" label="Min Booking Nights" value="%{property.minNights}"></s:textfield></div>
    <div id="bedrooms-select"><s:select name="bedrooms" label="Bedrooms" list="bedroomsOptions"></s:select></div>
    <div class="nofloat">
        <div id="currency-code-select"><s:select name="currencyCodeId" label="Currency" list="currencyCodes" listKey="id" listValue="displayText"></s:select></div>
        <div class="exclamation"><img alt="Your PayPal account must be configured to accept this currency" title="Your PayPal account must be configured to accept this currency" src='<%=request.getContextPath()%>/images/exclamation.png'></img></div>
    </div>
    <div class="nofloat bottom-pad">
        <div id="refund-policy-select">
            <s:radio name="property.refundPolicy" label="Refund Policy" list="refundPolicyOptions"></s:radio>
        </div>
        <div class="question"><a href="javascript:void(0)" onclick="refundPolicyPanel('refundPolicyPanel','<%=request.getContextPath()%>')"><img alt="See details of our refund policies." title="See details of our refund policies" src='<%=request.getContextPath()%>/images/question.png'></img></a></div>
    </div>
    <div id="private-fields">
    <div class="nofloat">
        <div id="unit-number-select"><s:textfield name="property.propertyDetails.unit" label="Unit / Apartment"></s:textfield></div>
        <div class="question"><img alt="Unit/Apartment numbers are only shown to guests when they have paid for a booking." title="Unit/Apartment numbers are only shown to customers when they have booked your property." src='<%=request.getContextPath()%>/images/question.png'></img></div>
        <div id="private-fields-label">Private Fields</div>
    </div>
    <div class="nofloat">
      <div id="street-number-select"><s:textfield name="property.propertyDetails.streetNumber" label="Street Number"></s:textfield></div>
      <div class="question"><img alt="Street numbers are only shown to customers when they have paid for a booking." title="Street numbers are only shown to customers when they have booked your property." src='<%=request.getContextPath()%>/images/question.png'></img></div>
    </div>
    </div>
    <s:textfield name="property.propertyDetails.street" label="Street"></s:textfield>
    <s:textfield name="location" label="City/Suburb"></s:textfield>
    <div class="input-long-auto" id="location-empty-results-container-list"></div>
    <s:textfield name="property.propertyDetails.postcode" label="Postcode"></s:textfield>
    <p id="availability-instructions">
        By default new properties are available for the next 3 months at a single rate, you can change this below.
        Once the property is listed the Availability and Pricing page gives you complete control.
    </p>
    <div id="always-available">
        <label>Always available for next 3 months</label><s:checkbox name="alwaysAvailable" fieldValue="true" value="%{alwaysAvailable}"></s:checkbox>
    </div>
    <s:textfield name="dailyRate" label="Daily Rate"></s:textfield>
    <a onclick="list.listNow()" href="javascript:void()" id="list-now-button"><img src="<%=request.getContextPath()%>/images/list_now_button.png" alt="Save Property Attributes" title="Save Property Attributes"/></a>
    <s:hidden id="location-id-hidden" name="locationId"></s:hidden>
    <s:hidden name="propertyId" />
    <s:hidden name="methodName" />
    <div id="locationNameDiv" style="display:none;"><s:property value="property.location.name" /></div>
</s:form>
</div>
</div>
</s:if><!--validPayPal-->
<s:else>
<div id="invalid-pay-pal">
    The first name, last name, email combination you have provided for your account do not match a valid PayPal account.
    Please ensure that these details are correct on <a href="<%=request.getContextPath()%>/Account/Account/edit">your account page</a> to be able to list properties. 
</div>
</div>
</s:else>
<s:include value="/App/list_footer.jsp"></s:include>                
<s:if test="property != null && property.id > 0">
<script>
function showLocation()
{
	$('#location-id-hidden').val(<s:property value="property.location.id" />);
    list.locationId = <s:property value="property.location.id" />;
    var method = '<s:property value="methodName" />';
    list.method = 'editsubmit_';
    list.displayLocations();
};
</script>
</s:if>
<script>
var explorer;
var list;
$(document).ready(function(){
	var userId = <% out.println(LoginStatus.getUser().getId()); %>
	explorer = new Explorer('explorer-container',userId,Explorer.PROPERTIES,'<%=request.getContextPath()%>');
	explorer.render();
	list = new List('<s:property value="methodName" />',
			$('#locationNameDiv').html(),
			'location',
			'location-empty-results-container-list',
			'<%=request.getContextPath()%>');
    list.method = 'listsubmit_';
	list.render();
	if( typeof showLocation == 'function')
	{
	    showLocation();
	}
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>