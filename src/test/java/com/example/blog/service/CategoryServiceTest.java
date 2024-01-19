package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.blog.domain.Category;
import com.example.blog.domain.User;
import com.example.blog.dto.CategoryDTO;
import com.example.blog.persistence.CategoryRepository;
import com.example.blog.persistence.UserRepository;

@ExtendWith(SpringExtension.class)
@Import({CategoryService.class})
public class CategoryServiceTest {
	
	@MockBean
	private CategoryRepository categoryRepository;
	@MockBean
	private UserRepository userRepository;
	
	@Autowired
	private CategoryService categoryService;
	
	/*
		-> when()을 통해 mock behavior를 지정해야 하는 method에 주어지는 argument에 대한 
		직접적인 제어를 이 test에서 할 수 없다면 any()를 사용해야 됨을 주의해야 한다.
		이것은 String과 다른 기본 data type은 가지고 있는 value만 같다면 equal한 것으로 취급되지만
		다른 object type의 경우에는 그렇지 않기 때문이다.
	*/
	@Test
	@DisplayName("Test for addCategory(): successful case")
	void addCategoryTest() throws Exception {
		String userName = "TestUser";
		
		CategoryDTO categoryDTO = CategoryDTO.builder()
									.user(userName)
									.name("Test Cate")
									.build();
		
		User user = User.builder()
						.id("TestUserID")
						.userName(userName)
						.password("TestUser Password")
						.email("testuser@test.com")
						.authProvider(null)
						.blogTitle("TestUser's Blog")
						.build();
		
		Category savedCategory = Category.builder()
									.id(3L)
									.user(user)
									.name("Test Cate")
									.build();
		
		CategoryDTO resultingCategoryDTO = CategoryDTO.builder()
											.id(3L)
											.user(userName)
											.name("Test Cate")
											.build();
		
		when(userRepository.findByUserName(userName))
				.thenReturn(Optional.of(user));
		when(categoryRepository.save(any(Category.class)))
				.thenReturn(savedCategory);
		
		CategoryDTO resultingCategoryDTOFromService =
				categoryService.addCategory(categoryDTO);
		
		assertThat(resultingCategoryDTOFromService)
			.isNotNull();
		assertThat(resultingCategoryDTOFromService.getId())
			.isEqualTo(resultingCategoryDTO.getId());
		assertThat(resultingCategoryDTOFromService.getUser())
			.isEqualTo(resultingCategoryDTO.getUser());
		assertThat(resultingCategoryDTOFromService.getName())
			.isEqualTo(resultingCategoryDTO.getName());
		
		verify(userRepository, times(1)).findByUserName(userName);
		verify(categoryRepository, times(1)).save(any(Category.class));
	}


	@Test
	@DisplayName("Test for getCategories(): successful case")
	void getCategoiesTest() throws Exception {
		String userName = "TestUser";
		
		User user = User.builder()
				.id("TestUserID")
				.userName(userName)
				.password("TestUser Password")
				.email("testuser@test.com")
				.authProvider(null)
				.blogTitle("TestUser's Blog")
				.build();
		
		List<Category> categoriesEntity = new ArrayList<>();
		categoriesEntity.add(Category.builder()
								.id(3L)
								.user(user)
								.name("Test Cate 1")
								.build());
		categoriesEntity.add(Category.builder()
								.id(4L)
								.user(user)
								.name("Test Cate 2")
								.build());
		categoriesEntity.add(Category.builder()
								.id(5L)
								.user(user)
								.name("Test Cate 3")
								.build());
		
		List<CategoryDTO> categories = categoriesEntity
									.stream()
									.map(c -> CategoryDTO.builder()
											.id(c.getId())
											.user(c.getUser().getUserName())
											.name(c.getName())
											.build())
									.collect(Collectors.toList());
		
		when(userRepository.findByUserName(userName))
				.thenReturn(Optional.of(user));
		when(categoryRepository.findAllByUser(user))
				.thenReturn(categoriesEntity);
		
		List<CategoryDTO> categoriesFromService =
				categoryService.getCategories(userName);
		
		assertThat(categoriesFromService)
			.isNotNull()
			.hasSize(3);
		
		for (int i = 0; i < 3; ++i) {
			assertThat(categoriesFromService.get(i))
				.extracting(
					CategoryDTO::getId,
					CategoryDTO::getUser,
					CategoryDTO::getName
				)
				.containsExactly(
					categories.get(i).getId(),
					categories.get(i).getUser(),
					categories.get(i).getName()
				);
		}
	
		verify(userRepository, times(1)).findByUserName(userName);
		verify(categoryRepository, times(1)).findAllByUser(user);
	}
	
	@Test
	@DisplayName("Test for editCategory(): successful case")
	void editCategoryTest() throws Exception {
		CategoryDTO categoryDTO = CategoryDTO.builder()
										.id(10L)
										.user("TestUser")
										.name("Test Category Modified")
										.build();
		
		User user = User.builder()
				.id("TestUserID")
				.userName("TestUser")
				.password("TestUser Password")
				.email("testuser@test.com")
				.authProvider(null)
				.blogTitle("TestUser's Blog")
				.build();
		
		Category existingCategory = Category.builder()
										.id(10L)
										.user(user)
										.name("Test Category")
										.build();
		
		Category savedCategory = Category.builder()
										.id(10L)
										.user(user)
										.name("Test Category Modified")
										.build();

		CategoryDTO resultingCategoryDTO = CategoryDTO.builder()
													.id(10L)
													.user("TestUser")
													.name("Test Category Modified")
													.build();
		
		when(categoryRepository.findById(categoryDTO.getId()))
			.thenReturn(Optional.of(existingCategory));
		when(categoryRepository.save(existingCategory))
			.thenReturn(savedCategory);
		
		CategoryDTO resultingCategoryDTOFromService = categoryService.editCategory(categoryDTO);
		
		assertThat(resultingCategoryDTOFromService)
			.isNotNull();
		assertThat(resultingCategoryDTOFromService.getId())
			.isEqualTo(resultingCategoryDTO.getId());
		assertThat(resultingCategoryDTOFromService.getUser())
			.isEqualTo(resultingCategoryDTO.getUser());
		assertThat(resultingCategoryDTOFromService.getName())
			.isEqualTo(resultingCategoryDTO.getName());
		
		verify(categoryRepository, times(1)).findById(categoryDTO.getId());
		verify(categoryRepository, times(1)).save(existingCategory);
											
	}
	
	@Test
	@DisplayName("Test for deleteCategory(): successful case")
	void deleteCategoryTest() throws Exception {
		categoryService.deleteCategory(3L);
		verify(categoryRepository, times(1)).deleteById(3L);
	}

}
