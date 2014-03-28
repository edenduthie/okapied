function refundPolicyPanel(target,context)
{
	var refundPolicyPanel = new YAHOO.widget.Panel(target, {
	    width: "580px", 
	    fixedcenter: true, 
	    constraintoviewport: true, 
	    underlay: "shadow", 
	    close: true, 
	    visible: true, 
	    draggable: true,
	    zIndex: 999
	});
	refundPolicyPanel.setHeader("Okapied Refund Policies");
	var body =
	    "<table>" +
	    "<tr>" +
	    "<th></th>" +
	    "<th>+30 days</th>" +
	    "<th>7-30 days</th>" +
	    "<th>2-6 days</th>" +
	    "<th><img src='"+context+"/images/question.png' title='between 12 noon the day before and 12 noon on the day' alt='between 12 noon day before and 12 noon on the day'>Day before</th>" +
	    "<th><img src='"+context+"/images/question.png' title='between 12 noon on the day and 11 am the day after' alt='between 12 noon day before and 12 noon on the day'>On the day</th>" +
	    "</tr>" + 
	    "<tr>" +
	    "<td>Flexible</td>" + 
	    "<td>100%</td>" +
	    "<td>100%</td>" +
	    "<td>100%</td>" +
	    "<td>100% - 1 day</td>" +
	    "<td>100% - 2 days</td>" +
	    "</tr>" +
	    "<tr>" +
	    "<td>Standard</td>" + 
	    "<td>80%</td>" +
	    "<td>50%</td>" +
	    "<td>25%</td>" +
	    "<td>25% - 1 day</td>" +
	    "<td>25% - 2 days</td>" +
	    "</tr>" +
	    "<tr>" +
	    "<td>Strict</td>" + 
	    "<td>0%</td>" +
	    "<td>0%</td>" +
	    "<td>0%</td>" +
	    "<td>0%</td>" +
	    "<td>0%</td>" +
	    "</tr>" +
	    "</table>" +
		"<p>All refunds are subject to the 7% Okapied fee except for full refunds.</p>" +
		"<p>No refunds are possible after 11am the day after the start of the booking.</p>" +
	    "<p>Partial refunds can only be processed automatically into a PayPal account. " +
	    "Please contact us if you wish to obtain a partial refund to your credit card, additional fees may apply.</p>" +
	    "<p>Contact us immediately if you have any problems with your booking.</p>";
	refundPolicyPanel.setBody(body);
	refundPolicyPanel.render();
};

function termsAndConditionsPanel(target)
{
	var termsAndConditionsPanel = new YAHOO.widget.Panel(target, {
	    width: "750px", 
	    fixedcenter: true, 
	    constraintoviewport: true, 
	    underlay: "shadow", 
	    close: true, 
	    visible: true, 
	    draggable: true
	});
	termsAndConditionsPanel.setHeader("Okapied Terms and Conditions");
	//termsAndConditionsPanel.setBody(body);
	$('#termsAndConditionsPanel .bd').show();
	termsAndConditionsPanel.render();
};