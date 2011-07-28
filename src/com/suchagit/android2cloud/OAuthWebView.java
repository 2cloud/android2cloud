package com.suchagit.android2cloud;

import com.suchagit.android2cloud.util.OAuth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OAuthWebView extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		String request_url = "";
		if(this.getIntent() != null && this.getIntent().getDataString() != null){
			request_url = this.getIntent().getDataString();
		}
		WebView browser= new WebView(this);
		setContentView(browser);

		browser.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		browser.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setProgress(progress * 1000);
			}
		});
		browser.setWebViewClient(new WebViewClient(){
			
			@Override
			public void onPageFinished(WebView view, String url){
				super.onPageFinished(view, url);
				Uri uri = Uri.parse(url);
				if(OAuth.CALLBACK_DOMAIN.equals(uri.getHost())){
					Intent intent = new Intent(OAuthWebView.this, OAuthActivity.class);
					intent.setData(uri);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		browser.loadUrl(request_url);
	}
}
