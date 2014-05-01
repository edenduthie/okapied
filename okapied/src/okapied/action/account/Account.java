package okapied.action.account;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import okapied.action.BaseAction;
import okapied.entity.OkapiedUserDetails;
import okapied.service.AccountService;
import okapied.service.OkapiedUserDetailsService;
import okapied.util.Configuration;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class Account extends BaseAction
{
    public static final String LOGIN = "login";
    public static final String CREATE = "create";
    public static final String SAVE = "save";
    public static final String ACTIVATE = "activate";
    public static final String EDIT = "edit";
    
    public OkapiedUserDetails user;
    public String confirmPassword;
    
    public Integer userId;
    public String key;
    public String message;
    public Boolean terms = false;
    
    OkapiedUserDetailsService okapiedUserDetailsService;
    AccountService accountService;
    
    public static final Logger log = Logger.getLogger(Account.class);
    
    @SkipValidation
    public String edit()
    {
    	userId = LoginStatus.getUserId();
    	user = accountService.get(userId);
    	return EDIT;
    }
    
    @SkipValidation
    public String login()
    {
    	return LOGIN;
    }
    
    @SkipValidation
    public String create()
    {
    	return CREATE;
    }
    
    public String save()
    {
    	//check matching password and confirmPassword
		if( user.getPassword() != null && user.getPassword().trim().length() > 0 )
		{
			if( !user.getPassword().equals(confirmPassword) )
			{
				addFieldError("user.password","Passwords do not match");
				log.info("Mismatched passwords");
				return ERROR;
			}
		}
		//make sure username is unique
		try
		{
		    UserDetails existingUser = okapiedUserDetailsService.loadUserByUsername(user.getUsername());
		    log.info("Duplicate username: " + user.getUsername());
		    addFieldError("user.username","Username taken, please choose another");
		    return ERROR;
		}
		catch( UsernameNotFoundException e ) {}
		//check terms
		if( !terms )
		{
			addFieldError("terms","Please acknowledge acceptance of Okapied's terms");
			return ERROR;
		}
		
    	user.addAuthority(accountService.getRoleUser());
    	accountService.encryptPassword(user);
    	user.setEnabled(false);
    	accountService.generateActivationKey(user);
    	user.setIp(getClientIp());
    	accountService.persist(user);
    	try {
    		accountService.sendActivationEmail(user, 
			    Configuration.instance().getStringProperty("BASE_REQUEST_URL"));
		} 
    	catch (AddressException e) 
    	{
			log.error(e.getMessage());
			addFieldError("user.email","Failed to send activation email to this address");
			return ERROR;
			
		} 
    	catch (MessagingException e) 
    	{
			log.error(e.getMessage());
			message = "Error sending activation email. Please try again or contact the site " +
    	    "<a href='mailto:"+Configuration.instance().getStringProperty("ADMIN_EMAIL")+"'>Administrator<a>";
			return ERROR;
		}
    	message = "An email has been sent to your address " + user.getEmail() + 
    	    ". Please click on the link provided to activate your account.";
        return SAVE;	
    }
    
    @SkipValidation
    public String activate()
    {
    	boolean result = accountService.activate(userId, key);
    	if(result) message = "Your account has been activated! Please log in below:";
    	else
        {
    		message = "There was a problem activating your account. Please try again or contact the site " +
    	        "<a href='mailto:"+Configuration.instance().getStringProperty("ADMIN_EMAIL")+"'>Administrator<a>";
    		return ERROR;
        }
    	return ACTIVATE;
    }

	public OkapiedUserDetails getUser() {
		return user;
	}

	public void setUser(OkapiedUserDetails user) {
		this.user = user;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public OkapiedUserDetailsService getOkapiedUserDetailsService() {
		return okapiedUserDetailsService;
	}

	public void setOkapiedUserDetailsService(
			OkapiedUserDetailsService okapiedUserDetailsService) {
		this.okapiedUserDetailsService = okapiedUserDetailsService;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public Boolean getTerms() {
		return terms;
	}

	public void setTerms(Boolean terms) {
		this.terms = terms;
	}
	
	public String getTitle()
	{
		return "Okapied Account Services";
	}
	
	public String getDescription()
	{
	    return "Sign up, sign in, or edit your Okapied account details";	
	}
}
