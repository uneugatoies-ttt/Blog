package com.example.blog.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/*
	This class acts as the bridge between OAuth2User and the User entity:
	Since the class 'Authentication' has no ID by default, we have nothing
	to put into JWTS.builder()'s setSubject() method. This 'bridge' is
	for filling the contents of the setSubject's argument.

	But honestly - I don't fully understand these classes and tools yet.
	If I grow more profound insight upon these items, IWAAIL.
*/

public class ApplicationOAuth2User implements OAuth2User {
	
	private String id;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;
	
	public ApplicationOAuth2User(String id, Map<String, Object> attributes) {
		this.id = id;
		this.attributes = attributes;
		this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
	
	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getName() {
		return id;
	}
	
}
