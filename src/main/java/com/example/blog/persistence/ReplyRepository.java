package com.example.blog.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.Article;
import com.example.blog.domain.Reply;

// Reply entity에 대한 operation을 포함하는 repository이다.
public interface ReplyRepository extends JpaRepository<Reply, Long> {
	
	Optional<List<Reply>> findAllByArticle(Article article);

}
