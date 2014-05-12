package okapied.entity;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AvailabilityWeekDay implements Matchable 
{
	@Id @GeneratedValue
	private Integer 		id;
	
	@ManyToOne(optional=false)
	private Property property;
	
	Integer available;
	
	Integer dayOfWeek;
	
	public static int PRECEDENCE = 3;
	
	Integer precedenceNum = PRECEDENCE;
	
	public AvailabilityWeekDay() {}
	
	public AvailabilityWeekDay(Integer dayOfWeek, boolean available)
	{
		this.dayOfWeek = dayOfWeek;
		setAvailableBool(available);
	}
	
	public boolean getAvailableBool()
	{
		if( available == 1 ) return true;
		else return false;
	}
	
	public void setAvailableBool(boolean availableBool)
	{
		if( availableBool ) available = 1;
		else available = 0;
	}
	
	public boolean match( Calendar day )
	{
		if( day.get(Calendar.DAY_OF_WEEK) == dayOfWeek ) return true;
		else return false;
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

	public Integer getAvailable() {
		return available;
	}

	public void setAvailable(Integer available) {
		this.available = available;
	}

	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	public Integer precedence()
	{
		return PRECEDENCE;
	}

	@Override
	public boolean getMatchedReservedBool(Calendar day) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer getBookingId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getPrecedenceNum() {
		return precedenceNum;
	}

	public void setPrecedenceNum(Integer precedenceNum) {
		this.precedenceNum = precedenceNum;
	}
}
