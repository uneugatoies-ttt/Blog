package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
import com.example.blog.domain.NotificationMessage;
import com.example.blog.domain.Reply;
import com.example.blog.domain.User;
import com.example.blog.dto.ReplyDTO;
import com.example.blog.persistence.ArticleRepository;
import com.example.blog.persistence.NotificationMessageRepository;
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
	@MockBean
	private NotificationMessageRepository notificationMessageRepository;
	
	@Autowired
	private ReplyService replyService;
	
	@Test
	@DisplayName("Test for createReply(): successful case")
	void createReplyTest() throws Exception {
		Long articleId = 3L;
		ReplyDTO replyDTO = ReplyDTO.builder()
								.content("Test Reply Contents")
								.writer("TestUser")
								.articleId(articleId)
								.where("/blog/testuser/article/2")
								.build();
		User writer = ServiceTestSupporting.buildUser();
		User articleWriter = User.builder()
								.id("ArticleWriterID")
								.userName("ArticleWriter")
								.password("ArticleWriterPassword")
								.email("articlewriter@test.com")
								.authProvider(null)
								.blogTitle("ArticleWriter's Blog")
								.build();
		
		Article article = ServiceTestSupporting.buildArticle();
		// buildArticle()은 기본적으로 위의 "writer"의 user를 writer로 지정하므로 추가 설정이 필요하다.
		article.setWriter(articleWriter);
		article.setId(articleId);
		
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
										.articleId(articleId)
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
		
		verify(userRepository, times(1)).findByUserName(replyDTO.getWriter());
		verify(articleRepository, times(1)).findById(replyDTO.getArticleId());
		verify(replyRepository, times(1)).save(any(Reply.class));
		verify(notificationMessageRepository, times(1)).save(any(NotificationMessage.class));
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
		
		Article article = ServiceTestSupporting.buildArticle();
		article.setId(articleId);
		article.setWriter(articleWriter);
		
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
					ReplyDTO::getId,
					ReplyDTO::getContent,
					ReplyDTO::getWriter,
					ReplyDTO::getArticleId,
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
		
		verify(articleRepository, times(1)).findById(articleId);
		verify(replyRepository, times(1)).findAllByArticle(article);
	}
	
	@Test
	@DisplayName("Test for editReply(): successful case")
	void editReplyTest() throws Exception {
		Long articleId = 3L;
		Long replyId = 10L;
		
		String where = "/blog/ArticleWriter/article/2";
		
		ReplyDTO replyDTO = ReplyDTO.builder()
								.id(replyId)
								.content("Test Reply Contents Modified")
								.writer("TestUser")
								.articleId(articleId)
								.where(where)
								.build();
		
		User writer = ServiceTestSupporting.buildUser();
		
		User articleWriter = User.builder()
								.id("ArticleWriterID")
								.userName("ArticleWriter")
								.password("ArticleWriterPassword")
								.email("articlewriter@test.com")
								.authProvider(null)
								.blogTitle("ArticleWriter's Blog")
								.build();
		
		Article article = ServiceTestSupporting.buildArticle();
		article.setWriter(articleWriter);
		article.setId(articleId);
		
		Reply existingReply = Reply.builder()
						.id(replyId)
						.content("Test Reply Contents")
						.writer(writer)
						.article(article)
						.where(where)
						.build();
						
		Reply resultingReply = Reply.builder()
						.id(replyId)
						.content(replyDTO.getContent())
						.writer(writer)
						.article(article)
						.where(where)
						.build();
		
		ReplyDTO resultingReplyDTO = ReplyDTO.builder()
										.id(replyId)
										.content(resultingReply.getContent())
										.writer(writer.getUserName())
										.articleId(articleId)
										.where(where)
										.build();
		
		when(replyRepository.findById(replyDTO.getId()))
			.thenReturn(Optional.of(existingReply));
		when(replyRepository.save(existingReply))
			.thenReturn(resultingReply);
		
		ReplyDTO resultingReplyDTOFromService = replyService.editReply(replyDTO);
		
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
		
		verify(replyRepository, times(1)).findById(replyDTO.getId());
		verify(replyRepository, times(1)).save(existingReply);
	}
	
	@Test
	@DisplayName("Test for deleteReply(): successful case")
	void deleteReplyTest() throws Exception {
		Long replyId = 15L;
		
		replyService.deleteReply(replyId);
		
		verify(replyRepository, times(1)).deleteById(replyId);
	}

}
