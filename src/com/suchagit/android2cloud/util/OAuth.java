package com.suchagit.android2cloud.util;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class OAuth {
	
	private static OAuthProvider provider;
	private static OAuthConsumer consumer;
	
	private static final String REQUEST_TOKEN_URL = "_ah/OAuthGetRequestToken";
	private static final String ACCESS_TOKEN_URL = "_ah/OAuthGetAccessToken";
	private static final String AUTHORISE_TOKEN_URL = "_ah/OAuthAuthorizeToken?btmpl=mobile";
	private static final String CALLBACK_URL = "http://2cloud.app/oauth/callback/";
	
	public static String getRequestUrl(String host){
		String request_token_url = host + REQUEST_TOKEN_URL;
		String access_token_url = host + ACCESS_TOKEN_URL;
		String authorise_token_url = host + AUTHORISE_TOKEN_URL;
		consumer = new CommonsHttpOAuthConsumer(HttpClient.CONSUMER_KEY, HttpClient.CONSUMER_SECRET);
        provider = new CommonsHttpOAuthProvider(request_token_url, access_token_url, authorise_token_url);
        
        String target = null;
        try {
			target = provider.retrieveRequestToken(consumer, CALLBACK_URL);
		} catch (OAuthMessageSignerException e) {
			target = "OAuthMessageSignerException";
		} catch (OAuthNotAuthorizedException e) {
			target = "OAuthNotAuthorizedException";
		} catch (OAuthExpectationFailedException e) {
			target = "OAuthExpectationFailedException";
		} catch (OAuthCommunicationException e) {
			target = "OAuthCommunicationException";
		}
		return target;
	}
}
