<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>
    <s:include value="/App/list_header.jsp"></s:include>
    <title>Enter Property Descriptions</title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/app.css" />
    
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
    <script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/connection_core-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/json-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/explorer.js"></script>

<div class='list-description'>
<s:form action="/App/PropertyText/listsubmit" method="post">
<h2>Description</h2>
<p>
The property details are your chance to sell the place. 
Give an brief overview of the property before describing all the features. 
Part of your property feedback score is whether it is as described, so be honest!
</p>
<s:textarea name="property.propertyDetails.description" cols="85" rows="18"></s:textarea>
<h2>Check-In Instructions</h2>
<p>
When a customer has booked and paid for your property they are given access to the following secret information.
Use this space to instruct the customer on how to access your property.
For example it may contain the access code and location of a lock box containing the keys. 
Alternatively you could list your mobile number and instructions to call so you can drop off the keys. It is up to you.
</p>
<s:textarea name="property.propertyDetails.checkInInstructions" cols="85" rows="18"></s:textarea>
<s:hidden name="propertyId" />
<input id="save-changes-now-button" type="image" src="<%=request.getContextPath()%>/images/save_changes_now_button.png"></input>
</s:form>
</div>
<s:include value="/App/list_footer.jsp"></s:include>
<script>
var explorer;
$(document).ready(function(){
	var userId = <% out.println(LoginStatus.getUser().getId()); %>
	explorer = new Explorer('explorer-container',userId,Explorer.PROPERTIES,'<%=request.getContextPath()%>');
	explorer.render();
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>