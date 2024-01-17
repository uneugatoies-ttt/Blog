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

/*
	-> 기존에는 file의 이름을 user로부터 input으로 받아 지정하고, 이후 user가 그 이름을 수정할
	수 있게 하려고 했지만, 불필요하고 거의 무의미한 기능이라고 여겼기에 그것은 구현하지 않는 것으로 방향을 변경했다.
*/

@RestController
@RequestMapping("/file")
public class FileController {
	
	private FileService fileService;
	
	public FileController(
			FileService fileService
	) {
		this.fileService = fileService;
	}
	
	@PostMapping
	public ResponseEntity<?> insertNewFile(
		@RequestPart("file") MultipartFile file,
		@RequestPart String userName,
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
	
	@GetMapping
	public ResponseEntity<?> getFile(
		@RequestParam String fileName,
		@RequestParam String uploader
	) {
		try {
			Resource resultingFileResource = fileService.getFile(fileName, uploader);
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
			boolean presence = fileService.isFileNamePresent(fileName, uploader);
			return ResponseEntity.ok().body(presence);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
