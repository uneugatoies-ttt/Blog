package com.example.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.blog.domain.User;
import com.example.blog.dto.UserDTO;
import com.example.blog.security.TokenProvider;
import com.example.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(value = UserController.class)
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
		
		Mockito.when(userService.createUser(userDTO))
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
								
		Mockito.verify(userService).createUser(userDTO);
	}
	
	@Test
	@DisplayName("Case of Successful signin")
	void successfulSigninTest() throws Exception {
		UserDTO userDTO = UserDTO.builder()
							.userName("TestUser")
							.password("TestUser_password")
							.build();
		
		User user = User.builder()
				.id("TestUser's ID")
				.userName("TestUser")
				.password("TestUser_password")
				.email("test@test.com")
				.blogTitle("TestUser's Blog")
				.build();
		
		Mockito.when(userService.getUserByCredentials("TestUser", "TestUser_password"))
				.thenReturn(user);
		
		Mockito.when(userService.createToken(user))
				.thenReturn("TestUser's Token");
		
		ResultActions result = mockMvc.perform(post("/auth/signin")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(userDTO)));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("TestUser's ID"))
				.andExpect(jsonPath("$.userName").value("TestUser"))
				.andDo(print());
		
		Mockito.verify(userService).getUserByCredentials("TestUser", "TestUser_password");
		Mockito.verify(userService).createToken(user);
	}
	
	@Test
	@DisplayName("Case of Failed signin: Invalid Credentials")
	void signinWithInvalidCredentials() throws Exception {
	    // Input with invalid credentials
	    UserDTO userDTO = UserDTO.builder()
	            .userName("NonExistentUser")
	            .password("InvalidPassword")
	            .build();

	    // Mocking to simulate no user found
	    Mockito.when(userService.getUserByCredentials("NonExistentUser", "InvalidPassword"))
	            .thenReturn(null);

	    // Perform the request
	    ResultActions result = mockMvc.perform(post("/auth/signin")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(userDTO)));

	    // Assertions
	    result.andExpect(status().isNotFound());

	    // Verifications
	    Mockito.verify(userService).getUserByCredentials("NonExistentUser", "InvalidPassword");
	    Mockito.verifyNoMoreInteractions(userService);
	}

}
