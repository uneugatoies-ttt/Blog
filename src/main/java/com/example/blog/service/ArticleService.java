package com.example.blog.service;

//import static java.io.File.separator;

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
import com.example.blog.domain.Tag;
import com.example.blog.domain.User;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.FileDTO;
import com.example.blog.persistence.ArticleRepository;
import com.example.blog.persistence.ArticleTagRepository;
import com.example.blog.persistence.CategoryRepository;
import com.example.blog.persistence.FileRepository;
import com.example.blog.persistence.TagRepository;
import com.example.blog.persistence.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ArticleService {
	
	private ArticleRepository articleRepository;
	private UserRepository userRepository;
	private CategoryRepository categoryRepository;
	private TagRepository tagRepository;
	private ArticleTagRepository articleTagRepository;
	private FileRepository fileRepository;

	/* 
	거의 비슷한 FileService의 logic을 그대로 가져오는 것은 지나치게 중복적이라고 판단했기에 FileService를
	dependency로 설정하고 그 method를 ArticleService에서 call하는 방식으로 사용한다.
	다만, 한 service에서 다른 service class를 dependency로 가지는 logic이 사용해도 문제가 없는지 여부는
	아직 불명확하기에 이후 확실해진다면 수정하도록 한다.
	*/
	private FileService fileService;
	
	/*
	@Transactional
	public Resource getMainImage(Long articleId) {
		try {
			String fileName = fileRepository.findBy(uploader)
									.orElseThrow(() -> new RuntimeException())
									.getFileName();
			String userName = uploader.getUserName();
		
			Resource fileResource = 
					new ClassPathResource(
							"static" + 
							separator +
							userName +
							separator +
							fileName
						);
			if (fileResource.exists())
				return fileResource;
			return null;
		} catch (Exception e) {
			throw e;
		}
	}*/
	
	@Transactional
	public String getMainImageName(Article article) {
		return fileRepository.findByArticle(article)
					.orElseThrow(() -> new EntityNotFoundException("File not found"))
					.getFileName();
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
									.createdAt(a.getCreatedAt())
									.updatedAt(a.getUpdatedAt())
									.mainImage(getMainImageName(a))
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
									.createdAt(ar.getCreatedAt())
									.updatedAt(ar.getUpdatedAt())
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
							.createdAt(at.getCreatedAt())
							.updatedAt(at.getUpdatedAt())
							.build())
				.collect(Collectors.toList());
		
		return articles;
	}
	
	// 생성과 변경의 logic은 상호 간 큰 차이가 없으므로 동일 method로 처리.
	@Transactional
	public ArticleDTO createOrEditArticle(ArticleDTO articleDTO) {
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
		
		if (articleDTO.getId() != null) article.setId(articleDTO.getId());
		
		Article savedArticle = articleRepository.save(article);
		
		List<Long> articleTagList = setTagsForArticle(savedArticle, articleDTO.getTag());
		
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
		
		return resultingArticleDTO;
	}
	
	@Transactional
	public Entry<ArticleDTO, FileDTO> createOrEditArticleWithFile(
		MultipartFile file,
		ArticleDTO articleDTO,
		FileDTO fileDTO
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
		
		if (articleDTO.getId() != null) article.setId(articleDTO.getId());
		
		Article savedArticle = articleRepository.save(article);
		
		List<Long> articleTagList = setTagsForArticle(savedArticle, articleDTO.getTag());
		
		fileDTO.setArticleId(savedArticle.getId());
		FileDTO resultingFileDTO = fileService.insertNewFileInSystem(file, fileDTO);
			
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
	
	@Transactional
	public void deleteArticle(Long articleId) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new EntityNotFoundException("Article not found"));
		
		Optional<List<ArticleTag>> articleTagList = articleTagRepository.findAllByArticle(article);
		
		if (articleTagList.isPresent()) {
			for (ArticleTag at : articleTagList.get())
				articleTagRepository.delete(at);
		}
		
		articleRepository.deleteById(articleId);
	}
	
}
