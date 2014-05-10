package okapied.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import okapied.util.Configuration;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class OkapiedGrantedAuthority implements GrantedAuthority
{
	@Id @GeneratedValue
	Integer 		id;
	
	String authority;
	
	@ManyToMany(fetch=FetchType.LAZY,mappedBy="authorities")
	List<OkapiedUserDetails> users = new ArrayList<OkapiedUserDetails>();
	
	public static OkapiedGrantedAuthority getLoginRole()
	{
		OkapiedGrantedAuthority auth = new OkapiedGrantedAuthority();
		auth.setAuthority(Configuration.instance().getStringProperty("LOGGED_IN_ROLE"));
		return auth;
	}
	
	@Override
	public String getAuthority() {
		return authority;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

}
