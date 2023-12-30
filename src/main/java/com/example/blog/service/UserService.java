package com.example.blog.service;

import java.io.IOException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.http.HttpEntity;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.blog.domain.User;
import com.example.blog.dto.UserDTO;
import com.example.blog.persistence.UserRepository;
import com.example.blog.security.TokenProvider;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	
	private UserRepository userRepository;
	private TokenProvider tokenProvider;
	private PasswordEncoder passwordEncoder;
	private final Environment env;
	private final RestTemplate restTemplate = new RestTemplate();
	
	public User getUserByCredentials(final String userName, final String password) {
			try {
				final User origUser = userRepository.findByUserName(userName)
						.orElseThrow(() -> new EntityNotFoundException("User not found"));
				if (
					origUser != null &&
						passwordEncoder.matches(
						password,
						origUser.getPassword()
					)
				) return origUser;
				return null;
			} catch (Exception e) {
				throw e;
			}
		}
	
	@Transactional
	public UserDTO createUser(final UserDTO userDTO) {
		User user = User.builder()
				.userName(userDTO.getUserName())
				.password(passwordEncoder.encode(userDTO.getPassword()))
				.email(userDTO.getEmail())
				.blogTitle(userDTO.getUserName() + "'s Blog")
				.build();
		
		if (userRepository.existsByUserName(user.getUserName()))
			throw new RuntimeException("User name already exists");
		
		User savedUser = userRepository.save(user);
		
		UserDTO resultingUserDTO = UserDTO.builder()
				.id(savedUser.getId())
				.userName(savedUser.getUserName())
				.email(savedUser.getEmail())
				.blogTitle(savedUser.getBlogTitle())
				.build();

		return resultingUserDTO;
	}

	public User findUserByUserName(String userName) {
		try {
			return userRepository.findByUserName(userName)
					.orElseThrow(() -> new EntityNotFoundException("User not found"));
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String createToken(User user) throws IOException {
		return tokenProvider.create(user);
	}
	
	public String getBlogTitle(String userName) {
		try {
			return userRepository.findByUserName(userName)
					.orElseThrow(() -> new EntityNotFoundException("User not found"))
					.getBlogTitle();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void socialLogin(String code, String registrationId) {
		String accessToken = getAccessTokenForSocialLogin(code, registrationId);
		System.out.println(accessToken);
	}
	
	// https://darrenlog.tistory.com/40
	// 원래 만들어두었던 JWT process를 응용해서 이 method와 비슷한 기능을 하는 과정을 새로 정의해야 함.
	private String getAccessTokenForSocialLogin(String authorizationCode, String registrationId) {
		String clientId = env.getProperty("oauth2." +registrationId + ".client-id");
		String clientSecret = env.getProperty("oauth2." +registrationId + ".client-secret");
		String redirectUri = env.getProperty("oauth2." +registrationId + ".redirect-uri");
		String tokenUri = env.getProperty("oauth2." +registrationId + ".token-uri");
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", authorizationCode);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("grant_type", "authorization_code");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		HttpEntity entity = new HttpEntity(params, headers);
		
		ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
		JsonNode accessTokenNode = responseNode.getBody();
		return accessTokenNode.get("access_token").asText();
	}

}
