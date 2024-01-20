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
	-> NotificationMessage는 이 application에서 각 user에게 전달되는 알림 메시지를 나타낸다.
*/

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@Table(name = "notification_message")
public class NotificationMessage extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String message;
	
	@ManyToOne
	@JoinColumn(name = "recipient")
	private User recipient;
	
	/* 
	이 Message를 발생시킨 frontend의 page가 가지는 pathname에 해당한다.
	예를 들어 "testuser"가 작성한 article 중 "35"의 ID를 가지는 것에 reply가 달려서 그에 대한
	notification message가 생성되었다면, 이 field의 value는 "/testuser/article/35"와 같이 될 것이다.
	*/
	private String where;

}