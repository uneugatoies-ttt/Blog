package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
	-> NotificationMessage entity를 frontend 및 상위 layer에서 표현하는 DTO이다.
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationMessageDTO {

	private Long id;
	
	private String message;
	
	private String recipient;

	private String where;

}
