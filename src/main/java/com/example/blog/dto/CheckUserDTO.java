package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckUserDTO {
	
	private String pathUserName;
	
	private String notCertifiedUserName;
	
	private String notCertifiedToken;

}
