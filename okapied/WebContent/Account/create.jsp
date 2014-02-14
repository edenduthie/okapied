<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

    <s:include value="/header.jsp"></s:include>

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/container.css">
    <script src="<%=request.getContextPath()%>/js/connection_core-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/json-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/explorer.js"></script>
    <script src="<%=request.getContextPath()%>/js/booking.js"></script>
    <script src="<%=request.getContextPath()%>/js/panels.js"></script>
    <script src="<%=request.getContextPath()%>/js/dragdrop-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/container-min.js"></script>

    <div id="create-account-page">
        <div id="login-page-top">
            <div class="top-right"></div>
            <div class="top-left"></div>
            <div class="top-center"></div>
        </div>
        <div class="login-content medium-height">
            <div class="login-text">
		    <h1>Create an Account</h1>
		    <s:if test="message!=null" >
		        <div class="message"><s:property escape="false" value="message" /></div>
		    </s:if>
		    <div class="message">Sign up for your free account to easily rent accomodation or list your place.</div>
		    <s:form action="/Account/Account/save">
		        <s:textfield label="username" name="user.username" cssClass="medium-input" />
		        <s:textfield label="first name" name="user.firstName" />
		        <s:textfield label="last name" name="user.lastName" />
		        <div class="group-padding-bottom">
		            <s:textfield label="PayPal email" name="user.email" />
		            <div class="question">
		                <img alt="This must be a valid PayPal email if you want to list properties." title="This must be a valid PayPal email to list properties." src='<%=request.getContextPath()%>/images/question.png'></img>
		            </div>
		        </div>
		        <s:password label="password" name="user.password" />
		        <s:password label="confirm pass" name="confirmPassword" />
		        <div class="sign-up-button"><s:submit type="image" src="%{getRequest().getContextPath()}/images/sign_up_button.png"></s:submit></div>
		        <div id="disclaimer"><s:checkbox name="terms" /> I have read and agree to abide by Okapied's <a href="javascript:void(0)" onclick="termsAndConditionsPanel('termsAndConditionsPanel')">Terms and Conditions</a></div>
		        <div class="clearfloat"></div>
		    </s:form>
		    </div>
		</div>
        <div id="login-bottom">
            <div class="bottom-right"></div>
            <div class="bottom-left"></div>
            <div class="bottom-center"></div>
        </div>
    </div>
    <s:include value="/footer.jsp"></s:include>
<div id="termsAndConditionsPanel">
<div class="hd"></div>
<div class="bd">
<div class="scroll">
<s:include value="/terms.jsp"></s:include>
</div>
</div>
<div class="ft"></div>
</div>
</body>
</html>