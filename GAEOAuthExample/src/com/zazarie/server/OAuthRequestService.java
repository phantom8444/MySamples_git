package com.zazarie.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.zazarie.shared.Constant;

public class OAuthRequestService {
	
	private final static Logger LOGGER = Logger
			.getLogger(OAuthRequestService.class.getName());
	
	public static void requestOAuth(HttpServletRequest req,
			HttpServletResponse res, HttpSession session) {
	
		LOGGER.setLevel(Constant.LOG_LEVEL);
	
		LOGGER.info("Initializing OAuthRequestService");
	
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(Constant.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(Constant.CONSUMER_SECRET);
	
		// Set the scope.
		oauthParameters.setScope(Constant.GOOGLE_RESOURCE);
	
		// Sets the callback URL.
		oauthParameters.setOAuthCallback(Constant.OATH_CALLBACK);
	
		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
				new OAuthHmacSha1Signer());
	
		try {
			// Request is still unauthorized at this point
			oauthHelper.getUnauthorizedRequestToken(oauthParameters);
	
			// Generate the authorization URL
			String approvalPageUrl = oauthHelper
					.createUserAuthorizationUrl(oauthParameters);
	
			session.setAttribute(Constant.SESSION_OAUTH_TOKEN,
					oauthParameters.getOAuthTokenSecret());
	
			LOGGER.info("Session attributes are: "
					+ session.getAttributeNames().hasMoreElements());
	
			res.getWriter().print(
					"<a href=\"" + approvalPageUrl
							+ "\">Request token for the Google Documents Scope</a>");
	
		} catch (OAuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

}
