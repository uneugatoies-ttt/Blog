package com.example.blog.controller;

import org.slf4j.LoggerFactory;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ValidatedRequestDTO;
import com.example.blog.dto.ValidationRequestDTO;
import com.example.blog.validation.group.ValidationGroup1;
import com.example.blog.validation.group.ValidationGroup2;

@RestController
@RequestMapping("/validation")
public class ValidationController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ValidationController.class);
	
	@PostMapping("/validated")
	public ResponseEntity<String> checkValidation(
			@Validated @RequestBody ValidatedRequestDTO validatedRequestDTO
	) {
		LOGGER.info(validatedRequestDTO.toString());
		return ResponseEntity.ok().body(validatedRequestDTO.toString());
	}
	
	@PostMapping("/validated/group1")
	public ResponseEntity<String> checkValidation1(
			@Validated(ValidationGroup1.class) @RequestBody ValidatedRequestDTO validatedRequestDTO
	) {
		LOGGER.info(validatedRequestDTO.toString());
		return ResponseEntity.ok().body(validatedRequestDTO.toString());
	}
	
	@PostMapping("/validated/group2")
	public ResponseEntity<String> checkValidation2(
			@Validated(ValidationGroup2.class) @RequestBody ValidatedRequestDTO validatedRequestDTO
	) {
		LOGGER.info(validatedRequestDTO.toString());
		return ResponseEntity.ok().body(validatedRequestDTO.toString());
	}
	
	@PostMapping("/validated/all-group")
	public ResponseEntity<String> checkValidation3(
			@Validated({ValidationGroup1.class, ValidationGroup2.class})
			@RequestBody ValidatedRequestDTO validatedRequestDTO
	) {
		LOGGER.info(validatedRequestDTO.toString());
		return ResponseEntity.ok().body(validatedRequestDTO.toString());
	}
	
	@PostMapping("/valid")
	public ResponseEntity<String> checkValidationByValid(
			@Valid @RequestBody ValidationRequestDTO validDTO
	) {
		LOGGER.info(validDTO.toString());
		return ResponseEntity.ok().body(validDTO.toString());
	}

}
