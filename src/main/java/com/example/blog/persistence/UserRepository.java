package com.example.blog.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.User;

// User entity에 대한 operation을 포함하는 repository이다.
public interface UserRepository extends JpaRepository<User, String> {
	
	Boolean existsByUserName(String username);
	
	Optional<User> findByUserName(String username);

}
