package com.example.blog.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.blog.dto.NotificationMessageDTO;
import com.example.blog.security.TokenProvider;
import com.example.blog.service.NotificationMessageService;

@WebMvcTest(NotificationMessageController.class)
public class NotificationMessageControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private NotificationMessageService notificationMessageService;
	@MockBean
	private TokenProvider tokenProvider;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@DisplayName("Test for getAllMessagesForThisUser: successful case")
	void getAllMessagesForThisUserTest() throws Exception {
		String userName = "TestUser";
		
		List<NotificationMessageDTO> messages = new ArrayList<>();
		messages.add(NotificationMessageDTO.builder()
				.id(3L)
				.message("Test message 1")
				.recipient(userName)
				.where("/TestUser/article/335")
				.build());
		messages.add(NotificationMessageDTO.builder()
				.id(4L)
				.message("Test message 2")
				.recipient(userName)
				.where("/TestUser/article/87")
				.build());
		messages.add(NotificationMessageDTO.builder()
				.id(5L)
				.message("Test message 3")
				.recipient(userName)
				.where("/TestUser/article/991")
				.build());
		
		when(notificationMessageService.getAllMessagesForThisUser(userName))
				.thenReturn(messages);
		
		ResultActions result = mockMvc.perform(get("/notification")
											.param("userName", userName));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(3)))
				.andExpect(jsonPath("$.data[0].id").value(3L))
				.andExpect(jsonPath("$.data[0].message").value("Test message 1"))
				.andExpect(jsonPath("$.data[0].recipient").value(userName))
				.andExpect(jsonPath("$.data[0].where").value("/TestUser/article/335"))
				.andExpect(jsonPath("$.data[1].id").value(4L))
				.andExpect(jsonPath("$.data[1].message").value("Test message 2"))
				.andExpect(jsonPath("$.data[1].recipient").value(userName))
				.andExpect(jsonPath("$.data[1].where").value("/TestUser/article/87"))
				.andExpect(jsonPath("$.data[2].id").value(5L))
				.andExpect(jsonPath("$.data[2].message").value("Test message 3"))
				.andExpect(jsonPath("$.data[2].recipient").value(userName))
				.andExpect(jsonPath("$.data[2].where").value("/TestUser/article/991"))
				.andDo(print());
		
		verify(notificationMessageService).getAllMessagesForThisUser(userName);
	}
	
	@Test
	@DisplayName("Test for clearAllMessagesForThisUser: successful case")
	void clearAllMessagesForThisUserTest() throws Exception {
		String userName = "TestUser";
		
		ResultActions result = mockMvc.perform(delete("/notification")
											.param("userName", userName));
		
		result.andExpect(status().isNoContent())
				.andDo(print());
		
		verify(notificationMessageService).clearAllMessagesForThisUser(userName);
	}
	
}
