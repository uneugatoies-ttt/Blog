package com.example.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
	-> AppConfig는 Service에서 @Transactional을 사용할 수 있도록 설정하는 역할을 하는 configuration class이다.
*/

@Configuration
@EnableTransactionManagement
public class AppConfig {

}
