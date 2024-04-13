package com.example.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
	-> HealthCheckController는 AWS 환경에서 이 애플리케이션이 동작하는지 확인하는 데에 사용되는 controller이다.
*/
@RestController
public class HealthCheckController {
	
	@GetMapping("/")
	public String healthCheck() {
		return "up and running";
	}

}
