package com.example.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.NotificationMessageDTO;
import com.example.blog.dto.ResponseDTO;
import com.example.blog.dto.ResponseListDTO;
import com.example.blog.service.NotificationMessageService;

@RestController
@RequestMapping("/notification")
public class NotificationMessageController {
	
	private NotificationMessageService notificationMessageService;
	
	public NotificationMessageController(NotificationMessageService notificationMessageService) {
		this.notificationMessageService = notificationMessageService;
	}
	
	@GetMapping
	public ResponseEntity<?> getAllMessagesForThisUser(@RequestParam String userName) {
		try {
			List<NotificationMessageDTO> messages = notificationMessageService.getAllMessagesForThisUser(userName);
			ResponseListDTO<NotificationMessageDTO> dto = ResponseListDTO.<NotificationMessageDTO>builder()
														.data(messages)
														.build();
			return ResponseEntity.ok().body(dto);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	// Remember not to set raw string data as the body of a response;
	// you'll see the error that says Unexpected token is not valid JSON.
	@DeleteMapping
	public ResponseEntity<?> clearAllMessagesForThisUser(@RequestParam String userName) {
		try {
			notificationMessageService.clearAllMessagesForThisUser(userName);
			
			ResponseDTO dto = ResponseDTO.builder()
											.data("All messages successfully deleted")
											.build();
			return ResponseEntity.ok().body(dto);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
