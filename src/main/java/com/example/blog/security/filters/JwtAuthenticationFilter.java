package com.example.blog.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.blog.security.TokenProvider;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private TokenProvider tokenProvider;
	
	public JwtAuthenticationFilter(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		try {
			String token = parseBearerToken(request);
			
			if (token != null && !token.equalsIgnoreCase("null")) {
				String userId = tokenProvider.validateAndGetUserId(token);
				
				AbstractAuthenticationToken authen =
					new UsernamePasswordAuthenticationToken(
							userId,
							null,
							AuthorityUtils.NO_AUTHORITIES
					);
				
				authen.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
				securityContext.setAuthentication(authen);
				SecurityContextHolder.setContext(securityContext);
			}
			
		} catch (Exception ex) {
			System.err.println("User authentication failed due to the security problem: " + ex);
		}
		
		filterChain.doFilter(request, response);
	}
	
	private String parseBearerToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
			return bearerToken.substring(7);
		
		return null;
	}
}
