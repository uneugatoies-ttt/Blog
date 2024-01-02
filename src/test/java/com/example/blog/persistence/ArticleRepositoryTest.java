package com.example.blog.persistence;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.blog.domain.Article;

@DataJpaTest
public class ArticleRepositoryTest {
	
	@Autowired
	private ArticleRepository articleRepository;
	
	@Test
	@DisplayName("Test for findAllByCategory(): successful case")
	void findAllByCategoryTest() throws Exception {
		List<Article> articles = new ArrayList<>();
		
		
	}
	

}
