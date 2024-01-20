package com.example.blog.dto;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
	-> 이것은 이 "userName"이, 이 "token"이 issue 되었을 때의 subject에
	부합하는지를 확인하기 위해 사용되는 DTO이다; 즉, userName과 JWT의 부합 여부를
	확인하기 위해 사용된다.
	
	확인 과정 자체는 UserService와 TokenProvider 등의 method 내부에서 처리된다.
	이것은 다른 DTO가 그렇듯 오로지 필요한 data를 전달하는 역할만을 한다.
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckUserDTO {
	
	// "userName"에는 whitespace가 포함되어서는 안된다.
	@Pattern(regexp = "[^\\s]+", message = "'userName' must not contain any whitespace")
	private String pathUserName;
	
	@Pattern(regexp = "[^\\s]+", message = "'userName' must not contain any whitespace")
	private String notCertifiedUserName;
	
	private String notCertifiedToken;

}
