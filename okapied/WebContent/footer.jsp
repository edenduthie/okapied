<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>

<div class="footer-container">
    <div class="left-text">
        &copy; 2011 Gator Logic Pty. Ltd.
    </div>
    <div class="right-text">
    <a href="<%=request.getContextPath()%>/Browse/AboutUs/list">About Us</a>
    <a href="<%=request.getContextPath()%>/Browse/Terms/list">Terms</a>
    <a href="<%=request.getContextPath()%>/Browse/Browse/list">Browse</a>
    <a href="<%=request.getContextPath()%>/Browse/Contact/list">Contact</a>
    </div>
</div>