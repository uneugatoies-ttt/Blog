package com.example.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
	-> WebMvcConfig는 backend와 frontend가 decoupled architecture를 유지할 때
	발생하는 CORS 문제를 해결하기 위한 설정을 담고 있는 class이다.
	
	현재 "http://localhost:3000" 을 허용된 origin으로 설정하고 있는데, 이것은 test용의
	frontend application이 그 domain을 사용하고 있기에 가능한 것이다.
*/

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	private final long MAX_AGE_SECS = 3600;
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000")
			.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
			.allowedHeaders("*")
			.allowCredentials(true)
			.maxAge(MAX_AGE_SECS);
	}
	
}
