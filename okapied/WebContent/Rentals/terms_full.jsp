<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/header.jsp"></s:include>

<div id="content">
<div class="content">
<div id="terms">
<div id="terms-top">
    <div class="top-right"></div>
    <div class="top-left"></div>
    <div class="top-center"></div>
</div>
<div id="terms-content">
<div id="terms-content-text">
<s:include value="/terms.jsp"></s:include>
</div><!-- terms-content-text -->
</div>
<div id="terms-bottom">
    <div class="bottom-right"></div>
    <div class="bottom-left"></div>
    <div class="bottom-center"></div>
</div>
</div><!-- terms -->
</div><!-- content class -->
<s:include value="/footer.jsp"></s:include>
</div><!-- content -->
</body>
</html>