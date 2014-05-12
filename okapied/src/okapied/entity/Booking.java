package okapied.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import okapied.exception.OkaSecurityException;
import okapied.util.Configuration;
import okapied.util.DateUtil;

import org.apache.log4j.Logger;
import org.hibernate.annotations.CollectionOfElements;

@Entity
public class Booking implements Matchable
{	
	public static final String BOOKING_STATUS_RESERVATION = "Reservation";
	public static final String BOOKING_STATUS_PENDING_PAYMENT = "Pending Payment";
	public static final String BOOKING_STATUS_CONFIRMED = "Confirmed";
	public static final String BOOKING_STATUS_ERROR = "Error";
	public static final String BOOKING_STATUS_CANCELLED = "Cancelled";
	public static final String BOOKING_STATUS_REFUND_FULL = "FullRefund";
	public static final String BOOKING_STATUS_REFUND_PARTIAL = "PartialRefund";
	public static final String BOOKING_STATUS_REFUND_PARTIAL_OWNER_PAID = "PartialRefundComplete";
	
	@Id @GeneratedValue
	private Integer 		id;
	
	Integer available = 0;
	Date startDate;
	Date endDate;
	Integer reserved = 0;
	Date expirationDate;
	Integer people;
	Float total;
	String payKey;
	String trackingId;
	String bookingStatus;
	Integer paymentComplete = 0;
	Date bookingDate;
	String refundPolicy;
	Integer confirmationEmail = 0;
	Integer reminderEmail = 0;
	Integer feedbackEmail = 0;
	
	@OneToOne(optional=true)
	Feedback userFeedback = null;
	
	@OneToOne(optional=true)
	UserFeedback ownerFeedback = null;
	
	@CollectionOfElements(fetch=FetchType.LAZY)
	List<PropertyDay> infoList;
	
	static Logger log = Logger.getLogger(Booking.class);
	
	public Booking() {}
	
	public Booking(Integer id, Date startDate, Date endDate, String propertyName)
	{
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.property = new Property();
		this.property.setName(propertyName);
	}
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	private Property property;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	private OkapiedUserDetails user;
	
	@OneToOne(optional=true, fetch=FetchType.LAZY)
	Cancel cancel;
	
	public static int PRECEDENCE_BOOKING = 1;
	
	@OneToOne(optional=true, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	Refund refund;
	
	@OneToOne(optional=true, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	Refund ownerPayment;
	
	@ManyToOne(optional=true)
	public CurrencyCode currencyCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public boolean match(Calendar day) {
		if( startDate == null || endDate == null ) return false;
		if (day.getTimeInMillis() >= startDate.getTime()
				&& day.getTimeInMillis() < endDate.getTime()) {
			if( expired() && !getBookingStatus().equals(BOOKING_STATUS_CONFIRMED)) return false;
			if( getReservedBool() ) return false;
			if( getBookingStatus() != null && 
			    ( getBookingStatus().equals(BOOKING_STATUS_CANCELLED) ||
			      getBookingStatus().equals(BOOKING_STATUS_REFUND_FULL) ||
			      getBookingStatus().equals(BOOKING_STATUS_REFUND_PARTIAL) ||
			      getBookingStatus().equals(BOOKING_STATUS_REFUND_PARTIAL_OWNER_PAID) ) )
			    return false;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean retrieveIsStillValid()
	{
		if( expired() && !getBookingStatus().equals(BOOKING_STATUS_CONFIRMED)) return false;
		//if( getReservedBool() ) return false;
		if( getBookingStatus() != null && 
			    ( getBookingStatus().equals(BOOKING_STATUS_CANCELLED) ||
			      getBookingStatus().equals(BOOKING_STATUS_REFUND_FULL) ||
			      getBookingStatus().equals(BOOKING_STATUS_REFUND_PARTIAL) ||
			      getBookingStatus().equals(BOOKING_STATUS_REFUND_PARTIAL_OWNER_PAID) ) )
			    return false;
		return true;
	}
	
	@Override
	public boolean getMatchedReservedBool(Calendar day) {
		if( startDate == null || endDate == null ) return false;
		if (day.getTimeInMillis() >= startDate.getTime()
				&& day.getTimeInMillis() < endDate.getTime()) {
			if( expired() ) return false;
			else return getReservedBool();
		} else {
			return false;
		}
	}
	
	public boolean expired()
	{
		Calendar now = DateUtil.getCurrentCalendarCompareTZ();
		if( expirationDate == null ) return false;
		if( now.getTimeInMillis() > expirationDate.getTime() ) 
		{
			return true;	
		}
		else return false;
	}

	@Override
	public Integer getAvailable() {
		return available;
	}

	@Override
	public Integer precedence() {
		return PRECEDENCE_BOOKING;
	}

	@Override
	public boolean getAvailableBool() {
		if( available == null ) return false;
		if( available == 1 ) return true; 
		else return false;
	}

	@Override
	public void setAvailableBool(boolean availableBool) {
		if( availableBool == true ) available = 1;
		else available = 0;
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

	public Integer getReserved() {
		return reserved;
	}

	public void setReserved(Integer reserved) {
		this.reserved = reserved;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public void setAvailable(Integer available) {
		this.available = available;
	}
	
	public boolean getReservedBool()
	{
		if( reserved == null ) return false;
		if( reserved == 1 && !expired() ) 
		{
			return true;
		}
		else return false;
	}
	
	public Double getPropertyOwnerAmount()
	{
		if( total == null ) return null;
		BigDecimal totalBD = new BigDecimal(total);
		BigDecimal myFraction = totalBD.multiply(Configuration.instance().getBigDecimalProperty("MY_FRACTION"));
		BigDecimal result = totalBD.subtract(myFraction);
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(result.doubleValue()));
	}
	
	public Double getMyAmount()
	{
		if( total == null ) return null;
		BigDecimal totalBD = new BigDecimal(total);
		BigDecimal result = totalBD.multiply(Configuration.instance().getBigDecimalProperty("MY_FRACTION"));
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(result.doubleValue()));
	}
	
	public void updateExpiration()
	{
		Calendar currentDay = DateUtil.getCurrentCalendarCompareTZ();
		currentDay.add(Calendar.MINUTE, Configuration.instance().getIntProperty("BOOKING_TIMEOUT_MIN"));
		setExpirationDate(currentDay.getTime());
	}
	
	public void setReservedBool(boolean reservedBool)
	{
		if(reservedBool) reserved = 1;
		else reserved = 0;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public OkapiedUserDetails getUser() {
		return user;
	}

	public void setUser(OkapiedUserDetails user) {
		this.user = user;
	}

	public Integer getPeople() {
		return people;
	}

	public void setPeople(Integer people) {
		this.people = people;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public String getPayKey() {
		return payKey;
	}

	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	@Override
	public Integer getBookingId() {
		return getId();
	}
	
	public int retrieveShowPhotoId()
	{
		if( property.getPhotos() != null && property.getPhotos().size() > 0)
		{
			return property.getPhotos().get(0).getId();
		}
		else
		{
			return -1;
		}
	}
	
	public long retrievePeriodDays()
	{
		long difference = endDate.getTime() - startDate.getTime();
		long millisInADay = 1000*60*60*24;
		return difference/millisInADay;
	}
	
	public int retrieveTotalRounded()
	{
		return Math.round(total);
	}
	
	public boolean checkIfConfirmedStatus()
	{
		if( getBookingStatus().equals(BOOKING_STATUS_CONFIRMED) ) return true;
		else return false;
	}
	
	public boolean readyToLeaveFeedbackUser()
	{
		if( getBookingStatus() != null && getBookingStatus().equals(BOOKING_STATUS_CONFIRMED) )
		{
			Calendar now = property.retrieveCurrentDay();
			Calendar start = property.retrieveCurrentDay();
			start.setTime(getStartDate());
			if( now.compareTo(start) > 0 ) return true;
		}
		return false;
	}
	
	public void checkIsBookingUser(Booking booking, int userId)
	    throws OkaSecurityException
	{
		if( booking.getUser() == null || !booking.getUser().getId().equals(userId) )
		{
			log.error("Illegal access attempt to feedback user: " + userId);
		    throw new OkaSecurityException("You cannot leave feedback for a booking of another user");
		}
	}

	public Feedback getUserFeedback() {
		return userFeedback;
	}

	public void setUserFeedback(Feedback userFeedback) {
		this.userFeedback = userFeedback;
	}

	public void checkIsBookingOwner(Booking booking, int userId) throws OkaSecurityException {
		if( booking.getProperty() == null || 
			booking.getProperty().getOwner() == null ||
			!booking.getProperty().getOwner().getId().equals(userId) )
		{
			log.warn("User not owner of property when checking booking userId:" + userId);
			throw new OkaSecurityException("You are not the owner of the property to leave feedback");
		}
	}

	public boolean readyToLeaveFeedbackOwner() 
	{
		if( getBookingStatus() != null && getBookingStatus().equals(BOOKING_STATUS_CONFIRMED) )
		{
			Calendar now = property.retrieveCurrentDay();
			Calendar end = property.retrieveCurrentDay();
			end.setTime(getEndDate());
			if( now.compareTo(end) > 0 ) return true;
		}
		return false;
	}

	public UserFeedback getOwnerFeedback() {
		return ownerFeedback;
	}

	public void setOwnerFeedback(UserFeedback ownerFeedback) {
		this.ownerFeedback = ownerFeedback;
	}

	public Cancel getCancel() {
		return cancel;
	}

	public void setCancel(Cancel cancel) {
		this.cancel = cancel;
	}
    
    public boolean checkCancelled()
    {
    	if( getBookingStatus().equals(BOOKING_STATUS_CANCELLED) ) return true;
    	else return false;
    }

	public Integer getPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(Integer paymentComplete) {
		this.paymentComplete = paymentComplete;
	}
	
	public boolean getPaymentCompleteBool() {
		if( paymentComplete == null ) return false;
		if( paymentComplete == 1 ) return true; 
		else return false;
	}

	public void setPaymentCompleteBool(boolean paymentCompleteBool) {
		if( paymentCompleteBool == true ) paymentComplete = 1;
		else paymentComplete = 0;
	}

	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getRefundPolicy() {
		return refundPolicy;
	}

	public void setRefundPolicy(String refundPolicy) {
		this.refundPolicy = refundPolicy;
	}
	
	public boolean isRefundAvailable()
	{
		if( getRefundPolicy() == null ) return false;
		if( getRefundPolicy().equals(Property.REFUND_POLICY_STRICT) )
		{
			return false;
		}
		else if( getPaymentCompleteBool() )
		{
			return false;
		}
		if( getBookingStatus().equals(BOOKING_STATUS_CONFIRMED) ) return true;
		else return false;
	}

	public List<PropertyDay> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<PropertyDay> infoList) {
		this.infoList = infoList;
	}

	public CurrencyCode getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(CurrencyCode currencyCode) {
		this.currencyCode = currencyCode;
	}

	public boolean bookedGreaterThan60DaysAgo() 
	{
		Calendar now = DateUtil.getCurrentCalendarCompareTZ();
		Calendar bookingDate = DateUtil.getCurrentCalendarCompareTZ();
		bookingDate.setTime(getBookingDate());
		long millisIn60Days = 5184000000l;
		long diff = now.getTimeInMillis() - bookingDate.getTimeInMillis();
		if( diff >= millisIn60Days ) return true;
		else return false;
	}

	public Refund getOwnerPayment() {
		return ownerPayment;
	}

	public void setOwnerPayment(Refund ownerPayment) {
		this.ownerPayment = ownerPayment;
	}
	
	public String retrieveRefundPolicyText()
	{
		return property.retrieveRefundPolicyText(getRefundPolicy());
	}

	public boolean retrieveFirstDayOfBooking(Calendar day) 
	{
		if( day.getTimeInMillis() == startDate.getTime() ) return true;
	    return false;	
	}
	
	public boolean retrieveLastDayOfBooking(Calendar day) 
	{
		if( day.getTimeInMillis() == endDate.getTime() ) return true;
	    return false;	
	}

	public Integer getConfirmationEmail() {
		return confirmationEmail;
	}

	public void setConfirmationEmail(Integer confirmationEmail) {
		this.confirmationEmail = confirmationEmail;
	}
	
	public boolean getConfirmationEmailBool()
	{
		if( confirmationEmail == null ) return false;
		if( confirmationEmail == 1 ) return true;
		else return false;
	}
	
	public void setConfirmationEmailBool(boolean confirmationEmailBool)
	{
		if( confirmationEmailBool ) confirmationEmail = 1;
		else confirmationEmail = 0;
	}

	public Integer getReminderEmail() {
		return reminderEmail;
	}

	public void setReminderEmail(Integer reminderEmail) {
		this.reminderEmail = reminderEmail;
	}
	
	public boolean getReminderEmailBool()
	{
		if( reminderEmail == null ) return false;
		if( reminderEmail == 1 ) return true;
		else return false;
	}
	
	public void setReminderEmailBool(boolean reminderEmailBool)
	{
		if( reminderEmailBool ) reminderEmail = 1;
		else reminderEmail = 0;
	}

	public Integer getFeedbackEmail() {
		return feedbackEmail;
	}

	public void setFeedbackEmail(Integer feedbackEmail) {
		this.feedbackEmail = feedbackEmail;
	}
	
	public boolean getFeedbackEmailBool()
	{
		if( feedbackEmail == null ) return false;
		if( feedbackEmail == 1 ) return true;
		else return false;
	}
	
	public void setFeedbackEmailBool(boolean feedbackEmailBool)
	{
		if( feedbackEmailBool ) feedbackEmail = 1;
		else feedbackEmail = 0;
	}
}
