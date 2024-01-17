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
	
	/*
		-> "category"와 "tag"는 이 article의 category와 tag들이 database에서 가지는 ID의 값이다.
		
		-> "categoryName"과 "tagName"은 그들의 이름이다.
	*/
	private Long category;
	
	private String categoryName;
	
	private List<Long> tag;
	
	private List<String> tagName;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	// 이 article의 main image에 해당하는 file의 fileName이다.
	private String mainImage;
	
}
