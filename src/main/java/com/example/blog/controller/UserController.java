package com.example.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ResponseDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Validated @RequestBody UserDTO userDTO) {
		try {
			if (userDTO == null || userDTO.getPassword() == null)
				throw new RuntimeException("invalid password");
			UserDTO resultingUserDTO = userService.createUser(userDTO);
			return ResponseEntity.ok().body(resultingUserDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> signin(@Validated @RequestBody UserDTO userDTO) {
		try {
			UserDTO resultingUserDTO = userService.getUserByCredentials(userDTO.getUserName(), userDTO.getPassword());
			if (resultingUserDTO == null)
				return ResponseEntity.notFound().build();
			else
				return ResponseEntity.ok().body(resultingUserDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	

	@PostMapping("/user-deletion")
	public ResponseEntity<?> deleteUser(@Validated @RequestBody UserDTO userDTO) {
		try {
			if (userDTO == null || userDTO.getPassword() == null) 
				throw new RuntimeException("invalid password");
			String message = userService.deleteUser(userDTO);
			return ResponseEntity.ok().body(ResponseDTO.builder().data(message).build());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
