package com.example.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.blog.dto.UserDTO;
import com.example.blog.security.TokenProvider;
import com.example.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private UserService userService;
	@MockBean
	private TokenProvider tokenProvider;
	@MockBean
	private PasswordEncoder encoder;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@DisplayName("Test for signup()")
	void signupTest() throws Exception {
		UserDTO userDTO = UserDTO.builder()
							.userName("TestUser")
							.password("TestUser_password")
							.email("test@test.com")
							.build();
		
		when(userService.createUser(userDTO))
				.thenReturn(UserDTO.builder()
								.id("TestUser's ID")
								.userName("TestUser")
								.email("test@test.com")
								.blogTitle("TestUser's Blog")
								.build());
		
		ResultActions result = mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userDTO)));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("TestUser's ID"))
				.andExpect(jsonPath("$.userName").value("TestUser"))
				.andExpect(jsonPath("$.email").value("test@test.com"))
				.andExpect(jsonPath("$.blogTitle").value("TestUser's Blog"))
				.andDo(print());
								
		verify(userService).createUser(userDTO);
	}
	
	@Test
	@DisplayName("Test for signin(): successful case")
	void successfulSigninTest() throws Exception {
		UserDTO userDTO = UserDTO.builder()
							.userName("TestUser")
							.password("TestUserPassword")
							.build();
		
		/*
		User user = User.builder()
				.id("TestUserID")
				.userName("TestUser")
				.password("TestUserPassword")
				.email("test@test.com")
				.blogTitle("TestUser's Blog")
				.build();
		*/
		
		UserDTO responseUserDTO = UserDTO.builder()
								.id("TestUserID")
								.userName("TestUser")
								.token("TestUserToken")
								.build();
		
		when(userService.getUserByCredentials("TestUser", "TestUserPassword"))
				.thenReturn(responseUserDTO);
		
		ResultActions result = mockMvc.perform(post("/auth/signin")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(userDTO)));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("TestUserID"))
				.andExpect(jsonPath("$.userName").value("TestUser"))
				.andExpect(jsonPath("$.token").value("TestUserToken"))
				.andDo(print());
		
		verify(userService).getUserByCredentials("TestUser", "TestUserPassword");
		//verify(userService).createToken(user);
	}
	
	@Test
	@DisplayName("Test for signin(): the case of invalid credentials")
	void signinWithInvalidCredentials() throws Exception {
	    UserDTO userDTO = UserDTO.builder()
	            .userName("NonExistentUser")
	            .password("InvalidPassword")
	            .build();

	    when(userService.getUserByCredentials("NonExistentUser", "InvalidPassword"))
	            .thenReturn(null);

	    ResultActions result = mockMvc.perform(post("/auth/signin")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(userDTO)));

	    result.andExpect(status().isNotFound());

	    verify(userService).getUserByCredentials("NonExistentUser", "InvalidPassword");
	    verifyNoMoreInteractions(userService);
	}

}
