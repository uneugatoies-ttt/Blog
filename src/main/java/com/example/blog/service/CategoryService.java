package com.example.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.blog.domain.Category;
import com.example.blog.domain.User;
import com.example.blog.dto.CategoryDTO;
import com.example.blog.persistence.CategoryRepository;
import com.example.blog.persistence.UserRepository;

import lombok.AllArgsConstructor;

// CategoryService에서 dependency로 사용되어 Category와 관련된 logic을 수행하는 service이다.
@Service
@AllArgsConstructor
public class CategoryService {
	
	private CategoryRepository categoryRepository;
	private UserRepository userRepository;

	@Transactional
	public CategoryDTO addCategory(CategoryDTO categoryDTO) {
		User user = userRepository.findByUserName(categoryDTO.getUser())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		Category category = Category.builder()
									.user(user)
									.name(categoryDTO.getName())
									.build();
		
		Category savedCategory = categoryRepository.save(category);
		
		CategoryDTO resultingCategoryDTO = CategoryDTO.builder()
													.id(savedCategory.getId())
													.user(savedCategory.getUser().getUserName())
													.name(savedCategory.getName())
													.build();
		
		return resultingCategoryDTO;
	}

	@Transactional
	public List<CategoryDTO> getCategories(String userName) {
		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		List<CategoryDTO> categories = categoryRepository
								.findAllByUser(user)
								.stream()
								.map(c -> CategoryDTO.builder()
											.id(c.getId())
											.user(c.getUser().getUserName())
											.name(c.getName())
											.build())
								.collect(Collectors.toList());
				
		return categories;
	}
	
	@Transactional
	public CategoryDTO editCategory(CategoryDTO categoryDTO) {
		Category existingCategory = categoryRepository.findById(categoryDTO.getId())
				.orElseThrow(() -> new EntityNotFoundException("Category not found"));
		
		existingCategory.setName(categoryDTO.getName());
		
		Category savedCategory = categoryRepository.save(existingCategory);
		
		CategoryDTO resultingCategoryDTO = CategoryDTO.builder()
												.id(savedCategory.getId())
												.user(savedCategory.getUser().getUserName())
												.name(savedCategory.getName())
												.build();
		
		return resultingCategoryDTO;
	}
	
	@Transactional
	public void deleteCategory(Long id) {
		categoryRepository.deleteById(id);
	}

}
