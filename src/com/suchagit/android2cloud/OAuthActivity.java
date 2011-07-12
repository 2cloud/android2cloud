package com.suchagit.android2cloud;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.suchagit.android2cloud.util.OAuth;


public class OAuthActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
    	setContentView(R.layout.main);
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