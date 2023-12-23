package com.example.blog.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ArticleTagRepositoryTest {
	
	@Autowired
	private ArticleTagRepository articleTagRepository;
	
	@Test
	@DisplayName("Test for deleteAllByArticle: successful case")
	void deleteAllByArticleTest() throws Exception {
		
	}

}
