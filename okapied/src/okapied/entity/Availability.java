package okapied.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Availability implements Matchable
{	
	@Id @GeneratedValue
	private Integer 		id;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	private Property property;
	
	Date startDate;
	
	Date endDate;
	
	Integer available;
	
	Integer precedenceNum = PRECEDENCE;

	public static int PRECEDENCE_DAY_ONLY = 2;
	public static int PRECEDENCE_SET_PERIOD = 4;
	public static int PRECEDENCE = 10;
	
	public boolean match( Calendar day )
	{
		if( day.getTimeInMillis() >= startDate.getTime() &&
			day.getTimeInMillis() < endDate.getTime() )
		{
			return true;
		}
		else
		{
			return false;
		}
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getAvailable() {
		return available;
	}

	public void setAvailable(Integer available) {
		this.available = available;
	}
	
	public boolean getDayOnlyBool()
	{
		if( precedenceNum == PRECEDENCE_DAY_ONLY ) return true;
		else return false;
	}
	
	public void setDayOnlyBool(boolean dayOnlyBool)
	{
		if( dayOnlyBool ) precedenceNum = PRECEDENCE_DAY_ONLY;
		else precedenceNum = PRECEDENCE;
	}
	
	public Integer precedence()
	{
		if( getDayOnlyBool() ) return PRECEDENCE_DAY_ONLY;
		else return precedenceNum;
	}
	
	public boolean getAvailableBool()
	{
		if( available == null ) return false;
		if( available == 1 ) return true;
		else return false;
	}
	
	public void setAvailableBool(boolean availableBool)
	{
		if( availableBool ) available = 1;
		else available = 0;
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
