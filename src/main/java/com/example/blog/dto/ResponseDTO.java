package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
	-> Response에 담을 object는 없지만 특정 message가 전달되어야 할 때 그것을 client에게 보내는 데에 사용되는 DTO이다;
	raw string을 바로 보내면 JSON을 사용해서 서버와 소통하는 API 특성 상 문제 발생의 여지가 있기에 DTO로 wrap했다.
	원래는 message조차 전달될 필요가 없는 경우라면 status 204를 return하는 것으로 충분하다고 생각했지만, 그 경우에도 일괄적으로 
	이 DTO를 사용하는 편이 일관성의 유지 측면에서 좋을 것이라고 생각해서 기각했다.
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
	
	private String data;
	
	private Boolean boolData;
	
}
