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

/*
	-> JwtAuthenticationFilter는 들어오는 request로부터:
		1) "Authorization" header를 확인해 token을 회수하고,
		2) 그것으로 얻을 수 있는 userId를 TokenProvider의 validateAndGetUserId()로부터 받아,
		3) ID를 "username"으로 사용하는 UsernamePasswordAuthenticationToken을 정의해,
		4) 그것의 detail에 이 request를 설정하고,
		5) SecurityContext에 이 authentication token를 담아 SecurityContextHolder에 저장한다.
*/

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

				/*
					-> UsernamePasswordAuthenticationToken의 constructor는 내부적으로
					super (AbstarctAuthenticationToken) 의 setAuthenticated()를 call한다.
					따라서 이 이후에 별도로 setAuthenticated()를 call할 필요는 없다.
					
					-> "credentials"에 token을 주지 않고 그냥 "null"으로 설정하는 이유는?
				*/
				AbstractAuthenticationToken authen =
					new UsernamePasswordAuthenticationToken(
							userId,
							null,
							AuthorityUtils.NO_AUTHORITIES
					);
				
				// 이 request에 담긴 detail들을 이 authentication token에 설정한다.
				authen.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				// security context를 만들어 위에서 정의한 authentication token을 담아 holder에 저장한다.
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
