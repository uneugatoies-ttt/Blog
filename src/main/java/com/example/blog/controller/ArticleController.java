package com.example.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.ResponseDTO;
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
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
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
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
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
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@PostMapping
	public ResponseEntity<?> createArticle(@Validated @RequestBody ArticleDTO articleDTO) {
		try {
			/*
			System.out.println("\n\nArticleDTO:");
			System.out.println(articleDTO.getContent());
			System.out.println(articleDTO.getTitle());
			System.out.println(articleDTO.getWriter());
			System.out.println(articleDTO.getCategory());
			System.out.println(articleDTO.getTag());
			
			return ResponseEntity.ok().body(articleDTO);
			*/
			ArticleDTO resultingArticleDTO = articleService.createOrEditArticle(articleDTO);
			return ResponseEntity.ok().body(resultingArticleDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@PutMapping
	public ResponseEntity<?> editArticle(@Validated @RequestBody ArticleDTO articleDTO) {
		try {
			ArticleDTO resultingArticleDTO = articleService.createOrEditArticle(articleDTO);
			return ResponseEntity.ok().body(resultingArticleDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteArticle(@RequestParam Long articleId) {
		try {
			articleService.deleteArticle(articleId);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
