package okapied.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class OkapiedUserDetails implements UserDetails 
{
	@Id @GeneratedValue
	private Integer 		id;
	String username;
	String password;
	Integer accountNonExpiredInt = 1;
	Integer accountNonLockedInt = 1;
	Integer credentialsNonExpiredInt = 1 ;
	Integer enabledInt = 1;
	String email;
	String activationKey;
	String ip;
	String preferredEmail;
	String phoneNumber;
	Integer frozen = 0;
	String firstName;
	String lastName;
	
	@ManyToMany(fetch=FetchType.EAGER)
	List<OkapiedGrantedAuthority> authorities = new ArrayList<OkapiedGrantedAuthority>();
	
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		for( OkapiedGrantedAuthority oa : authorities )
		{
			grantedAuthorities.add((GrantedAuthority)oa);
		}
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		if( accountNonExpiredInt != null )
		{
			if( accountNonExpiredInt == 1 ) return true;
			else return false;
		}
		else
		{
			return false;
		}
	}
	
	public void setAccountNonExpired(boolean accountNonExpired)
	{
		if( accountNonExpired ) accountNonExpiredInt = 1;
		else accountNonExpiredInt = 0;
	}

	@Override
	public boolean isAccountNonLocked() {
		if( accountNonLockedInt != null )
		{
			if( accountNonLockedInt == 1 ) return true;
			else return false;
		}
		else
		{
			return false;
		}
	}
	
	public void setAccountNonLocked(boolean accountnonLocked)
	{
		if( accountnonLocked ) accountNonLockedInt = 1;
		else accountNonLockedInt = 0;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		if( credentialsNonExpiredInt != null )
		{
			if( credentialsNonExpiredInt == 1 ) return true;
			else return false;
		}
		else
		{
			return false;
		}
	}
	
	public void setCredentialsNotExpired(boolean c)
	{
		if( c ) credentialsNonExpiredInt = 1;
		else credentialsNonExpiredInt = 0;
	}

	@Override
	public boolean isEnabled() {
		if( enabledInt != null )
		{
			if( enabledInt == 1 ) return true;
			else return false;
		}
		else
		{
			return false;
		}
	}
	
	public void setEnabled(boolean e)
	{
		if( e ) enabledInt = 1;
		else enabledInt = 0;
	}

	public Integer getAccountNonExpiredInt() {
		return accountNonExpiredInt;
	}

	public void setAccountNonExpiredInt(Integer accountNonExpiredInt) {
		this.accountNonExpiredInt = accountNonExpiredInt;
	}

	public Integer getAccountNonLockedInt() {
		return accountNonLockedInt;
	}

	public void setAccountNonLockedInt(Integer accountNonLockedInt) {
		this.accountNonLockedInt = accountNonLockedInt;
	}

	public Integer getCredentialsNonExpiredInt() {
		return credentialsNonExpiredInt;
	}

	public void setCredentialsNonExpiredInt(Integer credentialsNonExpiredInt) {
		this.credentialsNonExpiredInt = credentialsNonExpiredInt;
	}

	public Integer getEnabledInt() {
		return enabledInt;
	}

	public void setEnabledInt(Integer enabledInt) {
		this.enabledInt = enabledInt;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(List<OkapiedGrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public String getName() {
		return firstName + " " + lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void addAuthority(OkapiedGrantedAuthority auth)
	{
		if( authorities == null ) authorities = new ArrayList<OkapiedGrantedAuthority>();
		authorities.add(auth);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPreferredEmail() {
		return preferredEmail;
	}

	public void setPreferredEmail(String preferredEmail) {
		this.preferredEmail = preferredEmail;
	}
	
	public String retrieveBestEmail()
	{
		if( preferredEmail != null && preferredEmail.trim().length() > 0) return preferredEmail;
		else return email;
	}
	
	public List<OkapiedGrantedAuthority> retrieveAuthorities()
	{
		return authorities;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Integer getFrozen() {
		return frozen;
	}

	public void setFrozen(Integer frozen) {
		this.frozen = frozen;
	}
	
	public boolean getFrozenBool()
	{
		if( frozen == null ) return false;
		if( frozen == 1 ) return true;
		else return false;
	}
	
	public void setFrozenBool(boolean f)
	{
		if( f ) frozen = 1;
		else frozen = 0;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
