package okapied.entity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Photo 
{
	@Id @GeneratedValue
	private Integer 		id;
	
	@ManyToOne
	private Property property;
	
    @OneToOne(fetch=FetchType.LAZY,cascade={CascadeType.ALL})
    PhotoData data;
	
	/**
	 * jpg or png
	 */
	private String type;
	
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public byte[] getPicture() {
		return getData().getPicture();
	}

	public void setPicture(byte[] picture) {
		this.data = new PhotoData();
		this.data.setPicture(picture);
	}
	
	public InputStream retrieveInputStream()
	{
		return new ByteArrayInputStream(getPicture());
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PhotoData getData() {
		return data;
	}

	public void setData(PhotoData data) {
		this.data = data;
	}
}
