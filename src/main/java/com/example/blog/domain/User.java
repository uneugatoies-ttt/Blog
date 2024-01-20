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

/*
	-> User entity는 이 application에서 회원 가입을 완료한 사용자를 나타낸다.
*/

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
		-> @GeneratedValue의 "generator" value와 @GeneraicGenerator의 "name" value가 
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

	// password는 encoding되어서 저장된다.
	private String password;
	
	private String email;

	/*
	만약 OAuth2를 통한 사용자에 해당하는 record라면, 이 field의 값은 해당하는 provider의
	명칭이 들어간다. 이 application에서는 GitHub 혹은 Google이 된다.
	*/
	private String authProvider;
	
	private String blogTitle;

}