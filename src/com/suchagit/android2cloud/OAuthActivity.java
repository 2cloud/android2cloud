package com.suchagit.android2cloud;

import oauth.signpost.OAuthConsumer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.suchagit.android2cloud.util.OAuth;


public class OAuthActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
    	setContentView(R.layout.main);
    	
        Uri uri = this.getIntent().getData();
        if(uri != null) {
        	String verifier = uri.getQueryParameter("oauth_token");
        	OAuthConsumer consumer = OAuth.getAccessToken("https://2cloudapp.appspot.com/", verifier);
        	Toast.makeText(this, "Token: " + consumer.getToken(), Toast.LENGTH_LONG).show();
        	Toast.makeText(this, "Secret: " + consumer.getTokenSecret(), Toast.LENGTH_LONG).show();
        }
    	final Button go = (Button) findViewById(R.id.go);
    	
    	go.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		String requestUrl = OAuth.getRequestUrl("https://2cloudapp.appspot.com/");
	    		Intent intent = new Intent(Intent.ACTION_VIEW);
	    		intent.setData(Uri.parse(requestUrl));
	    		startActivity(intent);
	    	}
	    });
	}
}