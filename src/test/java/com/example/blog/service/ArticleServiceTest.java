package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.io.File.separator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.assertj.core.api.Assertions;

import com.example.blog.domain.Article;
import com.example.blog.domain.ArticleTag;
import com.example.blog.domain.Category;
import com.example.blog.domain.Tag;
import com.example.blog.domain.User;
import com.example.blog.domain.File;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.FileDTO;
import com.example.blog.persistence.ArticleRepository;
import com.example.blog.persistence.ArticleTagRepository;
import com.example.blog.persistence.CategoryRepository;
import com.example.blog.persistence.FileRepository;
import com.example.blog.persistence.ReplyRepository;
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
	@MockBean
	private FileRepository fileRepository;
	@MockBean
	private ReplyRepository replyRepository;
	@MockBean
	private FileService fileService;
	
	@Autowired
	private ArticleService articleService;

	/*
		-> 원래 이 Article은 Tag를 네 개 가지는 것으로 하려고 했지만 문제가 생겼기에
		일단은 그것이 해결될 때까지 단일 Tag만 사용해서 test를 하도록 한다.
		문제 상황은 이렇다:
		createOrEditArticle()이 그 내부에서 다른 private method인 setTagsForArticle()을
		call할 때 전달된 Tag들의 ID로 loop를 돌리는데, 이 때 만들어지는 ArticleTag object들에 대해서
		when()을 어떻게 하면 적용시켜서 각 loop의 경우에 대한 return value를 특정할 수 있는지
		알지 못했다.
		만약 any(ArticleTag.class)를 사용할 때 각 경우에 대해 return value를 다르게 할 수 있다면
		이 문제는 확실히 해결될 것이다. 하지만 아직 그것이 가능한지, 가능하다면 어떻게 해야 하는지 알지 못한다.
		
		-> 현재 이 test method는 ArticleService의 createOrEditArticle()과
		setTagsForArticle()을 동시에 사용하고 있지만, 이것이 과연 좋은 방식인지는 미상이다.
	*/
	@Test
	@DisplayName("Test for createOrEditArticle(): creating a new article")
	void createOrEditArticleTest() throws Exception {
		Article article = buildArticle();
		User writer = article.getWriter();
		Category category = article.getCategory();
		List<ArticleTag> articleTag = article.getTag();
		Tag tag = articleTag.get(0).getTag();
		ArticleTag at = articleTag.get(0);
		
		byte[] testFileBytes = Files.readAllBytes(Path.of(
				"." + separator + 
				"src" + separator +
				"test" + separator + 
				"java" + separator + 
				"com" + separator +
				"example" + separator +
				"blog" + separator +
				"controller" + separator + 
				"cat_selfie.jpg"
			));
		MockMultipartFile file =
				new MockMultipartFile(
						"file",
						"cat_selfie.jpg",
						"image/jpeg",
						testFileBytes
				);
		
		List<Long> tagIds = new ArrayList<>();
		tagIds.add(1L);
		ArticleDTO articleDTO = ArticleDTO.builder()
								.writer("TestUser")
								.content("Test Content")
								.title("Test Article")
								.category(1L)
								.tag(tagIds)
								.build();
		Article savedArticle = Article.builder()
								.id(15L)
								.writer(writer)
								.content("Test Content")
								.title("Test Article")
								.category(category)
								.tag(articleTag)
								.build();
		
		FileDTO resultingFileDTO = FileDTO.builder()
								.fileName(file.getOriginalFilename())
								.uploader(savedArticle.getWriter().getUserName())
								.createdAt(LocalDateTime.now())
								.id(10L)
								.articleId(savedArticle.getId())
								.build();
		
		when(userRepository.findByUserName(articleDTO.getWriter()))
			.thenReturn(Optional.of(writer));
		when(categoryRepository.findById(articleDTO.getCategory()))
			.thenReturn(Optional.of(category));
		when(articleRepository.save(any(Article.class)))
			.thenReturn(savedArticle);
		
		// setTagsForArticle()에서의 repository method calling
		when(tagRepository.findById(1L))
			.thenReturn(Optional.of(tag));
		when(articleTagRepository.save(any(ArticleTag.class)))
			.thenReturn(at);
		
		// file handling
		when(fileRepository.findByArticle(savedArticle))
			.thenReturn(Optional.ofNullable(null));
		when(fileService.insertNewFileInSystem(file, savedArticle.getWriter().getUserName(), savedArticle.getId()))
			.thenReturn(resultingFileDTO);
		
		ArticleDTO resultingArticleDTO = ArticleDTO.builder()
											.id(savedArticle.getId())
											.writer(savedArticle.getWriter().getUserName())
											.content(savedArticle.getContent())
											.title(savedArticle.getTitle())
											.category(savedArticle.getCategory().getId())
											.categoryName(article.getCategory().getName())
											.tag(tagIds)
											.tagName(List.of("Test Tag 1"))
											.createdAt(savedArticle.getCreatedAt())
											.updatedAt(savedArticle.getUpdatedAt())
											.build();
		
		Entry<ArticleDTO, FileDTO> resultingDTOsFromService = articleService.createOrEditArticle(articleDTO, file);
		
		assertThat(resultingDTOsFromService).isNotNull();
		
		ArticleDTO resultingArticleDTOFromService = resultingDTOsFromService.getKey();
		assertThat(resultingArticleDTOFromService.getId())
			.isEqualTo(resultingArticleDTO.getId());
		assertThat(resultingArticleDTOFromService.getWriter())
			.isEqualTo(resultingArticleDTO.getWriter());
		assertThat(resultingArticleDTOFromService.getContent())
			.isEqualTo(resultingArticleDTO.getContent());
		assertThat(resultingArticleDTOFromService.getTitle())
			.isEqualTo(resultingArticleDTO.getTitle());
		assertThat(resultingArticleDTOFromService.getCategory())
			.isEqualTo(resultingArticleDTO.getCategory());
		assertThat(resultingArticleDTOFromService.getTag())
			.isEqualTo(resultingArticleDTO.getTag());
		
		FileDTO resultingFileDTOFromService = resultingDTOsFromService.getValue();
		assertThat(resultingFileDTOFromService.getId())
			.isEqualTo(resultingFileDTO.getId());
		assertThat(resultingFileDTOFromService.getFileName())
			.isEqualTo(resultingFileDTO.getFileName());
		assertThat(resultingFileDTOFromService.getUploader())
			.isEqualTo(resultingFileDTO.getUploader());
		assertThat(resultingFileDTOFromService.getArticleId())
			.isEqualTo(resultingFileDTO.getArticleId());
		
		verify(userRepository, times(1)).findByUserName(articleDTO.getWriter());
		verify(categoryRepository, times(1)).findById(articleDTO.getCategory());
		verify(articleRepository, times(1)).save(any(Article.class));
		verify(tagRepository, times(1)).findById(1L);
		verify(articleTagRepository, times(1)).save(any(ArticleTag.class));
		verify(fileRepository, times(1)).findByArticle(savedArticle);
		verify(fileService, times(1)).insertNewFileInSystem(file, savedArticle.getWriter().getUserName(), savedArticle.getId());
	}
	
	@Test
	@DisplayName("Test for getArticleById(): successful case")
	void getArticleByIdTest() throws Exception {
		Long articleId = 15L;
		Article article = buildArticle();
		ArticleDTO resultingArticleDTO = ArticleDTO.builder()
											.id(article.getId())
											.writer(article.getWriter().getUserName())
											.content(article.getContent())
											.title(article.getTitle())
											.category(article.getCategory().getId())
											.categoryName(article.getCategory().getName())
											.tag(article.getTag().stream().map(at -> at.getTag().getId()).collect(Collectors.toList()))
											.tagName(article.getTag().stream().map(at -> at.getTag().getName()).collect(Collectors.toList()))
											.createdAt(article.getCreatedAt())
											.updatedAt(article.getUpdatedAt())
											.mainImage(article.getMainImage().getFileName())
											.build();
		
		when(articleRepository.findById(articleId))
			.thenReturn(Optional.of(article));
		when(fileRepository.findByArticle(article))
			.thenReturn(Optional.of(getFile(article.getWriter(), article)));
		
		ArticleDTO resultingArticleDTOFromService = articleService.getArticleById(articleId);
		
		assertThat(resultingArticleDTOFromService.getId())
			.isEqualTo(resultingArticleDTO.getId());
		assertThat(resultingArticleDTOFromService.getWriter())
			.isEqualTo(resultingArticleDTO.getWriter());
		assertThat(resultingArticleDTOFromService.getContent())
			.isEqualTo(resultingArticleDTO.getContent());
		assertThat(resultingArticleDTOFromService.getTitle())
			.isEqualTo(resultingArticleDTO.getTitle());
		assertThat(resultingArticleDTOFromService.getCategory())
			.isEqualTo(resultingArticleDTO.getCategory());
		assertThat(resultingArticleDTOFromService.getCategoryName())
			.isEqualTo(resultingArticleDTO.getCategoryName());
		assertThat(resultingArticleDTOFromService.getTag())
			.isEqualTo(resultingArticleDTO.getTag());
		assertThat(resultingArticleDTOFromService.getTagName())
			.isEqualTo(resultingArticleDTO.getTagName());
		assertThat(resultingArticleDTOFromService.getMainImage())
			.isEqualTo(resultingArticleDTO.getMainImage());
		
		verify(articleRepository, times(1)).findById(articleId);
	}
	
	/*
	@Test
	@DisplayName("Test for getArticlesForThisUser(): successful case")
	void getArticlesForThisUserTest() throws Exception {
		List<Article> articlesBeforeDTO = getBy();
		
		User user = articlesBeforeDTO.get(0).getWriter();
		String userName = user.getUserName();
		
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
		
		when(userRepository.findByUserName(userName))
			.thenReturn(Optional.of(user));
		when(articleRepository.findAllByWriter(user))
			.thenReturn(articlesBeforeDTO);
		
		List<ArticleDTO> articlesFromService = articleService.getArticlesForThisUser(userName);
		
		Assertions.assertThat(articlesFromService)
			.isNotNull()
			.hasSize(3);
	
		for (int i = 0; i < 3; ++i) {
			Assertions.assertThat(articlesFromService.get(i))
				.extracting(
					ArticleDTO::getId, ArticleDTO::getWriter,
					ArticleDTO::getCategory, ArticleDTO::getTag,
					ArticleDTO::getContent, ArticleDTO::getTitle
				)
				.containsExactly(
					articles.get(i).getId(),
					articles.get(i).getWriter(),
					articles.get(i).getCategory(),
					articles.get(i).getTag(),
					articles.get(i).getContent(),
					articles.get(i).getTitle()
				);
		}
		
		verify(userRepository).findByUserName(userName);
		verify(articleRepository).findAllByWriter(user);
	}
	
	@Test
	@DisplayName("Test for getArticlesByCategory(): successful case")
	void getArticlesByCategoryTest() {
		List<Article> articlesBeforeDTO = getBy();
		
		Category category = articlesBeforeDTO.get(0).getCategory();
		Long categoryId = category.getId();
		
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
				.collect(Collectors.toList());;
		
		when(categoryRepository.findById(categoryId))
			.thenReturn(Optional.of(category));
		when(articleRepository.findAllByCategory(category))
			.thenReturn(articlesBeforeDTO);
		
		List<ArticleDTO> articlesFromService = articleService.getArticlesByCategory(categoryId);

		Assertions.assertThat(articlesFromService)
			.isNotNull()
			.hasSize(3);
		
		for (int i = 0; i < 3; ++i) {
			Assertions.assertThat(articlesFromService.get(i))
				.extracting(
					ArticleDTO::getId, ArticleDTO::getWriter,
					ArticleDTO::getCategory, ArticleDTO::getTag,
					ArticleDTO::getContent, ArticleDTO::getTitle
				)
				.containsExactly(
					articles.get(i).getId(),
					articles.get(i).getWriter(),
					articles.get(i).getCategory(),
					articles.get(i).getTag(),
					articles.get(i).getContent(),
					articles.get(i).getTitle()
				);
		}
		
		verify(categoryRepository).findById(categoryId);
		verify(articleRepository).findAllByCategory(category);
	}
	
	@Test
	@DisplayName("Test for getArticlesByTag: successful case")
	void getArticlesByTagTest() throws Exception {
		List<Article> articlesBeforeDTO = getBy();
		
		Tag tag = articlesBeforeDTO.get(0).getTag().get(0).getTag();
		Long tagId = tag.getId();
		
		List<ArticleTag> articleTags = new ArrayList<>();
		articleTags.add(articlesBeforeDTO.get(0).getTag().get(0));
		articleTags.add(articlesBeforeDTO.get(1).getTag().get(0));
		articleTags.add(articlesBeforeDTO.get(2).getTag().get(0));
		
		List<ArticleDTO> articles = articlesBeforeDTO
									.stream()
									.map(ar -> ArticleDTO.builder()
												.id(ar.getId())
												.writer(ar.getWriter().getUserName())
												.content(ar.getContent())
												.title(ar.getTitle())
												.build())
									.collect(Collectors.toList());
		when(tagRepository.findById(tagId))
			.thenReturn(Optional.of(tag));
		when(articleTagRepository.findAllByTag(tag))
			.thenReturn(articleTags);
		
		List<ArticleDTO> articlesFromService = articleService.getArticlesByTag(tagId);
		
		Assertions.assertThat(articlesFromService)
			.isNotNull()
			.hasSize(3);
	
		for (int i = 0; i < 3; ++i) {
			Assertions.assertThat(articlesFromService.get(i))
				.extracting(
					ArticleDTO::getId, ArticleDTO::getWriter,
					ArticleDTO::getCategory, ArticleDTO::getTag,
					ArticleDTO::getContent, ArticleDTO::getTitle
				)
				.containsExactly(
					articles.get(i).getId(),
					articles.get(i).getWriter(),
					articles.get(i).getCategory(),
					articles.get(i).getTag(),
					articles.get(i).getContent(),
					articles.get(i).getTitle()
				);
		}
		
		verify(tagRepository).findById(tagId);
		verify(articleTagRepository).findAllByTag(tag);
	}

	*/
	
	
	@Test
	@DisplayName("Test for deleteArticle(): successful case")
	void deleteArticleTest() throws Exception {
		Article article = buildArticle();
		List<ArticleTag> articleTag1 = article.getTag();
		
		when(articleRepository.findById(article.getId()))
			.thenReturn(Optional.of(article));
		when(articleTagRepository.findAllByArticle(article))
			.thenReturn(Optional.of(articleTag1));
		
		articleService.deleteArticle(article.getId());
		
		verify(articleRepository).findById(article.getId());
		verify(articleTagRepository).findAllByArticle(article);
		verify(articleRepository).deleteById(article.getId());
	}
	
	/********************************************
	 * 											*
	 * 			private methods					*
	 * 											*
	 ********************************************/
	
	private Article buildArticle() {
		List<Long> tagIds = new ArrayList<>();
		tagIds.add(1L);
		
		User writer = User.builder()
				.id("TestUserID")
				.userName("TestUser")
				.password("TestUser password")
				.email("test@test.com")
				.blogTitle("TestUser's Blog")
				.build();
		
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
	
	private File getFile(User uploader, Article article) {
		return File.builder()
			.id(1L)
			.fileName("cat_selfie.jpg")
			.uploader(uploader)
			.filePath(Path.of(
					"." + separator + 
					"src" + separator +
					"test" + separator + 
					"java" + separator + 
					"com" + separator +
					"example" + separator +
					"blog" + separator +
					"controller" + separator + 
					"cat_selfie.jpg"
				).toString())
			.article(article)
			.build();
	}
	
	/*
		-> getBy()는 3개의 Article을 포함하는 List를 return한다:
			- article1: shaftCategory / user / tag1, tag2
			- article2: shaftCategory / user / tag1
			- article3: shartCategory / user / tag1, tag2, tag3
	*/
	private List<Article> getBy() {
		User user = User.builder()
						.id("TestUserID")
						.userName("TestUser")
						.password("TestUser password")
						.email("test@test.com")
						.blogTitle("TestUser's Blog")
						.build();
		
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
										.tag(tag1)
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
		
		return articlesBeforeDTO;
	}

}