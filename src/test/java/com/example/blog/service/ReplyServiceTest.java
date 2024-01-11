package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import com.example.blog.domain.Article;
import com.example.blog.domain.Category;
import com.example.blog.domain.Reply;
import com.example.blog.domain.User;
import com.example.blog.dto.ReplyDTO;
import com.example.blog.persistence.ArticleRepository;
import com.example.blog.persistence.ReplyRepository;
import com.example.blog.persistence.UserRepository;

@ExtendWith(SpringExtension.class)
@Import({ReplyService.class})
public class ReplyServiceTest {
	
	@MockBean
	private ReplyRepository replyRepository;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private ArticleRepository articleRepository;
	
	@Autowired
	private ReplyService replyService;
	
	@Test
	@DisplayName("Test for createReply: successful case")
	void createReplyTest() throws Exception {
		ReplyDTO replyDTO = ReplyDTO.builder()
								.content("Test Reply Contents")
								.writer("TestUser")
								.articleId(3L)
								.where("/blog/testuser/article/2")
								.build();
		User writer = User.builder()
				.id("TestUserID")
				.userName("TestUser")
				.password("TestUserPassword")
				.email("testuser@test.com")
				.authProvider(null)
				.blogTitle("TestUser's Blog")
				.build();
		User articleWriter = User.builder()
				.id("ArticleWriterID")
				.userName("ArticleWriter")
				.password("ArticleWriterPassword")
				.email("articlewriter@test.com")
				.authProvider(null)
				.blogTitle("ArticleWriter's Blog")
				.build();
		Article article = Article.builder()
								.id(3L)
								.writer(articleWriter)
								.content("Test Article Contents")
								.title("Test-Article")
								.category(
									Category.builder()
										.id(100L)
										.name("Test Cate")
										.user(articleWriter)
										.build()
								)
								.tag(null)	// Omitting setting tags for the article
								.build();
		Reply resultingReply = Reply.builder()
						.id(13L)
						.content(replyDTO.getContent())
						.writer(writer)
						.article(article)
						.where(replyDTO.getWhere())
						.build();
		ReplyDTO resultingReplyDTO = ReplyDTO.builder()
										.id(13L)
										.content("Test Reply Contents")
										.writer("TestUser")
										.articleId(3L)
										.where("/blog/testuser/article/2")
										.build();
		
		when(userRepository.findByUserName(replyDTO.getWriter()))
			.thenReturn(Optional.of(writer));
		when(articleRepository.findById(replyDTO.getArticleId()))
			.thenReturn(Optional.of(article));
		when(replyRepository.save(any(Reply.class)))
			.thenReturn(resultingReply);
		
		ReplyDTO resultingReplyDTOFromService = replyService.createReply(replyDTO);
		
		assertThat(resultingReplyDTOFromService)
			.isNotNull();
		assertThat(resultingReplyDTOFromService.getId())
			.isEqualTo(resultingReplyDTO.getId());
		assertThat(resultingReplyDTOFromService.getContent())
			.isEqualTo(resultingReplyDTO.getContent());
		assertThat(resultingReplyDTOFromService.getWriter())
			.isEqualTo(resultingReplyDTO.getWriter());
		assertThat(resultingReplyDTOFromService.getArticleId())
			.isEqualTo(resultingReplyDTO.getArticleId());
		assertThat(resultingReplyDTOFromService.getWhere())
			.isEqualTo(resultingReplyDTO.getWhere());
		
		verify(userRepository).findByUserName(replyDTO.getWriter());
		verify(articleRepository).findById(replyDTO.getArticleId());
		verify(replyRepository).save(any(Reply.class));
	}
	
	@Test
	@DisplayName("Test for getRepliesByArticle: successful case")
	void getRepliesByArticleTest() throws Exception {
		Long articleId = 3L;
		User articleWriter = User.builder()
				.id("ArticleWriterID")
				.userName("ArticleWriter")
				.password("ArticleWriterPassword")
				.email("articlewriter@test.com")
				.authProvider(null)
				.blogTitle("ArticleWriter's Blog")
				.build();
		User replyWriter1 = User.builder()
				.id("ReplyWriterID1")
				.userName("ReplyWriter1")
				.password("ReplyWriterPassword1")
				.email("replywriter1@test.com")
				.authProvider(null)
				.blogTitle("ReplyWriter1's Blog")
				.build();
		User replyWriter2 = User.builder()
				.id("ReplyWriterID2")
				.userName("ReplyWriter2")
				.password("ReplyWriterPassword2")
				.email("replywriter2@test.com")
				.authProvider(null)
				.blogTitle("ReplyWriter2's Blog")
				.build();
		Article article = Article.builder()
							.id(articleId)
							.writer(articleWriter)
							.content("Test Article Contents")
							.title("Test-Article")
							.category(
								Category.builder()
									.id(100L)
									.name("Test Cate")
									.user(articleWriter)
									.build()
							) 
							.tag(null) // Omitting setting tags for the article
							.build();
		List<Reply> replyList = new ArrayList<>();
		
		Reply reply1 = Reply.builder()
				.id(13L)
				.content("Test Reply Contents 1")
				.writer(replyWriter1)
				.article(article)
				.where("/blog/articlewriter/article/9")
				.build();
		reply1.setCreatedAt(LocalDateTime.now());
		reply1.setUpdatedAt(LocalDateTime.now());
		Reply reply2 = Reply.builder()
				.id(16L)
				.content("Test Reply Contents 2")
				.writer(replyWriter2)
				.article(article)
				.where("/blog/articlewriter/article/9")
				.build();
		reply2.setCreatedAt(LocalDateTime.now());
		reply2.setUpdatedAt(LocalDateTime.now());
		
		replyList.add(reply1);
		replyList.add(reply2);
		
		List<ReplyDTO> replyDTOList = replyList
				.stream()
				.map(r -> ReplyDTO.builder()
								.id(r.getId())
								.content(r.getContent())
								.writer(r.getWriter().getUserName())
								.articleId(r.getArticle().getId())
								.where(r.getWhere())
								.createdAt(r.getCreatedAt())
								.updatedAt(r.getUpdatedAt())
								.build())
				.collect(Collectors.toList());
		
		when(articleRepository.findById(articleId))
			.thenReturn(Optional.of(article));
		when(replyRepository.findAllByArticle(article))
			.thenReturn(Optional.of(replyList));
		
		List<ReplyDTO> replyDTOListFromService = replyService.getRepliesByArticle(articleId);
		
		assertThat(replyDTOListFromService)
			.isNotNull()
			.hasSize(2);
		
		for (int i = 0; i < 2; ++i) {
			assertThat(replyDTOListFromService.get(i))
				.extracting(
					ReplyDTO::getId, ReplyDTO::getContent,
					ReplyDTO::getWriter, ReplyDTO::getArticleId,
					ReplyDTO::getWhere
				)
				.containsExactly(
					replyDTOList.get(i).getId(),
					replyDTOList.get(i).getContent(),
					replyDTOList.get(i).getWriter(),
					replyDTOList.get(i).getArticleId(),
					replyDTOList.get(i).getWhere()
				);
		}
		
		verify(articleRepository).findById(articleId);
		verify(replyRepository).findAllByArticle(article);
	}
	
	@Test
	@DisplayName("Test for deleteReply(): successful case")
	void deleteReplyTest() throws Exception {
		Long replyId = 15L;
		
		replyService.deleteReply(replyId);
		
		verify(replyRepository).deleteById(replyId);
	}

}
