package com.example.blog.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.blog.dto.MemberDTO;

import reactor.core.publisher.Mono;

@Service
public class WebClientService {
	
	private static final String baseURL = "http://localhost:9090";
	private static final String basePathName = "/public/api/v1/crud-api";
	
	public String getName() {
		WebClient webClient = WebClient.builder()
								.baseUrl(baseURL)
								.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
								.build();
		
		return webClient
				.get()
				.uri(basePathName)
				.retrieve()
				.bodyToMono(String.class)
				.block();
	}
	
	public String getNameWithPathVariable() {
		WebClient webClient = WebClient.create(baseURL);
		
		ResponseEntity<String> responseEntity = 
				webClient
					.get()
					.uri(uriBuilder -> uriBuilder.path(basePathName + "/{name}")
						.build("Some Name"))
					.retrieve().toEntity(String.class).block();
		
		return responseEntity.getBody();
		// The above URI setting can be written in a more simpler way as follows:
		/*
			ResponseEntity<String> responseEntity = 
						webClient
							.get()
							.uri("/public/api/v1/crud-api/{name}", "Some Name")
							.toEntity(String.class)
							.block();
		*/
	}
	
	public String getNameWithParameter() {
		WebClient webClient = WebClient.create(baseURL);
		
		return webClient
					.get()
					.uri(uriBuilder -> 
							uriBuilder.path(basePathName)
									.queryParam("name", "Some Name")
									.build())
					.exchangeToMono(clientResponse -> {
						if (clientResponse.statusCode().equals(HttpStatus.OK))
							return clientResponse.bodyToMono(String.class);
						else 
							return clientResponse.createException().flatMap(Mono::error);
					})
					.block();
	}
	
	public ResponseEntity<MemberDTO> postWithParamAndBody() {
		WebClient webClient = WebClient.builder()
							.baseUrl(baseURL)
							.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
							.build();
		
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.setName("Some Name");
		memberDTO.setEmail("somename@test.com");
		memberDTO.setOrganization("Some Organization");
		
		return webClient
				.post()
				.uri(uriBuilder -> uriBuilder.path(basePathName)
								.queryParam("name", "Some Name 2")
								.queryParam("email", "somename2@test.com")
								.queryParam("organization", "Some Organization")
								.build())
				.bodyValue(memberDTO)
				.retrieve()
				.toEntity(MemberDTO.class)
				.block();
	}
	
	public ResponseEntity<MemberDTO> postWithHeader() {
		WebClient webClient = WebClient.builder()
							.baseUrl(baseURL)
							.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
							.build();
		
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.setName("Some Name");
		memberDTO.setEmail("somename@test.com");
		memberDTO.setOrganization("Some Organization");
		
		return webClient
				.post()
				.uri(uriBuilder -> uriBuilder.path(basePathName + "/add-header")
							.build())
				.bodyValue(memberDTO)
				.header("my-header", "Some API")
				.retrieve()
				.toEntity(MemberDTO.class)
				.block();
	}

}
