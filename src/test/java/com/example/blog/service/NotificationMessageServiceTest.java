package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.example.blog.domain.NotificationMessage;
import com.example.blog.domain.User;
import com.example.blog.dto.NotificationMessageDTO;
import com.example.blog.persistence.NotificationMessageRepository;
import com.example.blog.persistence.UserRepository;

@ExtendWith(SpringExtension.class)
@Import({NotificationMessageService.class})
public class NotificationMessageServiceTest {
	
	@MockBean
	private NotificationMessageRepository notificationMessageRepository;
	@MockBean
	private UserRepository userRepository;
	
	@Autowired
	private NotificationMessageService notificationMessageService;
	
	@Test
	@DisplayName("Test for getAllMessagesForThisUser: successful case")
	void getAllMessagesForThisUserTest() throws Exception {
		String userName = "TestUser";
		User user = ServiceTestSupporting.buildUser();
		
		List<NotificationMessage> allMessages = new ArrayList<>();
		allMessages.add(NotificationMessage.builder()
							.id(3L)
							.message("Test Message 1")
							.recipient(user)
							.where("/blog/testuser/content1")
							.build());
		allMessages.add(NotificationMessage.builder()
							.id(4L)
							.message("Test Message 2")
							.recipient(user)
							.where("/blog/testuser/content2")
							.build());
		allMessages.add(NotificationMessage.builder()
							.id(5L)
							.message("Test Message 3")
							.recipient(user)
							.where("/blog/testuser/content3")
							.build());
		
		List<NotificationMessageDTO> allMessagesDTO =
			allMessages.stream().map(m -> NotificationMessageDTO.builder()
					.id(m.getId())
					.message(m.getMessage())
					.recipient(m.getRecipient().getUserName())
					.where(m.getWhere())
					.build()).collect(Collectors.toList());
		
		when(userRepository.findByUserName(userName))
			.thenReturn(Optional.of(user));
		when(notificationMessageRepository.findAllByRecipient(user))
			.thenReturn(Optional.of(allMessages));
		
		List<NotificationMessageDTO> allMessagesDTOFromService =
			notificationMessageService.getAllMessagesForThisUser(userName);
		
		assertThat(allMessagesDTOFromService)
			.isNotNull()
			.hasSize(3);
		
		for (int i = 0; i < 3; ++i) {
			assertThat(allMessagesDTOFromService.get(i))
				.extracting(
					NotificationMessageDTO::getId,
					NotificationMessageDTO::getMessage,
					NotificationMessageDTO::getRecipient,
					NotificationMessageDTO::getWhere
				)
				.containsExactly(
					allMessagesDTO.get(i).getId(),
					allMessagesDTO.get(i).getMessage(),
					allMessagesDTO.get(i).getRecipient(),
					allMessagesDTO.get(i).getWhere()
				);
		}
		
		verify(userRepository, times(1)).findByUserName(userName);
		verify(notificationMessageRepository, times(1)).findAllByRecipient(user);
	}
	
	@Test
	@DisplayName("Test for clearAllMessagesForThisUser(): successful case")
	void clearAllMessagesForThisUserTest() throws Exception {
		String userName = "TestUser";
		User user = ServiceTestSupporting.buildUser();
		
		when(userRepository.findByUserName(userName))
			.thenReturn(Optional.of(user));
		
		notificationMessageService.clearAllMessagesForThisUser(userName);
		
		verify(userRepository, times(1)).findByUserName(userName);
		verify(notificationMessageRepository, times(1)).deleteAllByRecipient(user);
	}

}
