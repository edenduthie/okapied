<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>

<div class="content">
<div id="front-top">
<div id="front-top-top">
    <div class="top-right"></div>
    <div class="top-left"></div>
    <div class="top-center"></div>
</div>
<div id="front-top-content">
<div class="front-top-text">
<div id="front-top-left">
<div id="logo"></div>
<h2>the renting revolution</h2>
<div id="featured-property">
<div id="featured-property-top">
    <div class="green-top-right"></div>
    <div class="green-top-left"></div>
    <div class="green-top-center"></div>
</div>
<div id="featured-property-content">
<s:if test="featuredProperty != null">
<a href="<%=request.getContextPath()%>/<s:property value="featuredProperty.property.location.country.name" />/<s:property value="featuredProperty.property.location.region.name" />/<s:property value="featuredProperty.property.location.name" />/<s:property value="featuredProperty.property.name" />">
<img id="featured-property-image" alt="Featured Holiday Rental" title="Featured Holiday Rental"
    src='<%=request.getContextPath()%>/File/Photo_list.action?photoId=<s:property value="featuredProperty.photo.getId()" />'
></img></a>
</s:if>
<s:else>
<a href="<%=request.getContextPath()%>/<s:property value="featuredProperty.property.location.country.name" />/<s:property value="featuredProperty.property.location.region.name" />/<s:property value="featuredProperty.property.location.name" />/<s:property value="featuredProperty.property.name" />">
<img id="featured-property-image" alt="Featured Holiday Rental" title="Featured Holiday Rental" 
    src="<%=request.getContextPath()%>/images/default_property_image.jpg"></img>
</a>
</s:else>
<div id="featured-property-text">
<h1>Featured Holiday Rental</h1>
<h2><a href="<%=request.getContextPath()%>/<s:property value="featuredProperty.property.location.country.name" />/<s:property value="featuredProperty.property.location.region.name" />/<s:property value="featuredProperty.property.location.name" />/<s:property value="featuredProperty.property.name" />"><s:property value="featuredProperty.property.name" /></a></h2>
</div>
</div>
<div id="featured-property-bottom">
    <div class="green-bottom-right"></div>
    <div class="green-bottom-left"></div>
    <div class="green-bottom-center"></div>
</div>
</div>
<div id="rest"> 
<!--
<iframe src="https://www.facebook.com/plugins/like.php?href=https%3A%2F%2Fwww.okapied.com&amp;layout=standard&amp;show_faces=false&amp;width=320&amp;action=like&amp;font=tahoma&amp;colorscheme=light&amp;height=35" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:320px; height:35px;" allowTransparency="true"></iframe>
-->
<a Target="_blank" href="http://www.facebook.com/pages/okapiedcom/114769605279484" title="Okapied on Facebook"><img title="Okapied on Facebook" alt="Okapied on Facebook" src="<%=request.getContextPath()%>/images/facebook.png"></img></a>
<a href="javascript:twitterPop('https://www.okapied.com')"><img alt="Tweet" title="Tweet" src="<%=request.getContextPath()%>/images/tweet.png" /></a>
<a href="http://www.delicious.com/save" onclick="window.open('http://www.delicious.com/save?v=5&noui&jump=close&url='+encodeURIComponent(location.href)+'&title='+encodeURIComponent(document.title), 'delicious','toolbar=no,width=550,height=550'); return false;"><img alt="Bookmark on del.icio.us" title="Bookmark on del.icio.us" src="<%=request.getContextPath()%>/images/delicious.png"></img></a>
<a href="http://www.reddit.com/submit" onclick="window.open('http://www.reddit.com/submit?url=' + encodeURIComponent(location.href), 'reddit','toolbar=no,width=700,height=550'); return false"><img src="<%=request.getContextPath()%>/images/reddit.png" title="Submit to reddit"></a>
<a Target="_blank" href="http://digg.com/submit?url=https://www.okapied.com&amp;title=Okapied the Rental Revolution" title="Post this story to Digg" id="digg"><img src="<%=request.getContextPath()%>/images/digg.png" title="Post this story to Digg"></a>
<div id="list-your-property-now"><a href="<%=request.getContextPath()%>/App/Publish/list"><img src="<%=request.getContextPath()%>/images/list_your_property_now_button.png"></img></a></div>
</div>
</div>
<div class="images">
    <s:iterator value="photoGallery.photos">
        <img src='<%=request.getContextPath()%>/File/Photo_list.action?photoId=<s:property value="id" />'></img>
    </s:iterator>
</div>
</div>
</div>
<div id="front-top-bottom">
    <div class="bottom-right"></div>
    <div class="bottom-left"></div>
    <div class="bottom-center"></div>
</div>
</div><!-- front-top -->

<div id="front-middle">
<div id="front-middle-top">
    <div class="black-top-right"></div>
    <div class="black-top-left"></div>
    <div class="black-top-center"></div>
</div>
<div id="black-long-content">
<div id="black-long-content-text">
    <h3>Latest Listings</h3>
</div>
</div>
<div id="latest-listings-content">
<div id="latest-listings-left" class="latest-listings-column">
<s:if test="latestListings.size() > 0">
<a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(0).location.country.name" />/<s:property value="latestListings.get(0).location.region.name" />/<s:property value="latestListings.get(0).location.name" />/<s:property value="latestListings.get(0).name" />">
<img id="latest-listings-image" alt="<s:property value="latestListings.get(0).name" />" title="<s:property value="latestListings.get(0).name" />"
    src='<%=request.getContextPath()%>/File/Photo_list.action?photoId=<s:property value="latestListings.get(0).photos.get(0).getId()" />'
></img>
</a>
<h3><a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(0).location.country.name" />/<s:property value="latestListings.get(0).location.region.name" />/<s:property value="latestListings.get(0).location.name" />/<s:property value="latestListings.get(0).name" />"><s:property value="latestListings.get(0).name" /></a></h3>
<h4><a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(0).location.country.name" />"><s:property value="latestListings.get(0).location.country.name" /></a><s:if test="latestListings.get(0).location.region != null"> > <a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(0).location.country.name" />/<s:property value="latestListings.get(0).location.region.name" />"><s:property value="latestListings.get(0).location.region.name" /></a> </s:if> > <a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(0).location.country.name" />/<s:property value="latestListings.get(0).location.region.name" />/<s:property value="latestListings.get(0).location.name" />"><s:property value="latestListings.get(0).location.name" /></a></h4>
</s:if>
<s:else>
<a alt="List your place" title="List your place" href="<%=request.getContextPath()%>/App/Publish/list">
<img alt="List your place" title="List your place" src="<%=request.getContextPath()%>/images/your-place-here.png" />
</a>
</s:else>
</div>
<div id="latest-listtings-center" class="latest-listings-column">
<s:if test="latestListings.size() > 1">
<a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(1).location.country.name" />/<s:property value="latestListings.get(1).location.region.name" />/<s:property value="latestListings.get(1).location.name" />/<s:property value="latestListings.get(1).name" />">
<img id="latest-listings-image" alt="<s:property value="latestListings.get(1).name" />" title="<s:property value="latestListings.get(1).name" />"
    src='<%=request.getContextPath()%>/File/Photo_list.action?photoId=<s:property value="latestListings.get(1).photos.get(0).getId()" />'
></img>
</a>
<h3><a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(1).location.country.name" />/<s:property value="latestListings.get(1).location.region.name" />/<s:property value="latestListings.get(1).location.name" />/<s:property value="latestListings.get(1).name" />"><s:property value="latestListings.get(1).name" /></a></h3>
<h4><a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(1).location.country.name" />"><s:property value="latestListings.get(1).location.country.name" /></a><s:if test="latestListings.get(1).location.region != null"> > <a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(1).location.country.name" />/<s:property value="latestListings.get(1).location.region.name" />"><s:property value="latestListings.get(1).location.region.name" /></a> </s:if> > <a href="<%=request.getContextPath()%>/<s:property value="latestListings.get(1).location.country.name" />/<s:property value="latestListings.get(1).location.region.name" />/<s:property value="latestListings.get(1).location.name" />"><s:property value="latestListings.get(1).location.name" /></a></h4>
</s:if>
<s:else>
<a alt="List your place" title="List your place" href="<%=request.getContextPath()%>/App/Publish/list">
<img alt="List your place" title="List your place" src="<%=request.getContextPath()%>/images/your-place-here.png" />
</a>
</s:else>
</div>
<div id="latest-listings-right" class="latest-listings-column">
<a alt="List your place" title="List your place" href="<%=request.getContextPath()%>/App/Publish/list">
<img alt="List your place" title="List your place" src="<%=request.getContextPath()%>/images/your-place-here.png" />
</a>
</div>
</div>
<div id="front-middle-bottom">
    <div class="grey-bottom-right"></div>
    <div class="grey-bottom-left"></div>
    <div class="grey-bottom-center"></div>
</div>
</div><!-- front-middle-->

<div id="front-bottom">
<div id="twitter-feed" class="left-column">
<script src="<%=request.getContextPath()%>/js/widget.js"></script>
<script>
new TWTR.Widget({
	  version: 2,
	  type: 'profile',
	  rpp: 4,
	  interval: 6000,
	  width: 'auto',
	  height: 400,
	  theme: {
	    shell: {
	      background: '#333333',
	      color: '#ffffff'
	    },
	    tweets: {
	      background: '#000000',
	      color: '#ffffff',
	      links: '#4aed05'
	    }
	  },
	  features: {
	    scrollbar: false,
	    loop: false,
	    live: false,
	    hashtags: true,
	    timestamp: true,
	    avatars: false,
	    behavior: 'all'
	  }
	}).render().setUser('okapied').start();
</script>
</div>
<div class="right-column">
<div id="how-it-works">
    <div id="how-it-works-top">
        <div class="top-right"></div>
        <div class="top-left"></div>
        <div class="top-center"></div>
    </div>
    <div class="how-it-works-heading">
        <div id="how-it-works-heading-text"><h2>How it Works</h2></div>
    </div>
    <div id="how-it-works-content">
        <div id="how-it-works-text">
        <div class="grey-background">
            <div class="left-column-right-align">list</div>
            <div class="description-text">
            Sign up and list your place for free. 
            Prices and availability are fully customisable giving you full control over your property. 
            You don’t have to micromanage bookings so no more endless email negotiations with renters. 
            Set up access and cleaning and watch the cash flow! 
            Overheads are minimised, the only fee is a 7% Okapied fee.
            You need a PayPal account to receive rent.
            </div>
        </div>
        <div class="white-background">
            <div class="left-column-right-align">book</div>
            <div class="description-text">
            Instantly book a place to stay. 
            You don’t have to send out a lot of enquiries and wait for responses. 
            Just search for a place, prices and availability are always up to date, 
            and instantly book using PayPal or a credit card. 
            Once you receive a booking confirmation, containing check in instructions and the precise address, 
            you can relax and know that your stay is confirmed.
            </div>
        </div>
        <div class="grey-background">
            <div class="left-column-right-align-shorter">stay</div>
            <div class="description-text-shorter">
            Follow the check in instructions and enjoy your stay! 
            The full payment is paid to the owner’s PayPal account the day after check in. 
            After your stay you get a chance to provide feedback.
            The owner can also leave feedback about you!
            </div>
        </div>
        </div>
    </div>
    <div id="how-it-works-bottom">
        <div class="bottom-right"></div>
        <div class="bottom-left"></div>
        <div class="bottom-center"></div>
    </div>
</div><!--how-it-works-->
</div>
</div>

</div><!--content-->
<script>Galleria.loadTheme('<%=request.getContextPath()%>/galleria/src/themes/classic/galleria.classic.js');</script>
<script>
$(document).ready(function() {
	
	Galleria.loadTheme('<%=request.getContextPath()%>/galleria/src/themes/classic/galleria.classic.js');
	// picture galery
	var options = {
	    width: 650,
	    height: 500,
	    autoplay: 7000,
	    thumbnails: true
    };
	var gallery = new Galleria();
    gallery.init( $('.images'), options );
});
</script>
<script type="text/javascript">
function twitterPop(str) {
    mywindow = window.open('http://twitter.com/share?url='+str,"Tweet_widow","channelmode=no,directories=no,location=no,menubar=no,scrollbars=no,toolbar=no,status=no,width=500,height=375,left=300,top=200");
    mywindow.focus();
}
</script>
</body>
</html>