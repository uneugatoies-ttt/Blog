package com.example.blog.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.Category;
import com.example.blog.domain.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	List<Category> findAllByUser(User user);
	
	Category findByUserAndName(User user, String name);
	
}
