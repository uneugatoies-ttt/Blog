package com.example.blog.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
	
	Boolean existsByUserName(String username);
	
	Optional<User> findByUserName(String username);

}
