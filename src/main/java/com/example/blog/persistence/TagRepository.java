package com.example.blog.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.Tag;
import com.example.blog.domain.User;

// Tag entity에 대한 operation을 포함하는 repository이다.
public interface TagRepository extends JpaRepository<Tag, Long> {
	
	List<Tag> findAllByUser(User user);

}
