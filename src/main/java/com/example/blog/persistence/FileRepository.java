package com.example.blog.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.Article;
import com.example.blog.domain.File;
import com.example.blog.domain.User;

public interface FileRepository extends JpaRepository<File, Long> {
	
	Boolean existsByFileNameAndUploader(String fileName, User uploader);
	
	Boolean existsByArticle(Article article);
	
	Optional<File> findByUploader(User uploader);
	
	Optional<File> findByArticle(Article article);

}
