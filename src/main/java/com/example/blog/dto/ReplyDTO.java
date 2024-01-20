package com.example.blog.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
	-> Reply entity를 frontend 및 상위 layer에서 표현하는 DTO이다.
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDTO {
	
	private Long id;
	
	private String content;
	
	private String writer;
	
	private Long articleId;
	
	private String where;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
}
