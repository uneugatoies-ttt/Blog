package com.example.blog.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/*
	-> 이 class는 OAuth2User와 User entity 사이의 중간자 역할을 한다:
	class "Authentication"에는 ID가 없으므로, Jwts를 사용해서 JWT를 생성할 때 setSubject()에
	argument로 줄 값이 없다. 이 때 이 class는 setSubject()의 argument의 자리를 채우는 역할을 하게 된다.
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
