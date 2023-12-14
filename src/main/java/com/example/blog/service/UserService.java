package com.example.blog.service;

import java.io.IOException;

import javax.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.blog.domain.User;
import com.example.blog.dto.UserDTO;
import com.example.blog.persistence.UserRepository;
import com.example.blog.security.TokenProvider;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	
	private UserRepository userRepository;
	private TokenProvider tokenProvider;
	private PasswordEncoder passwordEncoder;
	
	public User getUserByCredentials(final String userName, final String password) {
			try {
				final User origUser = userRepository.findByUserName(userName);
				if (
					origUser != null &&
						passwordEncoder.matches(
						password,
						origUser.getPassword()
					)
				) return origUser;
				return null;
			} catch (Exception e) {
				throw e;
			}
		}
	
	@Transactional
	public UserDTO createUser(final UserDTO userDTO) {
		User user = User.builder()
				.userName(userDTO.getUserName())
				.password(passwordEncoder.encode(userDTO.getPassword()))
				.email(userDTO.getEmail())
				.blogTitle(userDTO.getUserName() + "'s Blog")
				.build();
		
		if (userRepository.existsByUserName(user.getUserName()))
			throw new RuntimeException("User name already exists");
		
		User savedUser = userRepository.save(user);
		
		UserDTO resultingUserDTO = UserDTO.builder()
				.id(savedUser.getId())
				.userName(savedUser.getUserName())
				.build();

		return resultingUserDTO;
	}

	public User findUserByUserName(String userName) {
		try {
			return userRepository.findByUserName(userName);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String createToken(User user) throws IOException {
		return tokenProvider.createToken(user);
	}
	
	public String getBlogTitle(String userName) {
		try {
			return userRepository.findByUserName(userName).getBlogTitle();
		} catch (Exception e) {
			throw e;
		}
	}

}
