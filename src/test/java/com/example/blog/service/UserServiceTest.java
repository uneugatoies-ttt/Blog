package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.blog.domain.User;
import com.example.blog.dto.UserDTO;
import com.example.blog.persistence.UserRepository;
import com.example.blog.security.TokenProvider;

@ExtendWith(SpringExtension.class)
@Import({UserService.class})
public class UserServiceTest {
	
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private TokenProvider tokenProvider;
	@MockBean
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserService userService;
	
	@Test
	@DisplayName("Test for getUserByCredentials(): successful case")
	void getUserByCredentialsTest() throws Exception {
		String userName = "TestUser";
		String password = "TestUserPassword";
		String encodedPassword = "EncodedTestUserPassword";
		User origUser = User.builder()
							.id("TestUserID")
							.userName(userName)
							.password(encodedPassword)
							.email("testuser@test.com")
							.authProvider(null)
							.blogTitle("TestUser's Blog")
							.build();
		UserDTO responseUserDTO = UserDTO.builder()
										.id("TestUserID")
										.userName(userName)
										.token("TestUserToken")
										.build();
		
		when(userRepository.findByUserName(userName))
			.thenReturn(Optional.of(origUser));
		when(passwordEncoder.matches(password, encodedPassword))
			.thenReturn(true);
		when(tokenProvider.create(origUser))
			.thenReturn("TestUserToken");
		
		UserDTO responseUserDTOFromService = userService.getUserByCredentials(userName, password);
		
		assertThat(responseUserDTOFromService)
			.isNotNull();
		assertThat(responseUserDTOFromService.getId())
			.isEqualTo(responseUserDTO.getId());
		assertThat(responseUserDTOFromService.getUserName())
			.isEqualTo(responseUserDTO.getUserName());
		assertThat(responseUserDTOFromService.getToken())
			.isEqualTo(responseUserDTO.getToken());
		
		verify(userRepository).findByUserName(userName);
		verify(passwordEncoder).matches(password, encodedPassword);
		verify(tokenProvider).create(origUser);
	}
	
	@Test
	@DisplayName("Test for createUser(): successful case")
	void createUserTest() throws Exception {
		UserDTO userDTO = UserDTO.builder()
								.userName("TestUser")
								.password("TestUserPassword")
								.email("testuser@test.com")
								.build();
		User savedUser = User.builder()
								.id("TestUserID")
								.userName(userDTO.getUserName())
								.password("EncodedTestUserPassword")
								.email(userDTO.getEmail())
								.blogTitle(userDTO.getBlogTitle())
								.build();
		UserDTO resultingUserDTO = UserDTO.builder()
										.id(savedUser.getId())
										.userName(savedUser.getUserName())
										.email(savedUser.getEmail())
										.blogTitle(savedUser.getBlogTitle())
										.build();
		
		when(passwordEncoder.encode(userDTO.getPassword()))
			.thenReturn("EncodedTestUserPassword");
		when(userRepository.existsByUserName(userDTO.getUserName()))
			.thenReturn(false);
		when(userRepository.save(any(User.class)))
			.thenReturn(savedUser);
		
		UserDTO resultingUserDTOFromService = userService.createUser(userDTO);
		
		assertThat(resultingUserDTOFromService)
			.isNotNull();
		assertThat(resultingUserDTOFromService.getId())
			.isEqualTo(resultingUserDTO.getId());
		assertThat(resultingUserDTOFromService.getUserName())
			.isEqualTo(resultingUserDTO.getUserName());
		assertThat(resultingUserDTOFromService.getEmail())
			.isEqualTo(resultingUserDTO.getEmail());
		assertThat(resultingUserDTOFromService.getBlogTitle())
			.isEqualTo(resultingUserDTO.getBlogTitle());
		
		verify(passwordEncoder).encode(userDTO.getPassword());
		verify(userRepository).existsByUserName(userDTO.getUserName());
		verify(userRepository).save(any(User.class));
	}

	@Test
	@DisplayName("Test for getBlogTitleByUserName(): successful case")
	void getBlogTitleByUserNameTest() throws Exception {
		String userName = "TestUser";
		User foundUser = User.builder()
						.id("TestUserID")
						.userName(userName)
						.password("EncodedTestUserPassword")
						.email("testuser@test.com")
						.blogTitle("TestUser's Blog")
						.build();
		
		when(userRepository.findByUserName(userName))
			.thenReturn(Optional.of(foundUser));
		
		String blogTitle = userService.getBlogTitleByUserName(userName);
		
		assertThat(blogTitle)
			.isEqualTo(foundUser.getBlogTitle());
			
		verify(userRepository).findByUserName(userName);
	}
	
	/*
	@Test
	@DisplayName("Test for findUserByUserName(): successful case")
	void findUserByUserNameTest() throws Exception {
		String userName = "TestUser";
		User foundUser = User.builder()
						.id("TestUserID")
						.userName(userName)
						.password("EncodedTestUserPassword")
						.email("testuser@test.com")
						.blogTitle("TestUser's Blog")
						.build();
		
		when(userRepository.findByUserName(userName))
			.thenReturn(Optional.of(foundUser));
		
		User savedUserFromService = userService.findUserByUserName(userName);
		
		assertThat(savedUserFromService)
			.isNotNull();
		assertThat(savedUserFromService.getId())
			.isEqualTo(foundUser.getId());
		assertThat(savedUserFromService.getUserName())
			.isEqualTo(foundUser.getUserName());
		assertThat(savedUserFromService.getPassword())
			.isEqualTo(foundUser.getPassword());
		assertThat(savedUserFromService.getEmail())
			.isEqualTo(foundUser.getEmail());
		assertThat(savedUserFromService.getBlogTitle())
			.isEqualTo(foundUser.getBlogTitle());
	}*/
	
}
