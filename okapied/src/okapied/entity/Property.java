package okapied.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import okapied.util.Configuration;
import okapied.util.MathUtil;


@Entity
public class Property implements Place
{
	@Transient
	List<PropertyDay> infoList;
	
	@Id @GeneratedValue
	private Integer 		id;
	
	@ManyToOne(optional=false)
	private Location location;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	private OkapiedUserDetails owner;
	
	public String name;
	
	public Integer positiveFeedback = 0;
	
	public Integer negativeFeedback = 0;
	
	public Date dateListed;
	
	@ManyToOne(optional=false)
	public CurrencyCode currencyCode;
	
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.ALL},mappedBy="property")
	public List<Photo> photos;
	
	@OneToOne(optional=true,fetch=FetchType.LAZY,cascade={CascadeType.ALL})
	public PropertyDetails propertyDetails;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY,cascade={CascadeType.ALL})
	Availability defaultAvailability;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY,cascade={CascadeType.ALL})
	Price defaultPrice;
	
	Float rating = 0f;
	
	Float accuracy = 0f;
	
	Float cleanliness = 0f;
	
	Float valueForMoney = 0f;
	
	Integer minNights = 1;
	
	String refundPolicy = REFUND_POLICY_STANDARD;
	
	public static String REFUND_POLICY_STRICT = "s";
	public static String REFUND_POLICY_STANDARD = "d";
	public static String REFUND_POLICY_FLEXIBLE = "f";
	
	public Property() 
	{
	}
	
	public Property(Integer id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public Property(Integer id, String name, 
			Integer locationId, String locationName,
			Integer regionId, String regionName, 
			Integer countryId, String countryName)
	{
		this.id = id;
		this.name = name;
		this.location = new Location(locationId,locationName,
		    countryId, countryName,
		    regionId, regionName);
	}
	
	public Property(Integer id, String name, 
			Integer locationId, String locationName,
			Integer countryId, String countryName)
	{
		this.id = id;
		this.name = name;
		this.location = new Location(locationId,locationName,
		    countryId, countryName);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setInfoList(List<PropertyDay> infoList )
	{
		this.infoList = infoList;
	}
	
	public List<PropertyDay> getInfoList()
	{
		return this.infoList;
	}

	public String retrieveTz() {
		return location.getTz();
	}

	public void updateTz(String tz) {
	    location.setTz(tz);
	}

	public Integer getPositiveFeedback() {
		return positiveFeedback;
	}

	public void setPositiveFeedback(Integer positiveFeedback) {
		this.positiveFeedback = positiveFeedback;
	}

	public Integer getNegativeFeedback() {
		return negativeFeedback;
	}

	public void setNegativeFeedback(Integer negativeFeedback) {
		this.negativeFeedback = negativeFeedback;
	}
	
	public Integer getPercentagePositive()
	{
		if( positiveFeedback == null ) positiveFeedback = 0;
		if( negativeFeedback == null ) negativeFeedback = 0;
		float total = positiveFeedback + negativeFeedback;
		if( total == 0f ) return 0;
		float result =  100*((float) positiveFeedback) / total;
		return Math.round(result);
	}

	public PropertyDetails getPropertyDetails() {
		return propertyDetails;
	}

	public void setPropertyDetails(PropertyDetails propertyDetails) {
		this.propertyDetails = propertyDetails;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	public OkapiedUserDetails getOwner() {
		return owner;
	}

	public void setOwner(OkapiedUserDetails owner) {
		this.owner = owner;
	}

	public CurrencyCode getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(CurrencyCode currencyCode) {
		this.currencyCode = currencyCode;
	}
	
    public Calendar retrieveCurrentDay()
    {
    	TimeZone tz = TimeZone.getTimeZone(retrieveTz());
    	Calendar instance = new GregorianCalendar(tz);
    	instance.set(Calendar.HOUR_OF_DAY, 12);
    	instance.set(Calendar.MINUTE, 0);
    	instance.set(Calendar.SECOND, 0);
    	instance.set(Calendar.MILLISECOND, 0);
    	return instance;
    }
    
    public Calendar retrieveCurrentTime()
    {
    	TimeZone tz = TimeZone.getTimeZone(retrieveTz());
    	Calendar instance = new GregorianCalendar(tz);
    	return instance;
    }

	public Date getDateListed() {
		return dateListed;
	}

	public void setDateListed(Date dateListed) {
		this.dateListed = dateListed;
	}

	public Availability getDefaultAvailability() {
		return defaultAvailability;
	}

	public void setDefaultAvailability(Availability defaultAvailability) {
		this.defaultAvailability = defaultAvailability;
	}

	public Price getDefaultPrice() {
		return defaultPrice;
	}

	public void setDefaultPrice(Price defaultPrice) {
		this.defaultPrice = defaultPrice;
	}
	
	public void incrementPositiveFeedback()
	{
		if( positiveFeedback == null ) positiveFeedback = 0;
		++positiveFeedback;
	}
	
	public void incrementNegativeFeedback()
	{
		if( negativeFeedback == null ) negativeFeedback = 0;
		++negativeFeedback;
	}

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}

	public Float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Float accuracy) {
		this.accuracy = accuracy;
	}

	public Float getCleanliness() {
		return cleanliness;
	}

	public void setCleanliness(Float cleanliness) {
		this.cleanliness = cleanliness;
	}

	public Float getValueForMoney() {
		return valueForMoney;
	}

	public void setValueForMoney(Float valueForMoney) {
		this.valueForMoney = valueForMoney;
	}
	
	public int getN() 
	{
		if( positiveFeedback == null ) positiveFeedback = 0;
		if( negativeFeedback == null ) negativeFeedback = 0;
		return positiveFeedback + negativeFeedback;
	}

	public void updateCleanliness(Integer cleanliness) 
	{
		if( this.cleanliness == null ) this.cleanliness = 0f;
		this.cleanliness = MathUtil.rollingAverage(getN(),this.cleanliness,cleanliness);
	}
	
	public void updateAccuracy(Integer accuracy) 
	{
		if( this.accuracy == null ) this.accuracy = 0f;
		this.accuracy = MathUtil.rollingAverage(getN(),this.accuracy,accuracy);
	}
	
	public void updateRating(Integer rating) 
	{
		if( this.rating == null ) this.rating = 0f;
		this.rating = MathUtil.rollingAverage(getN(),this.rating,rating);
	}
	
	public void updateValueForMoney(Integer valueForMoney) 
	{
		if( this.valueForMoney == null ) this.valueForMoney = 0f;
		this.valueForMoney = MathUtil.rollingAverage(getN(),this.valueForMoney,valueForMoney);
	}
	
	public Integer cleanlinessPercentage()
	{
		return toPercentage(cleanliness);
	}
	
	public Integer accuracyPercentage()
	{
		return toPercentage(accuracy);
	}
	
	public Integer valuePercentage()
	{
		return toPercentage(valueForMoney);
	}
	
	public Integer ratingPercentage()
	{
		return toPercentage(rating);
	}
	
	public Integer toPercentage(float value)
	{
		int largestScore = Configuration.instance().getIntProperty("LARGEST_FEEDBACK_SCORE");
		return Math.round((value/largestScore)*100);
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public Integer getNumProperties() {
		return 1;
	}

	@Override
	public Country getCountry() {
		if( location != null )
		    return location.getCountry();
		else return null;
	}

	@Override
	public Region getRegion() {
		if( location != null )
		    return location.getRegion();
		else return null;
	}

	@Override
	public String getT() {
		return Place.PROPERTY;
	}

	public String getRefundPolicy() {
		return refundPolicy;
	}

	public void setRefundPolicy(String refundPolicy) {
		this.refundPolicy = refundPolicy;
	}
	
	public String retrieveRefundPolicyText()
	{
		return retrieveRefundPolicyText(getRefundPolicy());
	}
	
	public String retrieveRefundPolicyText(String policy)
	{
		if( policy.equals(REFUND_POLICY_FLEXIBLE) ) return "Flexible";
		if( policy.equals(REFUND_POLICY_STANDARD) ) return "Standard";
		if( policy.equals(REFUND_POLICY_STRICT) ) return "Strict";
		else return "Standard";
	}

	public String retrieveAddressText() {
		String address = "";
		if( getPropertyDetails().getUnit() != null && getPropertyDetails().getUnit().trim().length() > 0 ) address += getPropertyDetails().getUnit() + "/";
		address += getPropertyDetails().getStreetNumber() + " " +
		    getPropertyDetails().getStreet() + ", " +
		    getLocation().getName() + ", " +
		    getLocation().getRegion().getName() + ", " +
		    getLocation().getCountry().getName() + " " + getPropertyDetails().getPostcode();
		return address;
	}
	
	public boolean retrieveFrozen()
	{
		return getOwner().getFrozenBool();
	}

	public Integer getMinNights() {
		return minNights;
	}

	public void setMinNights(Integer minNights) {
		this.minNights = minNights;
	}
}
