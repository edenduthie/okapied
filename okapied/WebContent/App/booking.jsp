<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>
    <s:include value="/header.jsp"></s:include>
    
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/app.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/container.css">
        
    <script src="<%=request.getContextPath()%>/js/yahoo-dom-event.js"></script>
    <script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/connection_core-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/json-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/explorer.js"></script>
    <script src="<%=request.getContextPath()%>/js/booking.js"></script>
    <script src="<%=request.getContextPath()%>/js/panels.js"></script>
    <script src="<%=request.getContextPath()%>/js/dragdrop-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/container-min.js"></script>

<div class="content">
    <div id="refundPolicyPanel">
        <div class="hd"></div>
        <div class="bd"></div>
        <div class="ft"></div>
    </div>
    <div id="app-content">
    <form id="booking-form" action="<%=request.getContextPath()%>/App/Booking/start">
    <div class="left-column-skinny">
       <div id="explorer">
           <div id="explorer-top">
                <div class="black-top-right"></div>
                <div class="black-top-left"></div>
                <div class="black-top-center"></div>
            </div>
            <div class="explorer-content">
                <div id="explorer-container"></div>
            </div>
            <div id="explorer-pagination">
                <a id="explorer-next" href="javascript:void(0)"><img alt="Next" title="Next" src="<%=request.getContextPath()%>/images/explorer-next.png" /></a>
                <a id="explorer-previous" href="javascript:void(0)"><img alt="Previous" title="Previous" src="<%=request.getContextPath()%>/images/explorer-previous.png" /></a>
            </div>
            <div id="explorer-bottom">
                <div class="bottom-right"></div>
                <div class="bottom-left"></div>
                <div class="bottom-center"></div>
            </div>
       </div>
    </div><!-- left-column -->
    <div class="right-column-wide">
        <s:if test="message != null">
            <div id="app-error-message">
                 <div id="app-error-message-top">
                    <div class="top-right"></div>
                    <div class="top-left"></div>
                    <div class="top-center"></div>
                </div>
                <div id="app-error-message-content">
                    <div class="text">
                        <s:property value="message" escape="false" />
                    </div>
                </div>
                <div id="app-error-message-bottom">
                    <div class="bottom-right"></div>
                    <div class="bottom-left"></div>
                    <div class="bottom-center"></div>
                </div>
            </div>
        </s:if>
        <div id="app-canvas">
            <div id="app-canvas-top">
                <div class="top-right"></div>
                <div class="top-left"></div>
                <div class="top-center"></div>
            </div>
            <div class="canvas-heading">
                <div class="text"><h1>Make A Booking</h1></div>
            </div>
            <div class="small-canvas app-canvas-content">
                <div class="text">
                    <div class="first-row">
                    <div class="wait"><img src="<%=request.getContextPath()%>/images/wait.gif"></img></div> 
                    <h2><s:property value="property.name"></s:property></h2>
                    <div id="reservation-message">
                        <div class="message-content">
                            <div id="countdown-label">Dates held for: </div><div id="countdown"></div>
                        </div>
                    </div>
                    <div class="calendars">
                        <div class="cal-col">
                            start date <input id="date-0" type="text" name="start" />
                            <div id="booking-cal-0"></div>
                        </div>
                        <div class="cal-col">
                            end date <input id="date-1" type="text" name="end" />
                            <div id="booking-cal-1"></div>
                        </div>
                    </div>
                    <div id="refund-policy">
                        <div class="title">Refund Policy</div>
                        <a href="javascript:void(0)" onclick="refundPolicyPanel('refundPolicyPanel','<%=request.getContextPath()%>')"><img alt="See details of our refund policies." title="See details of our refund policies" src='<%=request.getContextPath()%>/images/question.png'></img></a>
                        <div id="refund-policy-text"><s:property value="property.retrieveRefundPolicyText()" /></div>
                    </div>
                    </div><!-- first-row -->
                    
                    <div id="book-button">
                        <input type="hidden" name="bookingId" id="booking-id" />
                        <input type="hidden" name="people" value="2" />
                        <input type="hidden" name="propertyId" value="${property.id}" />
                        <input id="book-submit-button" type="image" src="<%=request.getContextPath()%>/images/book_now_button.png" />
                    </div>
                    <div id="reservation-details"></div>
                </div>
            </div>
            <div id="app-canvas-bottom">
                <div class="grey-bottom-right"></div>
                <div class="grey-bottom-left"></div>
                <div class="grey-bottom-center"></div>
            </div>
        </div>
    </div><!-- first-row -->
    </form>
    </div><!-- app-content -->
</div>
<script>
var explorer;
var booking;
$(document).ready(function(){
	booking = new Booking(<s:property value="propertyId"/>,'app-canvas','<%=request.getContextPath()%>');
	booking.render();
	var userId = <% out.println(LoginStatus.getUser().getId()); %>
	explorer = new Explorer('explorer-container',userId,Explorer.BOOKINGS,'<%=request.getContextPath()%>');
	explorer.render();
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>