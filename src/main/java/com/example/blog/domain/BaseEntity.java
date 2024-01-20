package com.example.blog.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
	-> BaseEntity는 JpaAuditingConfiguration에서 설정한 JPA auditing을
	사용하는 entity이다.
	
	다른 entity로 하여금 BaseEntity를 extend하게 해서 "createdAt"과 "updatedAt"을
	사용해 언제 해당하는 record가 삽입되었고 수정되었는지를 기록할 수 있다.
	
	-> @LastModifiedDate를 사용했으므로, record가 수정될 때에 일일이 entity에서
	"setUpdatedAt()"을 사용하지 않아도 자동적으로 처리된다는 것을 기억하도록 한다.
*/

@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
	
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	private LocalDateTime updatedAt;
}
