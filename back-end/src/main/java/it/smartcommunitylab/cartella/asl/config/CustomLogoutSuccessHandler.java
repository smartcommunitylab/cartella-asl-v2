package it.smartcommunitylab.cartella.asl.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

//@Component
public class CustomLogoutSuccessHandler {
	
//	@Autowired
//	@Value("${oauth.serverUrl}")
//	private String oauthServerUrl;

	
//	@Override
//	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
//			throws IOException, ServletException {
//		String target = request.getParameter("target");
//		if(target != null && !target.isEmpty()) {
//			String URL = oauthServerUrl + "/logout?target=" + target;
//			response.sendRedirect(URL);
//		}
//		response.setStatus(HttpStatus.OK.value());
//	}

}
