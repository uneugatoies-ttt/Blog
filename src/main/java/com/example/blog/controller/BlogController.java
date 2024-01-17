package com.example.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ResponseDTO;
import com.example.blog.service.UserService;

/*
	-> BlogController는 parameter로 들어온 "userName"에 대응되는 "blogTitle"의 value를 
	return하기 위한 controller이다.
*/

@RestController
@RequestMapping("/blog")
public class BlogController {
	
	private UserService userService;
	
	public BlogController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/title")
	public ResponseEntity<?> getBlogTitleByUserName(
		@RequestParam String userName
	) {
		try {
			String title = userService.getBlogTitleByUserName(userName);
			ResponseDTO responseDTO = ResponseDTO.builder()
												.data(title)
												.build();
			return ResponseEntity.ok().body(responseDTO);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
