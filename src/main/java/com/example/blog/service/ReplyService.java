package com.example.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.example.blog.domain.Article;
import com.example.blog.domain.Reply;
import com.example.blog.domain.User;
import com.example.blog.dto.ReplyDTO;
import com.example.blog.persistence.ArticleRepository;
import com.example.blog.persistence.ReplyRepository;
import com.example.blog.persistence.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReplyService {
	
	private ReplyRepository replyRepository;
	private UserRepository userRepository;
	private ArticleRepository articleRepository;

	public ReplyDTO createReply(ReplyDTO replyDTO) {
		User writer = userRepository.findByUserName(replyDTO.getWriter())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		Article article = articleRepository.findById(replyDTO.getArticle())
				.orElseThrow(() -> new EntityNotFoundException("Article not found"));
		
		Reply reply = Reply.builder()
						.content(replyDTO.getContent())
						.writer(writer)
						.article(article)
						.where(replyDTO.getWhere())
						.build();
		
		Reply savedReply = replyRepository.save(reply);
		
		ReplyDTO resultingReplyDTO = ReplyDTO.builder()
							.id(savedReply.getId())
							.content(savedReply.getContent())
							.writer(savedReply.getWriter().getUserName())
							.article(savedReply.getArticle().getId())
							.where(savedReply.getWhere())
							.createdAt(savedReply.getCreatedAt())
							.updatedAt(savedReply.getUpdatedAt())
							.build();
		
		return resultingReplyDTO;
	}

	public List<ReplyDTO> getRepliesByArticle(Long articleId) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new EntityNotFoundException("Article not found"));
		
		List<Reply> replyList = replyRepository.findAllByArticle(article)
				.orElseThrow(() -> new EntityNotFoundException("Replies not found"));
		
		List<ReplyDTO> replyDTOList = replyList
				.stream()
				.map(r -> ReplyDTO.builder()
								.id(r.getId())
								.content(r.getContent())
								.writer(r.getWriter().getUserName())
								.article(r.getArticle().getId())
								.where(r.getWhere())
								.createdAt(r.getCreatedAt())
								.updatedAt(r.getUpdatedAt())
								.build())
				.collect(Collectors.toList());
		
		return replyDTOList;
	}

	public void deleteReply(Long replyId) {
		replyRepository.deleteById(replyId);
	}

}
