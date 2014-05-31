package okapied.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import okapied.dao.DAO;
import okapied.entity.Booking;
import okapied.entity.Charges;
import okapied.exception.ExchangeRateNotFoundException;
import okapied.exception.InvalidPayPalAccountException;
import okapied.exception.PaymentException;
import okapied.exception.RefundException;
import okapied.exception.RepeatedTransactionException;

import org.springframework.transaction.annotation.Transactional;

import adaptivepayments.AdaptivePayments;

import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

public interface RefundServiceInterface {

	/**
	 * Gives a full refund
	 * @throws RepeatedTransactionException 
	 */
	public abstract void fullRefund(Booking booking) throws RefundException,
			RepeatedTransactionException;

	@Transactional(rollbackFor = { NullPointerException.class })
	public abstract void fullRefund(Booking booking, AdaptivePayments ap)
			throws RefundException, RepeatedTransactionException;

	public abstract AdaptivePayments getAdaptivePayments() throws IOException,
			FatalException, SSLConnectionException;

	public abstract Properties getPayPalProperties() throws IOException;

	public abstract void setAdaptivePayments(AdaptivePayments ap);

	public abstract void emailUserFullRefundFail(Booking booking);

	public abstract void emailUserFullRefundSuccess(Booking booking);

	public abstract void emailAdminFullRefundFail(Booking booking);

	public abstract void emailUserPartialRefundSuccess(Booking booking);

	public abstract void emailOwnerPartialRefundSuccess(Booking booking);

	public abstract void issueRefund(Booking booking, Integer userId)
			throws RefundException, SecurityException, PaymentException,
			InvalidPayPalAccountException, IOException, ExchangeRateNotFoundException;

	public abstract void directPayment(Booking booking, Float amount,
			String email) throws PaymentException;

	/**
	 * If it is a refund, the result data is set as the booking's refund, otherwise it as set as
	 * the booking's ownerPayment
	 */
	public abstract void directPayment(Booking booking, Float amount,
			String email, boolean isRefund) throws PaymentException;

	public abstract void directPaymentMass(Booking booking, Float amount,
			String email, boolean isRefund) throws PaymentException,
			IOException;

	public abstract Charges getCharges(Booking booking);

	public abstract double getFraction(Booking booking);

	public abstract Float getTotalFullPercentage(Booking booking);

	public abstract Float removeMyCut(Float total);

	public abstract List<Calendar> getRefundPeriod(Booking booking);

	/**
	 * Retrieves the index in the info list (starting from 0) of the first day that can be 
	 * refunded. This can then be used to work out the total.
	 */
	public abstract Integer getIndexOfFirstRefundDay(Booking booking);

	public abstract void sendRefund(Booking booking) throws RefundException;

	public abstract DAO getDao();

	public abstract void setDao(DAO dao);

	public abstract MailService getMailService();

	public abstract void setMailService(MailService mailService);

	public abstract PropertyService getPropertyService();

	public abstract void setPropertyService(PropertyService propertyService);

	public abstract AccountService getAccountService();

	public abstract void setAccountService(AccountService accountService);

	public abstract boolean getNextDay(Calendar now);

	public abstract void calculateStandardRefund(Charges charges, Booking booking);
	
//	public abstract void deductPayPalFees(Charges charges, Booking booking) throws ExchangeRateNotFoundException;

}