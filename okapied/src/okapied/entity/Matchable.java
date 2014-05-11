package okapied.entity;

import java.util.Calendar;

public interface Matchable 
{
    public boolean match(Calendar calendar);
    public Integer getAvailable();
    public Integer precedence();
	public boolean getAvailableBool();
    public void setAvailableBool(boolean availableBool);
    public boolean getMatchedReservedBool(Calendar day);
    public Integer getBookingId();
}
