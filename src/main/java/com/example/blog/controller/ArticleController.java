package com.example.blog.controller;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.FileDTO;
import com.example.blog.dto.ResponseDTO;
import com.example.blog.dto.ResponseListDTO;
import com.example.blog.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/article")
public class ArticleController {
	
	private ArticleService articleService;
	private ObjectMapper objectMapper;
	//private FileService fileService;
	
	public ArticleController(
		ArticleService articleService,
		ObjectMapper objectMapper
		//FileService fileService
	) {
		this.articleService = articleService;
		this.objectMapper = objectMapper;
		//this.fileService = fileService;
	}
	
	@GetMapping("/by-user")
	public ResponseEntity<?> getArticlesForThisUser(@RequestParam String userName) {
		try {
			List<ArticleDTO> articles = articleService.getArticlesForThisUser(userName);
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
	
	@PostMapping("/with-file")
	public ResponseEntity<?> createArticleWithFile(
		@RequestPart("file") MultipartFile file,
		@RequestPart("articleDTO") String articleDTOJson,
		@RequestPart("fileDTO") String fileDTOJson
	) {
		if (file.isEmpty())
			return ResponseEntity.badRequest().body("No file has been sent");
		try {
			ArticleDTO articleDTO = objectMapper.readValue(articleDTOJson, ArticleDTO.class);
			FileDTO fileDTO = objectMapper.readValue(fileDTOJson, FileDTO.class);
			
			Entry<ArticleDTO, FileDTO> resDTOs =
					articleService.createOrEditArticleWithFile(file, articleDTO, fileDTO);
			ArticleDTO resultingArticleDTO = resDTOs.getKey();
			FileDTO resultingFileDTO = resDTOs.getValue();
			
			return ResponseEntity.ok().body(ResponseDTO.builder().data("Article successfully inserted").build());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	/*
	@PostMapping("/with-file")
	public ResponseEntity<?> createArticleWithFile(
		@RequestPart("file") MultipartFile file,
		@RequestPart("articleDTO") String articleDTOJson,
		@RequestPart("fileDTO") String fileDTOJson
	) {
		if (file.isEmpty())
			return ResponseEntity.badRequest().body("No file has been sent");
		try {
			ArticleDTO articleDTO = objectMapper.readValue(articleDTOJson, ArticleDTO.class);
			FileDTO fileDTO = objectMapper.readValue(fileDTOJson, FileDTO.class);
			
			Entry<ArticleDTO, FileDTO> resDTOs =
					articleService.createOrEditArticleWithFile(file, articleDTO, fileDTO);
			ArticleDTO resultingArticleDTO = resDTOs.getKey();
			FileDTO resultingFileDTO = resDTOs.getValue();
			
			String fileName = resultingFileDTO.getFileName().replace(' ', '-').replace('_', '-');
			String userName = resultingFileDTO.getUploader().replace(' ', '-').replace('_', '-');
			
			System.out.println("\n\n\n" + fileName);
			System.out.println(userName + "\n\n\n");
			Resource mi = fileService.getFile(fileName, userName);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_MIXED);
			
			byte[] imageBytes = FileCopyUtils.copyToByteArray(mi.getInputStream());
			byte[] jsonBytes = objectMapper.writeValueAsBytes(resultingArticleDTO);
			
			byte[] combinedBytes = new byte[jsonBytes.length + imageBytes.length];
			System.arraycopy(jsonBytes, 0, combinedBytes, 0, jsonBytes.length);
			System.arraycopy(imageBytes, 0, combinedBytes, 0, imageBytes.length);
			
			return new ResponseEntity<>(combinedBytes, headers, 200);
			
			//return ResponseEntity.ok().body(null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}*/
	
	@PostMapping
	public ResponseEntity<?> createArticle(@Validated @RequestBody ArticleDTO articleDTO) {
		try {
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
