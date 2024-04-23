package com.example.blog.service;

import static java.io.File.separator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.example.blog.domain.Article;
import com.example.blog.domain.File;
import com.example.blog.domain.User;
import com.example.blog.dto.FileDTO;
import com.example.blog.persistence.ArticleRepository;
import com.example.blog.persistence.FileRepository;
import com.example.blog.persistence.UserRepository;

import lombok.AllArgsConstructor;

// FileService는 FileController의 dependency로 사용되어 File과 관련된 logic을 수행하는 service이다.
@Service
@AllArgsConstructor
public class FileService {
	
	// "forflie.txt"는 이 project를 만들 당시의 개발 환경인 system 내부의 어느 경로를 담고 있다.
	// image file 원본들은 모두 이 경로에 저장된다.
	private static final String FORFILE_FILEPATH = 
			"." + separator +
			"src" + separator +
			"main" + separator +
			"java" + separator +
			"com" + separator + 
			"example" + separator +
			"blog" + separator +
			"service" + separator +
			"forfile.txt";

	private FileRepository fileRepository;
	private UserRepository userRepository;
	private ArticleRepository articleRepository;
	
	private String getPath() throws IOException {
		Path p = Paths.get(FORFILE_FILEPATH);
		byte[] forfileByte = Files.readAllBytes(p);
		return new String(forfileByte, "UTF-8");
	}
	
	public Resource getFile(String fileName, String userName) throws IOException {
		try {
			// if fileName에 extension이 포함되어 있지 않다면 default로 .jpg를 append한다.
			String extension = extractFileExtension(fileName);
			if (extension.trim().equals(""))
				fileName = fileName + ".jpg";
			
			String path = getPath();
			path = path + userName + separator + fileName;
			Resource fileResource = new FileSystemResource(path);

			if (fileResource.exists())
				return fileResource;
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public boolean isFileNamePresent(String fileName, String userName) throws IOException {
		try {
			String extension = extractFileExtension(fileName);
			if (extension.trim().equals(""))
				fileName = fileName + ".jpg";
			
			User uploader = userRepository.findByUserName(userName)
					.orElseThrow(() -> new EntityNotFoundException("User not found"));
			boolean presence = fileRepository.existsByFileNameAndUploader(fileName, uploader);
			return presence;
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public FileDTO insertNewFileInSystem(
			MultipartFile file,
			String userName,
			Long articleId
	) throws IOException {
		try {
			byte[] bytes = file.getBytes();
			Path directoryPath = Paths.get( getPath() + userName );
			// 만약 해당 directory가 존재하지 않는다면 새로 만든다.
			Files.createDirectories(directoryPath);
			
			Path path = Paths.get(
					directoryPath.toString() + 
					separator + 
					file.getOriginalFilename()
			);
			Files.write(path, bytes);
			
			FileDTO resultingFileDTO = 
					insertOrUpdateFileInDatabase(
							path.toString(),
							file.getOriginalFilename(),
							userName,
							articleId
					);
			
			return resultingFileDTO;
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	@Transactional
	public FileDTO insertOrUpdateFileInDatabase(
			String filePath,
			String fileName,
			String userName,
			Long articleId
	) {
		try {
			User uploader = userRepository.findByUserName(userName)
					.orElseThrow(() -> new EntityNotFoundException("User not found"));
			Article article = articleRepository.findById(articleId)
					.orElseThrow(() -> new EntityNotFoundException("Article not found"));
			
			File file = File.builder()
								.fileName(fileName)
								.uploader(uploader)
								.filePath(filePath)
								.article(article)
								.build();
			File storedFile= fileRepository.save(file);
			
			FileDTO storedFileDTO = FileDTO.builder()
										.fileName(storedFile.getFileName())
										.uploader(storedFile.getUploader().getUserName())
										.createdAt(storedFile.getCreatedAt())
										.id(storedFile.getId())
										.articleId(storedFile.getArticle().getId())
										.build();
			
			return storedFileDTO;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/*
		-> Article이 삭제되거나 수정될 때만 ArticleService에서 call되는 method이므로
		특별히 return value를 지정할 필요는 없다고 생각해서 void로 했지만, 이것이 최선의 방법인지
		확신은 아직 없다.
	*/
	public void deleteFileInSystem(File existingFile) throws IOException {
		try {
			String path1 = getPath() +
					existingFile.getUploader().getUserName() +
					separator + 
					existingFile.getFileName();
			Path path = Paths.get(path1);
			if (Files.deleteIfExists(path)) {
				System.out.println("File deleted successfully");
			} else {
				throw new RuntimeException("File wasn't properly deleted");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/********************************************
	  											
	  		private methods					
	 											
	 ********************************************/

	private String extractFileExtension(String filename) {
	    int lastDotIndex = filename.lastIndexOf('.');
	    if (lastDotIndex > 0) {
	        return filename.substring(lastDotIndex + 1);
	    }
	    return ""; // extension이 포함되어 있지 않은 경우
	}
	
}
