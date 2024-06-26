package com.example.blog.service;

import java.io.IOException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.blog.domain.User;
import com.example.blog.dto.CheckUserDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.persistence.UserRepository;
import com.example.blog.security.TokenProvider;

import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;

// UserService는 UserController의 dependency로 사용되어 User와 관련된 logic을 수행하는 service이다.
@Service
@AllArgsConstructor
public class UserService {
	
	private UserRepository userRepository;
	private TokenProvider tokenProvider;
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public UserDTO getUserByCredentials(
			final String userName,
			final String password
	) throws IOException {
		try {
			final User origUser = userRepository.findByUserName(userName)
					.orElseThrow(() -> new EntityNotFoundException("User not found"));
			if (
				origUser == null ||
				!passwordEncoder.matches(
					password,
					origUser.getPassword()
				)
			) return null;
			
			final String token = createToken(origUser);
			final UserDTO responseUserDTO = UserDTO.builder()
											.id(origUser.getId())
											.userName(origUser.getUserName())
											.token(token)
											.build();
			return responseUserDTO;
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

	@Transactional
	public String getBlogTitleByUserName(String userName) {
		try {
			return userRepository.findByUserName(userName)
					.orElseThrow(() -> new EntityNotFoundException("User not found"))
					.getBlogTitle();
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional
	public boolean checkThisUser(CheckUserDTO checkUserDTO) throws IOException {
		try {
			String pathID = userRepository.findByUserName(checkUserDTO.getPathUserName())
						.orElseThrow(() -> new EntityNotFoundException("User not found"))
						.getId();
			String notCertifiedID = tokenProvider.validateAndGetUserId(checkUserDTO.getNotCertifiedToken());
			
			if (!pathID.equals(notCertifiedID))
				return false;
			
			return true;
		} catch (JwtException jwtException) {
			return false;
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public String deleteUser(UserDTO userDTO) throws IOException {
		try {
			CheckUserDTO checkUserDTO = CheckUserDTO.builder()
											.pathUserName(userDTO.getUserName())
											.notCertifiedUserName(userDTO.getUserName())
											.notCertifiedToken(userDTO.getToken())
											.build();
			
			if (!this.checkThisUser(checkUserDTO)) {
				throw new RuntimeException("Something went wrong");
			}
			
			User existingUser = userRepository.findByUserName(userDTO.getUserName())
					.orElseThrow(() -> new EntityNotFoundException("User not found"));
			
			if (
					existingUser == null 	||
					!passwordEncoder.matches(userDTO.getPassword(), existingUser.getPassword())
			) return null;
		
			userRepository.deleteById(existingUser.getId());
			
			return "User deleted successfully";
		} catch (Exception e) {
			throw e;
		}
	}
	
	/********************************************
	  											
	  		private methods					
	  											
	 ********************************************/
	
	private String createToken(User user) throws IOException {
		return tokenProvider.create(user);
	}

}
