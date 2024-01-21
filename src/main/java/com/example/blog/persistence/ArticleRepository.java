package com.example.blog.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.Article;
import com.example.blog.domain.Category;
import com.example.blog.domain.User;

// Article entity에 대한 operation을 포함하는 repository이다.
public interface ArticleRepository extends JpaRepository<Article, Long> {

	List<Article> findAllByCategory(Category category);
	
	List<Article> findAllByWriter(User user);
	
}
