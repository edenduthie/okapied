package okapied.service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.BaseTest;
import okapied.dao.DAO;
import okapied.entity.OkapiedGrantedAuthority;
import okapied.entity.OkapiedUserDetails;
import okapied.exception.InputValidationException;
import okapied.util.Configuration;
import okapied.util.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.svcs.services.PPFaultMessage;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

public class AccountServiceTest extends BaseTest
{
    @Autowired 
    OkapiedUserDetailsService okapiedUserDetailsService;
    
    @Autowired
    AccountService accountService;
    
    @Autowired 
    DAO dao;
    
	String FIRST_NAME = "Test";
	String LAST_NAME = "User";
	String EMAIL = "eduthi_1303218368_per@gatorlogic.com";
    
    @Test
    public void testEncryptPassword()
    {
    	OkapiedUserDetails user = Generator.okapiedUserDetails();
    	user.setPassword("password");
    	accountService.encryptPassword(user);
    	String md5 = "5f4dcc3b5aa765d61d8327deb882cf99";
    	Assert.assertEquals(user.getPassword(),md5);
    }
    
    @Test 
    public void testGetRoleUser()
    {
    	OkapiedGrantedAuthority auth = Generator.okapiedGrantedAuthority();
    	dao.persist(auth);
    	OkapiedGrantedAuthority result = accountService.getRoleUser();
    	Assert.assertEquals(result.getAuthority(),auth.getAuthority());
    	dao.deleteAll(OkapiedGrantedAuthority.class.getName());
    }
    
    @Test 
    public void testPersist()
    {
    	OkapiedGrantedAuthority auth = Generator.okapiedGrantedAuthority();
    	dao.persist(auth);
    	OkapiedGrantedAuthority result = accountService.getRoleUser();
    	OkapiedUserDetails user = Generator.okapiedUserDetails();
    	user.addAuthority(auth);
    	dao.addDelete(user);
    	dao.deleteAll(OkapiedGrantedAuthority.class.getName());
    }
    
    @Test
    public void testGenerateAuthKey()
    {
    	OkapiedUserDetails user = Generator.okapiedUserDetails();
    	accountService.generateActivationKey(user);
    	String firstAuth = user.getActivationKey();
    	Assert.assertNotNull(firstAuth);
    	
    	accountService.generateActivationKey(user);
    	String secondAuth = user.getActivationKey();
    	Assert.assertNotSame(firstAuth,secondAuth);
    }
    
    @Test
    public void testSendActivationEmail() throws AddressException, MessagingException
    {
    	OkapiedUserDetails user = Generator.okapiedUserDetails();
    	user.setEmail(Configuration.instance().getStringProperty("FROM_EMAIL"));
    	user.setFirstName("Eden");
    	user.setLastName("Duthie");
    	user.setId(12312);
    	accountService.generateActivationKey(user);
    	accountService.sendActivationEmail(user, "http://localhost:8080/okapied");
    }
    
    @Test 
    public void testActivate()
    {
    	OkapiedGrantedAuthority auth = Generator.okapiedGrantedAuthority();
    	dao.persist(auth);
    	OkapiedGrantedAuthority result = accountService.getRoleUser();
    	OkapiedUserDetails user = Generator.okapiedUserDetails();
    	user.addAuthority(auth);
    	user.setEnabled(false);
    	accountService.generateActivationKey(user);
    	dao.persist(user);
    	
    	boolean activated = accountService.activate(user.getId(), user.getActivationKey());
    	
    	Assert.assertTrue(activated);
    	OkapiedUserDetails returnedUser = accountService.get(user.getId());
    	Assert.assertTrue(returnedUser.isEnabled());
    	
    	accountService.remove(user);
    	dao.deleteAll(OkapiedGrantedAuthority.class.getName());
    }
    
    @Test 
    public void testActivateFailure()
    {
    	OkapiedGrantedAuthority auth = Generator.okapiedGrantedAuthority();
    	dao.persist(auth);
    	OkapiedGrantedAuthority result = accountService.getRoleUser();
    	OkapiedUserDetails user = Generator.okapiedUserDetails();
    	user.addAuthority(auth);
    	user.setEnabled(false);
    	accountService.generateActivationKey(user);
    	dao.persist(user);
    	
    	boolean activated = accountService.activate(user.getId(), "dodge key");
    	
    	Assert.assertFalse(activated);
    	OkapiedUserDetails returnedUser = accountService.get(user.getId());
    	Assert.assertFalse(returnedUser.isEnabled());
    	
    	accountService.remove(user);
    	dao.deleteAll(OkapiedGrantedAuthority.class.getName());
    }
    
    @Test
    public void validPayPalAccountVerified() throws InputValidationException
    {
    	String firstName = FIRST_NAME;
    	String lastName = LAST_NAME;
    	String email = EMAIL;
    	
    	Assert.assertTrue(accountService.validPayPalAccount(firstName, lastName, email));
    }
    
    @Test
    public void validPayPalAccountUnVerified() throws InputValidationException
    {
    	String firstName = FIRST_NAME;
    	String lastName = LAST_NAME;
    	String email = "eduthi_1303218368_per@gatorlogic.com";
    	
    	Assert.assertTrue(accountService.validPayPalAccount(firstName, lastName, email));
    }
    
    @Test
    public void validPayPalAccountWrongFirstName() throws InputValidationException
    {
    	String firstName = "WrongFirstName";
    	String lastName = LAST_NAME;
    	String email = EMAIL;
    	
    	Assert.assertFalse(accountService.validPayPalAccount(firstName, lastName, email));
    }
    
    @Test
    public void validPayPalAccountWrongLastName() throws InputValidationException
    {
    	String firstName = FIRST_NAME;
    	String lastName = "WrongLastName";
    	String email = EMAIL;
    	
    	Assert.assertFalse(accountService.validPayPalAccount(firstName, lastName, email));
    }
    
    @Test
    public void validPayPalAccountWrongEmail() throws InputValidationException
    {
    	String firstName = FIRST_NAME;
    	String lastName = LAST_NAME;
    	String email = "wrongemail@gatorlogic.com";
    	
    	Assert.assertFalse(accountService.validPayPalAccount(firstName, lastName, email));
    }
    
    @Test 
    public void testUpdateCheckPayPalEmailWorks() throws InputValidationException
    {
    	property = Generator.property();
    	property.getOwner().setFirstName(FIRST_NAME);
    	property.getOwner().setLastName(LAST_NAME);
    	property.getOwner().setEmail(EMAIL);
    	saveProperty();
    	
    	accountService.update(property.getOwner(),true);
    	
    	removeProperty();
    }
    
    @Test 
    public void testUpdateCheckPayPalEmailWorksChangeOnUpdate() throws InputValidationException
    {
    	property = Generator.property();
    	saveProperty();
    	
    	property.getOwner().setFirstName(FIRST_NAME);
    	property.getOwner().setLastName(LAST_NAME);
    	property.getOwner().setEmail(EMAIL);
    	
    	accountService.update(property.getOwner(),true);
    	
    	removeProperty();
    }
    
    @Test 
    public void testUpdateCheckPayPalEmailWorksInvalidEmail() throws InputValidationException
    {
    	property = Generator.property();
    	saveProperty();
    	
    	property.getOwner().setEmail("invalidemail@gmail.com");
    	
    	try
    	{
    	    accountService.update(property.getOwner(),true);
    	    Assert.fail();
    	}
    	catch( InputValidationException e) {}
    	 
    	
    	removeProperty();
    }
}
