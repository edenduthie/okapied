package okapied.entity;

import java.util.Calendar;

public interface PriceInterface 
{
    public Float getPrice();
    public boolean match(Calendar calendar);
    public Integer getAvailable();
    public Integer precedence();
	public boolean getAvailableBool();
    public void setAvailableBool(boolean availableBool);
    public boolean getMatchedReservedBool(Calendar day);
    public Integer getBookingId();
}
