<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/header.jsp"></s:include>
<div id="content">
<div id="login-page">
    <div id="login-page-top">
        <div class="top-right"></div>
        <div class="top-left"></div>
        <div class="top-center"></div>
    </div>
    <div class="login-content">
        <div class="full-message"><s:property value="message" /></div>
    </div>
    <div id="login-bottom">
        <div class="bottom-right"></div>
        <div class="bottom-left"></div>
        <div class="bottom-center"></div>
    </div>
</div>
<s:include value="/footer.jsp"></s:include>
</div><!-- content -->
</body>
</html>