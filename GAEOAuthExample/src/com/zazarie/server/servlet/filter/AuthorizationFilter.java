package com.zazarie.server.servlet.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.zazarie.domain.AppUser;
import com.zazarie.server.LoginService;
import com.zazarie.server.OAuthRequestService;
import com.zazarie.shared.Constant;

public final class AuthorizationFilter implements Filter {

	private FilterConfig filterConfig = null;
	
	private final static Logger LOGGER = Logger
			.getLogger(AuthorizationFilter.class.getName());

	public void init(FilterConfig filterConfig) throws ServletException {

		LOGGER.setLevel(Constant.LOG_LEVEL);
		
		LOGGER.info("Initializing Authorization Filter");
		
		this.filterConfig = filterConfig;
	}

	public void destroy() {
		this.filterConfig = null;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
	
		HttpServletRequest request = (HttpServletRequest) req;
	
		HttpServletResponse response = (HttpServletResponse) res;
	
		HttpSession session = request.getSession();
	
		LOGGER.info("Invoking Authorization Filter");
	
		LOGGER.info("Destination URL is: " + request.getRequestURI());
	
		if (filterConfig == null)
			return;
	
		// get the Google user
		AppUser appUser = LoginService.login(request, response);
	
		if (appUser != null) {
			session.setAttribute(Constant.AUTH_USER, appUser);
		}
	
		// identify if user has an OAuth accessToken - it not, will set in motion
		// oauth procedure
		if (appUser.getCredentials() == null) {
	
			// need to save the target URI in session so we can forward to it when
			// oauth is completed
			session.setAttribute(Constant.TARGET_URI, request.getRequestURI());
	
			OAuthRequestService.requestOAuth(request, response, session);
			return;
		} else
			// store DocService in the session so it can be reused
			session.setAttribute(Constant.DOC_SESSION_ID,
					LoginService.docServiceFactory(appUser));
	
		chain.doFilter(request, response);
	
	}

}