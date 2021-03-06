<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/header.jsp"></s:include>

<div id="content">
<div class="content">
<div id="contact-us">
<div id="contact-us-top">
    <div class="top-right"></div>
    <div class="top-left"></div>
    <div class="top-center"></div>
</div>
<div id="contact-us-content">
<div id="contact-us-content-text">
<h1>Contact Us</h1>
<p>
Just send us an email at <a href="mailto:<s:property value="email" />"><s:property value="email" /></a> and we will get back
to you as soon as possible.
</p>
</div><!-- contact-us-content-text -->
</div>
<div id="contact-us-bottom">
    <div class="bottom-right"></div>
    <div class="bottom-left"></div>
    <div class="bottom-center"></div>
</div>
</div><!--contact-us -->
</div><!-- content class -->
<s:include value="/footer.jsp"></s:include>
</div><!-- content -->
</body>
</html>