<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="okapied.web.LoginStatus" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<html>
<head>
    <!--   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></meta>  -->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <meta name="google-site-verification" content="2TxJLDvaZc7gdSn691ygdCta9zayFk3VxGoY7NUAoT8" />
    <title>
        <s:if test="title != null">
            <s:property value="title"/>
        </s:if>
        <s:else>
	        <s:if test="!mainPage">
	            <s:property value="propertyName" /> <s:property value="locationName" /> <s:property value="regionName" /> <s:property value="countryName" />
	        </s:if> Okapied Holiday Accommodation / Vacation Rentals
        </s:else>
    </title>
    <s:if test="description != null">
        <meta name="description" content="<s:property value="description"/>" />
    </s:if>
    <s:else>
        <meta name='description' content='<s:if test="countryName != null"><s:property value="propertyName" /> <s:property value="locationName" /> <s:property value="regionName" /> <s:property value="countryName" /> <s:property value="locationName" /> accommodation rental listings. </s:if>Okapied is the rental revolution, providing instant holiday accommodation / vacation rental bookings.' />
    </s:else>

	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/css/autocomplete.css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/button.css" />
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/okapied.css" />
	<link rel="icon" type="image/jpg" href="<%=request.getContextPath()%>/images/favicon.jpg">
	
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jQuery.js"/></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/okapied.js"/></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/yuiloader-min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/dom-min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/event-min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/animation-min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/datasource-min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/autocomplete-min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/element-min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/button-min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/get-min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/connection-min.js"></script>
	
	<script type="text/javascript" src="<%=request.getContextPath()%>/galleria/src/galleria.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/galleria/src/themes/classic/galleria.classic.js"></script>
	
	<meta property="og:title" content="Okapied Holiday Accommodation / Vacation Rentals" />
    <meta property="og:type" content="website" />
    <meta property="og:url" content="https://www.okapied.com" />
    <meta property="og:image" content="https://www.okapied.com/images/logo.png" />
    <meta property="og:site_name" content="Okapied" />
    <meta property="fb:admins" content="1268738681" /> 

	<script type="text/javascript">
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-24273389-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();	
	</script>
</head>

<body class="yui-skin-sam">
<div class="header-container">
<div id="header-right"></div>
<div class="header-content">
    <div id="header-right-box">
        <div id="login-top">
            <% if( LoginStatus.isLoggedIn() ) { %>
                <a href="<%=request.getContextPath()%>/Account/Account/edit"><% out.println(LoginStatus.getUser().getUsername()); %></a>
                <div class="vertical-line">&nbsp;</div>
                <a href="<%=request.getContextPath()%>/j_spring_security_logout">logout</a>
            <% } else { %>
            <form name="f" action="<%=request.getContextPath()%>/j_spring_security_check" method="post">
            <div class="top-pad">
	        username<input id='j_username' class="input-tiny" type='text' name='j_username' value='' />
	        password<input class="input-tiny" type='password' name='j_password'>
	        </div>
	        <!-- <input id="remember-me-header" type="checkbox" name="_spring_security_remember_me">&nbsp;&nbsp;Don't ask for my password for two weeks  -->
	        <input id="go-button" type="image" src="<%=request.getContextPath()%>/images/go_button.png" alt="Log In" title="Log In" />
	        <input id="login-top-redirect" type="hidden" name="spring-security-redirect" value='<s:property value="decodedURL"/>' />
	        </form>
	        <div id="create-account-top">New User: <a href="<%=request.getContextPath()%>/Account/Account/create">Sign Up!</a></div>
            <% } %>
        </div>
        <h1>Instant Booking of Holiday Accommodation</h1>
        <s:form id="search-form" action="">
        <div class="input-region"><s:textfield name="placeText" cssClass="input-long" disabled='true'/>&nbsp;</div>
        <div class="input-long-auto" id="region-empty-results-container"></div>
        <a href="javascript:void(0);" id="top-search-button"><img src="<%=request.getContextPath()%>/images/button-search.png" alt="Search" title="Search"></img></a>
        <input type="hidden" name="regionNameInsert" id="regionNameInsert" value='<s:property value="regionName" />'/>
        <input type="hidden" name="countryNameInsert" id="countryNameInsert" value='<s:property value="countryName" />'/>
        <input type="hidden" name="locationNameInsert" id="locationNameInsert" value='<s:property value="locationName" />'/>
        <input type="hidden" name="placeTypeInsert" id="placeTypeInsert" value='<s:property value="placeType" />'/>
        </s:form>
    </div>
    <div id="header-left-box">
        <a href="<%
            String thePath = request.getContextPath();
            if( thePath == null || thePath.length() <= 0 )
            {
            	out.println("/");
            }
            else
            {
            	out.println(thePath);
            }
        %>" alt="Okapied" title="Okapied">
        <div class="logo"></div>
        <div id="okapi"></div>
        </a>
    </div>
    <div class="line"></div>
    <h1 id="header-info-right">
    <a href="<%=request.getContextPath()%>/App/Publish/list"><img src="<%=request.getContextPath()%>/images/list_button.png"></img></a>
    </h1>
    <s:if test="!mainPage and countryName != null">
        <h1 id="header-info"><a href="<%=request.getContextPath()%>/<s:property value="countryName" />"><s:property value="countryName" /></a> <s:if test="regionName!=null && regionName.length() > 0">> <a href="<%=request.getContextPath()%>/<s:property value="countryName" />/<s:property value="regionName" />"><s:property value="regionName" /></a></s:if> <s:if test="locationName!=null">> <a href="<%=request.getContextPath()%>/<s:property value="countryName" />/<s:property value="regionName" />/<s:property value="locationName" />"><s:property value="locationName" /></a></s:if><s:if test="propertyName!=null"> > <a href="<%=request.getContextPath()%>/<s:property value="countryName" />/<s:property value="regionName" />/<s:property value="locationName" />/<s:property value="propertyName"/>"><s:property value="propertyName"/></a></s:if></h1>
    </s:if>
    <s:elseif test="app" >
        <h1 id="header-info">My Okapied</h1>
    </s:elseif>
</div>
<div class="clearfloat"></div>
<div id="header-bottom"></div>
</div>


<script type="text/javascript">

var goHoverOn = false;
var okapied;

$(document).ready(function() {

	$('#go-button').hover(function(){
		if( goHoverOn )
		{
		    $(this).attr('src','<%=request.getContextPath()%>/images/go_button.png');
		    goHoverOn = false;
		}
		else
		{
			$(this).attr('src','<%=request.getContextPath()%>/images/go_button_hover.png');
			goHoverOn = true;
		}
	});

	okapied = new Okapied(null,null,'<%=request.getContextPath()%>');
	okapied.place['region.name'] = "<s:property value='regionName' escape='false'/>";
	okapied.place['country.name'] = "<s:property value='countryName' escape='false'/>";
	okapied.place['location.name'] = "<s:property value='locationName' escape='false'/>";
	okapied.place['t'] = "<s:property value='placeType' escape='false'/>";
	if( okapied.place['t'] == 'P' )
	{
		okapied.place['name'] = "<s:property value='propertyName' escape='false' />";
	}
	else
	{
		okapied.place['name'] = "<s:property value='locationName' escape='false' />";
	}
    okapied.setCorrectPlaceName();
    $('.input-region div .wwctrl input').val(okapied.place['name']);
	okapied.render();
});
</script>