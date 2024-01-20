package com.example.blog.common;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/*
	-> 이 class는 OAuth2 흐름에서 userName의 값을 추출해서 이후에 frontend로 return하기 위해
	backend 내부에서의 저장소로 쓰이는 session이다.
*/

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class UserSession {
	
	private String username;

}
