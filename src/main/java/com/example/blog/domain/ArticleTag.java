package com.example.blog.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
	-> ArticleTag entity는 "Article"과 "Tag" 이 두 entity 간의 다대다 관계를 나타낸다.
	원래는 Article과 Tag 사이에 직접적으로 @ManyToMany를 사용하는 등의 설정으로 이 관계를 표현하려고
	했지만, database와 연동 이후 이 방법은 매개체 역할을 하는 중간의 table을 자동으로 생성한다는 것을
	알게 되었다.
	자동으로 생성된 table에서는 예상치 못한 동작을 보일 수도 있다는 이야기를 듣고, 이 관계를 나타내는
	entity를 직접 정의하는 것으로 entity들의 설계를 달리했다.
*/

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@Table(name = "article_tag")
public class ArticleTag extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "article_id")
	private Article article;
	
	@ManyToOne
	@JoinColumn(name = "tag_id")
	private Tag tag;

}
