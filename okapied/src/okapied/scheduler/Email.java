package okapied.scheduler;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.service.ExchangeRatesService;
import okapied.service.MailService;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Email
{
	static Logger log = Logger.getLogger(Email.class);
	
	MailService mailService;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		Email updater = new Email();
		updater.run();
	}
	
	public void run() throws AddressException, MessagingException
	{
		String activationAction = "/Account/Account/activate";
		String url = "https://www.okapied.com" + activationAction + 
		    "?key=" + "23423423432" +
		    "&userId=" + "12";
		
		String email = "Hi " + " Pals Tester" + ",\n\n" +
		    "Welcome to Okapied! We hope that you enjoy our convenient approach to accomodation rentals.\n\n" +
		    "To activate your account and get started, click on the link below:\n\n" +
		    url + "\n\n" +
		    "Regards,\n\n" +
		    "Okapied";
		
		String subject = "Activate your Okapied account";
		
		mailService.sendMessage("okapiedellie@gmail.com", subject, email);
	}
	
	public Email()
	{
		String appContextPath = "WebContent/WEB-INF/applicationContext.xml";
		ApplicationContext appCon = new FileSystemXmlApplicationContext(appContextPath);
		setMailService((MailService)appCon.getBean("mailService"));
	}

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

}
