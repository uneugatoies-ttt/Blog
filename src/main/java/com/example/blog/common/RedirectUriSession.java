package com.example.blog.common;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/*
	-> OAuth2를 사용해서 인증할 때 backend에서의 그 흐름이 종료되면 frontend로 사용자를 redirect시켜야 하는데,
	RedirectUriSession은 이 때 필요한 URL의 value를 저장하는 session의 역할을 한다.
	이것의 저장은 "RedirectUrlSessionFilter"에서, 저장된 값의 회수는 "OAuthSuccessHandler"에서 행해진다.
*/

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class RedirectUriSession {
	
	private String redirectUri;
	
}
