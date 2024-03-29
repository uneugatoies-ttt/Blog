package com.example.blog.practice;
/*
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ResponseDTO;
import com.example.blog.dto.ResponseListDTO;
import com.example.blog.dto.TestRegexDTO;

@RestController
@RequestMapping("/test")
public class PracticeController001 {
	
	@GetMapping("/test1")
	public ResponseEntity<?> testHandler() {
		try {
			List<String> list = new ArrayList<>();
			list.add("OKAY");
			ResponseListDTO<String> dto = ResponseListDTO.<String>builder()
												.data(list)
												.build();
			return ResponseEntity.ok().body(dto);
		} catch (Exception e) {
			ResponseListDTO<String> dto = ResponseListDTO.<String>builder()
												.error("Something went wrong")
												.build();
			return ResponseEntity.badRequest().body(dto);
		}
	}
	
	@PostMapping("/regex-valid-test")
	public ResponseEntity<?> testRegexValidation(@Validated @RequestBody TestRegexDTO tid) {
		try {
			return ResponseEntity.ok().body(tid);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
}*/
