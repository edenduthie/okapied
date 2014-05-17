package okapied.action.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okapied.entity.Property;
import okapied.exception.InvalidInputException;
import okapied.exception.OkaSecurityException;
import okapied.web.LoginStatus;

public class Photos extends ListBaseAction implements PageAction
{
	static String REMOVE = "remove";
	static String REMOVE_ALL = "remove_all";
	
	Integer photoId;
	
    private List<File> uploads = new ArrayList<File>();
    private List<String> uploadFileNames = new ArrayList<String>();
    private List<String> uploadContentTypes = new ArrayList<String>();
	
    public int getPage()
    {
    	return 4;
    }
    
    public String listsubmit()
    {
		property = new Property();
		property.setId(propertyId);
		try {
			property = propertyService.savePhotos(property,uploads,uploadFileNames,uploadContentTypes,
			    LoginStatus.getUser().getId());
			setPropertyId(property.getId());
			message = "Changes Saved";
		} 
		catch (OkaSecurityException e) 
		{
			message = e.getMessage();
			return ERROR;
		} catch (IOException e) {
			message = "Error uploading file(s)";
			return ERROR;
		} catch (InvalidInputException e) {
			message = e.getMessage();
			return ERROR;
		}
		finally
		{
		    try {
				loadProperty();
			} catch (OkaSecurityException e) {
				log.error(e.getMessage());
			}
		}
    	return LIST_SUBMIT;
    }
    
    public String remove()
    {
    	try
    	{
    		propertyService.removePhoto(propertyId, photoId, LoginStatus.getUser().getId());
    	}
    	catch (OkaSecurityException e) 
		{
			message = e.getMessage();
			return ERROR;
		}
		finally
		{
		    try {
				loadProperty();
			} catch (OkaSecurityException e) {
				log.error(e.getMessage());
			}
		}
    	return REMOVE;
    }
    
    public String removeall()
    {
    	try
    	{
    		propertyService.removeAllPhotos(propertyId, LoginStatus.getUser().getId());
    	}
    	catch (OkaSecurityException e) 
		{
			message = e.getMessage();
			return ERROR;
		}
		finally
		{
		    try {
				loadProperty();
			} catch (OkaSecurityException e) {
				log.error(e.getMessage());
			}
		}
		message = "All Images Deleted";
    	return REMOVE;
    }
    
    public List<File> getUpload() {
        return this.uploads;
    }
    public void setUpload(List<File> uploads) {
        this.uploads = uploads;
    }
    public List<String> getUploadFileName() {
        return this.uploadFileNames;
    }
    public void setUploadFileName(List<String> uploadFileNames) {
        this.uploadFileNames = uploadFileNames;
    }
    public List<String> getUploadContentType() {
        return this.uploadContentTypes;
    }
    public void setUploadContentType(List<String> contentTypes) {
        this.uploadContentTypes = contentTypes;
    }

	public Integer getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Integer photoId) {
		this.photoId = photoId;
	}
	
	public String getTitle()
	{
		return "Photos";
	}
	
	public String getDescription()
	{
		return "Add or remove photos which are displayed on your property listing";
	}
}
