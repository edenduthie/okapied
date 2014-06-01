package okapied.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import okapied.util.Configuration;

import org.apache.log4j.Logger;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.tools.jxc.gen.config.Config;

public class MailService 
{
	Properties props;
	String smtpServer;
	String username;
	String password;
	String from;
	String bcc;
	Integer port;
	String host;
	String auth;
	String tls;
	
	private static final Logger log = Logger.getLogger(MailService.class);
	
	public MailService()
	{
		smtpServer = Configuration.instance().getStringProperty("SMTP_SERVER");
		port = Configuration.instance().getIntProperty("SMTP_PORT");
		username = Configuration.instance().getStringProperty("SMTP_USERNAME");
		password = Configuration.instance().getStringProperty("SMTP_PASSWORD");
		from = Configuration.instance().getStringProperty("FROM_EMAIL");
		host = Configuration.instance().getStringProperty("HOST_EMAIL");
		bcc = Configuration.instance().getStringProperty("BCC_EMAIL");
		auth = Configuration.instance().getStringProperty("SMTP_AUTH");
		tls = Configuration.instance().getStringProperty("SMTP_TLS");
		props = System.getProperties();
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.auth", auth);
		props.put("mail.smtp.port", port.toString() );
		props.put("mail.smtp.starttls.enable",tls);
		
		log.debug("Setting up mail service");
		log.debug("mail.smtp.host: " + props.getProperty("mail.smtp.host"));
		log.debug("mail.smtp.auth: " + props.getProperty("mail.smtp.auth"));
		log.debug("mail.smtp.port: " + props.getProperty("mail.smtp.port"));
		log.debug("mail.smtp.starttls.enable: " + props.getProperty("mail.smtp.starttls.enable"));
	}
	
    public void sendMessage(String to, String subject, String text) throws AddressException, MessagingException
    {
    	Session session = Session.getInstance(props, null);
    	Message msg = new MimeMessage(session);
    	Address[] replyToList = new Address[1];
    	replyToList[0] = new InternetAddress(from);
    	log.debug("Mail reply to: " + from);
    	msg.setReplyTo(replyToList);
    	msg.setFrom(new InternetAddress(host));
    	log.debug("Mail from: " + host);
    	log.debug("Recipient: " + to);
    	msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to, false));
    	//msg.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(bcc, false));
    	msg.setSubject(subject);
    	msg.setText(text);
    	
    	SMTPTransport t = (SMTPTransport)session.getTransport("smtp");
    	try
    	{
    	    t.connect(smtpServer, username, password);
    	    t.sendMessage(msg, msg.getAllRecipients());
    	}
    	finally
    	{
    		log.info("Mail server response: " + t.getLastServerResponse());
    		t.close();
    	}
    }
}
