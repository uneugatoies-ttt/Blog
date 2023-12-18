package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response에 담을 object는 없지만 특정 message가 전달되어야 할 때 그것을 client에게 보내는 데에 사용되는 DTO.
// raw string을 바로 보내면 JSON을 사용해서 서버와 소통하는 API 특성 상 문제 발생의 여지가 있기에 DTO로 wrap함.
// 만약 message 또한 특별히 전달될 필요가 없다면, 단순히 204 No Content를 response의 status code로 지정하는
// 것만으로 충분할 듯.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
	private String data;
}
