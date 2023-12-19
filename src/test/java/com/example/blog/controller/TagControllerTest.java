package com.example.blog.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

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

import com.example.blog.dto.TagDTO;
import com.example.blog.security.TokenProvider;
import com.example.blog.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TagController.class)
public class TagControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private TagService tagService;
	@MockBean
	private TokenProvider tokenProvider;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@DisplayName("Test for addTag(): successful case")
	void addTagTest() throws Exception {
		TagDTO tagDTO = TagDTO.builder()
							.name("Test Tag")
							.user("TestUser")
							.build();
		
		when(tagService.addTag(tagDTO))
			.thenReturn(TagDTO.builder()
							.id(383L)
							.name("Test Tag")
							.user("TestUser")
							.build());
		
		ResultActions result = mockMvc.perform(post("/tag")
											.contentType(MediaType.APPLICATION_JSON)
											.content(objectMapper.writeValueAsString(tagDTO)));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(383L))
				.andExpect(jsonPath("$.name").value("Test Tag"))
				.andExpect(jsonPath("$.user").value("TestUser"))
				.andDo(print());
		
		verify(tagService).addTag(tagDTO);
	}
	
	@Test
	@DisplayName("Test for deleteTag(): successful case")
	void deleteTagTest() throws Exception {
		Long id = 383L;
		
		ResultActions result = mockMvc.perform(delete("/tag")
												.param("id", String.valueOf(id)));
		
		result.andExpect(status().isNoContent())
				.andDo(print());
		
		verify(tagService).deleteTag(id);
	}
	
	@Test
	@DisplayName("Test for getTag(): successful case")
	void getTagTest() throws Exception {
		String userName = "TestUser";
		
		List<TagDTO> tags = new ArrayList<>();
		tags.add(TagDTO.builder()
				.id(3L)
				.name("Test Tag 1")
				.user(userName)
				.build());
		tags.add(TagDTO.builder()
				.id(4L)
				.name("Test Tag 2")
				.user(userName)
				.build());
		tags.add(TagDTO.builder()
				.id(5L)
				.name("Test Tag 3")
				.user(userName)
				.build());
		
		when(tagService.getTag(userName))
			.thenReturn(tags);
		
		ResultActions result = mockMvc.perform(get("/tag")
											.param("userName", userName));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(3)))
				.andExpect(jsonPath("$.data[0].id").value(3L))
				.andExpect(jsonPath("$.data[0].name").value("Test Tag 1"))
				.andExpect(jsonPath("$.data[0].user").value(userName))
				.andExpect(jsonPath("$.data[1].id").value(4L))
				.andExpect(jsonPath("$.data[1].name").value("Test Tag 2"))
				.andExpect(jsonPath("$.data[1].user").value(userName))
				.andExpect(jsonPath("$.data[2].id").value(5L))
				.andExpect(jsonPath("$.data[2].name").value("Test Tag 3"))
				.andExpect(jsonPath("$.data[2].user").value(userName))
				.andDo(print());
		
		verify(tagService).getTag(userName);
	}
	
}
