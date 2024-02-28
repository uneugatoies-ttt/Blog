package com.example.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.blog.dto.CategoryDTO;
import com.example.blog.security.TokenProvider;
import com.example.blog.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private CategoryService categoryService;
	@MockBean
	private TokenProvider tokenProvider;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@DisplayName("Test for addCategory()")
	void addCategoryTest() throws Exception {
		CategoryDTO categoryDTO = CategoryDTO.builder()
						.user("TestUser")
						.name("Test Category")
						.build();
		
		when(categoryService.addCategory(categoryDTO))
				.thenReturn(CategoryDTO.builder()
							.id(10L)
							.user("TestUser")
							.name("Test Category")
							.build());
		
		ResultActions result = mockMvc.perform(post("/category")
											.contentType(MediaType.APPLICATION_JSON)
											.content(objectMapper.writeValueAsString(categoryDTO)));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(10L))
				.andExpect(jsonPath("$.user").value("TestUser"))
				.andExpect(jsonPath("$.name").value("Test Category"))
				.andDo(print());
		
		verify(categoryService).addCategory(categoryDTO);
	}
	
	@Test
	@DisplayName("Test for getCategories()")
	void getCategoriesTest() throws Exception {
		String userName = "TestUser";
		
		List<CategoryDTO> categories = new ArrayList<>();
		categories.add(CategoryDTO.builder()
								.id(1L)
								.user(userName)
								.name("Test Category 1")
								.build());
		categories.add(CategoryDTO.builder()
								.id(2L)
								.user(userName)
								.name("Test Category 2")
								.build());
		categories.add(CategoryDTO.builder()
								.id(3L)
								.user(userName)
								.name("Test Category 3")
								.build());
		
		when(categoryService.getCategories(userName))
				.thenReturn(categories);
		
		ResultActions result = mockMvc.perform(get("/category")
											.param("userName", String.valueOf(userName)));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(3)))
				.andExpect(jsonPath("$.data[0].id").value(1L))
				.andExpect(jsonPath("$.data[0].user").value("TestUser"))
				.andExpect(jsonPath("$.data[0].name").value("Test Category 1"))
				.andExpect(jsonPath("$.data[1].id").value(2L))
				.andExpect(jsonPath("$.data[1].user").value("TestUser"))
				.andExpect(jsonPath("$.data[1].name").value("Test Category 2"))
				.andExpect(jsonPath("$.data[2].id").value(3L))
				.andExpect(jsonPath("$.data[2].user").value("TestUser"))
				.andExpect(jsonPath("$.data[2].name").value("Test Category 3"));
		
		verify(categoryService).getCategories(userName);
	}
	
	@Test
	@DisplayName("Test for editCategory()")
	void editCategoryTest() throws Exception {
		CategoryDTO categoryDTO = CategoryDTO.builder()
				.id(10L)
				.user("TestUser")
				.name("Test Category")
				.build();

		when(categoryService.editCategory(categoryDTO))
				.thenReturn(CategoryDTO.builder()
							.id(10L)
							.user("TestUser")
							.name("Test Category")
							.build());
		
		ResultActions result = mockMvc.perform(put("/category")
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(categoryDTO)));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(10L))
				.andExpect(jsonPath("$.user").value("TestUser"))
				.andExpect(jsonPath("$.name").value("Test Category"))
				.andDo(print());
		
		verify(categoryService).editCategory(categoryDTO);
	}

	@Test
	@DisplayName("Test for deleteCategory()")
	void deleteCategoryTest() throws Exception {
		Long categoryId = 10L;
		
		ResultActions result = mockMvc.perform(delete("/category")
									.param("categoryId", String.valueOf(categoryId)));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath(".data").value("Category deleted successfully"));
		
		verify(categoryService).deleteCategory(categoryId);
	}
	
	
}
