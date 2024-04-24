package com.example.blog.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.filter.CorsFilter;

import com.example.blog.security.OAuthSuccessHandler;
import com.example.blog.security.OAuthUserServiceImpl;
import com.example.blog.security.filters.JwtAuthenticationFilter;
import com.example.blog.security.filters.RedirectUrlCookieFilter;

/*
	-> WebSecurityConfig는 security와 관련된 설정을 하는 class이다.
	어떤 path로 들어오는 request를 허용할 것인지, OAuth2의 처리는 어떻게 할 것인지,
	어떤 custom filter를 사용할 것인지 등의 설정을 하고 있다.

	-> ".redirectionEndpoint().baseUri("/oauth2/callback/*")":
	
		- 이것은 OAuth2 redirection callbacks으로 사용할 기본 URI를 명시한다.
		이 앱의 경우 "/oauth2/callback/*"라고 설정했는데, 이것이 OAuth2 provider (GitHub 혹은 Google) 가
		성공적인 인증 이후 사용자를 redirect할 장소가 된다.
	
	-> ".authorizationEndpoint().baseUri("/auth/authorize")":
	
		- 이것은 authorization end point가 노출될 장소가 될 기본 URI를 명시한다.
		authorization end point는 OAuth2 인증의 flow를 시작하는 데에 사용된다.
		이 앱의 경우 "/auth/authorize"라고 설정했는데, 이것이 GitHub이나 Google을 사용해
		OAuth2 인증을 시작할 때 사용하는 URL pattern이 된다.
		
		- 이 설정이 있기 때문에 
		"http://localhost:8080/auth/authorize/github" 혹은
		"http://localhost:8080/auth/authorize/google"
		와 같은 end point들에 접속하는 것만으로 OAuth 인증의 flow를 시작할 수 있는 것이다.
*/


@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
	
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	private OAuthUserServiceImpl oAuthUserService;
	private OAuthSuccessHandler oAuthSuccessHandler;
	private RedirectUrlCookieFilter redirectUrlCookieFilter;
	
	public WebSecurityConfig (
			JwtAuthenticationFilter jwtAuthenticationFilter,
			OAuthUserServiceImpl oAuthUserService,
			OAuthSuccessHandler oAuthSuccessHandler,
			RedirectUrlCookieFilter redirectUrlCookieFilter
	) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.oAuthUserService = oAuthUserService;
		this.oAuthSuccessHandler = oAuthSuccessHandler;
		this.redirectUrlCookieFilter = redirectUrlCookieFilter;
	}
	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors()
				.and()
			.csrf()
				.disable()
			.httpBasic()
				.disable()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.authorizeRequests()
				.antMatchers("/", "/auth/**", "/oauth2/**", "/user/**", "/test/**").permitAll()
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				.anyRequest().authenticated()
				.and()
			.oauth2Login()
				.redirectionEndpoint()
					.baseUri("/oauth2/callback/*")
					.and()
				.authorizationEndpoint()
					.baseUri("/auth/authorize")
					.and()
				.userInfoEndpoint()
					.userService(oAuthUserService)
					.and()
				.successHandler(oAuthSuccessHandler)
					.and()
				.exceptionHandling()
					.authenticationEntryPoint(new Http403ForbiddenEntryPoint());
		
		
		// 이 설정이 있기 때문에 RedirectUrlCookieSessionFilter가 사용된다.
		http.addFilterBefore(
			redirectUrlCookieFilter,
			OAuth2AuthorizationRequestRedirectFilter.class
		);
		
		// Filter 등록
		http.addFilterAfter(
			jwtAuthenticationFilter,
			CorsFilter.class
		);
		
		return http.build();
	}
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

