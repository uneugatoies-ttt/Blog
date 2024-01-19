package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.blog.domain.Article;
import com.example.blog.domain.File;
import com.example.blog.domain.User;
import com.example.blog.dto.FileDTO;
import com.example.blog.persistence.ArticleRepository;
import com.example.blog.persistence.FileRepository;
import com.example.blog.persistence.UserRepository;

/*
	-> FileService의 test는 repository의 method를 내부에서 call하여 
	database operation을 수행하는 method들에 대해서만 행한다.
*/

@ExtendWith(SpringExtension.class)
@Import({FileService.class})
public class FileServiceTest {
	
	@MockBean
	private FileRepository fileRepository;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private ArticleRepository articleRepository;
	
	@Autowired
	private FileService fileService;
	
	@Test
	@DisplayName("Test for isFileNamePresent(): successful case")
	void getFileTest() throws Exception {
		String fileName = ServiceTestSupporting.path.getFileName().toString();
		String userName = "TestUser";
		
		User uploader = User.builder()
							.id("TestUserID")
							.userName(userName)
							.password("TestUser Password")
							.email("testuser@test.com")
							.authProvider(null)
							.blogTitle("TestUser's Blog")
							.build();
		
		when(userRepository.findByUserName(userName))
			.thenReturn(Optional.of(uploader));
		when(fileRepository.existsByFileNameAndUploader(fileName, uploader))
			.thenReturn(false);
		
		boolean resultingPresenceFromService = fileService.isFileNamePresent(fileName, userName);
		
		assertThat(resultingPresenceFromService)
			.isEqualTo(false);
		
		verify(userRepository, times(1)).findByUserName(userName);
		verify(fileRepository, times(1)).existsByFileNameAndUploader(fileName, uploader);
	}
	
	
	@Test
	@DisplayName("Test for insertOrUpdateFileInDatabase(): successful case")
	void insertOrUpdateFileInDatabaseTest() throws Exception {
		String filePath = ServiceTestSupporting.path.toString();
		String fileName = ServiceTestSupporting.path.getFileName().toString();
		String userName = "TestUser";
		Long articleId = 1L;
		
		User uploader = ServiceTestSupporting.buildUser();
		
		Article article = ServiceTestSupporting.buildArticle();
		
		File storedFile = File.builder()
						.id(15L)
						.fileName(fileName)
						.uploader(uploader)
						.filePath(filePath)
						.article(article)
						.build();
		
		FileDTO storedFileDTO = FileDTO.builder()
									.id(storedFile.getId())
									.fileName(storedFile.getFileName())
									.uploader(storedFile.getUploader().getUserName())
									.createdAt(storedFile.getCreatedAt())
									.articleId(storedFile.getArticle().getId())
									.build();
		
		when(userRepository.findByUserName(userName))
			.thenReturn(Optional.of(uploader));
		when(articleRepository.findById(articleId))
			.thenReturn(Optional.of(article));
		when(fileRepository.save(any(File.class)))
			.thenReturn(storedFile);
		
		FileDTO storedFileDTOFromService = 
				fileService.insertOrUpdateFileInDatabase(
						filePath,
						fileName,
						userName,
						articleId
				);
		
		assertThat(storedFileDTOFromService)
			.extracting(
				FileDTO::getId,	
				FileDTO::getFileName,	
				FileDTO::getUploader
			)
			.containsExactly(
				storedFileDTO.getId(),
				storedFileDTO.getFileName(),
				storedFileDTO.getUploader()
			);
		
		verify(userRepository, times(1)).findByUserName(userName);
		verify(articleRepository, times(1)).findById(articleId);
		verify(fileRepository, times(1)).save(any(File.class));
	}
	
}