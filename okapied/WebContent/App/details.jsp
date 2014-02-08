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
    <script src="<%=request.getContextPath()%>/js/details.js"></script>
<div class="list-details">
<h2>Features</h2>
<s:form action="/App/Details/listsubmit" method="post">
<div id="details-features">
<div id="features-left-column">
    <p>
    The features that your propery has are an important part of the listing. 
    Users can perform advanced searches to filter property results by feature.
    </p>
    <p>
    If your property has any additional features that we haven’t thought of please let us know.
    You can put them in the description for now.
    </p>
</div>
<div id="features-right-column" class="features-right-column">
 <div class="left-smallmedium-column">
     <h3 class="no-pad">Bedroom</h3>
         <s:if test="!room"><s:select name="bedrooms" label="Bedrooms" list="bedroomsOptions"></s:select></s:if>
         <s:select name="property.propertyDetails.sleeps" label="Sleeps" list="numberList"></s:select>
         <s:select name="property.propertyDetails.kingBeds" label="King" list="numberList"></s:select>
         <s:select name="property.propertyDetails.queenBeds" label="Queen" list="numberList"></s:select>
         <s:select name="property.propertyDetails.doubleBeds" label="Double" list="numberList"></s:select>
         <s:select name="property.propertyDetails.singleBeds" label="Single" list="numberList"></s:select>
     <h3 class="top-pad">Kitchen</h3>
         <s:iterator value="options">
             <s:if test='optionType==2' >
                <div class="checkboxGrp"><s:checkbox name="%{name}_%{optionType}" fieldValue="true" value="%{isSelected}" label="%{name}"></s:checkbox></div>
                </s:if>
            </s:iterator>
     <h3 class="top-pad">Outdoor</h3>
         <s:iterator value="options">
             <s:if test='optionType==3' >
                <div class="checkboxGrp"><s:checkbox name="%{name}_%{optionType}" fieldValue="true" value="%{isSelected}" label="%{name}"></s:checkbox></div>
                </s:if>
            </s:iterator>
            <div id="dist-field">
             <s:textfield name="distanceToBeachM" label="Dist to Beach (m)"/>
         </div>
 </div>
 <div class="right-small-column">
     <h3 class="no-pad">Bathroom/Laundry</h3>
         <s:select name="property.propertyDetails.bathrooms" label="Bathrooms" list="numberList"></s:select>
         <s:iterator value="options">
             <s:if test='optionType==4' >
                <div class="checkboxGrp"><s:checkbox name="%{name}_%{optionType}" fieldValue="true" value="%{isSelected}" label="%{name}"></s:checkbox></div>
                </s:if>
            </s:iterator>
     <h3 class="top-pad">Ammenities</h3>
         <s:select name="flooringId" id="flooring-select" label="Flooring" list="flooring" listKey="id" listValue="name"></s:select>
         <s:iterator value="options">
             <s:if test='optionType==5' >
                <div class="checkboxGrp"><s:checkbox name="%{name}_%{optionType}" fieldValue="true" value="%{isSelected}" label="%{name}"></s:checkbox></div>
                </s:if>
            </s:iterator>
     <h3 class="top-pad">Multimedia</h3>
         <s:iterator value="options">
             <s:if test='optionType==6' >
                <div class="checkboxGrp"><s:checkbox name="%{name}_%{optionType}" fieldValue="true" value="%{isSelected}" label="%{name}"></s:checkbox></div>
                </s:if>
            </s:iterator>
 </div>
</div>
</div>
<s:hidden name="propertyId" />
<s:hidden id="kitchenOptions" name="kitchenOptions" />
<s:hidden id="bathroomOptions" name="bathroomOptions" />
<s:hidden id="outdoorOptions" name="outdoorOptions" />
<s:hidden id="amOptions" name="amOptions" />
<s:hidden id="multiOptions" name="multiOptions" />
<input id="save-changes-now-button" type="image" src="<%=request.getContextPath()%>/images/save_changes_now_button.png"></input>
</s:form>
</div>
</div>
<s:include value="/App/list_footer.jsp"></s:include>
<script>
var explorer;
var details;
$(document).ready(function(){
	var userId = <% out.println(LoginStatus.getUser().getId()); %>
	explorer = new Explorer('explorer-container',userId,Explorer.PROPERTIES,'<%=request.getContextPath()%>');
	explorer.render();
	details = new Details();
	details.render();
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>