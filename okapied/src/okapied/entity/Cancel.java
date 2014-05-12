package okapied.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Cancel 
{
	public static String CANCEL_STATUS_CUSTOMER_REQUEST = "cancelation_requested_customer";
	
	@Id @GeneratedValue
	private Integer 		id;
	
	@OneToOne(optional=false, fetch=FetchType.LAZY)
	Booking booking;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	OkapiedUserDetails requestor;
	
	String status;
	
	@Column(columnDefinition="TEXT")
	String reason;

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

	public OkapiedUserDetails getRequestor() {
		return requestor;
	}

	public void setRequestor(OkapiedUserDetails requestor) {
		this.requestor = requestor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
