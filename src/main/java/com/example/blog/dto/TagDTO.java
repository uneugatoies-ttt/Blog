package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
	-> Category entity를 frontend 및 상위 layer에서 표현하는 DTO이다.
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDTO {
	
	private Long id;
	
	private String user;
	
	private String name;
	
}
