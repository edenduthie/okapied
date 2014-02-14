<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>

<s:include value="/header.jsp"></s:include>

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/app.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/container.css">
    
    <script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/connection_core-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/json-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/explorer.js"></script>
    <script src="<%=request.getContextPath()%>/js/booking.js"></script>
    <script src="<%=request.getContextPath()%>/js/panels.js"></script>
    <script src="<%=request.getContextPath()%>/js/dragdrop-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/container-min.js"></script>

<div class="content">
    <div id="app-content">
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
        <div id="edit-account">
            <div id="app-canvas-top">
                <div class="top-right"></div>
                <div class="top-left"></div>
                <div class="top-center"></div>
            </div>
            <div class="white-edit-account-content">
                <h1>Account Details - <s:property value="user.name" /></h1>
            </div>
            <div class="edit-account-content">
				    <div class="edit-account-message">Edit your details:</div>
				    <s:form action="/Account/AccountEdit/list">
				        <s:textfield label="username" name="user.username" cssClass="medium-input" />
				        <s:textfield label="first name" name="user.firstName" />
				        <s:textfield label="last name" name="user.lastName" />
				        <div class="group-padding-bottom">
				            <s:textfield label="PayPal email" name="user.email" />
				            <div class="question">
				                <img alt="This must be a valid PayPal email if you want to list properties." title="This must be a valid PayPal email to list properties." src='<%=request.getContextPath()%>/images/question.png'></img>
				            </div>
				        </div>
				        <div class="group-padding-bottom">
				            <s:textfield label="alternate email" name="user.preferredEmail" />
				            <div class="question">
				                <img alt="If you have a preferred email address for correspondence from Okapied, enter it here." title="If you have a preferred email address for correspondence from Okapied, enter it here." src='<%=request.getContextPath()%>/images/question.png'></img>
				            </div>
				        </div>
				        <s:textfield label="phone" name="user.phoneNumber" />
				        <s:password label="password" name="user.password" />
				        <s:password label="confirm pass" name="confirmPassword" />
				        <div id="save-changes-button"><s:submit type="image" src="%{getRequest().getContextPath()}/images/save_changes_button.png"></s:submit></div>
				        <s:hidden name="user.id"></s:hidden>
				    </s:form>
            </div>
            <div id="app-canvas-bottom">
                <div class="grey-bottom-right"></div>
                <div class="grey-bottom-left"></div>
                <div class="grey-bottom-center"></div>
            </div>
        </div>
    </div><!-- first-row -->
    </div><!-- app-content -->
</div>
<script>
var explorer;
$(document).ready(function(){
	var userId = <% out.println(LoginStatus.getUser().getId()); %>
	explorer = new Explorer('explorer-container',userId,Explorer.BOOKINGS,'<%=request.getContextPath()%>');
	explorer.render();
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>