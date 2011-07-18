package com.suchagit.android2cloud.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.net.Uri;
import android.util.Log;

public class OAuth {
	
	private static OAuthProvider provider;
	private static OAuthConsumer consumer;
	
	private static final String REQUEST_TOKEN_URL = "_ah/OAuthGetRequestToken";
	private static final String ACCESS_TOKEN_URL = "_ah/OAuthGetAccessToken";
	private static final String AUTHORISE_TOKEN_URL = "_ah/OAuthAuthorizeToken?btmpl=mobile";
	
	public static final String CALLBACK_PATH = "oauth/callback/";
	private static final String CALLBACK_PROTOCOL = "2cloud";
	
	
	public static OAuthConsumer makeConsumer() {
		return new CommonsHttpOAuthConsumer(HttpClient.CONSUMER_KEY, HttpClient.CONSUMER_SECRET);
	}
	
	public static OAuthProvider makeProvider(String host) {
		String request_token_url = host + REQUEST_TOKEN_URL;
		String access_token_url = host + ACCESS_TOKEN_URL;
		String authorise_token_url = host + AUTHORISE_TOKEN_URL;
        return new CommonsHttpOAuthProvider(request_token_url, access_token_url, authorise_token_url);
	}
	
	public static String getRequestUrl(String host, String account){
		if(consumer == null) {
			consumer = makeConsumer();
		}
		
		if(provider == null) {
			provider = makeProvider(host);
		}
        String target = null;
        try {
        	account = Uri.encode(account);
        	Log.d("OAuth", CALLBACK_PROTOCOL+CALLBACK_PATH+"?account=" + account);
			target = provider.retrieveRequestToken(consumer, host+CALLBACK_PATH+"?protocol="+CALLBACK_PROTOCOL+"&account="+account);
		} catch (OAuthMessageSignerException e) {
			target = "OAuthMessageSignerException: "+e.getMessage();
		} catch (OAuthNotAuthorizedException e) {
			target = "OAuthNotAuthorizedException";
		} catch (OAuthExpectationFailedException e) {
			target = "OAuthExpectationFailedException";
		} catch (OAuthCommunicationException e) {
			target = "OAuthCommunicationException"+e.getMessage();
		}
		return target;
	}
	
	public static OAuthConsumer getAccessToken(String host, String verifier) {
		try {
			if(provider == null) {
		        provider = makeProvider(host);
			}
			
			if(consumer == null) {
				consumer = makeConsumer();
			}
			provider.retrieveAccessToken(consumer, verifier);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return consumer;
	}
}
