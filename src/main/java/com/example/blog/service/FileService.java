package com.example.blog.service;

import static java.io.File.separator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.example.blog.domain.File;
import com.example.blog.domain.User;
import com.example.blog.dto.FileDTO;
import com.example.blog.persistence.FileRepository;
import com.example.blog.persistence.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FileService {
	
	private static final String IMG_STORAGE_DIRECTORY = 
			"." + separator +
			"src" + separator +
			"main" + separator +
			"resources" + separator + 
			"static" + separator;
	
	private FileRepository fileRepository;
	private UserRepository userRepository;
	
	public Resource getFile(String fileName, String userName) {
		try {
			Resource fileResource = 
					new ClassPathResource(
							"static" + 
							separator +
							userName +
							separator +
							fileName
						);
			if (fileResource.exists())
				return fileResource;
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public boolean isFileNamePresent(String fileName, String userName) {
		try {
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
			@Validated FileDTO fileDTO
	) throws IOException {
		try {
			String fileNameWithHyphen = fileDTO.getFileName().replace(' ', '-').replace('_', '-');
			String userNameWithHyphen = fileDTO.getUploader().replace(' ', '-').replace('_', '-');
			
			byte[] bytes = file.getBytes();
			Path directoryPath = Paths.get(IMG_STORAGE_DIRECTORY + userNameWithHyphen);
			// Create the corresponding directory if it doesn't exist.
			Files.createDirectories(directoryPath);
			Path path = Paths.get(
					directoryPath.toString() + 
					separator + 
					fileNameWithHyphen +
					"." +
					extractFileExtension(file.getOriginalFilename())
			);
			Files.write(path, bytes);
			
			FileDTO resultingFileDTO = 
					insertNewFileInDatabase(
							fileDTO, path.toString(),
							fileNameWithHyphen, userNameWithHyphen
					);
			
			return resultingFileDTO;
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	@Transactional
	private FileDTO insertNewFileInDatabase(
			FileDTO fileDTO,
			String filePath,
			String fileNameWithHyphen,
			String userNameWithHyphen
	) {
		try {
			User uploader = userRepository.findByUserName(userNameWithHyphen)
					.orElseThrow(() -> new EntityNotFoundException("User not found"));
			
			File fileEntity = File.builder()
								.fileName(fileNameWithHyphen)
								.uploader(uploader)
								.description(fileDTO.getDescription())
								.filePath(filePath)
								.fileType(fileDTO.getFileType())
								.build();
			File storedFileEntity = fileRepository.save(fileEntity);
			
			FileDTO storedFileDTO = FileDTO.builder()
										.fileName(storedFileEntity.getFileName())
										.uploader(storedFileEntity.getUploader().getUserName())
										.description(storedFileEntity.getDescription())
										.createdAt(storedFileEntity.getCreatedAt())
										.id(storedFileEntity.getId())
										.fileType(storedFileEntity.getFileType())
										.build();
			
			return storedFileDTO;
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
