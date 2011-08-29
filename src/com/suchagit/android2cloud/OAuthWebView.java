package com.suchagit.android2cloud;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.secondbit.debug2cloud.R;
import com.suchagit.android2cloud.errors.OAuthWebviewNullIntentDialogFragment;
import com.suchagit.android2cloud.util.OAuth;

public class OAuthWebView extends FragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		String request_url = "";
		if(this.getIntent() != null && this.getIntent().getDataString() != null){
			request_url = this.getIntent().getDataString();
		} else {
			showDialog(R.string.oauthwebview_null_intent_error);
    	    DialogFragment errorFragment = OAuthWebviewNullIntentDialogFragment.newInstance();
    	    errorFragment.show(getSupportFragmentManager(), "dialog");
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
				if(("/" + OAuth.CALLBACK).equals(uri.getPath())){
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
