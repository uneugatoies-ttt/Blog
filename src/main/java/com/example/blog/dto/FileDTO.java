package com.example.blog.dto;

import java.time.LocalDateTime;

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
	private String fileName;
	private String uploader;
	private String filePath;
	private String description;
	private String fileType;
	private LocalDateTime createdAt;
}
