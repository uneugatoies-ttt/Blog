package com.example.blog.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.Article;
import com.example.blog.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
	
	Optional<List<Reply>> findAllByArticle(Article article);

}
