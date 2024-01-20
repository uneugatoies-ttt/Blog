package com.example.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ReplyDTO;
import com.example.blog.dto.ResponseDTO;
import com.example.blog.dto.ResponseListDTO;
import com.example.blog.service.ReplyService;

/*
	-> ReplyController는 이 application에서 reply와 관련된 request를 받는 controller이다.
*/

@RestController
@RequestMapping("/reply")
public class ReplyController {
	
	private ReplyService replyService;
	
	public ReplyController(ReplyService replyService) {
		this.replyService = replyService;
	}
	
	@PostMapping
	public ResponseEntity<?> createReply(@RequestBody ReplyDTO replyDTO) {
		try {
			ReplyDTO resultingReplyDTO = replyService.createReply(replyDTO);
			return ResponseEntity.ok().body(resultingReplyDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@GetMapping
	public ResponseEntity<?> getRepliesByArticle(@RequestParam Long articleId) {
		try {
			List<ReplyDTO> replyDTOList = replyService.getRepliesByArticle(articleId);
			ResponseListDTO<ReplyDTO> res = ResponseListDTO.<ReplyDTO>builder()
												.data(replyDTOList)
												.build();
			return ResponseEntity.ok().body(res);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@PutMapping
	public ResponseEntity<?> editReply(@RequestBody ReplyDTO replyDTO) {
		try {
			ReplyDTO resultingReplyDTO = replyService.editReply(replyDTO);
			return ResponseEntity.ok().body(resultingReplyDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteReply(@RequestParam Long replyId) {
		try {
			replyService.deleteReply(replyId);
			return ResponseEntity.ok().body(ResponseDTO.builder().data("Reply deleted successfully").build());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
