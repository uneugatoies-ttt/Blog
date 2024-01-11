package com.example.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Pattern;

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
	
	@Pattern(regexp = "[^_-]+")
	private String title;
	
	// "category" and "tag" represent their ID in the database.
	// "categoryName" and "tagName" represent their name.
	private Long category;
	
	private String categoryName;
	
	private List<Long> tag;
	
	private List<String> tagName;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	// 이 article의 main image에 해당하는 file의 fileName
	private String mainImage;
	
}
