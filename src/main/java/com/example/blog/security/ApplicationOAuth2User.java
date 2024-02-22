package com.example.blog.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/*
	-> ApplicationOAuth2User는 Spring Security에서 제공하는 OAuth2User와
	이 application에서 정의한 User entity 사이의 중간자 역할을 한다:
		- OAuth2에서 인증을 수행할 때에는 흐름이 성공적으로 종료될 때에 OAuthSuccessHandler 내부에서
		"Authentication"을 가지고 TokenProvider의 create()를 call해 token을 생성해야 하는데,
		이 때 class "Authentication"에는 ID가 없으므로, Jwts를 사용해서 JWT를 생성할 때
		setSubject()에 argument로 줄 값이 없다. 이 class가 바로 setSubject()의 argument의
		자리를 채우는 역할을 하게 된다.
		
		-> p.s. 다만 위의 서술에 대해서는 의문점이 있다. Authentication의 getPrincipal() method를 call하면
		Object가 return되는데, 이것을 곧바로 String으로 cast하면 안 되는 것인가?
		아직 시도해 보지는 않았다. 이후 추가.
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
