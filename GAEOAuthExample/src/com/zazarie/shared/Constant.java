package com.zazarie.shared;

import java.util.logging.Level;

public interface Constant {

	final static Level LOG_LEVEL = Level.INFO;
	
	final static String AUTH_USER = "LOGGED_IN_USER";
	
	final static String TARGET_URI = "TARGET_URI";
	
	final static String DOC_SERVICE = "TennisRX Doc Service";
	
	final static String DOC_SESSION_ID = "DOC_SESSION_ID";
	
	final static String GOOGLE_DOCS_FEED = "https://docs.google.com/feeds/default/private/full/-/document";
	
	final static String GOOGLE_SPREADSHEET_FEED = "https://docs.google.com/feeds/default/private/full/-/spreadsheet";
	
	/* OAUTH Related */
	
	final static String CONSUMER_KEY = "<enter yours>";
	
	final static String CONSUMER_SECRET = "<enter yours>";
	
	final static String GOOGLE_RESOURCE = "https://docs.google.com/feeds/";
	
	final static String SESSION_OAUTH_TOKEN = "oauthTokenSecret";
	
	// Use for running on GAE
	//final static String OATH_CALLBACK = "http://tennis-coachrx.appspot.com/authSub";
	
	// Use for local testing
	final static String OATH_CALLBACK = "http://127.0.0.1:8888/authSub";
	
}
