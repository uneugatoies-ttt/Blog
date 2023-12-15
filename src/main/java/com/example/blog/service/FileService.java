package com.example.blog.service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.blog.domain.File;
import com.example.blog.domain.User;
import com.example.blog.dto.FileDTO;
import com.example.blog.persistence.FileRepository;
import com.example.blog.persistence.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FileService {
	
	private FileRepository fileRepository;
	private UserRepository userRepository;

	@Transactional
	public FileDTO insertNewFile(FileDTO fileDTO, String filePath) {
		try {
			User uploader = userRepository.findByUserName(fileDTO.getUploader())
					.orElseThrow(() -> new EntityNotFoundException("User not found"));
			
			File fileEntity = File.builder()
								.fileName(fileDTO.getFileName())
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
}
