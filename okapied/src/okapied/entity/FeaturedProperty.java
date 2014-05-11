package okapied.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class FeaturedProperty 
{
	@Id @GeneratedValue
	private Integer 		id;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	private Property property;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	private Photo photo;

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

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
}
