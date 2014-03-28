showOverflow = true;

function Photos(galleryId,contextPath)
{
	this.gallery = null;
	this.galleryId = galleryId;
	this.contextPath = contextPath;
};

Photos.prototype.render = function()
{
	var that = this;
	//Galleria.loadTheme(this.contextPath + '/galleria/src/themes/classic/galleria.classic.js');
	var options = {
		    width: 650,
		    height: 500
	};
	this.gallery = new Galleria();
	this.gallery.init( $('#'+that.galleryId), options );
};