package com.example.blog.service;

import static java.io.File.separator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.example.blog.domain.Article;
import com.example.blog.domain.ArticleTag;
import com.example.blog.domain.Category;
import com.example.blog.domain.File;
import com.example.blog.domain.Tag;
import com.example.blog.domain.User;

/*
	-> 이 class는 service test class들에게 공통적으로 사용되는 method들을 정의해 놓은 class이다.
	원래는 각 test class 내부에 이러한 method들을 여럿 두고 있었지만, 다수의 class에서 중복되는 기능을 하는 
	method들이 많아졌기에 class 내부에 있던 method들을 삭제하고 통합해 이 class로 일괄적으로 관리하는 것으로
	했다.
*/

public class ServiceTestSupporting {
	
	public static final Path path = Path.of(
										"." + separator + 
										"src" + separator +
										"test" + separator + 
										"java" + separator + 
										"com" + separator +
										"example" + separator +
										"blog" + separator +
										"controller" + separator + 
										"cat_selfie.jpg");
	
	public static User buildUser() {
		return User.builder()
					.id("TestUserID")
					.userName("TestUser")
					.password("TestUser password")
					.email("test@test.com")
					.blogTitle("TestUser's Blog")
					.authProvider(null)
					.build();
	}
	
	public static Article buildArticle() {
		List<Long> tagIds = new ArrayList<>();
		tagIds.add(1L);
		
		User writer = buildUser();
		
		Category category = Category.builder()
									.id(1L)
									.user(writer)
									.build();
		
		Article article = Article.builder()
								.id(15L)
								.writer(writer)
								.content("Test Content")
								.title("Test Article")
								.category(category)
								.build();
		
		// Handling tags
		ArticleTag at1 = ArticleTag.builder()
				.id(1L)
				.article(article)
				.build();
		Tag tag1 = Tag.builder()
						.id(1L)
						.user(writer)
						.name("Test Tag 1")
						.build();
		at1.setTag(tag1);
		List<ArticleTag> articleTag1 = new ArrayList<>();
		articleTag1.add(at1);
		tag1.setArticleTag(articleTag1);
		article.setTag(articleTag1);
		
		// Handling the main image
		File file = getFile(writer, article);
				
		article.setMainImage(file);
		
		return article;
	}
	
	public static File getFile(User uploader, Article article) {
		return File.builder()
			.id(1L)
			.fileName("cat_selfie.jpg")
			.uploader(uploader)
			.filePath(path.toString())
			.article(article)
			.build();
	}
	
	/*
		-> buildArticlesForGetByTests()는 3개의 Article을 포함하는 List를 return한다:
			- article1: shaftCategory / user / tag1, tag2 / file1
			- article2: shaftCategory / user / tag1 / file2
			- article3: shartCategory / user / tag1, tag2, tag3 / file3
	*/
	public static List<Article> buildArticlesForGetByTests() {
		User user = buildUser();
		
		Category shaftCategory = Category.builder()
				.id(1L)
				.user(user)
				.name("Test Category")
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
		
		Article article1 = Article.builder()
				.id(15L)
				.writer(user)
				.content("Test Content 1")
				.title("Test Title 1")
				.category(shaftCategory)
				.build();
		Article article2 = Article.builder()
				.id(16L)
				.writer(user)
				.content("Test Content 2")
				.title("Test Title 2")
				.category(shaftCategory)
				.build();
		Article article3 = Article.builder()
				.id(17L)
				.writer(user)
				.content("Test Content 3")
				.title("Test Title 3")
				.category(shaftCategory)
				.build();
		
		// Article1의 tag 처리
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
		
		// Article2의 tag 처리
		ArticleTag articleTag3 = ArticleTag.builder()
										.article(article2)
										.tag(tag1)
										.build();
		List<ArticleTag> tagsForArticle2 = new ArrayList<>();
		tagsForArticle2.add(articleTag3);
		
		// Article3의 tag 처리
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
		
		// file 처리
		File file1 = getFile(user, article1);
		File file2 = getFile(user, article2);
		File file3 = getFile(user, article3);
		
		article1.setMainImage(file1);
		article2.setMainImage(file2);
		article3.setMainImage(file3);
		
		List<Article> articlesBeforeDTO = new ArrayList<>();
		articlesBeforeDTO.add(article1);
		articlesBeforeDTO.add(article2);
		articlesBeforeDTO.add(article3);
		
		return articlesBeforeDTO;
	}
	
}
