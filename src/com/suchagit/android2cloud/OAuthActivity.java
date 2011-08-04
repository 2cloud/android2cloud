package com.suchagit.android2cloud;

import java.io.PrintWriter;
import java.io.StringWriter;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	    		String requestUrl = "";
				try {
					requestUrl = OAuth.getRequestUrl(host_input.getText().toString(), account_input.getText().toString());
		    		Intent intent = new Intent(OAuthActivity.this, OAuthWebView.class);
		    		intent.setData(Uri.parse(requestUrl));
		    		startActivityForResult(intent, OAuth.INTENT_ID);
				} catch (OAuthMessageSignerException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
					showDialog(R.string.oauth_message_signer_exception_error, error_data);
				} catch (OAuthNotAuthorizedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
					showDialog(R.string.oauth_not_authorized_exception_error, error_data);
				} catch (OAuthExpectationFailedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
					showDialog(R.string.oauth_expectation_failed_exception_error, error_data);
				} catch (OAuthCommunicationException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
					showDialog(R.string.oauth_communication_exception_error, error_data);
				}
	    	}
	    });
	}
	

    @Override
	protected void onActivityResult(int req_code, int res_code, Intent intent) {
    	super.onActivityResult(req_code, res_code, intent);
    	if(intent != null) {
	        Uri uri = intent.getData();
	        if(uri != null) {
	        	SharedPreferences accounts = getSharedPreferences("android2cloud-accounts", 0);
	        	String verifier = uri.getQueryParameter("oauth_token");
	        	String account = uri.getQueryParameter("account");
	        	String host = uri.getScheme() + "://" + uri.getHost() + "/";
				try {
					OAuthConsumer consumer = OAuth.getAccessToken(host, verifier);
		        	OAuthAccount oauth_account = new OAuthAccount(account, host, consumer.getToken(), consumer.getTokenSecret());
		        	oauth_account.save(accounts);
		        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		        	SharedPreferences.Editor editor = settings.edit();
		        	editor.putString("account", account);
		        	editor.commit();
		        	Intent return_intent = new Intent(this, PostLinkActivity.class);
		        	this.startActivityForResult(return_intent, OAuth.INTENT_ID);
				} catch (OAuthMessageSignerException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host);
					error_data.putString("account", account);
					error_data.putString("verifier", verifier);
					showDialog(R.string.oauth_message_signer_exception_error, error_data);
				} catch (OAuthNotAuthorizedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host);
					error_data.putString("account", account);
					error_data.putString("verifier", verifier);
					showDialog(R.string.oauth_not_authorized_exception_error, error_data);
				} catch (OAuthExpectationFailedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host);
					error_data.putString("account", account);
					error_data.putString("verifier", verifier);
					showDialog(R.string.oauth_expectation_failed_exception_error, error_data);
				} catch (OAuthCommunicationException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host);
					error_data.putString("account", account);
					error_data.putString("verifier", verifier);
					showDialog(R.string.oauth_communication_exception_error, error_data);
				}
	        } else {
	        	showDialog(R.string.oauthactivity_null_uri_error);
	        }
    	} else {
    		showDialog(R.string.oauthwebview_null_intent_error);
    	}
	}
	
	@Override
	public Dialog onCreateDialog(int id, Bundle data) {
		super.onCreateDialog(id, data);
		ErrorDialogBuilder error = new ErrorDialogBuilder(OAuthActivity.this, data);
		error.build(id);
		AlertDialog alert = error.create();
		return alert;
	}
}