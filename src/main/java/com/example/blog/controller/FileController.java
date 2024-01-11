package com.example.blog.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dto.FileDTO;
import com.example.blog.dto.ResponseDTO;
import com.example.blog.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/file")
public class FileController {
	
	private FileService fileService;
	
	public FileController(
			FileService fileService
			//ObjectMapper objectMapper
	) {
		this.fileService = fileService;
		//this.objectMapper = objectMapper;
	}
	
	@GetMapping
	public ResponseEntity<?> getFile(
			@RequestParam String fileName,
			@RequestParam String uploader
	) {
		try {
			String fileNameWithHyphen = fileName.replace(' ', '-').replace('_', '-');
			String userNameWithHyphen = uploader.replace(' ', '-').replace('_', '-');
			Resource resultingFileResource = fileService.getFile(fileNameWithHyphen, userNameWithHyphen);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resultingFileResource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@GetMapping("/presence")
	public ResponseEntity<?> isFileNamePresent(
			@RequestParam String fileName,
			@RequestParam String uploader
	) {
		try {
			String fileNameWithHyphen = fileName.replace(' ', '-').replace('_', '-');
			String userNameWithHyphen = uploader.replace(' ', '-').replace('_', '-');
			boolean presence = fileService.isFileNamePresent(fileNameWithHyphen, userNameWithHyphen);
			return ResponseEntity.ok().body(presence);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@PostMapping
	public ResponseEntity<?> insertNewFile(
			@RequestPart("file") MultipartFile file,
			@RequestPart("userName") String userName,
			@RequestPart("articleId") Long articleId
	) {
		if (file.isEmpty())
			return ResponseEntity.badRequest().body("No file has been sent");
		try {
			FileDTO resultingFileDTO = fileService.insertNewFileInSystem(file, userName, articleId);
			return ResponseEntity.ok().body(resultingFileDTO);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
}
