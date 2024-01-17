package com.example.blog.dto;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckUserDTO {
	
	// "userName"에는 whitespace가 포함되어서는 안된다.
	@Pattern(regexp = "[^\\s]+", message = "'userName' must not contain any whitespace")
	private String pathUserName;
	
	@Pattern(regexp = "[^\\s]+", message = "'userName' must not contain any whitespace")
	private String notCertifiedUserName;
	
	private String notCertifiedToken;

}
