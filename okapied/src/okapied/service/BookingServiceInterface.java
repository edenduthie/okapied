package okapied.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.dao.DAO;
import okapied.entity.Booking;
import okapied.entity.OkapiedUserDetails;
import okapied.entity.Property;
import okapied.exception.AvailabilityException;
import okapied.exception.BookingException;
import okapied.exception.CancellationException;
import okapied.exception.ExchangeRateNotFoundException;
import okapied.exception.InputValidationException;
import okapied.exception.OkaSecurityException;
import okapied.exception.PaymentException;
import okapied.exception.PriceException;

import org.springframework.transaction.annotation.Transactional;

import adaptivepayments.AdaptivePayments;

import com.paypal.svcs.services.PPFaultMessage;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

public interface BookingServiceInterface {

	public static final String STATUS_COMPLETE = "COMPLETED";
	public static final String STATUS_INCOMPLETE = "INCOMPLETE";
	public static final String STATUS_CREATED = "CREATED";
	public static final String STATUS_ERROR = "ERROR";
	public static final String STATUS_REFUNDED = "REFUNDED";
	public static final String STATUS_ALREADY_REFUNDED = "ALREADY_REVERSED_OR_REFUNDED";
	public static final long WRONG_CURRENCY_ERROR_ID = 559044;

	@Transactional(rollbackFor = { AvailabilityException.class })
	public abstract Booking makeReservation(Property property,
			OkapiedUserDetails user, Calendar startDate, Calendar endDate,
			Integer people) throws AvailabilityException,
			InputValidationException, PriceException;

	@Transactional(rollbackFor = { AvailabilityException.class })
	public abstract Float calculateTotal(Property property, Calendar startDate,
			Calendar endDate) throws AvailabilityException,
			InputValidationException, PriceException;

	/**
	 * Drops all existing reservations on the given property for the given user.
	 */
	public abstract void dropExistingReservations(Property property,
			OkapiedUserDetails user);

	/**
	 * Drops all expired reservations for the given user
	 */
	public abstract void dropExpiredReservations(OkapiedUserDetails user);

	public abstract Float calculateTotal(Property property,
			List<Calendar> period) throws PriceException;

	public abstract void validateDates(Calendar startDate, Calendar endDate)
			throws InputValidationException;

	@SuppressWarnings("unchecked")
	public abstract List<Booking> getBookingsForUser(OkapiedUserDetails user);

	public abstract String startBooking(Integer id) throws FatalException,
			SSLConnectionException, PPFaultMessage, IOException,
			BookingException;

	@Transactional(rollbackFor = { FatalException.class,
			SSLConnectionException.class, PPFaultMessage.class })
	public abstract String startBooking(Booking booking) throws FatalException,
			SSLConnectionException, IOException, BookingException,
			PPFaultMessage;

	@Transactional(rollbackFor = { AddressException.class,
			MessagingException.class })
	public abstract void processIPN(Integer bookingId, String trackingId,
			String payKey, String senderEmail, String status);

	public abstract boolean verifyCompletion(Booking booking,
			String trackingId, String payKey, String senderEmail);

	/**
	 * When a user is on the PayPal page and decides not to make a booking they
	 * click on cancel, returning them to the site, consequently cancelling the booking
	 * @param booking
	 * @throws CancellationException 
	 */
	public abstract void cancelBooking(Booking booking, Integer userId)
			throws CancellationException;

	public abstract void emailCancellation(Booking booking);

	public abstract void emailCompletion(Booking booking);

	public abstract void emailPropertyOwnerCompletion(Booking booking);

	public abstract void emailPropertyOwnerError(Booking booking);

	public abstract void emailOwnerWrongCurrency(Booking booking);

	public abstract void emailMeWrongCurrency(Booking booking);

	public abstract BigDecimal roundBigDecimal(double value);

	public abstract String generateTrackingKey(Integer bookingId);

	public abstract Booking get(Integer id);

	public abstract Booking getWithCancel(Integer id);

	public abstract String retrieveBookingStatus(Integer id)
			throws FatalException, SSLConnectionException, PPFaultMessage,
			IOException;

	@Transactional(rollbackFor = { OkaSecurityException.class,
			FatalException.class, SSLConnectionException.class,
			PPFaultMessage.class, IOException.class })
	public abstract Booking loadBooking(Integer id, Integer userId)
			throws OkaSecurityException, FatalException,
			SSLConnectionException, PPFaultMessage, IOException;

	@Transactional(rollbackFor = { AddressException.class,
			MessagingException.class })
	public abstract Booking checkBookingStatus(Booking booking)
			throws FatalException, SSLConnectionException, PPFaultMessage,
			IOException;

	public abstract void updateCompleteBooking(Booking booking);

	public abstract void updateErrorBooking(Booking booking);

	public abstract void updateCancelledBooking(Booking booking);

	public abstract List<Booking> getBookingsWithStatus(String status);

	public abstract List<Booking> getBookingsWithStatusNoConfirmationEmail(
			String status);

	public abstract List<Booking> checkAllPendingPayments();

	/**
	 * Loads all the CONFIRMED bookings with paymentComplete = 0
	 * If the date is 23 hours past the start date of the booking the payment is processed.
	 * Doesn't send the money if the owner of the property is frozen
	 */
	public abstract void sendMoneyToPropertyOwners();

	public abstract boolean isPaymentTime(Booking booking);

	public abstract List<Booking> loadAllConfirmedBookingsUnpaidToPropertyOwners();

	/**
	 *  Finalise the payment and send an email to the owner. Done 23 hours after booking
	 *  start date.
	 */
	public abstract Booking sendMoneyToOwner(Booking booking)
			throws FatalException, SSLConnectionException, PPFaultMessage,
			IOException;

	public abstract void emailOwnerPaymentCompletion(Booking booking);

	public abstract AdaptivePayments getAdaptivePayments() throws IOException,
			FatalException, SSLConnectionException;

	public abstract PropertyService getPropertyService();

	public abstract void setPropertyService(PropertyService propertyService);

	public abstract DAO getDao();

	public abstract void setDao(DAO dao);

	public abstract PriceService getPriceService();

	public abstract void setPriceService(PriceService priceService);

	public abstract MailService getMailService();

	public abstract void setMailService(MailService mailService);

	public abstract BookingServiceInterface getListener();

	public abstract void setListener(BookingServiceInterface listener);

	public abstract void processBookingStatusReciept(Booking booking,
			String status);

	/**
	 * Checks to see if there are any overlapping bookings that have:
	 * 1. the same property
	 * 2. are not expired
	 * 3. have status RESERVATION, PENDING_PAYMENT, or CONFIRMED
	 * 4. do not have the same user (why stop people paying twice!)
	 */
	public abstract boolean overlappingBookings(Booking booking);

	public abstract void directPayment(Booking booking, Float amount,
			String email) throws PaymentException;

	public abstract RefundServiceInterface getRefundService();

	public abstract void setRefundService(RefundServiceInterface refundService);

	public abstract void setAdaptivePayments(AdaptivePayments ap);

	public abstract void sendConfirmationEmails();

	public abstract String formatDate(Date time, String tz);

//	public abstract BigDecimal deductPayPalFees(double amount, Booking booking)  throws ExchangeRateNotFoundException;

}