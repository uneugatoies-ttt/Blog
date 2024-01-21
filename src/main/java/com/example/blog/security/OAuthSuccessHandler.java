package com.example.blog.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.blog.common.RedirectUriSession;
import com.example.blog.common.UserSession;

import lombok.AllArgsConstructor;

/*
	-> OAuthSuccessHandler의 method는 OAuth의 흐름이 성공적으로 종료했을 때 call된다. OAuth를
	사용하는 user들을 위해서, 이 class 내부에서는 그들에 대응되는 JWT들을 만들어 주어야 한다.
	
	-> 원래는 frontend로부터 URI의 parameter로 "redirect_uri"의 값을 받아 그것을
	RedirectUrlCookieFilter에서 cookie에 담고서 이후에 이 class에서 그것을 회수하는 것으로 설정했다.
	그런데 어느 시점 이후로부터 저장된 userName의 값을 frontend로 return해야 했기에 이것 또한
	OAuthUserServiceImpl에서 처리될 때에 어딘가에 저장해야 했고, 이 때는 session을 사용해서 저장했다.
	
	이전에 이 부분을 만들 때에 기록했던 log를 살펴보면 과정은 이러했다:
		-> userName을 frontend에서 사용하기 위해서 JWT를 받을 때에
		이것도 backend로부터 받아서 local storage에 저장하려고 했고, 이를 위해서 userName을 authentication이
		끝난 다음에 frontend에 보내주어야 했다.
		
		하지만 일반 authentication이 아닌 OAuth를 구현할 때는 이것이 약간 어렵게 되었는데, 원래 생각에는 user input을
		받을 때에 회수한 "userName"을 그대로 사용한다는 것이었지만 OAuth를 사용할 때에는 frontend application에서는
		user input을 전혀 받지 않기 때문이었다; 따라서 다른 방법을 찾아야 했다.
		그래서 OAuth 흐름에서부터 userName으로 사용할 data를 OAuthUserServiceImpl에서 OAuth2User에 있는
		어떤 field의 값을 추출하는 것으로 정했고, 이것을 UserSession에 넣어서 이후 이 OAuthSuccessHandler에 
		사용하려고 한 것이었다.
		
		이 용도로 Cookie를 사용할 수 없었던 이유는 DefaultOAuth2UserService로부터 override하는 loadUser에서
		어떻게 하면 Cookie를 사용하는 데에 필요한 HttpServletResponse를 사용할 수 있는지를 알 수 없었기 때문이다.

	지금 review를 하는 시점에서 생각해 보면 굳이 Cookie와 session를 동시에 쓸 필요는 없다고 생각했기에, redirect URI를
	위한 session 설정 역시 추가하여 이 두 가지의 값에 대한 처리는 session으로만 하는 것으로 변경했다.
	
	물론, 혹여 이후에 이 결정이 잘못된 것이었다는 판단이 든다면 다시 돌아오겠다.
*/

@Component
@AllArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private static final String LOCAL_REDIRECT_URL = "http://localhost:3000";
	
	private UserSession userSession;
	private RedirectUriSession redirectUriSession;
	private TokenProvider tokenProvider;
	
	// Session만을 사용해 "userName"과 "redirectUri"를 회수한다.
	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		String token = tokenProvider.create(authentication);
		
		String redirectUri = redirectUriSession.getRedirectUri();
		
		/*
		(Cookie를 사용하고 있을 때에)
		어떤 이유에선지 만약 OAuth2 흐름을 initiate할 때 "redirect_uri"를 URI의 parameter로 
		명시하지 않으면 자동으로 "redirectUri"의 값으로 이 backend application의 domain이 들어가는 
		일이 있었기에, validation을 좀 더 확실하게 해야 했다.
		*/
		if (redirectUri == null || 
			redirectUri.equals("") || 
			redirectUri.equals("http://localhost:8080")
		) {
			redirectUri = LOCAL_REDIRECT_URL;
		}
		
		String userName = userSession.getUsername();
		
		response.sendRedirect(redirectUri + "/sociallogin?token=" + token + "&userName=" + userName);
	}
	
}
