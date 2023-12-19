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

import java.time.LocalDateTime;
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

import com.example.blog.dto.ReplyDTO;
import com.example.blog.security.TokenProvider;
import com.example.blog.service.ReplyService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ReplyController.class)
public class ReplyControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private ReplyService replyService;
	@MockBean
	private TokenProvider tokenProvider;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@DisplayName("Test for createReply(): successful case")
	void createReplyTest() throws Exception {
		ReplyDTO replyDTO = ReplyDTO.builder()
								.content("Test reply content")
								.writer("TestWriter")
								.article(35L)
								.where("/TestUser/article/35")
								.build();
		
		when(replyService.createReply(replyDTO))
			.thenReturn(ReplyDTO.builder()
								.id(1053L)
								.content("Test reply content")
								.writer("TestWriter")
								.article(35L)
								.where("/TestUser/article/35")
								.createdAt(LocalDateTime.now())
								.updatedAt(LocalDateTime.now())
								.build());
		
		ResultActions result = mockMvc.perform(post("/reply")
											.contentType(MediaType.APPLICATION_JSON)
											.content(objectMapper.writeValueAsString(replyDTO)));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1053L))
				.andExpect(jsonPath("$.content").value("Test reply content"))
				.andExpect(jsonPath("$.writer").value("TestWriter"))
				.andExpect(jsonPath("$.article").value(35L))
				.andExpect(jsonPath("$.where").value("/TestUser/article/35"))
				.andExpect(jsonPath("$.createdAt").exists())
				.andExpect(jsonPath("$.updatedAt").exists())
				.andDo(print());
		
		verify(replyService).createReply(replyDTO);
	}
	
	
	@Test
	@DisplayName("Test for getRepliesByArticle(): successful case")
	void getRepliesByArticleTest() throws Exception {
		Long articleId = 35L;
		String where = "/TestUser/article/35";
		List<ReplyDTO> replyDTOList = new ArrayList<>();
		replyDTOList.add(ReplyDTO.builder()
				.id(1053L)
				.content("Test reply content 1")
				.writer("TestWriter1")
				.article(articleId)
				.where(where)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build());
		replyDTOList.add(ReplyDTO.builder()
				.id(1055L)
				.content("Test reply content 2")
				.writer("TestWriter2")
				.article(articleId)
				.where(where)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build());
		replyDTOList.add(ReplyDTO.builder()
				.id(1067L)
				.content("Test reply content 3")
				.writer("TestWriter3")
				.article(articleId)
				.where(where)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build());
		
		when(replyService.getRepliesByArticle(articleId))
				.thenReturn(replyDTOList);
		
		ResultActions result = mockMvc.perform(get("/reply")
											.param("articleId", String.valueOf(articleId)));
											
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(3)))
				.andExpect(jsonPath("$.data[0].id").value(1053L))
				.andExpect(jsonPath("$.data[0].content").value("Test reply content 1"))
				.andExpect(jsonPath("$.data[0].writer").value("TestWriter1"))
				.andExpect(jsonPath("$.data[0].article").value(articleId))
				.andExpect(jsonPath("$.data[0].where").value(where))
				.andExpect(jsonPath("$.data[0].createdAt").exists())
				.andExpect(jsonPath("$.data[0].updatedAt").exists())
				.andExpect(jsonPath("$.data[1].id").value(1055L))
				.andExpect(jsonPath("$.data[1].content").value("Test reply content 2"))
				.andExpect(jsonPath("$.data[1].writer").value("TestWriter2"))
				.andExpect(jsonPath("$.data[1].article").value(articleId))
				.andExpect(jsonPath("$.data[1].where").value(where))
				.andExpect(jsonPath("$.data[1].createdAt").exists())
				.andExpect(jsonPath("$.data[1].updatedAt").exists())
				.andExpect(jsonPath("$.data[2].id").value(1067L))
				.andExpect(jsonPath("$.data[2].content").value("Test reply content 3"))
				.andExpect(jsonPath("$.data[2].writer").value("TestWriter3"))
				.andExpect(jsonPath("$.data[2].article").value(articleId))
				.andExpect(jsonPath("$.data[2].where").value(where))
				.andExpect(jsonPath("$.data[2].createdAt").exists())
				.andExpect(jsonPath("$.data[2].updatedAt").exists());
		
		verify(replyService).getRepliesByArticle(articleId);
	}
	
	@Test
	@DisplayName("Test for deleteReply(): successful case")
	void deleteReplyTest() throws Exception {
		Long replyId = 1067L;
		
		ResultActions result = mockMvc.perform(delete("/reply")
											.param("replyId", String.valueOf(replyId)));
		
		result.andExpect(status().isNoContent());
		
		verify(replyService).deleteReply(replyId);
	}
	
	
}
