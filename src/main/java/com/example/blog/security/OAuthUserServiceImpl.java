package com.example.blog.security;

import javax.persistence.EntityNotFoundException;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.blog.domain.User;
import com.example.blog.misc.UserSession;
import com.example.blog.persistence.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/*
	- This class is for checking whether the returned user information by GitHub
	is present in the database or not. And if it isn't, then the new account for
	the user must be made.
*/

@Slf4j
@Service
public class OAuthUserServiceImpl extends DefaultOAuth2UserService {
	
	private UserRepository userRepository;
	private UserSession userSession;
	
	public OAuthUserServiceImpl(UserRepository userRepository, UserSession userSession) {
		super();
		this.userRepository = userRepository;
		this.userSession = userSession;
	}
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// calling the existing loadUser of DefaultOAuth2UserService.
		// this method uses user-info-uri to bring the user information.
		final OAuth2User oAuth2User = super.loadUser(userRequest);
		
		try {
			// for debugging, log the user information.
			// this has to be used only when testing is performed.
			log.info("oAuth2User attributes {} ", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		// getting the login field.
		final String username = (String) oAuth2User.getAttributes().get("login");
		final String authProvider = userRequest.getClientRegistration().getClientName();
		
		User userEntity = null;
		
		// if the user does not exist, create it.
		if (!userRepository.existsByUserName(username)) {
			userEntity = User.builder()
									.userName(username)
									.authProvider(authProvider)
									.build();
			userEntity = userRepository.save(userEntity);
		} else {
			userEntity = userRepository.findByUserName(username)
					.orElseThrow(() -> new EntityNotFoundException("User not found"));;
		}
		
		log.info("Successfully pulled user info username {} authProvider {}", username, authProvider);
		
		userSession.setUsername(username);
		
		return new ApplicationOAuth2User(userEntity.getId(), oAuth2User.getAttributes());
	}
}
