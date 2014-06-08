package okapied.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class PropertyDay 
{
	@Id @GeneratedValue
	private Integer 		id;
	
	@Transient public boolean available;
    public Float price;
    @Transient public boolean weekend;
    @Transient public boolean reserved;
    @Transient public Integer bookingId = null;
    @Transient public Boolean firstDayOfBooking;
    @Transient public Boolean lastDayOfBooking;
    @Transient public Boolean lastReservedDay;
    @Transient public Boolean outOfPeriod;
    @Transient public Boolean bookingDay = false;
    @Transient public Boolean extraDay = false;
    
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public boolean isReserved() {
		return reserved;
	}
	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}
	public Integer getBookingId() {
		return bookingId;
	}
	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}
	
	public int retrievePriceRounded()
	{
		return Math.round(price);
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Boolean getFirstDayOfBooking() {
		return firstDayOfBooking;
	}
	public void setFirstDayOfBooking(Boolean firstDayOfBooking) {
		this.firstDayOfBooking = firstDayOfBooking;
	}
	public Boolean getLastDayOfBooking() {
		return lastDayOfBooking;
	}
	public void setLastDayOfBooking(Boolean lastDayOfBooking) {
		this.lastDayOfBooking = lastDayOfBooking;
	}
	public Boolean getLastReservedDay() {
		return lastReservedDay;
	}
	public void setLastReservedDay(Boolean lastReservedDay) {
		this.lastReservedDay = lastReservedDay;
	}
	public Boolean getOutOfPeriod() {
		return outOfPeriod;
	}
	public void setOutOfPeriod(Boolean outOfPeriod) {
		this.outOfPeriod = outOfPeriod;
	}
	public Boolean getBookingDay() {
		return bookingDay;
	}
	public void setBookingDay(Boolean bookingDay) {
		this.bookingDay = bookingDay;
	}
	public Boolean getExtraDay() {
		return extraDay;
	}
	public void setExtraDay(Boolean extraDay) {
		this.extraDay = extraDay;
	}
}
