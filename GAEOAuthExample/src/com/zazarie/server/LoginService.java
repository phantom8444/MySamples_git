package com.zazarie.server;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.docs.DocsService;
import com.googlecode.objectify.Key;
import com.zazarie.domain.AppUser;
import com.zazarie.domain.AppUser.OauthCredentials;
import com.zazarie.server.servlet.filter.AuthorizationFilter;
import com.zazarie.shared.Constant;
import com.zazarie.shared.TooManyResultsException;


public class LoginService
{
	
	private final static Logger LOGGER = Logger.getLogger(LoginService.class.getName());

	public static AppUser login(HttpServletRequest req, HttpServletResponse res) {
	
		LOGGER.setLevel(Constant.LOG_LEVEL);
	
		LOGGER.info("Initializing LoginService");
	
		String URI = req.getRequestURI();
	
		UserService userService = UserServiceFactory.getUserService();
	
		User user = userService.getCurrentUser();
	
		if (user != null) {
	
			LOGGER.info("User id is: '" + userService.getCurrentUser().getUserId()
					+ "'");
	
			String userEmail = userService.getCurrentUser().getEmail();
	
			AppUser appUser = (AppUser) req.getSession().getAttribute(
					Constant.AUTH_USER);
	
			if (appUser == null) {
	
				LOGGER.info("appUser not found in session");
	
				// see if it is a new user
				appUser = findUser(userEmail);
	
				if (appUser == null) {
					LOGGER.info("User not found in datastore...creating");
					appUser = addUser(userEmail);
				} else {
					LOGGER.info("User found in datastore...updating");
					appUser = updateUserTimeStamp(appUser);
				}
			} else {
				appUser = updateUserTimeStamp(appUser);
			}
	
			return appUser;
	
		} else {
	
			LOGGER.info("Redirecting user to login page");
	
			try {
				res.sendRedirect(userService.createLoginURL(URI));
			} catch (IOException e) {
				e.printStackTrace();
			}
	
		}
	
		return null;
	}

	public static AppUser getById(long id) {
		AppUser appUser = null;
		try
		{
			AppUserDao userDao = new AppUserDao();
			// Query for user by id
			appUser = userDao.get(id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appUser;
	}

	public static AppUser findUser(String userEmail)
	{
		AppUser appUser = null;
		try
		{
			AppUserDao userDao = new AppUserDao();
			// Query for user by email
			appUser = userDao.getByProperty("email", userEmail);
		} catch (TooManyResultsException e)
		{
			throw new RuntimeException(e);
		}
		return appUser;
	}

	public static AppUser addUser(String email)
	{
		AppUserDao userDao = new AppUserDao();
		
		AppUser newUser = new AppUser(email);
		newUser.setFirstLogin(new Date());
		Key<AppUser> newUserKey = userDao.put(newUser);

		return newUser;
	}
	
	public static AppUser updateUserTimeStamp(AppUser user)
	{
		AppUserDao userDao = new AppUserDao();
		
		try {
			// make sure we have the latest object
			user = userDao.get(new Key<AppUser>(AppUser.class, user.getId()));
			
			user.setLastLogin(new Date());

			userDao.put(user);
			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
	}
	
	public static AppUser updateUserCredentials(AppUser user, AppUser.OauthCredentials credentials)
	{
		AppUserDao userDao = new AppUserDao();
		
		try {
			// make sure we have the latest object
			user = userDao.get(new Key<AppUser>(AppUser.class, user.getId()));
			
			user.setCredentials(credentials);

			userDao.put(user);
			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
	}

	public static DocsService docServiceFactory(AppUser user) {
		
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		
		oauthParameters.setOAuthConsumerKey(Constant.CONSUMER_KEY);
        oauthParameters.setOAuthConsumerSecret(Constant.CONSUMER_SECRET);
        
        oauthParameters.setOAuthToken(user.getCredentials().getAccessToken());
        oauthParameters.setOAuthTokenSecret(user.getCredentials().getAccessTokenSecret());
        
     // Create an instance of the DocsService to make API calls
        DocsService client = new DocsService(Constant.DOC_SERVICE);

        // Use our newly built oauthParameters
        try {
			client.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
			return client;
		} catch (OAuthException e) {
			e.printStackTrace();
		}
        
        return null;
	}
}
