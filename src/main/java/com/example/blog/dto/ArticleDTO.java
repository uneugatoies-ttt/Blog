package com.example.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleDTO {
	private Long id;
	private String writer;
	private String content;
	private String title;
	// tags and category represent their ID in the database.
	private Long category;
	private List<Long> tag;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
