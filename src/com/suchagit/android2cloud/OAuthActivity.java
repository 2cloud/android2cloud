package com.suchagit.android2cloud;

import java.io.PrintWriter;
import java.io.StringWriter;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.suchagit.android2cloud.errors.OAuthActivityNullUriDialogFragment;
import com.suchagit.android2cloud.errors.OAuthCommunicationExceptionDialogFragment;
import com.suchagit.android2cloud.errors.OAuthExpectationFailedExceptionDialogFragment;
import com.suchagit.android2cloud.errors.OAuthMessageSignerExceptionDialogFragment;
import com.suchagit.android2cloud.errors.OAuthNotAuthorizedExceptionDialogFragment;
import com.suchagit.android2cloud.errors.OAuthWebviewNullIntentDialogFragment;
import com.suchagit.android2cloud.util.OAuth;
import com.suchagit.android2cloud.util.OAuthAccount;

public class OAuthActivity extends FragmentActivity {
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
	        	    DialogFragment errorFragment = OAuthMessageSignerExceptionDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
				} catch (OAuthNotAuthorizedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
	        	    DialogFragment errorFragment = OAuthNotAuthorizedExceptionDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
				} catch (OAuthExpectationFailedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
	        	    DialogFragment errorFragment = OAuthExpectationFailedExceptionDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
				} catch (OAuthCommunicationException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
					error_data.putString("response_body", e.getResponseBody());
	        	    DialogFragment errorFragment = OAuthCommunicationExceptionDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
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
		        	finish();
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
	        	    DialogFragment errorFragment = OAuthMessageSignerExceptionDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
				} catch (OAuthNotAuthorizedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host);
					error_data.putString("account", account);
					error_data.putString("verifier", verifier);
	        	    DialogFragment errorFragment = OAuthNotAuthorizedExceptionDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
				} catch (OAuthExpectationFailedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host);
					error_data.putString("account", account);
					error_data.putString("verifier", verifier);
	        	    DialogFragment errorFragment = OAuthExpectationFailedExceptionDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
				} catch (OAuthCommunicationException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host);
					error_data.putString("account", account);
					error_data.putString("verifier", verifier);
					error_data.putString("response_body", e.getResponseBody());
	        	    DialogFragment errorFragment = OAuthCommunicationExceptionDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
				}
	        } else {
        	    DialogFragment errorFragment = OAuthActivityNullUriDialogFragment.newInstance();
        	    errorFragment.show(getSupportFragmentManager(), "dialog");
	        }
    	} else {
    	    DialogFragment errorFragment = OAuthWebviewNullIntentDialogFragment.newInstance();
    	    errorFragment.show(getSupportFragmentManager(), "dialog");
    	}
	}
}