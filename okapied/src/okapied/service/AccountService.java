package okapied.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.Query;

import okapied.dao.DAO;
import okapied.entity.OkapiedGrantedAuthority;
import okapied.entity.OkapiedUserDetails;
import okapied.exception.InputValidationException;
import okapied.util.Configuration;
import okapied.web.LoginStatus;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import src.paypalsamples.utils.ClientInfoUtil;
import adaptiveaccounts.AdaptiveAccounts;
import adaptivepayments.AdaptivePayments;

import com.paypal.svcs.services.PPFaultMessage;
import com.paypal.svcs.types.aa.GetVerifiedStatusRequest;
import com.paypal.svcs.types.aa.GetVerifiedStatusResponse;
import com.paypal.svcs.types.common.ErrorData;
import common.com.paypal.platform.sdk.exceptions.FatalException;
import common.com.paypal.platform.sdk.exceptions.SSLConnectionException;

@Transactional
public class AccountService 
{
	DAO dao;
    MailService mailService;
    PropertyService propertyService;
    
    public String ACCOUNT_STATUS_VERIFIED = "Verified";
    public String ACCOUNT_STATUS_UNVERIFIED = "UNVERIFIED";
    
    public static final Logger log = Logger.getLogger(AccountService.class);
    
    private AdaptiveAccounts adaptiveAccounts;
    
    public void encryptPassword(OkapiedUserDetails user)
	{
		Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		encoder.setEncodeHashAsBase64(false);
		user.setPassword(encoder.encodePassword(user.getPassword(),null));	
	}
	
	public OkapiedGrantedAuthority getRoleUser()
	{
		String queryString = "from OkapiedGrantedAuthority where authority=:roleUser";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("roleUser",Configuration.instance().getStringProperty("LOGGED_IN_ROLE"));
		return (OkapiedGrantedAuthority) query.getSingleResult();
	}
	
	public void generateActivationKey(OkapiedUserDetails user)
	{
		String authString =
		     Calendar.getInstance().getTime().toString() +
		     Math.random();
		String authHex = new Md5PasswordEncoder().encodePassword(authString,null);
		user.setActivationKey(authHex);
	}
	
	public void sendActivationEmail(OkapiedUserDetails user, String baseUrl) throws AddressException, MessagingException
	{
		String activationAction = "/Account/Account/activate";
		String url = baseUrl + activationAction + 
		    "?key=" + user.getActivationKey() +
		    "&userId=" + user.getId();
		
		String email = "Hi " + user.getName() + ",\n\n" +
		    "Welcome to Okapied! We hope that you enjoy our convenient approach to accomodation rentals.\n\n" +
		    "To activate your account and get started, click on the link below:\n\n" +
		    url + "\n\n" +
		    "Regards,\n\n" +
		    "Okapied";
		
		String subject = "Activate your Okapied account";
		
		mailService.sendMessage(user.getEmail(), subject, email);
	}
	
	public boolean activate(Integer userId, String key)
	{
		OkapiedUserDetails user = (OkapiedUserDetails) dao.get(OkapiedUserDetails.class.getName(),"id",userId);
		if( key.equals(user.getActivationKey()) )
		{
			user.setEnabled(true);
			dao.update(user);
			return true;
		}
		return false;
	}
	
	public boolean validPayPalAccount(OkapiedUserDetails user) throws InputValidationException
	{
		user = get(user.getId());
		return validPayPalAccount(user.getFirstName(),user.getLastName(),user.getEmail());
	}
	
	public boolean validPayPalAccountDontLoad(OkapiedUserDetails user) throws InputValidationException
	{
		return validPayPalAccount(user.getFirstName(),user.getLastName(),user.getEmail());
	}
	
	/**
	 * Returns the current status of the given PayPal account.
	 * 
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @return
	 * @throws FatalException
	 * @throws SSLConnectionException
	 * @throws PPFaultMessage
	 */
	public boolean validPayPalAccount(String firstName, String lastName, String email) 
	    throws InputValidationException
	{
		System.out.println("Checking valid PayPal account: " + firstName + " " + lastName + " " + email);
		GetVerifiedStatusRequest getStatusRequest=new GetVerifiedStatusRequest();
		getStatusRequest.setRequestEnvelope(ClientInfoUtil.getMyAppRequestEnvelope());
		getStatusRequest.setEmailAddress(email);
		getStatusRequest.setFirstName(firstName);
		getStatusRequest.setLastName(lastName);
		getStatusRequest.setMatchCriteria("NAME");
		   
		try
		{
			AdaptiveAccounts aa = getAdaptiveAccounts();
			GetVerifiedStatusResponse getVerifiedStatusResponse  = aa.getVerifiedStatus(getStatusRequest);
            String status=getVerifiedStatusResponse.getAccountStatus();
            if( status.equalsIgnoreCase(ACCOUNT_STATUS_UNVERIFIED) || 
                status.equalsIgnoreCase(ACCOUNT_STATUS_VERIFIED) ) 
            {
                return true;
            }
            else return false;
		}
		catch(PPFaultMessage e)
		{
			if( e.getFaultInfo() != null )
			{
				for( ErrorData error : e.getFaultInfo().getError() )
				{
					log.info(error.getMessage());
				}
			}
			return false;
		} 
		catch (FatalException e) 
		{
			log.error(e);
			throw new InputValidationException("Unable to check PayPal account status");
		} 
		catch (SSLConnectionException e) 
		{
			log.error(e);
			throw new InputValidationException("Unable to check PayPal account status");
		} 
		catch (IOException e) 
		{
			log.error(e);
			throw new InputValidationException("Unable to check PayPal account status");
		}
	}
	
	public void persist(OkapiedUserDetails user)
	{	
		dao.persist(user);
	}
	
	public void update(OkapiedUserDetails user) throws InputValidationException
	{
		update(user,false);
	}
	
	@Transactional(rollbackFor={InputValidationException.class})
	public void update(OkapiedUserDetails user, boolean checkPayPalAccount) throws InputValidationException
	{
		if( checkPayPalAccount )
		{
		    long count = propertyService.getTotalProperties(user.getId());
		    if( count > 0 )
		    {
		    	if( !validPayPalAccountDontLoad(user) )
		    	{
		    		throw new InputValidationException("You currently have properties listed for your account." +
		    		    " The first name, last name, email combination you provided does not match a valid PayPal account." +
		    		    " You can only change your details to a valid PayPal account once you have listed a property.");
		    	}
		    }
		}
		dao.update(user);
	}
	
	public OkapiedUserDetails get(Integer id)
	{
		return (OkapiedUserDetails) dao.get(OkapiedUserDetails.class.getName(),"id", id);
	}
	
	public void remove(OkapiedUserDetails user)
	{
		String queryString = "from OkapiedUserDetails where id=:id";
		Query query = dao.getEntityManager().createQuery(queryString);
		query.setParameter("id",user.getId());
	    user = (OkapiedUserDetails) query.getSingleResult();
	    dao.remove(user);
	}
	
	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}
	
    public AdaptiveAccounts getAdaptiveAccounts() throws IOException, FatalException, SSLConnectionException
    {
    	if( adaptiveAccounts != null ) return adaptiveAccounts;
    	InputStream in = getClass().getResourceAsStream("paypal_sdk_client.properties");
    	Properties sdkClientProperties = new Properties();
    	sdkClientProperties.load( in );
    	AdaptiveAccounts aa = new AdaptiveAccounts(sdkClientProperties);
    	return aa;
    }
}
