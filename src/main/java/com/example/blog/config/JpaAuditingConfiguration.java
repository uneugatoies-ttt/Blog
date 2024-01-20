package com.example.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
	-> JpaAuditingConfiguration은 JPA auditing을 적용시키기 위한 configuration class이다;
	이것이 있기에 "BaseEntity"에 "createdAt"과 "updatedAt"과 같은 field를 두고 다른 entity로
	하여금 그것을 extend하게 해 모든 entity에서 명확한 기록을 남길 수 있다.
*/

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {

}
