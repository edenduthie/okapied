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
<div id="browse-content">
<div id="browse-content-text">
<div class="browse-content">
<h1>Browse Okapied's Holiday Rental Listings</h1>
<h2><a href="<%=request.getContextPath()%>/Browse/Browse/list">Countries</a> > <a href="<%=request.getContextPath()%>/<s:property value="country.name"/>"><s:property value="country.name"/></a></h2>
<div class="browse-list">
<s:iterator value="regions">
<div><a href="<%=request.getContextPath()%>/<s:property value="country.name"/>/<s:property value="name"/>"><s:property value="name"/></a>  <a href='<%=request.getContextPath()%>/Browse/Browse/location?regionId=<s:property value="id"/>'>(locations)</a></div>
</s:iterator>
</div>
</div>
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
</div><!--content-->
</body>
</html>