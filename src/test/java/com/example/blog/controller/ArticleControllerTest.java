package com.example.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.security.TokenProvider;
import com.example.blog.service.ArticleService;

/*
	> TokenProvider의 MockBean을 제공하지 않으면 그것의 bean을 찾지 못했다는 error가 계속해서 발생.
	당분간은 각 test class 내부에서 이것을 @MockBean으로 추가; 해결 방법 탐색할 것.
*/

@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private ArticleService articleService;
	@MockBean
	private TokenProvider tokenProvider;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@DisplayName("Test for getAticlesForThisUser()")
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
						
		when(articleService.getArticlesForThisUser("TestUser"))
					.thenReturn(articles);
		
		
		ResultActions result = mockMvc.perform(get("/article/by-user")
						.param("userName", "TestUser"));
						
		// Loop를 사용할 수 있다면 좋겠지만.
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data", hasSize(3)))		
				.andExpect(jsonPath("$.data[0].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[0].title").value("Test Article 1"))
				.andExpect(jsonPath("$.data[0].category").value(35L))
				.andExpect(jsonPath("$.data[0].tag").exists())
				.andExpect(jsonPath("$.data[1].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[1].title").value("Test Article 2"))
				.andExpect(jsonPath("$.data[1].category").value(46L))
				.andExpect(jsonPath("$.data[1].tag").exists())
				.andExpect(jsonPath("$.data[2].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[2].title").value("Test Article 3"))
				.andExpect(jsonPath("$.data[2].category").value(38L))
				.andExpect(jsonPath("$.data[2].tag").exists())
				.andDo(print());
		
		verify(articleService).getArticlesForThisUser("TestUser");
	}
	
	@Test
	@DisplayName("Test for getArticlesByCategory()")
	void getArticlesByCategoryTest() throws Exception {
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
								.category(100L)
								.tag(tag1)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1366L)
								.writer("TestUser")
								.content("Test Article 2 Content")
								.title("Test Article 2")
								.category(100L)
								.tag(tag1)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1367L)
								.writer("TestUser")
								.content("Test Article 3 Content")
								.title("Test Article 3")
								.category(100L)
								.tag(tag1)
								.build());
						
		when(articleService.getArticlesByCategory(100L))
					.thenReturn(articles);
		
		ResultActions result = mockMvc.perform(get("/article/by-category")
						.param("categoryId", "100"));
						
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data", hasSize(3)))
				.andExpect(jsonPath("$.data[0].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[0].title").value("Test Article 1"))
				.andExpect(jsonPath("$.data[0].category").value(100L))
				.andExpect(jsonPath("$.data[0].tag").exists())
				.andExpect(jsonPath("$.data[1].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[1].title").value("Test Article 2"))
				.andExpect(jsonPath("$.data[1].category").value(100L))
				.andExpect(jsonPath("$.data[1].tag").exists())
				.andExpect(jsonPath("$.data[2].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[2].title").value("Test Article 3"))
				.andExpect(jsonPath("$.data[2].category").value(100L))
				.andExpect(jsonPath("$.data[2].tag").exists())
				.andDo(print());
		
		verify(articleService).getArticlesByCategory(100L);
	}
	
	@Test
	@DisplayName("Test for getArticlesByTag()")
	void getArticlesByTagTest() throws Exception {
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
								.category(59L)
								.tag(tag1)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1366L)
								.writer("TestUser")
								.content("Test Article 2 Content")
								.title("Test Article 2")
								.category(33L)
								.tag(tag1)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1367L)
								.writer("TestUser")
								.content("Test Article 3 Content")
								.title("Test Article 3")
								.category(82L)
								.tag(tag1)
								.build());
						
		when(articleService.getArticlesByTag(6L))
					.thenReturn(articles);
		
		ResultActions result = mockMvc.perform(get("/article/by-tag")
						.param("tagId", "6"));
						
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data", hasSize(3)))
				.andExpect(jsonPath("$.data[0].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[0].title").value("Test Article 1"))
				.andExpect(jsonPath("$.data[0].category").value(59L))
				.andExpect(jsonPath("$.data[0].tag").exists())
				.andExpect(jsonPath("$.data[1].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[1].title").value("Test Article 2"))
				.andExpect(jsonPath("$.data[1].category").value(33L))
				.andExpect(jsonPath("$.data[1].tag").exists())
				.andExpect(jsonPath("$.data[2].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[2].title").value("Test Article 3"))
				.andExpect(jsonPath("$.data[2].category").value(82L))
				.andExpect(jsonPath("$.data[2].tag").exists())
				.andDo(print());
		
		verify(articleService).getArticlesByTag(6L);
	}
	
	/*
	@Test
	@DisplayName("Test for createArticle()")
	void createArticleTest() throws Exception {
		List<Long> tags = new ArrayList<>();
		tags.add(3L);
		tags.add(5L);
		tags.add(6L);
		
		ArticleDTO articleDTO = ArticleDTO.builder()
							.writer("TestUser")
							.content("Test Content")
							.title("Test Title")
							.category(16L)
							.tag(tags)
							.build();
		
		LocalDateTime now = LocalDateTime.now();
		
		when(articleService.createOrEditArticle(articleDTO))
					.thenReturn(ArticleDTO.builder()
									.id(3019L)
									.writer("TestUser")
									.content("Test Content")
									.title("Test Title")
									.category(16L)
									.tag(tags)
									.createdAt(now)
									.updatedAt(now)
									.build());
		
		ResultActions result = mockMvc.perform(post("/article")
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(articleDTO)));
	
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(3019L))
				.andExpect(jsonPath("$.writer").value("TestUser"))
				.andExpect(jsonPath("$.content").value("Test Content"))
				.andExpect(jsonPath("$.title").value("Test Title"))
				.andExpect(jsonPath("$.category").value(16L))
				.andExpect(jsonPath("$.tag").exists())
				.andExpect(jsonPath("$.createdAt").exists())
				.andExpect(jsonPath("$.updatedAt").exists())
				.andDo(print());
		
		verify(articleService).createOrEditArticle(articleDTO);
	}
	
	@Test
	@DisplayName("Test for editArticle()")
	void editArticleTest() throws Exception {
		List<Long> tags = new ArrayList<>();
		tags.add(58L);
		tags.add(75L);
		tags.add(9L);
		
		ArticleDTO articleDTO = ArticleDTO.builder()
							.id(3058L)
							.writer("TestUser")
							.content("Test Content Modified")
							.title("Test Title")
							.category(16L)
							.tag(tags)
							.build();
		
		LocalDateTime createdAt = LocalDateTime.of(2022, 9, 17, 7, 3);
		LocalDateTime now = LocalDateTime.now();
		
		when(articleService.createOrEditArticle(articleDTO))
					.thenReturn(ArticleDTO.builder()
									.id(3058L)
									.writer("TestUser")
									.content("Test Content Modified")
									.title("Test Title")
									.category(16L)
									.tag(tags)
									.createdAt(createdAt)
									.updatedAt(now)
									.build());
		
		ResultActions result = mockMvc.perform(put("/article")
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(articleDTO)));
	
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(3058L))
				.andExpect(jsonPath("$.writer").value("TestUser"))
				.andExpect(jsonPath("$.content").value("Test Content Modified"))
				.andExpect(jsonPath("$.title").value("Test Title"))
				.andExpect(jsonPath("$.category").value(16L))
				.andExpect(jsonPath("$.tag").exists())
				.andExpect(jsonPath("$.createdAt").exists())
				.andExpect(jsonPath("$.updatedAt").exists())
				.andDo(print());
		
		verify(articleService).createOrEditArticle(articleDTO);
	}*/
	
	@Test
	@DisplayName("Test for deleteArticle()")
	void deleteArticleTest() throws Exception {
		Long articleId = 10L;
		
		ResultActions result = mockMvc.perform(delete("/article")
						.param("articleId", String.valueOf(articleId)));
		
		result.andExpect(status().isNoContent());
		
		verify(articleService).deleteArticle(articleId);
	}

}
