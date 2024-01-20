package com.example.blog.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
	-> Article entity는 이 application의 게시글을 나타낸다.
*/

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@Table(name = "article")
public class Article extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "writer")
	private User writer;
	
	private String content;
	
	private String title;
	
	@ManyToOne
	@JoinColumn(name = "category")
	private Category category;
	
	@OneToMany(mappedBy = "article")
	@ToString.Exclude
	private List<ArticleTag> tag;
	
	@OneToOne(mappedBy = "article")
	@ToString.Exclude
	private File mainImage;

}
