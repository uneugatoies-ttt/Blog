package com.example.blog.controller;

import org.springframework.core.io.ClassPathResource;
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
import com.example.blog.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/file")
public class FileController {
	
	private static final String IMG_STORAGE_DIRECTORY = 
			"." + File.separator +
			"src" + File.separator +
			"main" + File.separator +
			"resources" + File.separator + 
			"static" + File.separator;
	
	private FileService fileService;
	
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}
	
	@GetMapping
	public ResponseEntity<?> getFile(@RequestParam String fileName, @RequestParam String userName) {
		try {
			String fileNameWithUnderBar = fileName.replace(' ', '_');
			String userNameWithUnderBar = userName.replace(' ', '_');
			
			Resource fileResource = 
					new ClassPathResource(
							"static" + 
							File.separator +
							userNameWithUnderBar +
							File.separator +
							fileNameWithUnderBar
						);
			if (fileResource.exists()) {
				return ResponseEntity
						.ok()
						.contentType(MediaType.IMAGE_JPEG)
						.body(fileResource);
			}
			return null;
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity<?> insertNewFile(
			@RequestPart("file") MultipartFile file,
			@RequestPart("fileDTO") String fileDTOJson
	) {
		if (file.isEmpty())
			return ResponseEntity.badRequest().body("No file has been sent");
		
		try {
			FileDTO fileDTO = new ObjectMapper().readValue(fileDTOJson, FileDTO.class);
			String fileNameWithUnderBar = fileDTO.getFileName().replace(' ', '_');
			String userNameWithUnderBar = fileDTO.getUploader().replace(' ', '_');
			byte[] bytes = file.getBytes();
			
	        Path directoryPath = Paths.get(
	                IMG_STORAGE_DIRECTORY + userNameWithUnderBar
	        );

	        // Create the directories if they don't exist
	        Files.createDirectories(directoryPath);
			
			Path path = Paths.get(
					directoryPath.toString() + File.separator +
					fileNameWithUnderBar + "." +
					extractFileExtension(file.getOriginalFilename())
				);
			Files.write(path, bytes);
			
			FileDTO storedFileDTO = fileService.insertNewFile(fileDTO, path.toString());
			
			return ResponseEntity.ok().body(storedFileDTO);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Failed to upload file");
		}
	}
	
	private String extractFileExtension(String filename) {
	    int lastDotIndex = filename.lastIndexOf('.');
	    if (lastDotIndex > 0) {
	        return filename.substring(lastDotIndex + 1);
	    }
	    return ""; // there's no extension
	}

}
