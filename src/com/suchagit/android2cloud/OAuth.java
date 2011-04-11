package com.suchagit.android2cloud;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
//import android.widget.Toast;
import android.widget.Toast;

public class OAuth extends Activity {
	private static OAuthConsumer consumer;
	private static OAuthProvider provider;
	private static String host;
	private static String account;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		host = "";
		account = "";
		if(this.getIntent() != null && this.getIntent().getExtras() != null && this.getIntent().getExtras().getString("host") != null){
			host = this.getIntent().getExtras().getString("host");
			Log.i("android2cloud", "OAuth(37) host: "+host);
		}
		if(this.getIntent() != null && this.getIntent().getExtras() != null && this.getIntent().getExtras().getString("account") != null){
			account = this.getIntent().getExtras().getString("account");
			Log.i("android2cloud", "OAuth(41) account: "+account);
		}
		//Toast.makeText(this, host, Toast.LENGTH_LONG).show();
		String oauth_request_url = getRequestURL(host, getResources());
		Log.i("android2cloud", "OAuth(45) oauth_request_url: "+oauth_request_url);
		//Toast.makeText(this, oauth_request_url, Toast.LENGTH_LONG).show();
		WebView browser= new WebView(this);
		setContentView(browser);

		browser.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		browser.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
			// Activities and WebViews measure progress with different scales.
			// The progress meter will automatically disappear when we reach 100%
			activity.setProgress(progress * 1000);
			}
		});
		browser.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url){
				super.onPageFinished(view, url);
				String callback = host+getResources().getString(R.string.callback_url);
				Log.i("android2cloud", "OAuth(65) callback: "+callback);
				Log.i("android2cloud", "OAuth(66) url: "+url);
				if(url.length() >= callback.length() && url.substring(0, callback.length()).equals(callback)){
					Intent intent = new Intent(OAuth.this, AccountAdd.class);
					intent.putExtra("host", host);
					String verifier = "";
					String[] params = url.split("\\?|&");
					for(String param:params){
						Log.i("android2cloud", "OAuth(73) param: "+param);
						if(param.substring(0, 14).equals("oauth_verifier")){
							verifier = param.substring(15);
							Log.d("android2cloud", "verifier: "+verifier);
						}
					}
					try {
						provider.retrieveAccessToken(consumer, verifier);
					} catch (OAuthMessageSignerException e) {
						Toast.makeText(OAuth.this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthMessageSignerException' to the project page.", Toast.LENGTH_LONG).show();
						Log.i("android2cloud", "OAuth(84) e.getMessage: "+e.getMessage());
					} catch (OAuthNotAuthorizedException e) {
						Toast.makeText(OAuth.this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthNotAuthorizedException' to the project page.", Toast.LENGTH_LONG).show();
						Log.i("android2cloud", "OAuth(86) e.getMessage: "+e.getMessage());
					} catch (OAuthExpectationFailedException e) {
						Toast.makeText(OAuth.this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthExpectationFailedException' to the project page.", Toast.LENGTH_LONG).show();
						Log.i("android2cloud", "OAuth(89) e.getMessage: "+e.getMessage());
					} catch (OAuthCommunicationException e) {
						Toast.makeText(OAuth.this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthCommunicationException' to the project page.", Toast.LENGTH_LONG).show();
						Log.i("android2cloud", "OAuth(92) e.getMessage: "+e.getMessage());
					}
					intent.putExtra("token", consumer.getToken());
					Log.i("android2cloud", "OAuth(95) token: "+consumer.getToken());
					intent.putExtra("secret", consumer.getTokenSecret());
					Log.i("android2cloud", "OAuth(97) secret: "+consumer.getTokenSecret());
					intent.putExtra("account", account);
					Log.i("android2cloud", "OAuth(99) account: "+account);
					setResult(1, intent);
					finish();
				}
			}
		});
		browser.loadUrl(oauth_request_url);
	}
	
	public static String getRequestURL(String host, Resources r){
        // create a new service provider object and configure it with
        // the URLs which provide request tokens, access tokens, and
        // the URL to which users are sent in order to grant permission
        // to your application to access protected resources
		String consumer_key = r.getString(R.string.consumer_key);
		String consumer_secret = r.getString(R.string.consumer_secret);
		if(!host.equals("http://android2cloud.appspot.com")){
			consumer_key = "anonymous";
			consumer_secret = "anonymous";
		}
		Log.i("android2cloud", "OAuth(119) consumer_key: "+consumer_key);
		Log.i("android2cloud", "OAuth(120) consumer_secret: "+consumer_secret);
        consumer = new CommonsHttpOAuthConsumer(consumer_key,
                consumer_secret);
        provider = new CommonsHttpOAuthProvider(host+r.getString(R.string.request_url), host+r.getString(R.string.access_url), host+r.getString(R.string.authorize_url));

        // fetches a request token from the service provider and builds
        // a url based on AUTHORIZE_WEBSITE_URL and CALLBACK_URL to
        // which your app must now send the user
        String target = null;
        try {
			target = provider.retrieveRequestToken(consumer, host+r.getString(R.string.callback_url));
			Log.i("android2cloud", "OAuth(131) target: "+target);
		} catch (OAuthMessageSignerException e) {
			target = "OAuthMessageSignerException";
			Log.i("android2cloud", "OAuth(134) e.getMessage(): "+e.getMessage());
		} catch (OAuthNotAuthorizedException e) {
			target = "OAuthNotAuthorizedException";
			Log.i("android2cloud", "OAuth(137) e.getMessage(): "+e.getMessage());
		} catch (OAuthExpectationFailedException e) {
			target = "OAuthExpectationFailedException";
			Log.i("android2cloud", "OAuth(140) e.getMessage(): "+e.getMessage());
		} catch (OAuthCommunicationException e) {
			target = r.getString(R.string.request_url);
			Log.i("android2cloud", "OAuth(143) e.getMessage(): "+e.getMessage());
			Log.i("android2cloud", "OAuth(144) provider.request_url: "+host+r.getString(R.string.request_url));
		}
		Log.i("android2cloud", "OAuth(146) target: "+target);
		return target;
	}
}