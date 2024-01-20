package com.example.blog.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
	-> Response에 담겨서 전달되어야 하는 data가 List의 형태로 있을 때에 그들을 wrap하기
	위해서 사용되는 DTO이다. type parameter T에는 물론 "data" field의 List의
	type parameter T와 일치되는 type이 명시되어야 할 것이다.
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseListDTO<T> {
	
	private String error;
	
	private List<T> data;
	
}
