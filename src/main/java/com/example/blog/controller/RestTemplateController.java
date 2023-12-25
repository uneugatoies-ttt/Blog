package com.example.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.MemberDTO;
import com.example.blog.service.RestTemplateService;

@RestController
@RequestMapping("/rest-template")
public class RestTemplateController {
	
	private final RestTemplateService restTemplateService;
	
	public RestTemplateController(RestTemplateService restTemplateService) {
		this.restTemplateService = restTemplateService;
	}
	
	@GetMapping
	public String getName() {
		return restTemplateService.getName();
	}
	
	@GetMapping("/path-variable")
	public String getNameWithPathVarible() {
		return restTemplateService.getNameWithPathVariable();
	}
	
	@GetMapping("/parameter")
	public String getNameWithParameter() {
		return restTemplateService.getNameWithParameter();
	}
	
	@PostMapping
	public ResponseEntity<MemberDTO> postDTO() {
		return restTemplateService.postWithParamAndBody();
	}
	
	@PostMapping("/header")
	public ResponseEntity<MemberDTO> postWithHeader() {
		return restTemplateService.postWithHeader();
	}

}
