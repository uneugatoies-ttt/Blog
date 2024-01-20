package com.example.blog.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
	-> File entity를 frontend 및 상위 layer에서 표현하는 DTO이다.
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDTO {
	
	private Long id;
	
	private String fileName;
	
	private String uploader;
	
	private String filePath;
	
	private Long articleId;
	
	private LocalDateTime createdAt;

}
