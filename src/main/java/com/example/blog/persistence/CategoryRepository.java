package com.example.blog.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.Category;
import com.example.blog.domain.User;

// Category entity에 대한 operation을 포함하는 repository이다.
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	List<Category> findAllByUser(User user);
	
	Category findByUserAndName(User user, String name);
	
}
