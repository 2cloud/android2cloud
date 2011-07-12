package com.suchagit.android2cloud.util;

import java.io.IOException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.SharedPreferences;
import android.util.Log;

public class HttpClient extends DefaultHttpClient {
	
	protected static final String CONSUMER_KEY = "anonymous";
	protected static final String CONSUMER_SECRET = "anonymous";
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
	
	public HttpClient(SharedPreferences preferences) {
		super();
		String oauth_token = preferences.getString("oauth_token", "error");
		String oauth_secret = preferences.getString("oauth_secret", "error");
		if(!oauth_token.equals("error")) {
			this.setOAuthToken(oauth_token);
		} else {
			Log.e("HttpClient", "OAuthToken not set");
		}
		if(!oauth_secret.equals("error")) {
			this.setOAuthSecret(oauth_secret);
		} else {
			Log.e("HttpClient", "OAuthSecret not set");
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
	
	@SuppressWarnings("unchecked")
	public String exec(HttpRequestBase request, ResponseHandler responseHandler) {
		this.sign(request);
		String returnString = "";
		try {
			String response = super.execute(request, responseHandler);
			returnString = response;
		} catch(ClientProtocolException e) {
			//TODO: Handle error
		} catch(IOException e) {
			//TODO: Handle error
		}
		return returnString;
	}
}
