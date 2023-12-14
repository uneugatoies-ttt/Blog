package com.example.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

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
	private CategoryRepository categoryRepository;
	private TagRepository tagRepository;
	private ArticleTagRepository articleTagRepository;
	private UserRepository userRepository;
	
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
				.findAllByTagId(t)
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
	
	@Transactional
	public List<ArticleDTO> getArticlesForThisUser(String userName) {
		User user = userRepository.findByUserName(userName);
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

}
