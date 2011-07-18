package com.suchagit.android2cloud;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import oauth.signpost.OAuthConsumer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.suchagit.android2cloud.util.OAuth;
import com.suchagit.android2cloud.util.OAuthAccount;



public class OAuthActivity extends Activity {
	private EditText host_input;
	private EditText account_input;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
    	setContentView(R.layout.add_account);
    	host_input = (EditText) findViewById(R.id.host_entry);
    	account_input = (EditText) findViewById(R.id.account_entry);
    	
    	final Button add_button = (Button) findViewById(R.id.add_account);
    	
    	add_button.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		String requestUrl = OAuth.getRequestUrl(host_input.getText().toString(), account_input.getText().toString());
	    		Intent intent = new Intent(Intent.ACTION_VIEW);
	    		intent.setData(Uri.parse(requestUrl));
	    		Log.d("OAuthActivity", Uri.parse(requestUrl).toString());
	    		startActivity(intent);
	    	}
	    });
	}
	
	public void onResume() {
		super.onResume();

        Uri uri = this.getIntent().getData();
        if(uri != null) {
        	SharedPreferences accounts = getSharedPreferences("android2cloud-accounts", 0);
        	String verifier = uri.getQueryParameter("oauth_token");
        	String account = uri.getQueryParameter("account");
        	String host = uri.getHost();
			try {
				host = URLDecoder.decode(host, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				Toast.makeText(this, "UnsupportedEncodingException: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
        	OAuthConsumer consumer = OAuth.getAccessToken(host, verifier);
        	OAuthAccount oauth_account = new OAuthAccount(account, host, consumer.getToken(), consumer.getTokenSecret());
        	oauth_account.save(accounts);
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	SharedPreferences.Editor editor = settings.edit();
        	editor.putString("account", account);
        	editor.commit();
        	Intent intent = new Intent(this, PostLinkActivity.class);
        	this.startActivity(intent);
        }
	}
}