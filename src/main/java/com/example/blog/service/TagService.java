package com.example.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.blog.domain.Tag;
import com.example.blog.domain.User;
import com.example.blog.dto.TagDTO;
import com.example.blog.persistence.TagRepository;
import com.example.blog.persistence.UserRepository;

import lombok.AllArgsConstructor;

// TagService는 TagController의 dependency로 사용되어 Tag와 관련된 logic을 수행하는 service이다.
@Service
@AllArgsConstructor
public class TagService {
	
	private TagRepository tagRepository;
	private UserRepository userRepository;
	
	@Transactional
	public TagDTO addTag(TagDTO tagDTO) {
		User user = userRepository.findByUserName(tagDTO.getUser())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		Tag tag = Tag.builder()
						.user(user)
						.name(tagDTO.getName())
						.build();
		
		Tag savedTag = tagRepository.save(tag);
		
		TagDTO resultingTagDTO = TagDTO.builder()
										.id(savedTag.getId())
										.user(savedTag.getUser().getUserName())
										.name(savedTag.getName())
										.build();
		
		return resultingTagDTO;
	}

	@Transactional
	public List<TagDTO> getTag(String userName) {
		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		List<TagDTO> resultingTagDTOs = tagRepository
					.findAllByUser(user)
					.stream()
					.map(t -> TagDTO.builder()
									.id(t.getId())
									.user(t.getUser().getUserName())
									.name(t.getName())
									.build())
					.collect(Collectors.toList());
		return resultingTagDTOs;
	}
	
	@Transactional
	public TagDTO editTag(TagDTO tagDTO) {
		Tag existingTag = tagRepository.findById(tagDTO.getId())
				.orElseThrow(() -> new EntityNotFoundException("Tag not found"));
		
		existingTag.setName(tagDTO.getName());
		
		Tag savedTag = tagRepository.save(existingTag);
		
		TagDTO resultingTagDTO = TagDTO.builder()
										.id(savedTag.getId())
										.user(savedTag.getUser().getUserName())
										.name(savedTag.getName())
										.build();

		return resultingTagDTO;
	}
	

	@Transactional
	public void deleteTag(Long id) {
		tagRepository.deleteById(id);
	}

}
