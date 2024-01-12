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

@Service
@AllArgsConstructor
public class FileService {
	
	/*
	private static final String IMG_STORAGE_DIRECTORY = 
			"." + separator +
			"src" + separator +
			"main" + separator +
			"resources" + separator + 
			"static" + separator;
	*/
	
	// Using this should be replaced by something like @Value("${image.directory}")
	// after you define that value within the configuration file (.yml or .properties)
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
	//private ResourceLoader resourceLoader;
	
	private String getPath() throws IOException {
		Path p = Paths.get(FORFILE_FILEPATH);
		byte[] forfileByte = Files.readAllBytes(p);
		return new String(forfileByte, "UTF-8");
	}
	
	public Resource getFile(String fileName, String userName) throws IOException {
		try {
			// if fileName에 extension이 포함되어 있지 않다면 default로 .jpg를 append함.
			String extension = extractFileExtension(fileName);
			if (extension.trim().equals(""))
				fileName = fileName + ".jpg";
			
			String path = getPath();
			path = path + userName + separator + fileName;
			Resource fileResource = new FileSystemResource(path);
			
			System.out.print("\n\n\nFile Length: ");
			System.out.println(fileResource.contentLength());
			System.out.println("\n\n");
			
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
			//@Validated FileDTO fileDTO
	) throws IOException {
		try {
			String fileNameWithHyphen = file.getOriginalFilename().replace(' ', '-').replace('_', '-');
			String userNameWithHyphen = userName.replace(' ', '-').replace('_', '-');
			//String extension = extractFileExtension(file.getOriginalFilename());
			
			byte[] bytes = file.getBytes();
			Path directoryPath = Paths.get( getPath() + userNameWithHyphen );
			// Create the corresponding directory if it doesn't exist.
			Files.createDirectories(directoryPath);
			
			Path path = Paths.get(
					directoryPath.toString() + 
					separator + 
					fileNameWithHyphen
			);
			Files.write(path, bytes);
			
			FileDTO resultingFileDTO = 
					insertOrUpdateFileInDatabase(
							path.toString(),
							fileNameWithHyphen,
							userNameWithHyphen,
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
			String fileNameWithHyphen,
			String userNameWithHyphen,
			Long articleId
	) {
		try {
			User uploader = userRepository.findByUserName(userNameWithHyphen)
					.orElseThrow(() -> new EntityNotFoundException("User not found"));
			Article article = articleRepository.findById(articleId)
					.orElseThrow(() -> new EntityNotFoundException("Article not found"));
			
			File fileEntity = File.builder()
								.fileName(fileNameWithHyphen)
								.uploader(uploader)
								.filePath(filePath)
								.article(article)
								.build();
			File storedFileEntity = fileRepository.save(fileEntity);
			
			FileDTO storedFileDTO = FileDTO.builder()
										.fileName(storedFileEntity.getFileName())
										.uploader(storedFileEntity.getUploader().getUserName())
										.createdAt(storedFileEntity.getCreatedAt())
										.id(storedFileEntity.getId())
										.articleId(storedFileEntity.getArticle().getId())
										.build();
			
			return storedFileDTO;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void deleteFileInSystem(com.example.blog.domain.File existingFile) throws IOException {
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

	private String extractFileExtension(String filename) {
	    int lastDotIndex = filename.lastIndexOf('.');
	    if (lastDotIndex > 0) {
	        return filename.substring(lastDotIndex + 1);
	    }
	    return ""; // there's no extension
	}
}
