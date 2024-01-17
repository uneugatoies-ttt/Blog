package com.example.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.CategoryDTO;
import com.example.blog.dto.ResponseDTO;
import com.example.blog.dto.ResponseListDTO;
import com.example.blog.service.CategoryService;

/*
	-> CategoryController는 
*/

@RestController
@RequestMapping("/category")
public class CategoryController {
	
	private CategoryService categoryService;
	
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@PostMapping
	public ResponseEntity<?> addCategory(@RequestBody CategoryDTO categoryDTO) {
		try {
			CategoryDTO resultingCategoryDTO = categoryService.addCategory(categoryDTO);
			return ResponseEntity.ok().body(resultingCategoryDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}	
	
	@GetMapping
	public ResponseEntity<?> getCategories(
		@RequestParam String userName
	) {
		try {
			List<CategoryDTO> categories = categoryService.getCategories(userName);
			ResponseListDTO<CategoryDTO> responseListDTO = ResponseListDTO.<CategoryDTO>builder()
														.data(categories)
														.build();
			return ResponseEntity.ok().body(responseListDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

	@PutMapping
	public ResponseEntity<?> editCategory(@RequestBody CategoryDTO categoryDTO) {
		try {
			CategoryDTO resultingCategoryDTO = categoryService.editCategory(categoryDTO);
			return ResponseEntity.ok().body(resultingCategoryDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}	
	@DeleteMapping
	public ResponseEntity<?> deleteCategory(@RequestParam Long categoryId) {
		try {
			categoryService.deleteCategory(categoryId);
			return ResponseEntity.ok().body(ResponseDTO.builder().data("Category deleted successfully").build());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
