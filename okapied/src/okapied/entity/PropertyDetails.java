package okapied.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CollectionOfElements;

@Entity
public class PropertyDetails 
{
	@Id @GeneratedValue
	private Integer 		id;
	
	@ManyToOne
    PropertyType type;
    Integer sleeps;
    Integer bedrooms;
    
    Integer kingBeds;
    Integer queenBeds;
    Integer doubleBeds;
    Integer singleBeds;
	
	Integer cleanliness;
	Integer accuracy;
	Integer rating;
	Integer valueForMoney;
    
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean dishwasher;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean microwave;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean oven;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean fridge;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean doubleSink;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean barFridge;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean potsAndPans;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean tableware;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean blender;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean toaster;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean kettle;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean coffeeMaker;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean juicer;
    
    @CollectionOfElements(fetch=FetchType.EAGER)
    Set<String> kitchen;
    
    Integer bathrooms;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean sharedBathroom;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean bath;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean spa;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean washingMachine;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean drier;
    
    @CollectionOfElements(fetch=FetchType.EAGER)
    Set<String> bathroomLaundry;
    
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean garden;
    Integer distanceToBeachMeters = -1;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean beachfront;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean pool;
    
    @CollectionOfElements(fetch=FetchType.EAGER)
    Set<String> outdoor;
    
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean publicTransport;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean fireplace;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean aircon;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean heating;
    @ManyToOne
    Flooring flooring;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean petsAllowed;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean hypo;
    
    @CollectionOfElements(fetch=FetchType.EAGER)
    Set<String> ammenities;
    
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean internetAccess;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean wirelessInternet;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean cableTv;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean localPhone;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean internationalPhone;
//    @Type(type = "org.hibernate.type.YesNoType")
//    boolean stereo;
    
    @CollectionOfElements(fetch=FetchType.EAGER)
    Set<String> multimedia;
  
    Double lat;
    Double lon;
    
    String unit;
    String street;
    String streetNumber;
    String postcode;
    
    String fullName;
    
    @Column(columnDefinition="TEXT")
    String description;
    
    @Column(columnDefinition="TEXT")
    String checkInInstructions;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSleeps() {
		return sleeps;
	}
	public void setSleeps(Integer sleeps) {
		this.sleeps = sleeps;
	}
	public Integer getBedrooms() {
		return bedrooms;
	}
	public void setBedrooms(Integer bedrooms) {
		this.bedrooms = bedrooms;
	}
	public Integer getKingBeds() {
		return kingBeds;
	}
	public void setKingBeds(Integer kingBeds) {
		this.kingBeds = kingBeds;
	}
	public Integer getQueenBeds() {
		return queenBeds;
	}
	public void setQueenBeds(Integer queenBeds) {
		this.queenBeds = queenBeds;
	}
	public Integer getDoubleBeds() {
		return doubleBeds;
	}
	public void setDoubleBeds(Integer doubleBeds) {
		this.doubleBeds = doubleBeds;
	}
	public Integer getSingleBeds() {
		return singleBeds;
	}
	public void setSingleBeds(Integer singleBeds) {
		this.singleBeds = singleBeds;
	}
	public Set<String> getKitchen() {
		return kitchen;
	}
	public void setKitchen(Set<String> kitchen) {
		this.kitchen = kitchen;
	}
	public Set<String> getBathroomLaundry() {
		return bathroomLaundry;
	}
	public void setBathroomLaundry(Set<String> bathroomLaundry) {
		this.bathroomLaundry = bathroomLaundry;
	}
	public Set<String> getOutdoor() {
		return outdoor;
	}
	public void setOutdoor(Set<String> outdoor) {
		this.outdoor = outdoor;
	}
	public Set<String> getAmmenities() {
		return ammenities;
	}
	public void setAmmenities(Set<String> ammenities) {
		this.ammenities = ammenities;
	}
	public Set<String> getMultimedia() {
		return multimedia;
	}
	public void setMultimedia(Set<String> multimedia) {
		this.multimedia = multimedia;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Integer getDistanceToBeachMeters() {
		return distanceToBeachMeters;
	}
	public void setDistanceToBeachMeters(Integer distanceToBeachMeters) {
		this.distanceToBeachMeters = distanceToBeachMeters;
	}
	public Integer getBathrooms() {
		return bathrooms;
	}
	public void setBathrooms(Integer bathrooms) {
		this.bathrooms = bathrooms;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public PropertyType getType() {
		return type;
	}
	public void setType(PropertyType type) {
		this.type = type;
	}
	public Flooring getFlooring() {
		return flooring;
	}
	public void setFlooring(Flooring flooring) {
		this.flooring = flooring;
	}
	public Integer getCleanliness() {
		return cleanliness;
	}
	public void setCleanliness(Integer cleanliness) {
		this.cleanliness = cleanliness;
	}
	public Integer getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(Integer accuracy) {
		this.accuracy = accuracy;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public Integer getValueForMoney() {
		return valueForMoney;
	}
	public void setValueForMoney(Integer valueForMoney) {
		this.valueForMoney = valueForMoney;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getCheckInInstructions() {
		return checkInInstructions;
	}
	public void setCheckInInstructions(String checkInInstructions) {
		this.checkInInstructions = checkInInstructions;
	}
    
}
