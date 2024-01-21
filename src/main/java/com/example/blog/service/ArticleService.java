package com.example.blog.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.domain.Article;
import com.example.blog.domain.ArticleTag;
import com.example.blog.domain.Category;
import com.example.blog.domain.File;
import com.example.blog.domain.Reply;
import com.example.blog.domain.Tag;
import com.example.blog.domain.User;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.FileDTO;
import com.example.blog.persistence.ArticleRepository;
import com.example.blog.persistence.ArticleTagRepository;
import com.example.blog.persistence.CategoryRepository;
import com.example.blog.persistence.FileRepository;
import com.example.blog.persistence.ReplyRepository;
import com.example.blog.persistence.TagRepository;
import com.example.blog.persistence.UserRepository;

// ArticleController에서 dependency로 사용되어 Article과 관련된 logic을 수행하는 service이다.
@Service
public class ArticleService {
	
	private ArticleRepository articleRepository;
	private UserRepository userRepository;
	private CategoryRepository categoryRepository;
	private TagRepository tagRepository;
	private ArticleTagRepository articleTagRepository;
	private FileRepository fileRepository;
	private ReplyRepository replyRepository;
	
	/* 
		-> 거의 비슷한 FileService의 logic을 그대로 가져와서 여기에도 두는 것은 지나치게 중복적이라고
		판단했기에 FileService를 dependency로 설정하고 그 method를 ArticleService에서
		call하는 방식으로 사용한다.
	*/
	private FileService fileService;
	
	public ArticleService(
		ArticleRepository articleRepository,
		UserRepository userRepository,
		CategoryRepository categoryRepository,
		TagRepository tagRepository,
		ArticleTagRepository articleTagRepository,
		FileRepository fileRepository,
		ReplyRepository replyRepository,
		FileService fileService
	) {
		this.articleRepository = articleRepository;
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
		this.tagRepository = tagRepository;
		this.articleTagRepository = articleTagRepository;
		this.fileRepository = fileRepository;
		this.replyRepository = replyRepository;
		this.fileService = fileService;
	}

	/*
		-> 생성과 변경의 logic은 상호 간 큰 차이가 없으므로 동일 method로 처리한다.
	 	이 때 file 관련 처리는 application 특성 상 항상 동반되므로 원래는 구분되어 있었던
	 	createOrEditArticle() 과 createOrEditArticleWithFile()을 통합해서
	 	createOrEditArticle()으로 만들었다.

	 	-> 원래는 article에 부수되는 file은 그대로 유지하며 file name만을 변경할 수 있게 하려고 했지만,
	 	그럴 이유가 별로 없다고 판단했기에 관련된 logic은 삭제하는 것으로 수정했다.
	 		 	
	 	-> 명심해야 할 점은:
	 		-> CREATE의 경우 (ArticleController의 createArticle에서 이 method를 call)
	 		언제나 file이 동반되지만;
	 		-> UPDATE의 경우 (ArticleController의 editArticle에서 이 method를 call)
	 		file이 동반될 수도 아닐 수도 있다는 점이다.
	 	이 때문에 file이 null인지는 언제나 확인해서 경우에 따른 처리를 해야 한다.
	*/
	@Transactional
	public Entry<ArticleDTO, FileDTO> createOrEditArticle(
		ArticleDTO articleDTO,
		MultipartFile file
	) throws IOException {
		User writer = userRepository.findByUserName(articleDTO.getWriter())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		Category category = null;
		if (articleDTO.getCategory() != null) {
			category = categoryRepository.findById(articleDTO.getCategory())
				.orElseThrow(() -> new EntityNotFoundException("Category not found"));
		}
		
		Article article = Article.builder()
								.writer(writer)
								.content(articleDTO.getContent())
								.title(articleDTO.getTitle())
								.category(category)
								.build();
		
		if (articleDTO.getId() != null) {
			article.setId(articleDTO.getId());
		} 
		
		Article savedArticle = articleRepository.save(article);
		
		List<Long> articleTagList = setTagsForArticle(savedArticle, articleDTO.getTag());
		
		FileDTO resultingFileDTO = null;
		
		// 만약 file이 있다면
		if (file != null) {
			Optional<File> existingFileOpt = fileRepository.findByArticle(savedArticle);

			// 만약 이미 해당하는 Article에 File이 등록되어 있다면, 먼저 그것을 삭제한다.
			if (existingFileOpt.isPresent()) {
				File existingFile = existingFileOpt.get();
				fileRepository.delete(existingFile);
				fileService.deleteFileInSystem(existingFile);
			}
			resultingFileDTO = fileService.insertNewFileInSystem(
					file, 
					savedArticle.getWriter().getUserName(),
					savedArticle.getId());
		}
		
		ArticleDTO resultingArticleDTO = ArticleDTO.builder()
													.id(savedArticle.getId())
													.writer(savedArticle.getWriter().getUserName())
													.content(savedArticle.getContent())
													.title(savedArticle.getTitle())
													.category(savedArticle.getCategory().getId())
													.tag(articleTagList)
													.createdAt(savedArticle.getCreatedAt())
													.updatedAt(savedArticle.getUpdatedAt())
													.build();
		
		return new SimpleImmutableEntry<>(resultingArticleDTO, resultingFileDTO);
	}

	@Transactional
	public ArticleDTO getArticleById(Long articleId) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new EntityNotFoundException("Article not found"));
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
		return resultingArticleDTO;
	}
	
	@Transactional
	public List<ArticleDTO> getArticlesForThisUser(String userName) {
		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		List<ArticleDTO> articles= articleRepository.findAllByWriter(user)
				.stream()
				.map(a -> ArticleDTO.builder()
									.id(a.getId())
									.writer(a.getWriter().getUserName())
									.content(a.getContent())
									.title(a.getTitle())
									.category(a.getCategory().getId())
									.categoryName(a.getCategory().getName())
									.tag(a.getTag().stream().map(at -> at.getTag().getId()).collect(Collectors.toList()))
									.tagName(a.getTag().stream().map(at -> at.getTag().getName()).collect(Collectors.toList()))
									.createdAt(a.getCreatedAt())
									.updatedAt(a.getUpdatedAt())
									.mainImage(a.getMainImage().getFileName())
									.build())
				.collect(Collectors.toList());
		
		return articles;
	}
	
	@Transactional
	public List<ArticleDTO> getArticlesByCategory(Long categoryId) {
		Category c = categoryRepository.findById(categoryId).get();
		List<ArticleDTO> articles = articleRepository
				.findAllByCategory(c)
				.stream()
				.map(ar -> ArticleDTO.builder()
									.id(ar.getId())
									.writer(ar.getWriter().getUserName())
									.content(ar.getContent())
									.title(ar.getTitle())
									.category(ar.getCategory().getId())
									.categoryName(ar.getCategory().getName())
									.tag(ar.getTag().stream().map(at -> at.getTag().getId()).collect(Collectors.toList()))
									.tagName(ar.getTag().stream().map(at -> at.getTag().getName()).collect(Collectors.toList()))
									.createdAt(ar.getCreatedAt())
									.updatedAt(ar.getUpdatedAt())
									.mainImage(ar.getMainImage().getFileName())
									.build())
				.collect(Collectors.toList());
		return articles;
	}

	@Transactional
	public List<ArticleDTO> getArticlesByTag(Long tagId) {
		Tag t = tagRepository.findById(tagId).get();
		List<ArticleDTO> articles= articleTagRepository
				.findAllByTag(t)
				.stream()
				.map(at -> ArticleDTO.builder()
							.id(at.getArticle().getId())
							.writer(at.getArticle().getWriter().getUserName())
							.content(at.getArticle().getContent())
							.title(at.getArticle().getTitle())
							.category(at.getArticle().getCategory().getId())
							.categoryName(at.getArticle().getCategory().getName())
							.tag(at.getArticle().getTag().stream().map(aatt -> aatt.getTag().getId()).collect(Collectors.toList()))
							.tagName(at.getArticle().getTag().stream().map(aatt -> aatt.getTag().getName()).collect(Collectors.toList()))							
							.createdAt(at.getCreatedAt())
							.updatedAt(at.getUpdatedAt())
							.mainImage(at.getArticle().getMainImage().getFileName())
							.build())
				.collect(Collectors.toList());
		
		return articles;
	}

	@Transactional
	public void deleteArticle(Long articleId) throws IOException {
		try {
			Article article = articleRepository.findById(articleId)
					.orElseThrow(() -> new EntityNotFoundException("Article not found"));
			
			Optional<List<ArticleTag>> articleTagList = articleTagRepository.findAllByArticle(article);
			Optional<File> existingFileOpt = fileRepository.findByArticle(article);
			Optional<List<Reply>> replyForThisArticle = replyRepository.findAllByArticle(article);
			
			if (articleTagList.isPresent()) {
				for (ArticleTag at : articleTagList.get())
					articleTagRepository.deleteById(at.getId());
			}
			
			if (existingFileOpt.isPresent()) {
				File existingFile = existingFileOpt.get();
				fileRepository.deleteById(existingFile.getId());
				fileService.deleteFileInSystem(existingFile);
			}
			
			if (replyForThisArticle.isPresent()) {
				for (Reply reply : replyForThisArticle.get())
					replyRepository.deleteById(reply.getId());
			}
			
			articleRepository.deleteById(articleId);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/********************************************
	 * 											*
	 * 			private methods					*
	 * 											*
	 ********************************************/
	
	@Transactional
	private List<Long> setTagsForArticle(Article article, List<Long> tags) {
		// 만약 edit의 경우라면 기존의 태그들을 찾아서 삭제.
		Optional<List<ArticleTag>> existingList = articleTagRepository.findAllByArticle(article);
		if (existingList.isPresent()) {
			articleTagRepository.deleteAllByArticle(article);
		}
		
		List<Long> articleTagList = new ArrayList<>();
		Tag tag;
		ArticleTag at;
		for (Long t : tags) {
			tag = tagRepository.findById(t)
					.orElseThrow(() -> new EntityNotFoundException("Tag not found"));
			
			at = ArticleTag.builder()
					.article(article)
					.tag(tag)
					.build();
			
			articleTagList.add(articleTagRepository.save(at).getTag().getId());
		}
		return articleTagList;
	}
	
	/*
		-> 이 method를 굳이 사용해야 하는 이유를 잘 모르겠다.
		Article에도 File type의 "mainImage"가 있으므로 Article을 가지고 있다면
		article.getMainImage().getFileName()을 사용하면 되는 것 아닌가?
		어째서 repository를 사용해 database operation을 따로 해야 하는 것인가?
		
		이 method를 정의할 때의 내가 실수를 했을 가능성도, 지금의 내가 무언가 중요한 것을 놓치고 있을 가능성도
		있다고 생각하므로 일단은 이렇게 놔두고 이후 확신이 생긴다면 재사용 혹은 삭제를 하도록 하겠다.
	*/
	/*
	@Transactional
	private String getMainImageName(Article article) {
		return fileRepository.findByArticle(article)
					.orElseThrow(() -> new EntityNotFoundException("File not found"))
					.getFileName();
	}*/
	
}
