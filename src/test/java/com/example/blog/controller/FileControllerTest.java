package com.example.blog.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import javax.servlet.http.Part;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dto.FileDTO;
import com.example.blog.security.TokenProvider;
import com.example.blog.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FileController.class)
public class FileControllerTest {
	
	private static final String IMG_STORAGE_DIRECTORY = 
			"." + File.separator +
			"src" + File.separator +
			"main" + File.separator +
			"resources" + File.separator + 
			"static" + File.separator;
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private FileService fileService;
	@MockBean
	private TokenProvider tokenProvider;

	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	@DisplayName("Test for insertNewFile()")
	void insertNewFileTest() throws Exception {
		FileDTO fileDTO = FileDTO.builder()
				.fileName("fileName")
				.uploader("TestUser")
				.description("Test file")
				.fileType("image")
				.build();
		
		String filePathPath = IMG_STORAGE_DIRECTORY +
				"TestUser" + File.separator + "fileName.jpg";
		
		FileDTO resultingDTO = FileDTO.builder()
				.id(10L)
				.fileName("fileName")
				.uploader("TestUser")
				.description("Test file")
				.fileType("image")
				.filePath(filePathPath)
				.createdAt(LocalDateTime.now())
				.build();
		
		when(fileService.insertNewFile(fileDTO, filePathPath))
			.thenReturn(resultingDTO);
		
		MockMultipartFile fileDTOJson = 
				new MockMultipartFile(
						"fileDTO",
						"",
						"application/json",
						objectMapper.writeValueAsBytes(fileDTO)
					);
				
		MockMultipartFile file = 
				new MockMultipartFile(
						"file",
						"fileName.jpg",
						"image/jpeg",
						"imgimgimgimgimg".getBytes()
					);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart("/file")
        			.file(file)
        			.file(fileDTOJson));
		
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(10L))
			.andExpect(jsonPath("$.uploader").value("TestUser"));
		
		verify(fileService).insertNewFile(fileDTO, filePathPath);
	}
	
	
	@Test
	@DisplayName("Test for isFileNamePresent()")
	void isFileNamePresentTest() throws Exception {
		
	}

}
