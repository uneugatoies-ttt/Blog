package com.example.blog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

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

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ArticleService {
	
	private ArticleRepository articleRepository;
	private UserRepository userRepository;
	private CategoryRepository categoryRepository;
	private TagRepository tagRepository;
	private ArticleTagRepository articleTagRepository;
	
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
		
		System.out.println("\n\n\n1111111111111111111111111\n\n\n");
		
		Article article = Article.builder()
								.writer(writer)
								.content(articleDTO.getContent())
								.title(articleDTO.getTitle())
								.category(category)
								.build();
		
		System.out.println("\n\n\n22222222222222222222222222\n\n\n");
		
		
		if (articleDTO.getId() != null) article.setId(articleDTO.getId());
		
		System.out.println("\n\n\n3333333333333333333333333333\n\n\n");
		
		
		Article savedArticle = articleRepository.save(article);
		
		System.out.println("\n\n\n44444444444444444444444444444\n\n\n");
		
		
		List<Long> articleTagList = setTagsForArticle(savedArticle, articleDTO.getTag());
		
		System.out.println("\n\n\555555555555555555555555555555\n\n\n");
		
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
		
		System.out.println("\n\n\666666666666666666666666666666\n\n\n");
		
		return resultingArticleDTO;
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
