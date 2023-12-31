package com.example.blog.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.blog.dto.FileDTO;
import com.example.blog.persistence.FileRepository;
import com.example.blog.persistence.UserRepository;

@ExtendWith(SpringExtension.class)
@Import({FileService.class})
public class FileServiceTest {
	
	@MockBean
	private FileRepository fileRepository;
	@MockBean
	private UserRepository userRepository;
	
	@Autowired
	private FileService fileService;
	
	@Test
	@DisplayName("Test for insertNewFile(): successful case")
	void insertNewFileTest() throws Exception {
		
	}
	
	@Test
	@DisplayName("Test for isFileNamePresent(): successful case")
	void isFileNamePresentTest() throws Exception {
		
	}

}
