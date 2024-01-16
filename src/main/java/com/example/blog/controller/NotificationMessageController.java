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

/*
	-> NotificationMessage는 현재 이 user의 blog의 article에 reply가 달렸을 때에 자동적으로 추가되며,
	main 화면에서 그 user가 "CLEAR MESSAGES"라는 label이 붙은 button을 click하면 일괄적으로 삭제된다.
	그러므로 UPDATE나 CREATE에 대한 API를 작성할 필요가 없다고 판단했다. 따라서 그들은 생략한다.
*/

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
	
	//raw string data를 response의 body에 바로 지정하지 않도록 할 것.
	@DeleteMapping
	public ResponseEntity<?> clearAllMessagesForThisUser(@RequestParam String userName) {
		try {
			notificationMessageService.clearAllMessagesForThisUser(userName);
			return ResponseEntity.ok().body(ResponseDTO.builder().data("Messages deleted successfully").build());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
