package okapied.service;

import javax.persistence.NoResultException;

import okapied.dao.DAO;
import okapied.entity.OkapiedUserDetails;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class OkapiedUserDetailsService implements UserDetailsService
{
	DAO dao;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException 
	{
        try
        {
        	OkapiedUserDetails user = (OkapiedUserDetails) 
        	    dao.get(OkapiedUserDetails.class.getName(),"username",username);
        	return user;
        }
        catch( NoResultException e )
        {
        	throw new UsernameNotFoundException(e.getMessage());
        }
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
}
