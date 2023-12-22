package com.example.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.blog.domain.Article;
import com.example.blog.domain.ArticleTag;
import com.example.blog.domain.Category;
import com.example.blog.domain.Tag;
import com.example.blog.domain.User;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.persistence.ArticleRepository;
import com.example.blog.persistence.ArticleTagRepository;
import com.example.blog.persistence.CategoryRepository;
import com.example.blog.persistence.TagRepository;
import com.example.blog.persistence.UserRepository;

@ExtendWith(SpringExtension.class)
@Import({ArticleService.class})
public class ArticleServiceTest {
	
	@MockBean
	private ArticleRepository articleRepository;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private CategoryRepository categoryRepository;
	@MockBean
	private TagRepository tagRepository;
	@MockBean
	private ArticleTagRepository articleTagRepository;
	
	@Autowired
	private ArticleService articleService;
	
	// This should be revised; 
	// 	1) 	data setup should be reduced
	//	2) 	use parameterized test; if you have multiple test cases with similar logic, 
	//		consider using parameterized tests to avoid duplicating code
	//	3) 	assertions and verification after assertNotNull()?
	//	4)	Using AssertJ or Hamcrest; IWAAIL.
	@Test
	@DisplayName("Test for getArticlesByCategory(): successful case")
	void getArticlesByCategoryTest() {
		Long categoryId = 10L;
		
		User user = User.builder()
						.id("TestUserID")
						.userName("TestUser")
						.password("TestUser password")
						.email("test@test.com")
						.blogTitle("TestUser's Blog")
						.build();
		
		Category category = Category.builder()
				.id(categoryId)
				.user(user)
				.name("Test Category")
				.build();
		
		Article article1 = Article.builder()
				.id(15L)
				.writer(user)
				.content("Test Content 1")
				.title("Test Title 1")
				.category(category)
				.build();
		
		Article article2 = Article.builder()
				.id(16L)
				.writer(user)
				.content("Test Content 2")
				.title("Test Title 2")
				.category(category)
				.build();
		
		Article article3 = Article.builder()
				.id(17L)
				.writer(user)
				.content("Test Content 3")
				.title("Test Title 3")
				.category(category)
				.build();
		
		Tag tag1 = Tag.builder()
				.id(1L)
				.user(user)
				.name("Test Tag 1")
				.build();

		Tag tag2 = Tag.builder()
				.id(2L)
				.user(user)
				.name("Test Tag 2")
				.build();
		
		Tag tag3 = Tag.builder()
				.id(3L)
				.user(user)
				.name("Test Tag 3")
				.build();
		
		ArticleTag articleTag1 = ArticleTag.builder()
										.article(article1)
										.tag(tag1)
										.build();
		
		ArticleTag articleTag2 = ArticleTag.builder()
										.article(article1)
										.tag(tag2)
										.build();
		
		List<ArticleTag> tagsForArticle1 = new ArrayList<>();
		tagsForArticle1.add(articleTag1);
		tagsForArticle1.add(articleTag2);
		
		ArticleTag articleTag3 = ArticleTag.builder()
										.article(article2)
										.tag(tag2)
										.build();
		
		List<ArticleTag> tagsForArticle2 = new ArrayList<>();
		tagsForArticle2.add(articleTag3);
		
		ArticleTag articleTag4 = ArticleTag.builder()
										.article(article3)
										.tag(tag1)
										.build();
		
		ArticleTag articleTag5 = ArticleTag.builder()
										.article(article3)
										.tag(tag2)
										.build();
								
		ArticleTag articleTag6 = ArticleTag.builder()
										.article(article3)
										.tag(tag3)
										.build();
		
		List<ArticleTag> tagsForArticle3 = new ArrayList<>();
		tagsForArticle3.add(articleTag4);
		tagsForArticle3.add(articleTag5);
		tagsForArticle3.add(articleTag6);

		article1.setTag(tagsForArticle1);
		article2.setTag(tagsForArticle2);
		article3.setTag(tagsForArticle3);
		
		List<Article> articlesBeforeDTO = new ArrayList<>();
		articlesBeforeDTO.add(article1);
		articlesBeforeDTO.add(article2);
		articlesBeforeDTO.add(article3);
		
		when(categoryRepository.findById(categoryId))
			.thenReturn(Optional.of(category));
		when(articleRepository.findAllByCategory(category))
			.thenReturn(articlesBeforeDTO);
		
		List<ArticleDTO> articles = articlesBeforeDTO
				.stream()
				.map(ar -> ArticleDTO.builder()
									.id(ar.getId())
									.writer(ar.getWriter().getUserName())
									.content(ar.getContent())
									.title(ar.getTitle())
									.createdAt(ar.getCreatedAt())
									.updatedAt(ar.getUpdatedAt())
									.build())
				.collect(Collectors.toList());
		
		List<ArticleDTO> articlesFromService = articleService.getArticlesByCategory(categoryId);
		
		assertEquals(articles, articlesFromService);
		
		assertNotNull(articlesFromService);
		
		for (int i = 0; i < 3; ++i) {
			assertEquals(
					articles.get(i).getId(),
					articlesFromService.get(i).getId()
			);
			assertEquals(
					articles.get(i).getWriter(),
					articlesFromService.get(i).getWriter()
			);
			assertEquals(
					articles.get(i).getCategory(),
					articlesFromService.get(i).getCategory()
			);
			assertEquals(
					articles.get(i).getTag(),
					articlesFromService.get(i).getTag()
			);
			assertEquals(
					articles.get(i).getContent(),
					articlesFromService.get(i).getContent()
			);
			assertEquals(
					articles.get(i).getTitle(),
					articlesFromService.get(i).getTitle()
			);
		}
		
		verify(categoryRepository).findById(categoryId);
		verify(articleRepository).findAllByCategory(category);
	}
	

}
