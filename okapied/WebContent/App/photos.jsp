<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
"http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>

<s:include value="/App/list_header.jsp"></s:include>

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/app.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css">
    
    <script src="<%=request.getContextPath()%>/js/calendar-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/connection_core-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/json-min.js"></script>
    <script src="<%=request.getContextPath()%>/js/explorer.js"></script>
    <script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
    <!--  
    <script src="<%=request.getContextPath()%>/galleria/src/galleria.js"></script>
    <script src="<%=request.getContextPath()%>/galleria/src/themes/classic/galleria.classic.js"></script>
    -->
    <script src="<%=request.getContextPath()%>/js/photos.js"></script>
    
<div class='list-photos'>
<h2>Photos</h2>
<p>
You have to upload some photos of the property to spice up your listing! 
These are resized to 650x488 when shown in the photo gallery. 
Try and capture as much of the place as you can, people love to see photos. 
You can upload up to 20.
</p>
<s:form action="/App/Photos/listsubmit" method="post" enctype="multipart/form-data">
    <div id="list-photo-buttons">
    <input id="upload-file-button" type="image" src="<%=request.getContextPath()%>/images/upload_button.png" alt="Upload Selected Images" title="Upload Selected Images"></input>
    <a id="remove-all" href='<%=request.getContextPath()%>/App/Photos/removeall?propertyId=<s:property value="propertyId" />'>
        <img src="<%=request.getContextPath()%>/images/remove_all_button.png" alt="Delete all Images" title="Delete all Images"/>
    </a>
    </div>
    <s:file name="upload" label="Upload A Picture"></s:file>
    <s:file name="upload" label="Upload A Picture"></s:file>
    <s:file name="upload" label="Upload A Picture"></s:file>
    <s:file name="upload" label="Upload A Picture"></s:file>
    <s:file name="upload" label="Upload A Picture"></s:file>
<div id="list-photos-gallery">
<s:iterator value="property.photos">
    <img 
        src='<%=request.getContextPath()%>/File/Photo_list.action?photoId=<s:property value="id" />' 
        id='<%=request.getContextPath()%>/App/Photos/remove?photoId=<s:property value="id" />&propertyId=<s:property value="propertyId" />' 
    ></img>
</s:iterator>
</div>
<s:hidden name="propertyId" />
</s:form>
</div>
<s:include value="/App/list_footer.jsp"></s:include>
<script>
contextPath = '<%=request.getContextPath()%>';
var explorer;
var photos;
$(document).ready(function(){

	// left sidebar
	var userId = <%out.println(LoginStatus.getUser().getId());%>
	explorer = new Explorer('explorer-container',userId,Explorer.PROPERTIES,'<%=request.getContextPath()%>');
	explorer.render();

	var photos = new Photos('list-photos-gallery','<%=request.getContextPath()%>');
	photos.render();
});
</script>
<s:include value="/footer.jsp"></s:include>
</body>
</html>