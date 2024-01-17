package com.example.blog.dto;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	
	private String id;
	
	/* 
		-> "userName"에는 whitespace가 포함되어서는 안된다.
	
		-> 처음에는 "\"userName\" must not contain any whitespace"를 message로 주려고 했지만,
		어째서인지 validation이 적용되어 exception이 throw되었을 경우에 exception handler method로 처리된
		response에 담긴 message를 이후에 보면 escaping의 용도로 포함시켰던 \가 그대로 남아있는 문제가 있었다.
		해결하려고 했지만 방법을 찾지 못했기 때문에, 일단은 "를 '로 변경하는 것으로 임시적인 처리를 해두었다.
	*/
	@Pattern(regexp = "[^\\s]+", message = "'userName' must not contain any whitespace")
	private String userName;
	
	private String password;
	
	private String email;
	
	private String token;
	
	private String blogTitle;
	
}
