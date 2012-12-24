package com.zazarie.server.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;
import com.zazarie.domain.AppUser;
import com.zazarie.domain.AppUser.OauthCredentials;
import com.zazarie.server.LoginService;
import com.zazarie.shared.Constant;

public class RequestTokenCallbackServlet extends HttpServlet {

	private final static Logger LOGGER = Logger
			.getLogger(RequestTokenCallbackServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
		LOGGER.setLevel(Constant.LOG_LEVEL);
	
		LOGGER.info("Initializing RequestTokenCallbackServlet");
	
		// Create an instance of GoogleOAuthParameters
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(Constant.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(Constant.CONSUMER_SECRET);
	
		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(
				new OAuthHmacSha1Signer());
	
		// For testing -- showing the session attributes
		Enumeration en = req.getSession().getAttributeNames();
		while (en.hasMoreElements()) {
			LOGGER.info("Attribute name is: " + en.nextElement());
		}
	
		String oauthTokenSecret = (String) req.getSession().getAttribute(
				Constant.SESSION_OAUTH_TOKEN);
	
		LOGGER.info("Session oauthTokenSecret value is: " + oauthTokenSecret);
	
		LOGGER.info("Session user is: "
				+ req.getSession().getAttribute(Constant.AUTH_USER));
	
		AppUser appUser = (AppUser) req.getSession().getAttribute(
				Constant.AUTH_USER);
	
		oauthParameters.setOAuthTokenSecret(oauthTokenSecret);
	
		// The query string should contain the oauth token, so we can just
		// pass the query string to our helper object to correctly
		// parse and add the parameters to our instance of oauthParameters
		oauthHelper.getOAuthParametersFromCallback(req.getQueryString(),
				oauthParameters);
	
		try {
	
			// Now that we have all the OAuth parameters we need, we can
			// generate an access token and access token secret. These
			// are the values we want to keep around, as they are
			// valid for all API calls in the future until a user revokes
			// our access.
			String accessToken = oauthHelper.getAccessToken(oauthParameters);
	
			String accessTokenSecret = oauthParameters.getOAuthTokenSecret();
	
			LOGGER.info("accessTokenSecret is: " + accessTokenSecret);
	
			// Store the accessToken and accessTokenSecret in the user's credentials.
	
			appUser = LoginService.getById(appUser.getId());
	
			LOGGER.info("Appuser is: " + appUser);
	
			// Add the credentials to the user
			appUser = LoginService.updateUserCredentials(appUser,
					new OauthCredentials(accessToken, accessTokenSecret));
	
			// Add the doc service to the session so we can easily reuse it
			req.getSession().setAttribute(Constant.DOC_SESSION_ID,
					LoginService.docServiceFactory(appUser));
	
			// Now we can redirect the user to the destination URL
			RequestDispatcher dispatcher = req.getRequestDispatcher((String) req
					.getSession().getAttribute(Constant.TARGET_URI));
			if (dispatcher != null)
				dispatcher.forward(req, resp);
	
		} catch (OAuthException e) {
			e.printStackTrace();
		}
	}
}