package com.example.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.blog.security.TokenProvider;
import com.example.blog.service.UserService;

@WebMvcTest(BlogController.class)
public class BlogControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private UserService userService;
	@MockBean
	private TokenProvider tokenProvider;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@DisplayName("Test for getBlogTitle()")
	void getBlogTitleTest() throws Exception {
		String userName = "TestUser";
		String title = "TestUser's Blog";
		
		when(userService.getBlogTitle(userName))
				.thenReturn(title);
		
		ResultActions result = mockMvc.perform(get("/blog/title")
								.param("userName", userName));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value(title))
				.andDo(print());
		
		verify(userService).getBlogTitle(userName);
	}

}
