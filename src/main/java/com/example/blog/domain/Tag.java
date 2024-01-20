package com.example.blog.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
	-> Tag entity는 이 application의 게시물들이 분류되는 태그를 나타낸다.
	카테고리와는 다르게 한 게시물이 다수의 태그를 가질 수 있기 때문에 Article과 Tag에는 
	다대다 관계가 성립한다.
	
	하지만 appliation의 동작 방식 때문에 직접적으로 @ManyToMany를 사용하지 않고,
	중간에 "ArticleTag"라는 매개체 역할을 하는 별개의 entity를 정의하는 방식으로 
	이 관계는 표현된다.
*/

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@Table(name = "tag")
public class Tag extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user")
	private User user;
	
	private String name;
	
	@OneToMany(mappedBy = "tag")
	@ToString.Exclude
	private List<ArticleTag> articleTag;
	
}