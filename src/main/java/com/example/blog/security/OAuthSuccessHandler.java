package com.example.blog.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.blog.misc.UserSession;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.example.blog.security.filters.RedirectUrlCookieFilter.REDIRECT_URI_PARAM;

/*
	- The method of this class is called when the OAuth's flow is
	successfully finished. In this class, we have to create a 
	JWT for the corresponding user.
	
	- Because this is executed when the authentication flow is complete,
	the token must be stored in the cookie beforehand;
	the storing process is done within 'RedirectUrlCookieFilter'.
	REDIRECT_URI_PARAM is a String constant that indicates
	the name of the cookie.
	
	- Remember the 'filter' operation that is used on the Cookie array
	is utilizing Java's Stream API.
	
	- I used to manage this class without cookies since I thought setting the predefined same
	redirect URI again and again. But I have to append the name of the user who is the subject
	of this authentication process to the URL, so we can store the 'USERNAME' in the
	localStorage at the frontend. Now, because this new requirement has arisen,
	I should go back using cookies; I have no better way to implement all these.
*/

@Slf4j
@Component
@AllArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private static final String LOCAL_REDIRECT_URL = "http://localhost:3000";
	
	private UserSession userSession;
	private TokenProvider tokenProvider;
	
	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		log.info("auth succeeded");
		String token = tokenProvider.create(authentication);
		
		Optional<Cookie> oCookie = Arrays.stream(request.getCookies())
										.filter(
											cookie -> cookie.getName()
													.equals(REDIRECT_URI_PARAM)
										)
										.findFirst();
				
		Optional<String> redirectUri = oCookie.map(Cookie::getValue);
		//	the above line is equivalent to the below line;
		// Optional<String> redirectUri = oCookie.map(c -> c.getValue());
		
		// I don't know why, but if you don't specify the param "redirect_uri"
		// when you initiate the OAuth2 flow, then the value of "redirectUri.get()"
		// becomes this backend app's URI; so I had to append this validation.
		// I'll fix the problem later.
		String ru = null;
		if (redirectUri.isEmpty() || 
			redirectUri.get().equals("") || 
			redirectUri.get().equals("http://localhost:8080")) {
			ru = LOCAL_REDIRECT_URL;
		} else {
			ru = redirectUri.get();
		}
		
		String userName = userSession.getUsername();
		
		log.info("token {}", token);
		log.info("userName {}", userName);
		
		response.sendRedirect(ru + "/sociallogin?token=" + token + "&userName=" + userName);
	}
}
