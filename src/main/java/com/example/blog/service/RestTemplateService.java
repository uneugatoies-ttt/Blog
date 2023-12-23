package com.example.blog.service;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class RestTemplateService {
	
	public String getName() {
		URI uri = UriComponentsBuilder
				.fromUriString("http://localhost:9090")
				.path("/api/v1/crud-api")
				.encode()
				.build()
				.toUri();
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri,  String.class);
		
		return responseEntity.getBody();
	}
	
	public String getNameWithPathVariable() {
		URI uri = UriComponentsBuilder
				.fromUriString("http://localhost:9090")
				.path("/api/v1/crud-api/{name}")
				.encode()
				.build()
				.expand("Some Name")
				.toUri();
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
		
		return responseEntity.getBody();
	}
	
	public String getNameWithParameter() {
		URI uri = UriComponentsBuilder
				.fromUriString("https://localhost:9090")
				.path("/api/v1/curd-api/param")
				.queryParam("name", "Some Name")
				.encode()
				.build()
				.toUri();
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
		
		return responseEntity.getBody();
	}

}
