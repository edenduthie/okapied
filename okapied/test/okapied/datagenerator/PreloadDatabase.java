package okapied.datagenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okapied.BaseTest;
import okapied.dao.DAO;
import okapied.entity.Photo;
import okapied.entity.Property;
import okapied.service.PhotoService;
import okapied.service.PropertyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class PreloadDatabase extends BaseTest 
{
    @Autowired
    DAO dao;
    
    @Autowired 
    PhotoService photoService;
    
    @Autowired 
    PropertyService propertyService;
    
	String filename1 = "testdata/IMGP2660.JPG";
	String filename2 = "testdata/IMGP2659.JPG";
	Integer propertyId = 1;
    
    @Test
    public void preLoad() throws FileNotFoundException, IOException
    {
    	Property property = propertyService.get(propertyId);
    	
    	for( int i=0; i < 5; ++i )
    	{
	    	Photo photo =  photoService.load(new FileInputStream(new File(filename1)));
	    	photo.setProperty(property);
	    	dao.update(photo);
    	}
    	for( int i=0; i < 5; ++i )
    	{
	    	Photo photo =  photoService.load(new FileInputStream(new File(filename2)));
	    	photo.setProperty(property);
	    	dao.update(photo);
    	}
    }
}
