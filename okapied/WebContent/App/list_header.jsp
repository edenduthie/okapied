<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
    <s:include value="/header.jsp"></s:include>
	<div id="refundPolicyPanel">
	    <div class="hd"></div>
	    <div class="bd"></div>
	    <div class="ft"></div>
	</div>
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
                <div id="explorer-container">
                </div>
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
                <div class="canvas-heading" >
                    <div class="text">
                    <s:if test="property != null && property.id > 0" >
	                    <h1 id="list-h"><a href="<%=request.getContextPath()%>/<s:property value="property.location.country.name" />/<s:property value="property.location.region.name" />/<s:property value="property.location.name" />/<s:property value="property.name" />">
	                        <s:property value="property.name" />
	                    </a></h1>
                    </s:if> 
                    <s:else>
                        <h1 id="list-h">List Your Property</h1>
                    </s:else>
                    <div id="list-menu">
                        <a href='<%=request.getContextPath()%>/App/Publish/list?propertyId=<s:property value="propertyId" />' class="<s:if test='page==1'>selected-list-item</s:if>">Essential</a>|
                        <s:if test="propertyId != null && property.id > 0" >
                            <a href='<%=request.getContextPath()%>/App/Details/list?propertyId=<s:property value="propertyId" />' class="<s:if test='page==2'>selected-list-item</s:if>">Features</a>|
                            <a href='<%=request.getContextPath()%>/App/PropertyText/list?propertyId=<s:property value="propertyId" />' class="<s:if test='page==3'>selected-list-item</s:if>">Description</a>|
                            <a href='<%=request.getContextPath()%>/App/Photos/list?propertyId=<s:property value="propertyId" />' class="<s:if test='page==4'>selected-list-item</s:if>">Photos</a>|
                            <a href='<%=request.getContextPath()%>/App/Av/list?propertyId=<s:property value="propertyId" />' class="<s:if test='page==5'>selected-list-item</s:if>">Availability and Pricing</a>
                        </s:if>
                        <s:else>
                            <a>Features</a>|
                            <a>Description</a>|
                            <a>Photos</a>|
                            <a>Availability and Pricing</a>
                        </s:else>
                    </div>
                    </div>
                    </div>
            <div class="medium-height app-canvas-content">
                <div class="text">
                    <div class="wait"><img src="<%=request.getContextPath()%>/images/wait.gif"></img></div>
