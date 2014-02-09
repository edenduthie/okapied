<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>
<s:include value="/App/booking_header.jsp"></s:include>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/app.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
    <script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/connection_core-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/json-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/explorer.js"></script>

<div id="view-booking-details">
<div id="show-photo">
<s:if test="booking.retrieveShowPhotoId() != -1">
<img id="show-image" src=<%=request.getContextPath()%>/File/Photo_list.action?photoId=<s:property value="booking.retrieveShowPhotoId()" /> />
</s:if>
<s:else>
<img id="show-image" src="<%=request.getContextPath()%>/images/default_property_image.jpg" />
</s:else>
</div>
<h1>Booking Summary</h1>
<div id="summary-first-column">
<table>
    <tr>
        <td class="bold-td">From</td>
        <td><s:date name="booking.startDate" format="E d MMM yyyy"/></td>
    </tr>
    <tr>
        <td class="bold-td">To</td>
        <td><s:date name="booking.endDate" format="E d MMM yyyy"/></td>
    </tr>
    <tr>
        <td class="bold-td">Period</td>
        <td><s:property value="booking.retrievePeriodDays()"/> days</td>
    </tr>
    <tr>
        <td class="bold-td">Total Cost</td>
        <td><s:property value="booking.retrieveTotalRounded()"/> <s:property value="booking.property.currencyCode.code" /></td>
    </tr>
    <tr>
        <td class="bold-td">Status</td>
        <td><s:property value="booking.bookingStatus"/></td>
    </tr>
</table>
</div>
<div id="summary-second-column">
<s:if test="booking.checkIfConfirmedStatus()" >
<b>Customer</b><br>
<s:property value="booking.user.name" /><br>
<s:property value="booking.user.retrieveBestEmail()" /><br>
<s:property value="booking.user.phoneNumber" />
</s:if>
</div>
</div>
            </div><!--text-->
            </div><!--app-canvas-content-->
            <div id="app-canvas-bottom">
                <div class="grey-bottom-right"></div>
                <div class="grey-bottom-left"></div>
                <div class="grey-bottom-center"></div>
            </div>
    </div><!-- app-canvas -->
<s:if test="booking.readyToLeaveFeedbackOwner()" >

<div id="feedback-form">
<div id="feedback-form-top">
	<div class="top-right"></div>
	<div class="top-left"></div>
	<div class="top-center"></div>
</div>
<div id="feedback-form-heading">
<div id="feedback-form-heading-text">
<s:if test="booking.ownerFeedback == null">
<h1>Leave Feedback</h1>
</s:if>
<s:else>
<h1>Your Feedback <s:if test="booking.ownerFeedback.positive">+</s:if><s:else>-</s:else></h1>
</s:else>
</div>
</div>
<div id="feedback-form-content">
<s:if test="booking.ownerFeedback == null">
<s:form  action="/App/ViewBookingOwner/feedback" method="post">
<div id="feedback-form-left-col">
<s:radio cssClass="positive-radio" label="Feedback Type" name="positive" list="{'Positive','Negative'}"/>
<s:radio label="Left in Resonable Condition" name="userFeedback.conditionOnExit" list="feedbackOptions"/>
<s:radio label="Overall Rating" name="userFeedback.overall" list="feedbackOptions"/>
</div>
<div id="feedback-form-right-col">
<s:textarea name="userFeedback.text" rows="8" cols="29"></s:textarea>
<s:hidden name="id" value="%{id}" />
<input type="image" src="<%=request.getContextPath()%>/images/post_feedback_button.png" alt="Post your feedback, you can only do this once for a booking" title="Post your feedback, you can only do this once for a booking"/>
</div>
</s:form>
</s:if>
<s:else>
<div id="your-feedback-left">
    <div id="overall-feedback-graph">
       Left in Resonable Condition: <div class="percentage-pos"><s:property value="booking.ownerFeedback.conditionOnExitPercentage()" />%</div><br>
       <div class="feedback-bar"><div style='width: <s:property value="booking.ownerFeedback.conditionOnExitPercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
       Overall: <div class="percentage-pos"><s:property value="booking.ownerFeedback.overallPercentage()" />%</div><br>
       <div class="feedback-bar"><div style='width: <s:property value="booking.ownerFeedback.overallPercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
   </div>
</div>
<div id="your-feedback-right">
    <s:property value="ownerFeedbackText" escape="false" />
</div>
</s:else>
</div>
<div id="feedback-form-bottom">
	<div class="grey-bottom-right"></div>
	<div class="grey-bottom-left"></div>
	<div class="grey-bottom-center"></div>
	</div>
</div>

</s:if>
    </div><!-- right-column-wide -->
</div><!--app-content-->
<script>
var explorer;
$(document).ready(function(){
	var userId = <% out.println(LoginStatus.getUser().getId()); %>
	explorer = new Explorer('explorer-container',userId,Explorer.PROPERTY_BOOKINGS,'<%=request.getContextPath()%>');
	explorer.render();
	if( $('#feedback-form-text .errorMessage').length > 0 )
	{
		$('#feedback-form-content').css('height','300px');
	}
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>