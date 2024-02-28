package com.example.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.hasSize;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.FileDTO;
import com.example.blog.security.TokenProvider;
import com.example.blog.service.ArticleService;
import com.example.blog.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
	-> 하술한 문제의 원인이 아직 명확하지 않기 때문에 최대한 저것들을 피해서 test code를 작성하려고 했다.
	이후 해결법을 알아낸다면 그에 따른 변경과 추가를 하도록 하겠다.
	
	-> "Failed to Load ApplicationContext" 문제:
		-> TokenProvider의 MockBean을 제공하지 않으면 그것의 bean을 찾지 못했다는 error가 계속해서 발생.
		당분간은 각 test class 내부에서 이것을 @MockBean으로 추가; 해결 방법 탐색할 것.
	
		-> RedirectUriSession을 추가한 이후, 이에 대해서도 비슷한 문제가 발생:
		Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException:
		No qualifying bean of type 'com.example.blog.misc.RedirectUriSession' available:
		expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
	
		-> ObjectMapper와 관련되어서도 비슷한 문제가 발생:
		만약 이것을 @MockBean으로 등록하고 @Autowired로 등록하지 않으면 다음과 같은 message를 표시함: 
		Caused by: org.springframework.beans.factory.BeanCreationException: 
		Error creating bean with name 'requestMappingHandlerAdapter' defined
		in class path resource
		[org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration$EnableWebMvcConfiguration.class]:
		Bean instantiation via factory method failed;
		nested exception is org.springframework.beans.BeanInstantiationException:
		Failed to instantiate
		[org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter]:
		Factory method 'requestMappingHandlerAdapter' threw exception;
		nested exception is java.lang.NullPointerException:
		Cannot invoke "com.fasterxml.jackson.databind.ObjectReader.forType(java.lang.Class)"
		because the return value of "com.fasterxml.jackson.databind.ObjectMapper.reader()" is null
		
		TokenProvider나 RedirectUriSession과 다른 점은, 만약 ObjectMapper를 아예 사용하지 않으면 괜찮다는 것이다.
		하지만 이것을 @MockBean을 통해서 사용하려고 하면 문제가 발생한다; 오로지 @Autowired를 통해서만 사용이 가능하다.
		@Autowired와 @MockBean을 동시에 사용하려고 해도 동일한 문제가 발생한다.
		
	(24-02-28)
	-> RedirectUriSession class 자체를 삭제한 이후로는 이와 관련된 context 문제가 사라졌다.
	session 기능과 모종의 관련이 있으리라 예측하지만, 아직 정확히 알 수 없다.
*/

@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ArticleService articleService;
	@MockBean
	private FileService fileService;
	@MockBean
	private TokenProvider tokenProvider;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

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
		byte[] testFileBytes = Files.readAllBytes(Path.of(
													"." + File.separator + 
													"src" + File.separator +
													"test" + File.separator + 
													"java" + File.separator + 
													"com" + File.separator +
													"example" + File.separator +
													"blog" + File.separator +
													"controller" + File.separator + 
													"cat_selfie.jpg"
												));
		MockMultipartFile file =
				new MockMultipartFile(
						"file",
						"cat_selfie.jpg",
						"image/jpeg",
						testFileBytes
				);
		MockMultipartFile articleDTOJson =
				new MockMultipartFile(
						"articleDTO",
						"",
						"application/json",
						objectMapper.writeValueAsBytes(articleDTO)
				);
		
		ArticleDTO resultingArticleDTO = ArticleDTO.builder()
												.id(3019L)
												.writer("TestUser")
												.content("Test Content")
												.title("Test Title")
												.category(16L)
												.tag(tags)
												.createdAt(now)
												.updatedAt(now)
												.build();
		FileDTO resultingFileDTO = FileDTO.builder()
										.id(500L)
										.fileName("cat-selfie.jpg")
										.uploader("TestUser")
										.createdAt(now)
										.articleId(3019L)
										.build();
		
		when(articleService.createOrEditArticle(any(ArticleDTO.class), any(MultipartFile.class)))
			.thenReturn(new SimpleImmutableEntry<>(resultingArticleDTO, resultingFileDTO));
		
		ResultActions result = 
				mockMvc.perform(MockMvcRequestBuilders.multipart("/article")
						.file(file)
						.file(articleDTOJson));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Article inserted successfully"))
				.andDo(print());
		
		verify(articleService, times(1)).createOrEditArticle(any(ArticleDTO.class), any(MultipartFile.class));
	}
	
	@Test
	@DisplayName("Test for getArticleById()")
	void getArticlesByIdTest() throws Exception {
		Long articleId = 10L;
		List<Long> tags = new ArrayList<>();
		tags.add(3L);
		tags.add(5L);
		tags.add(6L);
		LocalDateTime now = LocalDateTime.now();
		ArticleDTO resultingArticleDTO = ArticleDTO.builder()
											.id(articleId)
											.writer("TestUser")
											.content("Test Content")
											.title("Test Title")
											.category(16L)
											.tag(tags)
											.createdAt(now)
											.updatedAt(now)
											.build();
		
		when(articleService.getArticleById(articleId))
			.thenReturn(resultingArticleDTO);
		
		ResultActions result = mockMvc.perform(
											get("/article/by-id")
												.param("articleId", String.valueOf(articleId))
											);
						
		result.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(articleId))
			.andExpect(jsonPath("$.writer").value("TestUser"))
			.andExpect(jsonPath("$.content").value("Test Content"))
			.andExpect(jsonPath("$.title").value("Test Title"))
			.andExpect(jsonPath("$.category").value(16L))
			.andExpect(jsonPath("$.tag").exists())
			.andExpect(jsonPath("$.createdAt").exists())
			.andDo(print());
	}
	
	
	@Test
	@DisplayName("Test for getArticlesForThisUser()")
	void getArticlesForThisUserTest() throws Exception {
		List<Long> tags = new ArrayList<>();
		tags.add(3L);
		tags.add(5L);
		tags.add(6L);
		Long[] categories = new Long[3];
		categories[0] = 35L;
		categories[1] = 46L;
		categories[2] = 38L;
		
		List<ArticleDTO> articles = new ArrayList<>();
		articles.add(ArticleDTO.builder()
								.id(1365L)
								.writer("TestUser")
								.content("Test Article 1 Content")
								.title("Test Article 1")
								.category(categories[0])
								.tag(tags)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1366L)
								.writer("TestUser")
								.content("Test Article 2 Content")
								.title("Test Article 2")
								.category(categories[1])
								.tag(tags)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1367L)
								.writer("TestUser")
								.content("Test Article 3 Content")
								.title("Test Article 3")
								.category(categories[2])
								.tag(tags)
								.build());
						
		when(articleService.getArticlesForThisUser("TestUser"))
					.thenReturn(articles);
		
		
		ResultActions result = mockMvc.perform(get("/article/by-user")
						.param("userName", "TestUser"));
		
	
						
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data", hasSize(3)));
		for (int i = 0; i < 3; ++i) {
			result
				.andExpect(jsonPath("$.data[" + i + "].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[" + i + "].title").value("Test Article " + (i + 1)))
				.andExpect(jsonPath("$.data[" + i + "].content").value("Test Article " + (i + 1) + " Content"))
				.andExpect(jsonPath("$.data[" + i + "].category").value(categories[i]))
				.andExpect(jsonPath("$.data[" + i + "].tag").exists());
		}
		result
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
				.andExpect(jsonPath("$.data", hasSize(3)));
		for (int i = 0; i < 3; ++i) {
			result
				.andExpect(jsonPath("$.data[" + i + "].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[" + i + "].title").value("Test Article " + (i + 1)))
				.andExpect(jsonPath("$.data[" + i + "].content").value("Test Article " + (i + 1) + " Content"))
				.andExpect(jsonPath("$.data[" + i + "].category").value(100L))
				.andExpect(jsonPath("$.data[" + i + "].tag").exists());
		}
		result
				.andDo(print());
		
		verify(articleService).getArticlesByCategory(100L);
	}
	
	@Test
	@DisplayName("Test for getArticlesByTag()")
	void getArticlesByTagTest() throws Exception {
		List<Long> tags = new ArrayList<>();
		tags.add(3L);
		tags.add(5L);
		tags.add(6L);
		Long[] categories = new Long[3];
		categories[0] = 35L;
		categories[1] = 46L;
		categories[2] = 38L;
		
		List<ArticleDTO> articles = new ArrayList<>();
		articles.add(ArticleDTO.builder()
								.id(1365L)
								.writer("TestUser")
								.content("Test Article 1 Content")
								.title("Test Article 1")
								.category(categories[0])
								.tag(tags)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1366L)
								.writer("TestUser")
								.content("Test Article 2 Content")
								.title("Test Article 2")
								.category(categories[1])
								.tag(tags)
								.build());
		articles.add(ArticleDTO.builder()
								.id(1367L)
								.writer("TestUser")
								.content("Test Article 3 Content")
								.title("Test Article 3")
								.category(categories[2])
								.tag(tags)
								.build());
						
		when(articleService.getArticlesByTag(6L))
					.thenReturn(articles);
		
		ResultActions result = mockMvc.perform(get("/article/by-tag")
										.param("tagId", "6"));
						
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data", hasSize(3)));
		for (int i = 0; i < 3; ++i) {
			result
				.andExpect(jsonPath("$.data[" + i + "].writer").value("TestUser"))
				.andExpect(jsonPath("$.data[" + i + "].title").value("Test Article " + (i + 1)))
				.andExpect(jsonPath("$.data[" + i + "].content").value("Test Article " + (i + 1) + " Content"))
				.andExpect(jsonPath("$.data[" + i + "].category").value(categories[i]))
				.andExpect(jsonPath("$.data[" + i + "].tag").exists());
		}
		result
				.andDo(print());
		
		verify(articleService).getArticlesByTag(6L);
	}
	
	@Test
	@DisplayName("Test for editArticle()")
	void editArticleTest() throws Exception {
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
		byte[] testFileBytes = Files.readAllBytes(Path.of(
													"." + File.separator + 
													"src" + File.separator +
													"test" + File.separator + 
													"java" + File.separator + 
													"com" + File.separator +
													"example" + File.separator +
													"blog" + File.separator +
													"controller" + File.separator + 
													"cat_selfie.jpg"
												));
		MockMultipartFile file =
				new MockMultipartFile(
						"file",
						"cat_selfie.jpg",
						"image/jpeg",
						testFileBytes
				);
		MockMultipartFile articleDTOJson =
				new MockMultipartFile(
						"articleDTO",
						"",
						"application/json",
						objectMapper.writeValueAsBytes(articleDTO)
				);
		
		ArticleDTO resultingArticleDTO = ArticleDTO.builder()
												.id(3019L)
												.writer("TestUser")
												.content("Test Content")
												.title("Test Title")
												.category(16L)
												.tag(tags)
												.createdAt(now)
												.updatedAt(now)
												.build();
		FileDTO resultingFileDTO = FileDTO.builder()
										.id(500L)
										.fileName("cat-selfie.jpg")
										.uploader("TestUser")
										.createdAt(now)
										.articleId(3019L)
										.build();
		
		when(articleService.createOrEditArticle(any(ArticleDTO.class), any(MultipartFile.class)))
			.thenReturn(new SimpleImmutableEntry<>(resultingArticleDTO, resultingFileDTO));
		
		ResultActions result = 
				mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/article")
						.file(file)
						.file(articleDTOJson));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Article modified successfully"))
				.andDo(print());
		
		verify(articleService, times(1)).createOrEditArticle(any(ArticleDTO.class), any(MultipartFile.class));
	}
	
	@Test
	@DisplayName("Test for deleteArticle()")
	void deleteArticleTest() throws Exception {
		Long articleId = 10L;
		
		ResultActions result = mockMvc.perform(delete("/article")
									.param("articleId", String.valueOf(articleId)));
		
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").value("Article deleted successfully"));
		
		verify(articleService).deleteArticle(articleId);
	}

}
