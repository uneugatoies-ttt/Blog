package com.example.blog.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/*
	-> 기존에는 Session을 사용해서 redirectUrl을 저장했지만, 이것이 REST API의 
	statelessness를 위반한다는 판단에 다시 cookie를 사용하는 것으로 변경했다.
*/

@Component
public class RedirectUrlCookieFilter extends OncePerRequestFilter {
	
	public static final String REDIRECT_URI_PARAM = "redirect_uri";
	
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
				Cookie c = new Cookie("redirectUri", redirectUri);
				response.addCookie(c);
			}
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			throw e;
		}
	}
	
}