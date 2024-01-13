package com.example.blog.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@Table(name = "file")
public class File extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String fileName;
	
	@ManyToOne
	@JoinColumn(name = "uploader")
	private User uploader;

	private String filePath;

	@OneToOne
	@JoinColumn(name = "article_id")
	private Article article;
	
	/*
	원래는 각 file에 대한 설명을 담는 용도로 설정한 field이다; 현재는 불필요하기에 삭제한다.
	*/
	//private String description;
	
	/*
	원래는 각 file의 type을 "image"나 "audio" 등의 indicator로 명시하는 field이다;
	현재는 오로지 image file만이 존재하는데다가 fileName의 extension을 확인함으로 type을 
	아는 것이 가능하므로 삭제한다.
	*/
	//private String fileType;
	
}
