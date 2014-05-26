package okapied.datagenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okapied.BaseTest;
import okapied.dao.DAO;
import okapied.entity.Photo;
import okapied.entity.PhotoGallery;
import okapied.service.PhotoService;
import okapied.service.PropertyService;
import okapied.util.Configuration;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class PreloadGallery extends BaseTest 
{
    @Autowired
    DAO dao;
    
    @Autowired 
    PhotoService photoService;
    
    @Autowired 
    PropertyService propertyService;
    
	String directory = "D:\\Everything\\Pictures\\Okapied";
    
    @Test
    public void preLoad() throws FileNotFoundException, IOException
    {	
    	PhotoGallery gallery = new PhotoGallery();
    	gallery.setName(Configuration.instance().getStringProperty("GALLERY_NAME"));
    	gallery.setPhotos(new ArrayList<Photo>());
    	
    	File dir = new File(directory);
    	Iterator<File> i = FileUtils.iterateFiles(dir,null,false);
    	
    	while( i.hasNext() )
    	{
    		File file = i.next();
	    	Photo photo =  photoService.load(new FileInputStream(file));
	    	gallery.getPhotos().add(photo);
    	}
    	
    	dao.persist(gallery);
    }
}
