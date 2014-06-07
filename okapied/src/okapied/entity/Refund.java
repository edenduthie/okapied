package okapied.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Refund 
{
	@Id @GeneratedValue
	private Integer 		id;
	String encryptedRefundTransactionId;
	Float refundFeeAmount;
	Float refundGrossAmount;
	Float refundNetAmount;
	String refundStatus;
	String refundTransactionStatus;
	Float totalOfAllRefunds;
	String payKey;
	String trackingId;
	Date refundDate;
	String emailRefundSentTo;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEncryptedRefundTransactionId() {
		return encryptedRefundTransactionId;
	}
	public void setEncryptedRefundTransactionId(String encryptedRefundTransactionId) {
		this.encryptedRefundTransactionId = encryptedRefundTransactionId;
	}
	public Float getRefundFeeAmount() {
		return refundFeeAmount;
	}
	public void setRefundFeeAmount(Float refundFeeAmount) {
		this.refundFeeAmount = refundFeeAmount;
	}
	public Float getRefundGrossAmount() {
		return refundGrossAmount;
	}
	public void setRefundGrossAmount(Float refundGrossAmount) {
		this.refundGrossAmount = refundGrossAmount;
	}
	public Float getRefundNetAmount() {
		return refundNetAmount;
	}
	public void setRefundNetAmount(Float refundNetAmount) {
		this.refundNetAmount = refundNetAmount;
	}
	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
	public String getRefundTransactionStatus() {
		return refundTransactionStatus;
	}
	public void setRefundTransactionStatus(String refundTransactionStatus) {
		this.refundTransactionStatus = refundTransactionStatus;
	}
	public Float getTotalOfAllRefunds() {
		return totalOfAllRefunds;
	}
	public void setTotalOfAllRefunds(Float totalOfAllRefunds) {
		this.totalOfAllRefunds = totalOfAllRefunds;
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
	public Date getRefundDate() {
		return refundDate;
	}
	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
	}
	public String getEmailRefundSentTo() {
		return emailRefundSentTo;
	}
	public void setEmailRefundSentTo(String emailRefundSentTo) {
		this.emailRefundSentTo = emailRefundSentTo;
	}
}
