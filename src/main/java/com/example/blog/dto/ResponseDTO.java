package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 특별히 response에 담을 데이터가 없을 때, 성공했음을 나타내는 메시지를 client에게 보내는 데에 사용되는 DTO.
// raw string을 바로 보내면 JSON을 사용해서 서버와 소통하는 API 특성 상 문제 발생의 여지가 있기에 DTO로 wrap함.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
	private String data;
}
