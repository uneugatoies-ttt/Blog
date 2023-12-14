package com.example.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.ResponseListDTO;
import com.example.blog.service.ArticleService;

@RestController
@RequestMapping("/article")
public class ArticleController {
	
	private ArticleService articleService;
	
	public ArticleController(ArticleService articleService) {
		this.articleService = articleService;
	}
	
	@GetMapping("/by-user")
	public ResponseEntity<?> getArticlesForThisUser(@RequestParam String userName) {
		try {
			List<ArticleDTO> articles = articleService
					.getArticlesForThisUser(userName);
			ResponseListDTO<ArticleDTO> res = ResponseListDTO.<ArticleDTO>builder()
										.data(articles)
										.build();
			return ResponseEntity.ok().body(res);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/by-category")
	public ResponseEntity<?> getArticlesByCategory(@RequestParam Long categoryId) {
		try {
			List<ArticleDTO> articles = articleService
					.getArticlesByCategory(categoryId);
			ResponseListDTO<ArticleDTO> res = ResponseListDTO.<ArticleDTO>builder()
										.data(articles)
										.build();
			return ResponseEntity.ok().body(res);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/by-tag")
	public ResponseEntity<?> getArticlesByTag(@RequestParam Long tagId) {
		try {
			List<ArticleDTO> articles = articleService
					.getArticlesByTag(tagId);
			ResponseListDTO<ArticleDTO> res = ResponseListDTO.<ArticleDTO>builder()
										.data(articles)
										.build();
			return ResponseEntity.ok().body(res);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
