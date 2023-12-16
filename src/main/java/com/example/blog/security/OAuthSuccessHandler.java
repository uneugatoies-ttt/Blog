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
	- This class's method is called when the OAuth's flow is
	successfully finished. So, we can utilize this for
	issuing a token after the OAuth authentication for
	a user is completed.
	
	- Because this is executed when the authentication flow is complete,
	the token must be stored in the cookie beforehand;
	the storing process is done within 'RedirectUrlCookieFilter'.
	REDIRECT_URI_PARAM is a String constant that indicates
	the name of the cookie.
	
	- Remember the 'filter' operation that is used on the Cookie array
	is utilizing Java's Stream API.
	
	- The commented final part of the method "onAuthenticationSuccess()" is 
	originally getting the redirection URI from the request's cookies.
	But, since, as of now, the redirection URI is always the same as the value of
	the constant LOCAL_REDIRECT_URL, we can say the process is redundant,
	therefore I omit the cookie retrieval.
	If any change is made in the future, then corresponding handling should be done;
	but while the situation remains the same, I'll just keep doing like this.
	
	- I have to append the name of the user who is the subject of this authentication
	process to the URL, so we can store the 'USERNAME' in the localStorage at the frontend.
	Now, because this new requirement has arisen, I should go back using cookies;
	I have no better way to implement all these.
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
		//	the above line is equivalent to the below line; the difference is 
		//	whether they're using a lambda expression or are they using the method reference.
		// Optional<String> redirectUri = oCookie.map(c -> c.getValue());
		
		String username = userSession.getUsername();
		
		log.info("token {}", token);
		log.info("username {}", username);
		
		response.sendRedirect(redirectUri.orElseGet(() -> LOCAL_REDIRECT_URL) + "/sociallogin?token=" + token + "&username=" + username);
	}
}
