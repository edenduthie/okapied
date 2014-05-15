package okapied.action.file;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import okapied.action.BaseAction;
import okapied.entity.Photo;
import okapied.service.PhotoService;

import org.apache.log4j.Logger;

public class PhotoAction extends BaseAction
{
    private InputStream inputStream;
    private static final String LIST = "list";
    private Integer photoId;
    private PhotoService photoService;
    private Photo photo;
    
    private static final Logger log = Logger.getLogger(PhotoAction.class);
    
    public String list()
    {
    	if( photoId != null )
    	{
    	    photo = photoService.retrieve(photoId);
    	    inputStream = photo.retrieveInputStream();
    	}
    	else
    	{
    		log.error("No photo id supplied");
    	}
    	return LIST;
    }

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public Integer getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Integer photoId) {
		this.photoId = photoId;
	}

	public PhotoService getPhotoService() {
		return photoService;
	}

	public void setPhotoService(PhotoService photoService) {
		this.photoService = photoService;
	}
    
}
