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
		Long[] ids = new Long[3];
		ids[0] = 3L;
		ids[1] = 4L;
		ids[2] = 5L;
		String[] paths = new String[3];
		paths[0] = "/TestUser/article/335";
		paths[1] = "/TestUser/article/87";
		paths[2] = "/TestUser/article/991";
		
		List<NotificationMessageDTO> messages = new ArrayList<>();
		messages.add(NotificationMessageDTO.builder()
				.id(ids[0])
				.message("Test message 1")
				.recipient(userName)
				.where(paths[0])
				.build());
		messages.add(NotificationMessageDTO.builder()
				.id(ids[1])
				.message("Test message 2")
				.recipient(userName)
				.where(paths[1])
				.build());
		messages.add(NotificationMessageDTO.builder()
				.id(ids[2])
				.message("Test message 3")
				.recipient(userName)
				.where(paths[2])
				.build());
		
		when(notificationMessageService.getAllMessagesForThisUser(userName))
				.thenReturn(messages);
		
		ResultActions result = mockMvc.perform(get("/notification")
											.param("userName", userName));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data", hasSize(3)));
		
		for (int i = 0; i < 3; ++i) {
			result
				.andExpect(jsonPath("$.data[" + i + "].id").value(ids[i]))
				.andExpect(jsonPath("$.data[" + i + "].message").value("Test message " + (i + 1)))
				.andExpect(jsonPath("$.data[" + i + "].recipient").value(userName))
				.andExpect(jsonPath("$.data[" + i + "].where").value(paths[i]));
		}
		result
				.andDo(print());
		
		verify(notificationMessageService).getAllMessagesForThisUser(userName);
	}
	
	@Test
	@DisplayName("Test for clearAllMessagesForThisUser: successful case")
	void clearAllMessagesForThisUserTest() throws Exception {
		String userName = "TestUser";
		
		ResultActions result = mockMvc.perform(delete("/notification")
											.param("userName", userName));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Messages deleted successfully"))		
				.andDo(print());
		
		verify(notificationMessageService).clearAllMessagesForThisUser(userName);
	}
	
}
