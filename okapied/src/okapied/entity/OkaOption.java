package okapied.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class OkaOption 
{
    public static final Integer OPTION_TYPE_BEDROOM = 1;
    public static final Integer OPTION_TYPE_KITCHEN = 2;
    public static final Integer OPTION_TYPE_OUTDOOR = 3;
    public static final Integer OPTION_TYPE_BATHROOM = 4;
    public static final Integer OPTION_TYPE_AM = 5;
    public static final Integer OPTION_TYPE_MULTIMEDIA = 6;
	
	@Id @GeneratedValue
	private Integer 		id;
	
	Integer optionType;
	String name;
	
	@Transient
	Boolean isSelected;
	@Transient 
	Integer key;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOptionType() {
		return optionType;
	}
	public void setOptionType(Integer optionType) {
		this.optionType = optionType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsSelected() {
		return isSelected;
	}
	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
}
