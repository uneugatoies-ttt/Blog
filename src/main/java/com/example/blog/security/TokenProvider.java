package com.example.blog.security;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.blog.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;

/*
 	-> TokenProvider는 JWT를 생성한다.
 	
 	-> authentication 결과의 token은 이 class에서 만들어진다는 점을 기억할 것. database에 저장되었다가
 	fetch되는 것이 아니다.
 	
 	-> Jwts를 사용해서 JWT가 만들어지는 과정이 이곳에 모두 명시되어 있다:
 		-> ".setSubject(userEntity.getId())": 이렇게 지정하기 때문에 이후 subject를 retrieve하면 
 		그 value는 database의 user의 ID와 동일한 값이 있는 것을 확인할 수 있다.
*/

@AllArgsConstructor
@Service
public class TokenProvider {
	
	public String create(User userEntity) throws IOException {
		final String secretKey = JwtKeyReader.readKey();
		
		Date expiryDate = Date.from(
			Instant.now().plus(1, ChronoUnit.DAYS)
		);
		
		return Jwts.builder()
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.setSubject(userEntity.getId())		
				.setIssuer("blog")
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.compact();
	}
	
	/*
		-> OAuth 2의 흐름에서 token을 생성할 때, User가 아닌 Authentication을 사용해서 생성하기 위한
		method이다.
		
		-> 이 method를 위해서 ApplicationOAuth2User를 정의한 것이다. 이를 사용함으로써 결과물로 나오는
		JWT의 subject에 들어갈 값을 만들 수 있다.
	*/
	public String create(final Authentication authentication) throws IOException {
		final String secretKey = JwtKeyReader.readKey();
		
		ApplicationOAuth2User userPrincipal = (ApplicationOAuth2User) authentication.getPrincipal();
		Date expiryDate = Date.from( 
							Instant
							.now()
							.plus(1, ChronoUnit.DAYS) 
						);
		
		return Jwts
				.builder()
				.setSubject(userPrincipal.getName())
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
	}
	
	/*
		-> 이 application에서 JWT를 생성할 때에 subject로 ID를 지정하고 있기 때문에,
		이 method는 해당 token에 상응하는 user의 ID 값을 return하게 될 것이다.
		
		-> 현재는 JWT의 expiration이 지났을 경우 RuntimeException을 throw하고 있지만,
		이것이 최선의 방법인지는 아직 잘 모르겠다.
	*/
	public String validateAndGetUserId(String token) throws IOException {
		final String secretKey = JwtKeyReader.readKey();
		
		Claims claims = Jwts.parser()
							.setSigningKey(secretKey)
							.parseClaimsJws(token)
							.getBody();
		
		// JWT의 만기가 지났을 경우
		if (claims.getExpiration().before(new Date())) {
			throw new RuntimeException("The given JWT has expired.");
		}
		
		return claims.getSubject();
	}
	
}
