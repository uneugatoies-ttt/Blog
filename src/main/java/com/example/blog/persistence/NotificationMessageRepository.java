package com.example.blog.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.domain.NotificationMessage;
import com.example.blog.domain.User;

// NotificationMessage entity에 대한 operation을 포함하는 repository이다.
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {
	
	Optional<List<NotificationMessage>> findAllByRecipient(User recipient); 
	
	void deleteAllByRecipient(User recipient);

}
