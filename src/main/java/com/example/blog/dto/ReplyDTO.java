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
public class ReplyDTO {
	private Long id;
	private String content;
	private String from;
	private String writer;
	private Long article;
	private String where;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
