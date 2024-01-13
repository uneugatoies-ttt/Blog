package com.example.blog.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "user")
public class User extends BaseEntity {
	
	/*
	@GeneratedValue의 "generator" value와 @GeneraicGenerator의 "name" value가 
	동일하다는 것에 주목하라. @GeneratedValue의 "generator"에는 사전에 정의된 값이 있는 것이 아니다;
	이 attribute에는 어떤 generator를 사용할 것인지를 명시하기 위해, generator의 "name"의 값과
	동일한 값을 사용하면 된다.
	*/
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	@Column(nullable = false)
	private String userName;

	private String password;
	
	private String email;

	private String authProvider;
	
	private String blogTitle;

}