package com.example.blog.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.ArticleTag;
import com.example.blog.domain.Tag;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
	
	List<ArticleTag> findAllByTagId(Tag tag);

}
