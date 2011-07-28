package com.suchagit.android2cloud;

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
	    		Intent intent = new Intent(OAuthActivity.this, OAuthWebView.class);
	    		intent.setData(Uri.parse(requestUrl));
	    		Log.d("OAuthActivity", requestUrl);
	    		startActivityForResult(intent, OAuth.INTENT_ID);
	    	}
	    });
	}
	

    @Override
	protected void onActivityResult(int req_code, int res_code, Intent intent) {
    	super.onActivityResult(req_code, res_code, intent);
        Uri uri = intent.getData();
        if(uri != null) {
        	SharedPreferences accounts = getSharedPreferences("android2cloud-accounts", 0);
        	String verifier = uri.getQueryParameter("oauth_token");
        	String account = uri.getQueryParameter("account");
        	String host = uri.getQueryParameter("protocol") + "://" + uri.getQueryParameter("domain") + "/";
        	OAuthConsumer consumer = OAuth.getAccessToken(host, verifier);
        	OAuthAccount oauth_account = new OAuthAccount(account, host, consumer.getToken(), consumer.getTokenSecret());
        	oauth_account.save(accounts);
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	SharedPreferences.Editor editor = settings.edit();
        	editor.putString("account", account);
        	editor.commit();
        	Intent return_intent = new Intent(this, PostLinkActivity.class);
        	this.startActivityForResult(return_intent, OAuth.INTENT_ID);
        }
	}
}