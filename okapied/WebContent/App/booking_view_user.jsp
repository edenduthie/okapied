<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>


<s:include value="/App/booking_header.jsp"></s:include>

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/app.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/container.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
    
    <script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/connection_core-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/json-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/explorer.js"></script>
    <script src="<%=request.getContextPath()%>/js/panels.js"></script>
    <script src="<%=request.getContextPath()%>/js/dragdrop-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/container-min.js"></script>

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
        <td class="bold-td">Total</td>
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
<b>Address</b><br>
<s:if test="booking.property.propertyDetails.unit != null && booking.property.propertyDetails.unit.length() > 0" >
    <s:property value="booking.property.propertyDetails.unit" />/<s:property value="booking.property.propertyDetails.streetNumber" />
</s:if>
<s:else>
    <s:property value="booking.property.propertyDetails.streetNumber" />
</s:else>
<s:property value="booking.property.propertyDetails.street" /><br> <s:property value="booking.property.location.name" />, <s:property value="booking.property.location.region.name" />, <s:property value="booking.property.location.country.name" /> <s:property value="booking.property.propertyDetails.postcode" />
<div class="top-pad">Owner</div>
<s:property value="booking.property.owner.name" /><br>
<s:property value="booking.property.owner.retrieveBestEmail()" /><br>
<s:property value="booking.property.owner.phoneNumber" />
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
    
<s:if test="booking.checkIfConfirmedStatus()" >
<div id="check-in">
<div id="check-in-top">
    <div class="black-top-right"></div>
    <div class="black-top-left"></div>
    <div class="black-top-center"></div>
</div>
<div id="black-check-in-content">
    <div id="black-check-in-content-text"><h1>Check-In Instructions</h1></div>
</div>
<div id="check-in-content">
<div class="check-in-text">
    <div id="check-in-instructions"><s:property value='checkInText' escape='false'/></div>
</div>
</div>
<div id="check-in-bottom">
    <div class="bottom-right"></div>
    <div class="bottom-left"></div>
    <div class="bottom-center"></div>
</div>
</div>
</s:if>

<div id="booking-third-row">
<s:if test="booking.readyToLeaveFeedbackUser()" >
<div id="feedback-form">
<div id="feedback-form-top">
	<div class="top-right"></div>
	<div class="top-left"></div>
	<div class="top-center"></div>
</div>
<div id="feedback-form-heading">
<div id="feedback-form-heading-text">
<s:if test="booking.userFeedback == null">
<h1>Leave Feedback</h1>
</s:if>
<s:else>
<h1>Your Feedback <s:if test="booking.userFeedback.positive">+</s:if><s:else>-</s:else></h1>
</s:else>
</div>
</div>
<div id="feedback-form-content">
<s:if test="booking.userFeedback == null">
<s:form  action="/App/ViewBooking/feedback" method="post">
<div id="feedback-form-left-col">
<s:radio cssClass="positive-radio" label="Feedback Type" name="positive" list="{'Positive','Negative'}"/>
<s:radio label="Cleanliness" name="feedback.cleanliness" list="feedbackOptions"/>
<s:radio label="As Described" name="feedback.accuracy" list="feedbackOptions"/>
<s:radio label="Value for Money" name="feedback.valueForMoney" list="feedbackOptions"/>
<s:radio label="Overall Rating" name="feedback.rating" list="feedbackOptions"/>
</div>
<div id="feedback-form-right-col">
<s:textarea name="feedback.text" rows="8" cols="29"></s:textarea>
<s:hidden name="id" value="%{id}" />
<input type="image" src="<%=request.getContextPath()%>/images/post_feedback_button.png" alt="Post your feedback, you can only do this once for a booking" title="Post your feedback, you can only do this once for a booking"/>
</div>
</s:form>
</s:if>
<s:else>
<div id="your-feedback-left">
    <div id="overall-feedback-graph">
       Cleanliness: <div class="percentage-pos"><s:property value="booking.userFeedback.cleanlinessPercentage()" />%</div><br>
       <div class="feedback-bar"><div style='width: <s:property value="booking.userFeedback.cleanlinessPercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
       As Described: <div class="percentage-pos"><s:property value="booking.userFeedback.accuracyPercentage()" />%</div><br>
       <div class="feedback-bar"><div style='width: <s:property value="booking.userFeedback.accuracyPercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
       Value: <div class="percentage-pos"><s:property value="booking.userFeedback.valuePercentage()" />%</div><br>
       <div class="feedback-bar"><div style='width: <s:property value="booking.userFeedback.valuePercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
       Overall: <div class="percentage-pos"><s:property value="booking.userFeedback.ratingPercentage()" />%</div><br>
       <div class="feedback-bar"><div style='width: <s:property value="booking.userFeedback.ratingPercentage()" />%;' class="feedback-bar-fill">&nbsp;</div></div>
   </div>
</div>
<div id="your-feedback-right">
    <s:property value="userFeedbackText" escape="false" />
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
<s:if test="booking.isRefundAvailable() && refundAmount > 0">
<div id="cancellation">
<div id="cancellation-top">
    <div class="black-top-right"></div>
    <div class="black-top-left"></div>
    <div class="black-top-center"></div>
</div>
<div id="cancellation-heading">
    <div class="cancellation-heading-text"><h1>Cancellation</h1></div>
</div>
<div id="cancellation-content">
<div class="cancellation-text">
<s:form action="/App/ViewBooking/refund" method="post">
    <table>
    <tr>
        <td class="title-td">Total Paid</td>
        <td><s:property value="booking.total" /></td>
        <td><s:property value="booking.currencyCode.code" /></td>
    </tr>
    <tr>
        <td class="title-td">Refund Amount</td>
        <td><s:property value="refundAmount" /></td>
        <td><s:property value="booking.currencyCode.code" /></td>
    </tr>
    <tr>
        <td class="title-td">Refund Policy</td>
        <td><s:property value="booking.retrieveRefundPolicyText()" /></td>
        <td><a href="javascript:void(0)" onclick="refundPolicyPanel('refundPolicyPanel','<%=request.getContextPath()%>')"><img alt="See details of our refund policies." title="See details of our refund policies" src='<%=request.getContextPath()%>/images/question.png'></img></a></td>
	</tr>
	</table>
	<input onClick="return cancelBooking();" class="cancellation-button" type="image" src="<%=request.getContextPath()%>/images/cancel_booking_button.png" alt="Cancel this booking" title="Cancel this booking"/>
    <s:hidden name="id" value="%{id}" />
</s:form>
</div>
</div>
<div id="cancellation-bottom">
    <div class="bottom-right"></div>
    <div class="bottom-left"></div>
    <div class="bottom-center"></div>
</div>
</div>
</s:if>
</div><!--booking-third-row-->
    </div><!-- right-column-wide -->
</div><!--app-content-->
<script>

function cancelBooking()
{
    agree = confirm("Are you sure you want to cancel this booking? It cannot be undone.");
    if( agree ) return true;
    else return false;
}
var explorer;
$(document).ready(function(){
	var userId = <%out.println(LoginStatus.getUser().getId());%>
	explorer = new Explorer('explorer-container',userId,Explorer.BOOKINGS,'<%=request.getContextPath()%>');
	explorer.render();

	if( $('#feedback-form-text .errorMessage').length > 0 )
	{
		$('#feedback-form-content').css('height','300px');
	}

	if( $('#requestcancel .errorMessage').length > 0 )
	{
		$('#cancellation-content').css('height','230px');
	}
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>