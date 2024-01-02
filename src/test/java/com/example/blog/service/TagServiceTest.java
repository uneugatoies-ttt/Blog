package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.blog.domain.Tag;
import com.example.blog.domain.User;
import com.example.blog.dto.TagDTO;
import com.example.blog.persistence.TagRepository;
import com.example.blog.persistence.UserRepository;

@ExtendWith(SpringExtension.class)
@Import({TagService.class})
public class TagServiceTest {
	
	@MockBean
	private TagRepository tagRepository;
	@MockBean
	private UserRepository userRepository;
	
	@Autowired
	private TagService tagService;
	
	@Test
	@DisplayName("Test for addTag(): successful case")
	void addTagTest() throws Exception {
		TagDTO tagDTO = TagDTO.builder()
							.user("TestUser")
							.name("Test Tag")
							.build();
		User user = User.builder()
				.id("TestUserID")
				.userName("TestUser")
				.password("TestUserPassword")
				.email("testuser@test.com")
				.authProvider(null)
				.blogTitle("TestUser's Blog")
				.build();
		
		Tag resultingTag = Tag.builder()
						.id(39L)
						.user(user)
						.name("Test Tag")
						.articleTag(null)
						.build();
		
		TagDTO resultingTagDTO = TagDTO.builder()
						.id(39L)
						.user("TestUser")
						.name("Test Tag")
						.build();
		
		when(userRepository.findByUserName(tagDTO.getUser()))
			.thenReturn(Optional.of(user));
		when(tagRepository.save(any(Tag.class)))
			.thenReturn(resultingTag);
		
		TagDTO resultingTagDTOFromService = tagService.addTag(tagDTO);
		
		assertThat(resultingTagDTOFromService).isNotNull();
		assertThat(resultingTagDTOFromService.getId())
			.isEqualTo(resultingTagDTO.getId());
		assertThat(resultingTagDTOFromService.getName())
			.isEqualTo(resultingTagDTO.getName());
		assertThat(resultingTagDTOFromService.getUser())
			.isEqualTo(resultingTagDTO.getUser());
		
		verify(userRepository).findByUserName(tagDTO.getUser());
		verify(tagRepository).save(any(Tag.class));
	}
	
	@Test
	@DisplayName("Test for deleteTag(): successful case")
	void deleteTagTest() throws Exception {
		Long id = 394L;
		
		tagService.deleteTag(id);
		
		verify(tagRepository).deleteById(id);
	}
	
	@Test
	@DisplayName("Test for getTag(): successful case")
	void getTagTest() throws Exception {
		String userName = "TestUser";
		User user = User.builder()
				.id("TestUserID")
				.userName(userName)
				.password("TestUserPassword")
				.email("testuser@test.com")
				.authProvider(null)
				.blogTitle("TestUser's Blog")
				.build();
		List<Tag> tagsEntity = new ArrayList<>();
		tagsEntity.add(Tag.builder()
						.id(39L)
						.user(user)
						.name("Test Tag 1")
						.articleTag(null)
						.build());
		tagsEntity.add(Tag.builder()
				.id(68L)
				.user(user)
				.name("Test Tag 2")
				.articleTag(null)
				.build());
		tagsEntity.add(Tag.builder()
				.id(125L)
				.user(user)
				.name("Test Tag 3")
				.articleTag(null)
				.build());
		tagsEntity.add(Tag.builder()
				.id(188L)
				.user(user)
				.name("Test Tag 4")
				.articleTag(null)
				.build());
		List<TagDTO> tags = tagsEntity.stream()
								.map(t -> TagDTO.builder()
										.id(t.getId())
										.user(t.getUser().getUserName())
										.name(t.getName())
										.build())
								.collect(Collectors.toList());
		
		when(userRepository.findByUserName(userName))
			.thenReturn(Optional.of(user));
		when(tagRepository.findAllByUser(user))
			.thenReturn(tagsEntity);
		
		List<TagDTO> tagsFromService = tagService.getTag(userName);
		
		assertThat(tagsFromService)
			.isNotNull()
			.hasSize(4);
		
		for (int i = 0; i < 4; ++i) {
			assertThat(tagsFromService.get(i))
				.extracting(
					TagDTO::getId,
					TagDTO::getUser,
					TagDTO::getName
				)
				.containsExactly(
					tags.get(i).getId(),
					tags.get(i).getUser(),
					tags.get(i).getName()
				);
		}
		
		verify(userRepository).findByUserName(userName);
		verify(tagRepository).findAllByUser(user);
	}

}
