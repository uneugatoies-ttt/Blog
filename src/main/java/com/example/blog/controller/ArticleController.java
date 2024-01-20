package com.example.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.ResponseDTO;
import com.example.blog.dto.ResponseListDTO;
import com.example.blog.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
	-> ArticleController는 이 application에서 article에 관한 request를 받는 controller이다.
*/

@RestController
@RequestMapping("/article")
public class ArticleController {
	
	private ArticleService articleService;
	private ObjectMapper objectMapper;
	
	public ArticleController(
		ArticleService articleService,
		ObjectMapper objectMapper
	) {
		this.articleService = articleService;
		this.objectMapper = objectMapper;
	}

	/*
		-> 원래는 request에서 FileDTO를 받았지만, 다음의 변경점으로 인해 그렇게 할 이유가 없어졌다:
			1) user input으로 fileName을 지정했던 원래의 logic에서, file이 본디 가지고 있었던 fileName을
			그대로 사용하는 대신 이 application의 naming convention을 따르도록 변경하는 것으로 수정
			2) File에서 "description" 과 "fileType" 에 해당하는 metadata field 두 개를
			삭제하는 것으로 수정
		따라서 삭제했다.
		
		-> 현재 "createOrEditArticle()의 과정이 성공하면 Entry<ArticleDTO, FileDTO>를 return하는 것으로
		설정했다; 하지만 이렇게 나온 두 개의 DTO를 response에 그대로 실어서 return할지는 아직 결정하지 못했다.
		
		-> 원래의 method명은 "createArticleWithFile"으로 file이 동반된다는 것을 명시했지만,
		frontend와의 상호 작용을 확인하고 이후 수정점으로 인해 article이 만들어질 때는 언제나 file이
		동반되는 것으로 바뀌었기 때문에 "WithFile"이라는 suffix는 삭제했다.
	*/
	@PostMapping
	public ResponseEntity<?> createArticle(
		@RequestPart("file") MultipartFile file,
		@RequestPart("articleDTO") String articleDTOJson
	) {
		if (file.isEmpty() || file == null)
			return ResponseEntity.badRequest().body("No file has been sent");
		try {
			ArticleDTO articleDTO = objectMapper.readValue(articleDTOJson, ArticleDTO.class);
			articleService.createOrEditArticle(articleDTO, file);
			return ResponseEntity.ok().body(ResponseDTO.builder().data("Article inserted successfully").build());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@GetMapping("/by-id")
	public ResponseEntity<?> getArticleById(@RequestParam Long articleId) {
		try {
			ArticleDTO resultingArticleDTO = articleService.getArticleById(articleId);
			return ResponseEntity.ok().body(resultingArticleDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@GetMapping("/by-user")
	public ResponseEntity<?> getArticlesForThisUser(
		@RequestParam String userName
	) {
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
	
	@PutMapping
	public ResponseEntity<?> editArticle(
		@RequestPart("file") MultipartFile file,
		@RequestPart("articleDTO") String articleDTOJson
	) {
		try {
			ArticleDTO articleDTO = objectMapper.readValue(articleDTOJson, ArticleDTO.class);		
			
			articleService.createOrEditArticle(articleDTO, file);
			
			return ResponseEntity
					.ok()
					.body(ResponseDTO
						.builder()
						.data("Article modified successfully")
						.build()
					);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	/*
		-> 원래는 204 NO CONTENT status를 response에 지정하려고 했지만, 이 application은 명시적 설정이 없다면
		기본적으로 JSON을 frontend-backend communication에 사용하므로 일관성을 위해 ResponseDTO를 사용한다.
	*/
	@DeleteMapping
	public ResponseEntity<?> deleteArticle(@RequestParam Long articleId) {
		try {
			articleService.deleteArticle(articleId);
			return ResponseEntity.ok().body(ResponseDTO.builder().data("Article deleted successfully").build());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
