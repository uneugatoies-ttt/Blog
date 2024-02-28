package com.example.blog.common;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class OAuthUserNameGetter {
	
	private UsernameSession usernameSession;
	
	public OAuthUserNameGetter(UsernameSession usernameSession) {
		this.usernameSession = usernameSession;
	}
	
	public String getInOAuthUserService(String authProvider, OAuth2User oAuth2User) {
		String username = null;
		
		if (authProvider.equals("GitHub")) {
			// "login" field를 가져온다; 모든 whitespace를 -로 대체한다.
			username = ((String) oAuth2User.getAttributes().get("login")).replaceAll(" ", "-");
		} else if (authProvider.equals("Google")) {
			// Google의 OAuth2 flow에서는 "login" field가 존재하지 않기 때문에,
			// "name" field를 대신해서 가져와야 한다; 역시 모든 whitespace를 -로 대체한다.
			username = ((String) oAuth2User.getAttributes().get("name")).replaceAll(" ", "-");
		}
		
		usernameSession.setUsername(username);
		
		return username;
	}
	
}
