package okapied.scheduler;

import okapied.service.BookingServiceInterface;
import okapied.service.NotificationService;
import okapied.util.Configuration;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Scheduler extends Thread
{
	static Logger log = Logger.getLogger(Scheduler.class);
	
	static boolean pleaseShutdown = false;
	
	BookingServiceInterface bookingService;
	NotificationService notificationService;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Runtime.getRuntime().addShutdownHook(new Thread() 
		{
		      public void run() 
		      {
		          log.info("Received shutdown signal");
		          pleaseShutdown = true;
		          while(pleaseShutdown) 
		          {
		        	try 
		        	{
						Thread.sleep(1000);
					} 
		        	catch (InterruptedException e) {}
		          }
		          log.info("Shutdown complete");
		      }
		});
		
		Scheduler scheduler = new Scheduler();
		scheduler.start();
	}
	
	public void run()
	{
		while(true)
		{
			log.debug("Scheduler running");
			
			try
			{
				log.debug("Checking all pending paymnts");
			    bookingService.checkAllPendingPayments();
			    log.debug("Sending confirmation emails");
			    bookingService.sendConfirmationEmails();
			    log.debug("Sending money to property owners");
			    bookingService.sendMoneyToPropertyOwners();
			    log.debug("Email reminders day before booking");
			    notificationService.emailRemindersDayBeforeBooking();
			    log.debug("Email feedback reminder day after booking");
			    notificationService.emailFeedbackReminderDayAfterBooking();
			}
			catch(Exception e)
			{
				log.error(e);
				e.printStackTrace();
			}
			
			try {
				if( pleaseShutdown )
				{
					log.info("Received pleaseShutdown message");
					pleaseShutdown = false;
					return;
				}
				sleep(Configuration.instance().getIntProperty("SCHEDULER_PERIOD_MILLIS"));
			} catch (InterruptedException e) {
				log.warn("Scheduler thread interrupted " + e.getMessage());
			}
		}
	}
	
	public Scheduler()
	{
		String appContextPath = "WebContent/WEB-INF/applicationContext.xml";
		ApplicationContext appCon = new FileSystemXmlApplicationContext(appContextPath);
		setBookingService((BookingServiceInterface)appCon.getBean("bookingService"));
		setNotificationService((NotificationService)appCon.getBean("notificationService"));
	}

	public BookingServiceInterface getBookingService() {
		return bookingService;
	}

	public void setBookingService(BookingServiceInterface bookingService) {
		this.bookingService = bookingService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}
