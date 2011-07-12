package com.suchagit.android2cloud;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	
	@Override
	public void onResume() {
		super.onResume();
		mReceiver = new AddLinkResponse(new Handler());
		mReceiver.setReceiver(this);
	}
	
	public void onPause() {
		super.onPause();
		mReceiver.setReceiver(null);
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
		case HttpClient.STATUS_COMPLETE:
			int code = resultData.getInt("response_code");
			String resp = "";
			Log.d("TestActivity", code+"");
			if(code == 200) {
				resp = "Successfully sent " + resultData.getString("link") + " to the cloud.";
			} else if(code == 500) {
				if(resultData.getString("type") == "client_error") {
					resp = "There was an error understanding the result from the server. Please try again.";
				}
			} else if(code == 401) {
				resp = "You need to log in before your link will be stored.";
			} else if(code == 503) {
				resp = "The server is over quota. Your link ";
				resp += resultData.getString("link");
				resp += " was stored and will be sent to Chrome tomorrow.";
			}
			Toast.makeText(this, resp, Toast.LENGTH_LONG).show();
			break;
		case HttpClient.STATUS_ERROR:
			Toast.makeText(this, "Oops! Error processing your request.", Toast.LENGTH_LONG).show();
			break;
		}
	}
}