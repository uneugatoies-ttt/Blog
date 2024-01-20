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

import com.example.blog.dto.ResponseDTO;
import com.example.blog.dto.ResponseListDTO;
import com.example.blog.dto.TagDTO;
import com.example.blog.service.TagService;

/*
	-> TagController는 이 application에서 tag와 관련된 request를 받는 controller이다.
*/

@RestController
@RequestMapping("/tag")
public class TagController {
	
	private TagService tagService;
	
	public TagController(TagService tagService) {
		this.tagService = tagService;
	}
	
	@PostMapping
	public ResponseEntity<?> addTag(
		@RequestBody TagDTO tagDTO
	) {
		try {
			TagDTO resultingTagDTO = tagService.addTag(tagDTO);
			return ResponseEntity.ok().body(resultingTagDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@GetMapping
	public ResponseEntity<?> getTags(
		@RequestParam String userName
	) {
		try {
			List<TagDTO> tags = tagService.getTag(userName);
			ResponseListDTO<TagDTO> responseListDTO = ResponseListDTO.<TagDTO>builder()
										.data(tags)
										.build();
			return ResponseEntity.ok().body(responseListDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@PutMapping
	public ResponseEntity<?> editTag(@RequestBody TagDTO tagDTO) {
		try {
			TagDTO resultingTagDTO = tagService.editTag(tagDTO);
			return ResponseEntity.ok().body(resultingTagDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteTag(@RequestParam Long id) {
		try {
			tagService.deleteTag(id);
			return ResponseEntity.ok().body(ResponseDTO.builder().data("Tag deleted successfully").build());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

	
}
