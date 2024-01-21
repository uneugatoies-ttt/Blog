package com.example.blog.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.Article;
import com.example.blog.domain.ArticleTag;
import com.example.blog.domain.Tag;

// ArticleTag entity에 대한 operation을 포함하는 repository이다.
public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
	
	List<ArticleTag> findAllByTag(Tag tag);
	
	Optional<List<ArticleTag>> findAllByArticle(Article article);
	
	void deleteAllByArticle(Article article);

}
