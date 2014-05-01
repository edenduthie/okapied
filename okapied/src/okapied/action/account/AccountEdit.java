package okapied.action.account;

import okapied.action.BaseAction;
import okapied.entity.OkapiedUserDetails;
import okapied.exception.InputValidationException;
import okapied.service.AccountService;
import okapied.service.OkapiedUserDetailsService;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AccountEdit extends BaseAction
{
    public static final String LIST= "list";
    
    public OkapiedUserDetails user;
    public String confirmPassword;
    
    public Integer userId;
    public String key;
    public String message;
    
    OkapiedUserDetailsService okapiedUserDetailsService;
    AccountService accountService;
    
    public static final Logger log = Logger.getLogger(AccountEdit.class);
    
    public String list()
    {	
    	OkapiedUserDetails userToEdit = accountService.get(LoginStatus.getUserId());
    	
    	//check matching password and confirmPassword
		if( user.getPassword() != null && user.getPassword().trim().length() > 0 )
		{
			if( !user.getPassword().equals(confirmPassword) )
			{
				addFieldError("user.password","Passwords do not match");
				log.info("Mismatched passwords");
				return ERROR;
			}
			else if( user.getPassword().length() < 6 )
			{
				addFieldError("user.password","Password must be 6 characters or more");
				log.info("Invalid password length");
				return ERROR;
			}
			else
			{
				accountService.encryptPassword(user);
				userToEdit.setPassword(user.getPassword());
			}
		}
		
		if( user.getUsername() != null && user.getUsername().length() > 0)
		{
			//make sure username is unique
			if( !user.getUsername().equals(userToEdit.getUsername()) )
			{
				try
				{
				    UserDetails existingUser = okapiedUserDetailsService.loadUserByUsername(user.getUsername());
				    log.info("Duplicate username: " + user.getUsername());
				    addFieldError("user.username","Username taken, please choose another");
				    return ERROR;
				}
				catch( UsernameNotFoundException e ) {}
			}
			userToEdit.setUsername(user.getUsername());
        }
		if( user.getFirstName() != null ) userToEdit.setFirstName(user.getFirstName());
		if( user.getLastName() != null ) userToEdit.setLastName(user.getLastName());
		if( user.getEmail() != null ) userToEdit.setEmail(user.getEmail());
		if( user.getPreferredEmail() != null ) userToEdit.setPreferredEmail(user.getPreferredEmail());
		
		try
		{
		    accountService.update(userToEdit,true);
		}
		catch( InputValidationException e)
		{
			log.error(e);
			message = e.getMessage();
			return ERROR;
		}
    	
    	message = "Changes Saved";
    	
    	return LIST;
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
	
	public String getTitle()
	{
		return "Account Details Changed";
	}
	
	public String getDescription()
	{
	    return "Your account details have been modified successfully";	
	}
}
