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
public class TestRegexDTO {
	
	@Pattern(
		regexp = "[^_-]+",
		message = "String should not contain spaces or underscores"
	)
	private String someField;

}
