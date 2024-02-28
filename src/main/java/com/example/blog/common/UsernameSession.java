package com.example.blog.common;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/*
	-> REST API의 statelessness를 위반한다고 생각해 Session을 사용하지 않으려고 했지만, 이를 사용하지
	않고 어떻게 하면 "onAuthenticationSuccess()"에서 username을 가져올 수 있을지 떠올리지 못했다.
	만약 하나의 resource server provider만을 사용한다면 (e.g., Google) 그냥 "authentication"에서
	getPrincipal()을 call해 그것을 ApplicationOAuth2User로 cast하고 거기서 attribute 중에 하나를 
	사용하면 되겠지만, 다른 provider들을 사용할 때에 문제 발생의 여지가 있다는 판단에 그것은 할 수 없다고 생각했다.
*/

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class UsernameSession {
	
	private String username;
	

}
