package com.example.blog.security;

import javax.persistence.EntityNotFoundException;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.blog.common.UserSession;
import com.example.blog.domain.User;
import com.example.blog.persistence.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/*
	-> OAuthUserServiceImpl은 GitHub나 Google에서 return된 user information이
	이 application의 database 내부에 있는지의 여부를 확인하기 위해서 존재한다.
	만약 이 확인 과정에서 해당하는 user가 없다면, 새로운 account가 만들어진다.
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
		/* 
			DefaultOAuth2UserService의 존재하는 loadUser를 call한다.
			이 method는 "user-info-uri"를 사용해 user information을 가져온다.
		*/
		final OAuth2User oAuth2User = super.loadUser(userRequest);
		
		try {
			// debugging을 위해서 user information을 log한다.
			log.info("oAuth2User attributes {} ", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		final String authProvider = userRequest.getClientRegistration().getClientName();

		String username = null;
		if (authProvider.equals("GitHub")) {
			// "login" field를 가져온다; 모든 whitespace를 -로 대체한다.
			username = ((String) oAuth2User.getAttributes().get("login")).replaceAll(" ", "-");
		} else if (authProvider.equals("Google")) {
			// Google의 OAuth2 flow에서는 "login" field가 존재하지 않기 때문에,
			// "name" field를 대신해서 가져와야 한다; 역시 모든 whitespace를 -로 대체한다.
			username = ((String) oAuth2User.getAttributes().get("name")).replaceAll(" ", "-");
		}
		
		if (username == null)
			throw new RuntimeException("username is null");
		
		User userEntity = null;
		
		// 만약 user가 존재하지 않는다면 새롭게 만들어 추가한다.
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
		
		userSession.setUsername(username);
		
		return new ApplicationOAuth2User(userEntity.getId(), oAuth2User.getAttributes());
	}
}
