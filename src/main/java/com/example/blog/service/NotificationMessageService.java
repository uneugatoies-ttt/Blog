package com.example.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.blog.domain.NotificationMessage;
import com.example.blog.dto.NotificationMessageDTO;
import com.example.blog.persistence.NotificationMessageRepository;
import com.example.blog.persistence.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationMessageService {
	
	private NotificationMessageRepository notificationMessageRepository;
	private UserRepository userRepository;

	@Transactional
	public List<NotificationMessageDTO> getAllMessagesForThisUser(String userName) {
		try {
			List<NotificationMessage> allMessages = 
						notificationMessageRepository
							.findAllByRecipient(userRepository.findByUserName(userName).get())
								.orElseThrow(() -> new EntityNotFoundException("Message not found"));
			
			List<NotificationMessageDTO> allMessagesDTO =
						allMessages.stream()
							.map(m -> NotificationMessageDTO.builder()
								.id(m.getId())
								.message(m.getMessage())
								.recipient(m.getRecipient().getUserName())
								.where(m.getWhere())
								.build()).collect(Collectors.toList()
							);

			return allMessagesDTO;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional
	public void clearAllMessagesForThisUser(String userName) {
		try {
			notificationMessageRepository.deleteAllByRecipient(userRepository.findByUserName(userName).get());
		} catch (Exception e) {
			throw e;
		}
	}
	
}
