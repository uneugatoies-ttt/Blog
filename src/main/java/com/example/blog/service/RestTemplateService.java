package com.example.blog.service;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.blog.dto.MemberDTO;

@Service
public class RestTemplateService {
	
	public String getName() {
		URI uri = UriComponentsBuilder
				.fromUriString("http://localhost:9090")
				.path("/public/api/v1/crud-api")
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
				.path("/public/api/v1/crud-api/{name}")
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
				.fromUriString("http://localhost:9090")
				.path("/public/api/v1/crud-api/param")
				.queryParam("name", "Some Name")
				.encode()
				.build()
				.toUri();
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
		
		return responseEntity.getBody();
	}
	
	
	/*
	public ResponseEntity<MemberDTO> postWithParamAndBody() {
		URI uri = UriComponentsBuilder
				.fromUriString("http://localhost:9090")
				.path("/public/api/v1/crud-api/param")
				.queryParam("name", "Some Name")
				.queryParam("email",  "somename@test.com")
				.queryParam("organization", "Some Organization")
				.encode()
				.build()
				.toUri();
		
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.setName("Some Name2");
		memberDTO.setEmail("somename2@test.com");
		memberDTO.setOrganization("Some Organization 2");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RequestEntity<MemberDTO> requestEntity = RequestEntity
				.post(uri)
				.headers(headers)
				.body(memberDTO);
									
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<MemberDTO> responseEntity = 
				restTemplate.exchange(requestEntity, MemberDTO.class);
				//restTemplate.postForEntity(uri, memberDTO, MemberDTO.class);
		
		return responseEntity;
	}*/
	
	public ResponseEntity<MemberDTO> postWithParamAndBody() {
		URI uri = UriComponentsBuilder
				.fromUriString("http://localhost:9090")
				.path("/public/api/v1/crud-api")
				.queryParam("name", "Some Name")
				.queryParam("email",  "somename@test.com")
				.queryParam("organization", "Some Organization")
				.encode()
				.build()
				.toUri();
		
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.setName("Some Name2");
		memberDTO.setEmail("somename2@test.com");
		memberDTO.setOrganization("Some Organization 2");
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<MemberDTO> responseEntity = 
				restTemplate.postForEntity(uri, memberDTO, MemberDTO.class);
		
		return responseEntity;
	}
	
	public ResponseEntity<MemberDTO> postWithHeader() {
		URI uri = UriComponentsBuilder
				.fromUriString("http://localhost:9090")
				.path("/public/api/v1/crud-api/add-header")
				.encode()
				.build()
				.toUri();
		
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.setName("Some Name");
		memberDTO.setEmail("somename@test.com");
		memberDTO.setOrganization("Some Organization");
		
		RequestEntity<MemberDTO> requestEntity = 
				RequestEntity
					.post(uri)
					.header("my-header", "API")
					.body(memberDTO);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<MemberDTO> responseEntity = 
				restTemplate.exchange(requestEntity,  MemberDTO.class);
		
		return responseEntity;
	}

}
