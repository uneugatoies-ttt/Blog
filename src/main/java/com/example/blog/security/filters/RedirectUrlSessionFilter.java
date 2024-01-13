package com.example.blog.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.blog.misc.RedirectUriSession;

/*
	-> "OAuthSuccessHandler의 comment에서 설명된 것과 같이, 원래는 이 class 내부에서 
	Cookie를 사용해서 이 URI로 들어온 "redirect_uri"라는 이름의 parameter가 있다면
	그것을 저장했지만, 이미 UserSession을 사용하고 있는 상황에서 굳이 cookie와 session을
	동시에 쓸 필요는 없다고 생각해서 일괄적으로 session을 사용하는 것으로 노선을 변경했다.
	
	물론, 이후에 어떤 변경 사항이 생긴다면 그에 따라 바꾸도록 하겠다.
*/

@Component
public class RedirectUrlSessionFilter extends OncePerRequestFilter {
	
	public static final String REDIRECT_URI_PARAM = "redirect_uri";
	
	private RedirectUriSession redirectUriSession;
	
	public RedirectUrlSessionFilter(RedirectUriSession redirectUriSession) {
		this.redirectUriSession = redirectUriSession;
	}
	
	// Using RedirectUriSession
	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		try {
			if (request.getRequestURI().startsWith("/auth/authorize")) {
				String redirectUri = request.getParameter(REDIRECT_URI_PARAM);
				redirectUriSession.setRedirectUri(redirectUri);
			}
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			throw e;
		}
	}
	
}