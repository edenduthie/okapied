package okapied.web;

import okapied.entity.OkapiedUserDetails;
import okapied.util.Configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class LoginStatus 
{
	
    public static boolean isLoggedIn()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean loggedIn = false;
        for (GrantedAuthority authority : auth.getAuthorities()) 
        {
        	if(authority.getAuthority().equals(Configuration.instance().getStringProperty("LOGGED_IN_ROLE"))) 
        	    loggedIn = true;
        }
        return loggedIn;
    }
    
    public static OkapiedUserDetails getUser()
    {
    	Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if( o instanceof OkapiedUserDetails )
    	{
            OkapiedUserDetails user = (OkapiedUserDetails) o;
            return user;
    	}
    	else
    	{
    		return null;
    	}
    }
    
    public static int getUserId()
    {
    	if( getUser() != null ) return getUser().getId();
    	else return -1;
    }
    
    /**
     * Returns true if the given user id matches the current logged in user
     * @return
     */
    public static boolean isUser(Integer id)
    {
    	OkapiedUserDetails user = getUser();
    	if( user == null ) return false;
    	if( user.getId().equals(id) ) return true;
    	else return false;
    }
}
