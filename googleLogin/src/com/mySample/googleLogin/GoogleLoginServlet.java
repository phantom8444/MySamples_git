package com.mySample.googleLogin;

import java.io.IOException;
import javax.servlet.http.*;

import com.google.appengine.api.oauth.*;
import com.google.appengine.api.users.*;

import java.util.logging.Logger;

@SuppressWarnings("serial")
public class GoogleLoginServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(GoogleLoginServlet.class.getName());
	
	
	private String consumerKey;
	private String consumerSecret;
	private String scope;
	private String callBackUrl;
	private UserService userService = UserServiceFactory.getUserService();
	
	public GoogleLoginServlet() {
		
	}
	
	private void initProperties() {
		
	}
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		User user = null;
		try {
			OAuthService oauth = OAuthServiceFactory.getOAuthService();
			user = oauth.getCurrentUser();
			resp.getWriter().println("Authenticated: " + user.getEmail());
//			log.info(user.getEmail());
		} catch (OAuthRequestException e) {
//			log.info(e.getMessage());
			resp.getWriter().println("Not authenticated: " + e.getMessage());
		}
	}
}
