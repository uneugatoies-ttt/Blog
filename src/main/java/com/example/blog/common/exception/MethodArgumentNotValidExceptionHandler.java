package com.example.blog.common.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.blog.dto.ResponseDTO;

/*
	-> UserDTO와 CheckUserDTO에 적용된 @Pattern에는 regular expression으로 
	whitespace가 포함될 수 없다는 constraint를 설정해두었다. 이 exception handler
	method는 그 제한 사항을 위반한 data가 들어왔을 때 MethodArgumentNotValidException가
	throw된 경우에 대한 처리를 담당하는 용도로 사용된다.
	
	-> 이 handler를 일괄적으로 사용해서 "userName"의 제한 사항을 관리하고 싶었기 때문에, 기존에는
	GET method에서 "userName"만을 parameter로 받아들이던 method들을 전부 POST method로
	UserDTO를 JSON으로 받아들이도록 변경하려고 했지만, HTTP method들의 구분도 더 어려워지고 전체적인
	logic의 의도가 불분명해진다는 느낌이 있었기 때문에, URL parameter로 들어오는 "userName"들은
	기본적으로 frontend에서 확인을 한다는 전제를 세우고 기존의 method들은 변경하지 않기로 했다.
	
	GET이나 DELETE 등의 method도 body를 포함하게 할 수는 있지만, 가능한 한 기본적인 상세에서 벗어나지
	않는 동작 방식을 유지하고 싶었기에 그것은 하지 않는 편이 좋다고 판단했다.
	
	다만 이것은 application의 기조에 대해 불안 요소로 계속해서 잠재해 있는 것이나 마찬가지라고 생각하기 때문에
	가능하면 validation을 좀 더 철저하게 하고 싶지만, 지금은 일일이 들어오는 URL parameter를 
	확인하는 것밖에 생각나지 않는다.
*/

@RestControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseDTO> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e,
		HttpServletRequest request
	) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
		
		ResponseDTO responseDTO = ResponseDTO.builder()
											.data(message)
											.build();
		
		return ResponseEntity.badRequest().headers(headers).body(responseDTO);
	}

}
