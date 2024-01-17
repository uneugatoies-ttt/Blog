package com.example.blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.common.RedirectUriSession;
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
	@MockBean
	private RedirectUriSession redirectUriSession;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	@DisplayName("Test for insertNewFile()")
	void insertNewFileTest() throws Exception {
		String filePath = IMG_STORAGE_DIRECTORY +
				"TestUser" + File.separator + "fileName.jpg";
		
		FileDTO resultingDTO = FileDTO.builder()
				.id(10L)
				.fileName("fileName")
				.uploader("TestUser")
				.filePath(filePath)
				.createdAt(LocalDateTime.now())
				.build();
		
		when(fileService.insertNewFileInSystem(any(MultipartFile.class), any(String.class), any(Long.class)))
			.thenReturn(resultingDTO);
		
		MockMultipartFile userName = 
				new MockMultipartFile(
						"userName",
						"",
						"application/json",
						objectMapper.writeValueAsBytes("TestUser")
				);
		
		MockMultipartFile articleId = 
				new MockMultipartFile(
						"articleId",
						"",
						"application/json",
						objectMapper.writeValueAsBytes("100")
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
        			.file(userName)
        			.file(articleId));
		
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(10L))
			.andExpect(jsonPath("$.fileName").value("fileName"))
			.andExpect(jsonPath("$.uploader").value("TestUser"))
			.andExpect(jsonPath("$.filePath").value(filePath));
		
		verify(fileService).insertNewFileInSystem(any(MultipartFile.class), any(String.class), any(Long.class));
	}
	
	@Test
	@DisplayName("Test for getFile()") 
	void getFileTest() throws Exception {
		String fileName = "fileName.jpg";
		String uploader = "TestUser";
		String fileNameWithHyphen = fileName.replace(' ', '-').replace('_', '-');
		String userNameWithHyphen = uploader.replace(' ', '-').replace('_', '-');
		
		Resource resultingFileResource =
						new FileSystemResource(
							"." + File.separator + 
							"src" + File.separator +
							"test" + File.separator + 
							"java" + File.separator + 
							"com" + File.separator +
							"example" + File.separator +
							"blog" + File.separator +
							"controller" + File.separator + 
							"cat_selfie.jpg");
		
		when(fileService.getFile(fileNameWithHyphen, userNameWithHyphen))
			.thenReturn(resultingFileResource);
		
		ResultActions result = mockMvc.perform(get("/file")
										.param("fileName", fileName)
										.param("uploader", uploader));
		
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.IMAGE_JPEG));
		
		// byte array를 result.getResponse.getContentAsByteArray()로 받아서
		// expected byte array와 비교하면 assert할 수 있을 것이다.
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
