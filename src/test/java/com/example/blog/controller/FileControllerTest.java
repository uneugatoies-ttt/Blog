package com.example.blog.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
	@DisplayName("Test for isFileNamePresent(): the file name isn't present")
	void isFileNamePresentTest() throws Exception {
		when(fileService.isFileNamePresent("fileName", "TestUser"))
			.thenReturn(false);
		
		ResultActions result = mockMvc.perform(get("/file/presence")
											.param("fileName", "fileName")
											.param("uploader", "TestUser"));
		
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(false))
				.andDo(print());
		
		verify(fileService).isFileNamePresent("fileName", "TestUser");							
	}

}
