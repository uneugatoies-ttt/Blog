package com.example.blog.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.NotificationMessage;
import com.example.blog.domain.User;

public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {
	
	Optional<List<NotificationMessage>> findAllByRecipient(User recipient); 
	
	void deleteAllByRecipient(User recipient);

}
