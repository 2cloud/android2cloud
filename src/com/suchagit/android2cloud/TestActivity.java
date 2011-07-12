package com.suchagit.android2cloud;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.suchagit.android2cloud.util.AddLinkResponse;
import com.suchagit.android2cloud.util.HttpClient;

public class TestActivity extends Activity implements AddLinkResponse.Receiver {
	
	public AddLinkResponse mReceiver;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
    	setContentView(R.layout.main);
    	mReceiver = new AddLinkResponse(new Handler());
    	mReceiver.setReceiver(this);
    	
    	final Button go = (Button) findViewById(R.id.go);
    	
    	go.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent = new Intent();
	    		intent.setComponent(new ComponentName("com.suchagit.android2cloud", "com.suchagit.android2cloud.HttpService"));
	    		intent.setAction("AddLink");
	    		intent.putExtra("result_receiver", mReceiver);
				intent.putExtra("host", "https://2cloudapp.appspot.com/");
				intent.putExtra("oauth_token", "error");
				intent.putExtra("oauth_secret", "error");
				intent.putExtra("link", "http://www.wired.com");
				intent.putExtra("receiver", "Web");
				intent.putExtra("sender", "Android");
				startService(intent);
	    	}
	    });
	}
	
	public void onPause() {
		mReceiver.setReceiver(null);
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
		case HttpClient.STATUS_OK:
			Toast.makeText(this, resultData.getString("raw_result"), Toast.LENGTH_LONG).show();
			break;
		}
	}
}