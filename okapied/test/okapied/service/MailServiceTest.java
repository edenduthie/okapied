package okapied.service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.BaseTest;
import okapied.util.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class MailServiceTest extends BaseTest
{
   @Autowired
   MailService mailService;
   
   @Test
   public void sendEmail() throws AddressException, MessagingException
   {
	   mailService.sendMessage(Configuration.instance().getStringProperty("FROM_EMAIL"),
	       "Email subject","Test message");
   }
}
