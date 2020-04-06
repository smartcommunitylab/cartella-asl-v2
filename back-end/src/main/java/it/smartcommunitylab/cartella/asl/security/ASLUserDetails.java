package it.smartcommunitylab.cartella.asl.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

public class ASLUserDetails implements UserDetails {
	private static final long serialVersionUID = 1970015369860723085L;

	private ASLUser user;
	
	public ASLUserDetails() {
		super();
	}

	public ASLUserDetails(ASLUser user) {
		super();
		this.user = user;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
		return result;
	}

	// TODO
	@Override
	public String getPassword() {
		return "";
	}

	// TODO
	public String getUsername() {
		return "";
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public ASLUser getUser() {
		return user;
	}

	public void setUser(ASLUser user) {
		this.user = user;
	}

	
	
}
