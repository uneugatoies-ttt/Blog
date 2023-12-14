package com.example.blog.security.filters;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;



/*
	- This class is for storing the token in the cookie
	so that OAuthSuccessHandler can use it by searching the cookie.
	Since GitHub is in between this flow of OAuth authentication,
	you cannot pass the token just by using the URL's parameter;
	you have to store it in the cookie.
	
	- As I described the situation in 'OAuthSuccessHandler,'
	cookie storing and retrieving are redundant as of now.
	So I made this whole class be a bunch of comments.
	If any change is made in the future, then responding handling should be done.
	
	- But I think I have to use cookies, since now we have to send not only 
	the redirect URI but also the subject of this authentication - id est,
	the user's name.
	
	- Remember that if you want this filter to work, you'll have to manipulate
	the WebSecurityConfig class also - not only this class.
*/



@Slf4j
@Component
public class RedirectUrlCookieFilter extends OncePerRequestFilter {
	public static final String REDIRECT_URI_PARAM = "redirect_uri";
	private static final int MAX_AGE = 180;
	
	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		if (request.getRequestURI().startsWith("/auth/authorize")) {
			try {
				log.info("request url {} ", request.getRequestURI());
			
				log.info("request param: redirect_uri {} ", request.getParameter("redirect_uri"));
				
				String redirectUrl = request.getParameter(REDIRECT_URI_PARAM);
				
				Cookie cookieRedirect = new Cookie(REDIRECT_URI_PARAM, redirectUrl);
				cookieRedirect.setPath("/");
				cookieRedirect.setHttpOnly(true);
				cookieRedirect.setMaxAge(MAX_AGE);
				response.addCookie(cookieRedirect);
				
			} catch (Exception e) {
				logger.error(e);
				log.info("unauthorized request");
			}
		}
		filterChain.doFilter(request, response);
	}
}