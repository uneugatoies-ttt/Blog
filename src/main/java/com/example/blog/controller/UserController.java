package com.example.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.domain.User;
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
	public ResponseEntity<?> signup(@RequestBody UserDTO userDTO) {
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
	public ResponseEntity<?> signin(@RequestBody UserDTO userDTO) {
		try {
			User user = userService.getUserByCredentials(userDTO.getUserName(), userDTO.getPassword());
			if (user != null) {
				final String token = userService.createToken(user);
				final UserDTO responseUserDTO = UserDTO
							.builder()
							.id(user.getId())
							.userName(user.getUserName())
							.token(token)
							.build();
					
				return ResponseEntity.ok().body(responseUserDTO);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
