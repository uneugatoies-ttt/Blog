package com.example.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private ArticleService articleService;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@DisplayName("")
	void getArticlesForThisUserTest() throws Exception {
		List<Long> tag1 = new ArrayList<>();
		tag1.add(3L);
		tag1.add(5L);
		tag1.add(6L);
		List<ArticleDTO> articles = new ArrayList<>();
		articles.add(ArticleDTO.builder()
								.id(1365L)
								.writer("TestUser")
								.content("Test Article 1 Content")
								.title("Test Article 1")
								.category(35L)
								.tag(tag1)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1366L)
								.writer("TestUser")
								.content("Test Article 2 Content")
								.title("Test Article 2")
								.category(46L)
								.tag(tag1)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1367L)
								.writer("TestUser")
								.content("Test Article 3 Content")
								.title("Test Article 3")
								.category(38L)
								.tag(tag1)
								.build());
						
		Mockito.when(articleService.getArticlesForThisUser("TestUser"))
					.thenReturn(articles);
		
		
		ResultActions result = mockMvc.perform(get("/article/by-user?userName=TestUser")
						.contentType(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk())
				.andExpect()
		
		
		
		
		Mockito.verify(articleService).getArticlesForThisUser("TestUser");
	}
	

}
