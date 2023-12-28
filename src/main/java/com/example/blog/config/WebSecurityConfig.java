package com.example.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
	
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	private OAuthUserServiceImpl oAuthUserService;
	private OAuthSuccessHandler oAuthSuccessHandler;
	private RedirectUrlCookieFilter redirectUrlCookieFilter;
	
	public WebSecurityConfig(
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
				.antMatchers("/", "/auth/**", "/oauth2/**", "/test").permitAll()
				.anyRequest()
				.authenticated()
				.and()
			.oauth2Login()
				.redirectionEndpoint()
				.baseUri("/oauth2/callback/*")
			/*
			 	Don't forget that the reason why you can use OAuth
			 	with the path "/auth/authorize/github" is ascribed to
			 	the following 3 lines.
			*/
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
		
		// register filter
		http.addFilterAfter(
			jwtAuthenticationFilter,
			CorsFilter.class
		);
		
		http.addFilterBefore(
			redirectUrlCookieFilter,
			// the filter must be executed before redirection is performed.
			OAuth2AuthorizationRequestRedirectFilter.class
		);
		
		return http.build();
	}
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
}
