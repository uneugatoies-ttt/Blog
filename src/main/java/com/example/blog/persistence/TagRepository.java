package com.example.blog.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.Tag;
import com.example.blog.domain.User;

public interface TagRepository extends JpaRepository<Tag, Long> {
	
	List<Tag> findAllByUser(User user);

}
