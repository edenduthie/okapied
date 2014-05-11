package okapied.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import okapied.util.Configuration;

@Entity
public class Feedback 
{	
	static int CUTOFF_TEXT_CHARS = 201;
	
	@Id @GeneratedValue
	private Integer 		id;
	
	@ManyToOne(optional=true,fetch=FetchType.LAZY)
	private Property property;
	
	@ManyToOne(optional=false,fetch=FetchType.LAZY)
	private Booking booking;
	
	@Transient
	public Boolean positive;
	
	public Integer positiveInt;
	
	@Column(columnDefinition="TEXT")
    public String text;
	
	Date dateLeft;
	
	Integer rating;
	
	Integer accuracy;
	
	Integer cleanliness;
	
	Integer valueForMoney;
	
	public Feedback() {}
	
	public Feedback(Integer id,Integer positiveInt, String text, Date dateLeft, Integer rating,
        Integer accuracy, Integer cleanliness, Integer valueForMoney)
	{
		this.id = id;
		setPositiveInt(positiveInt);
		this.text = text;
		this.dateLeft = dateLeft;
		this.rating = rating;
		this.accuracy = accuracy;
		this.cleanliness = cleanliness;
		this.valueForMoney = valueForMoney;
	}

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

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDateLeft() {
		return dateLeft;
	}

	public void setDateLeft(Date dateLeft) {
		this.dateLeft = dateLeft;
	}

	public Boolean getPositive() 
	{
		if( positiveInt != null ) positive =  positiveInt == 1 ? true : false;
		else positive = null;
		return positive;
	}

	public void setPositive(Boolean positive) {
		this.positive = positive;
		if( positive != null )
		{
			if( positive == true ) positiveInt = 1;
			else positiveInt = 0;
		}
		else positiveInt = null;
	}

	public Integer getPositiveInt() {
		return positiveInt;
	}

	public void setPositiveInt(Integer positiveInt) {
		this.positiveInt = positiveInt;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Integer getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Integer accuracy) {
		this.accuracy = accuracy;
	}

	public Integer getCleanliness() {
		return cleanliness;
	}

	public void setCleanliness(Integer cleanliness) {
		this.cleanliness = cleanliness;
	}

	public Integer getValueForMoney() {
		return valueForMoney;
	}

	public void setValueForMoney(Integer valueForMoney) {
		this.valueForMoney = valueForMoney;
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
	
	public Integer toPercentage(int value)
	{
		int largestScore = Configuration.instance().getIntProperty("LARGEST_FEEDBACK_SCORE");
		return Math.round(((new Float(value))/(new Float(largestScore)))*100);
	}
	
	public String retrieveTextCutoff()
	{
		if( text != null )
		{
			if( text.length() > CUTOFF_TEXT_CHARS ) return text.substring(0,CUTOFF_TEXT_CHARS-1);
			else return text;
		}
		else
		{
			return null;
		}
	}
}
