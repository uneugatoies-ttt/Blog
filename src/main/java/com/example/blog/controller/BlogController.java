package com.example.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ResponseDTO;
import com.example.blog.service.UserService;

@RestController
@RequestMapping("/blog")
public class BlogController {
	
	private UserService userService;
	
	public BlogController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/title")
	public ResponseEntity<?> getBlogTitle(@RequestParam String userName) {
		try {
			ResponseDTO responseDTO = ResponseDTO.builder()
												.data(userService.getBlogTitle(userName))
												.build();
			return ResponseEntity.ok().body(responseDTO);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
