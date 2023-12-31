package com.example.blog.dto;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	
	private String id;
	
	@Pattern(regexp = "[^_-]+")
	private String userName;
	
	private String password;
	
	private String email;
	
	private String token;
	
	private String blogTitle;
	
}
