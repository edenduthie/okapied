function Details()
{
	this.kitchens = '';
	this.outdoor = '';
	this.bathroom = '';
	this.am = '';
	this.multi = '';
};

Details.prototype.render = function()
{
    this.prepareCheckboxes();	
}

Details.prototype.prepareCheckboxes = function()
{
	that = this;
	$('form').submit(function(){
		$(":checkbox:checked").each(function(){
			that.checkboxChecked($(this).attr('name'));
		});
	});
};

Details.prototype.checkboxChecked = function(nameAndType)
{
	var splitString = nameAndType.split("_");
	if( splitString.length == 2 )
    {
		var name = splitString[0];
		var type = splitString[1];
		if( type == 2 )
		{
		    if( this.kitchens.length > 0 ) this.kitchens += ",";
		    this.kitchens += name;
		}
		else if( type == 3 )
		{
		    if( this.outdoor.length > 0 ) this.outdoor += ",";
		    this.outdoor += name;
		}
		else if( type == 4 )
		{
		    if( this.bathroom.length > 0 ) this.bathroom += ",";
		    this.bathroom += name;
		}
		else if( type == 5 )
		{
		    if( this.am.length > 0 ) this.am += ",";
		    this.am += name;
		}
		else if( type == 6 )
		{
		    if( this.multi.length > 0 ) this.multi += ",";
		    this.multi += name;
		}
		$('#kitchenOptions').val(this.kitchens);
		$('#outdoorOptions').val(this.outdoor);
		$('#bathroomOptions').val(this.bathroom);
		$('#amOptions').val(this.am);
		$('#multiOptions').val(this.multi);
    }
};