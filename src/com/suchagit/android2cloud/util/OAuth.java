package com.suchagit.android2cloud.util;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.net.Uri;

public class OAuth {
	
	private static OAuthProvider provider;
	private static OAuthConsumer consumer;
	
	private static final String REQUEST_TOKEN_URL = "_ah/OAuthGetRequestToken";
	private static final String ACCESS_TOKEN_URL = "_ah/OAuthGetAccessToken";
	private static final String AUTHORISE_TOKEN_URL = "_ah/OAuthAuthorizeToken?btmpl=mobile";
	
	public static final String CALLBACK_DOMAIN = "2cloud.app";
	public static final int INTENT_ID = 0x1234;
	
	
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
        	Uri host_uri = Uri.parse(host);
			target = provider.retrieveRequestToken(consumer, "http://" + CALLBACK_DOMAIN+"/?account="+account+"&protocol="+host_uri.getScheme()+"&domain="+host_uri.getHost());
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
