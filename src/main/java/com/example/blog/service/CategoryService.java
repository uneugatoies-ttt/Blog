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
	
	public void deleteCategory(Long id) {
		categoryRepository.deleteById(id);
	}

	@Transactional
	public List<CategoryDTO> getCategory(String userName) {
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

}
