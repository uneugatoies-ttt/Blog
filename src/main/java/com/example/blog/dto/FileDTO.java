package com.example.blog.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDTO {
	
	private Long id;
	
	@Pattern(regexp = "[^_-]+")
	private String fileName;
	
	private String uploader;
	
	private String filePath;
	
	private Long articleId;
	
	private LocalDateTime createdAt;

}
