package com.example.blog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ResponseListDTO;

@RestController
public class TestController {
	
	@GetMapping("/test")
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
	
}
