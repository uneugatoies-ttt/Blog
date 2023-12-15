package com.example.blog.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.File;
import com.example.blog.domain.User;

public interface FileRepository extends JpaRepository<File, Long> {
	
	Boolean existsByFileNameAndUploader(String fileName, User uploader);

}
