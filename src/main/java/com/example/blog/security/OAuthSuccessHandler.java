package com.example.blog.security;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.blog.common.UsernameSession;

/*
	-> OAuthSuccessHandler의 method는 OAuth의 흐름이 성공적으로 종료했을 때 call된다. OAuth를
	사용하는 user들을 위해서, 이 class 내부에서는 그들에 대응되는 JWT들을 만들어 주어야 한다.
	
	(24-02-28)
	-> REST API의 statelessness를 위반한다고 생각해 Session을 사용하지 않으려고 했지만, 이를 사용하지
	않고 어떻게 하면 "onAuthenticationSuccess()"에서 username을 가져올 수 있을지 떠올리지 못했다.
	만약 하나의 resource server provider만을 사용한다면 (e.g., Google) 그냥 "authentication"에서
	getPrincipal()을 call해 그것을 ApplicationOAuth2User로 cast하고 거기서 attribute 중에 하나를 
	사용하면 되겠지만, 다른 provider들을 사용할 때에 문제 발생의 여지가 있다는 판단에 그것은 할 수 없다고 생각했다.
*/

@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private static final String LOCAL_REDIRECT_URL = "http://localhost:3000";
	
	private TokenProvider tokenProvider;
	private UsernameSession usernameSession;
	
	public OAuthSuccessHandler(TokenProvider tokenProvider, UsernameSession usernameSession) {
		this.tokenProvider = tokenProvider;
		this.usernameSession = usernameSession;
	}
	
	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		String token = tokenProvider.create(authentication);
		
		Cookie[] cs = request.getCookies();
		String redirectUri = LOCAL_REDIRECT_URL;
		for (Cookie c : cs)
			if (c.getName().equals("redirectUri"))
				redirectUri = c.getValue();
		
		/*
		(Cookie를 사용하고 있을 때에)
		어떤 이유에선지 만약 OAuth2 흐름을 initiate할 때 "redirect_uri"를 URI의 parameter로 
		명시하지 않으면 자동으로 "redirectUri"의 값으로 이 backend application의 domain이 들어가는 
		일이 있었기에, validation을 좀 더 확실하게 해야 했다.
		*/
		if (redirectUri == null || 
			redirectUri.equals("") || 
			redirectUri.equals("http://localhost:8080")
		) {
			redirectUri = LOCAL_REDIRECT_URL;
		}
		
		String userName = usernameSession.getUsername();
		
		response.sendRedirect(redirectUri + "/sociallogin?token=" + token + "&userName=" + userName);
	}
	
}
