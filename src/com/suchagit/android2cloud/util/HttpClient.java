package com.suchagit.android2cloud.util;

import java.io.IOException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpClient extends DefaultHttpClient {
	
	public static final int STATUS_COMPLETE = 1;
	public static final int STATUS_RUNNING = 0;
	public static final int STATUS_ERROR = -1;
	
	protected static final String CONSUMER_KEY = "INSERT CONSUMER KEY";
	protected static final String CONSUMER_SECRET = "INSERT CONSUMER SECRET";
	
	private String oauth_token;
	private String oauth_secret;
	
	private OAuthConsumer consumer;

	public void setOAuthToken(String newToken) {
		this.oauth_token = newToken;
	}
	
	public String getOAuthToken() {
		return this.oauth_token;
	}
	
	public void setOAuthSecret(String newSecret) {
		this.oauth_secret = newSecret;
	}
	
	public String getOAuthSecret() {
		return this.oauth_secret;
	}
	
	public void setConsumer(OAuthConsumer newConsumer) {
		this.consumer = newConsumer;
	}
	
	public OAuthConsumer getConsumer() {
		return this.consumer;
	}
	
	public HttpClient(String oauth_token, String oauth_secret) {
		super();
		if(!"error".equals(oauth_token)) {
			this.setOAuthToken(oauth_token);
		} else {
			//TODO: find some way of erroring
		}
		if(!"error".equals(oauth_secret)) {
			this.setOAuthSecret(oauth_secret);
		} else {
			//TODO: find some way of erroring
		}
		this.setConsumer(initConsumer());
	}
	
	public OAuthConsumer initConsumer() {
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		consumer.setTokenWithSecret(this.getOAuthToken(), this.getOAuthSecret());
		return consumer;
	}
	
	public void sign(HttpRequestBase request) {
		OAuthConsumer consumer = this.getConsumer();
		try {
			consumer.sign(request);
		} catch(OAuthMessageSignerException e) {
			//TODO: Handle error
		} catch(OAuthExpectationFailedException e) {
			//TODO: Handle error
		} catch(OAuthCommunicationException e) {
			//TODO: Handle error
		}
	}
	
	public String exec(HttpRequestBase request) throws IllegalStateException {
		this.sign(request);
		String returnString = "";
		try {
			String response = super.execute(request, new BasicResponseHandler());
			returnString = response;
		} catch(ClientProtocolException e) {
			//TODO: Handle error
		} catch(IOException e) {
			//TODO: Handle error
		}
		return returnString;
	}
}
