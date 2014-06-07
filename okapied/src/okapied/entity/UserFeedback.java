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
public class UserFeedback 
{	
	@Id @GeneratedValue
	private Integer 		id;
	
	@ManyToOne(optional=false,fetch=FetchType.LAZY)
	private OkapiedUserDetails user;
	
	@ManyToOne(optional=false,fetch=FetchType.LAZY)
	private Booking booking;
	
	@Transient
	public Boolean positive;
	
	public Integer positiveInt;
	
	@Column(columnDefinition="TEXT")
    public String text;
	
	Date dateLeft;
	
	Integer overall;
	Integer conditionOnExit;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public OkapiedUserDetails getUser() {
		return user;
	}

	public void setUser(OkapiedUserDetails user) {
		this.user = user;
	}

	public Integer getOverall() {
		return overall;
	}

	public void setOverall(Integer overall) {
		this.overall = overall;
	}

	public Integer getConditionOnExit() {
		return conditionOnExit;
	}

	public void setConditionOnExit(Integer conditionOnExit) {
		this.conditionOnExit = conditionOnExit;
	}
	
	public Integer overallPercentage()
	{
		return toPercentage(overall);
	}
	
	public Integer conditionOnExitPercentage()
	{
		return toPercentage(conditionOnExit);
	}
	
	public Integer toPercentage(int value)
	{
		int largestScore = Configuration.instance().getIntProperty("LARGEST_FEEDBACK_SCORE");
		return Math.round(((new Float(value))/(new Float(largestScore)))*100);
	}
}
