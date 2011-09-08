package com.suchagit.android2cloud;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.suchagit.android2cloud.errors.IncorrectTimeDialogFragment;
import com.suchagit.android2cloud.errors.OAuthActivityNullUriDialogFragment;
import com.suchagit.android2cloud.errors.OAuthCommunicationExceptionDialogFragment;
import com.suchagit.android2cloud.errors.OAuthExpectationFailedExceptionDialogFragment;
import com.suchagit.android2cloud.errors.OAuthMessageSignerExceptionDialogFragment;
import com.suchagit.android2cloud.errors.OAuthNotAuthorizedExceptionDialogFragment;
import com.suchagit.android2cloud.errors.OAuthWebviewNullIntentDialogFragment;
import com.suchagit.android2cloud.util.CheckTimeResponse;
import com.suchagit.android2cloud.util.HttpClient;
import com.suchagit.android2cloud.util.OAuth;
import com.suchagit.android2cloud.util.OAuthAccount;

public class OAuthActivity extends FragmentActivity implements CheckTimeResponse.Receiver {
	private EditText host_input;
	private EditText account_input;
	public CheckTimeResponse mReceiver;
	
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
					getServerTime();
					mReceiver.setPassThrough(error_data);
	        	    //getServerTime() is asynchronous, so we pass the error information to it so it can display the error if the time is right
					//DialogFragment errorFragment = OAuthCommunicationExceptionDialogFragment.newInstance(error_data);
	        	    //errorFragment.show(getSupportFragmentManager(), "dialog");
				}
	    	}
	    });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mReceiver = new CheckTimeResponse(new Handler());
		mReceiver.setReceiver(this);
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
    
    public void getServerTime() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.suchagit.android2cloud", "com.suchagit.android2cloud.HttpService"));
		intent.setAction("com.suchagit.android2cloud.CheckTime");
		intent.putExtra("com.suchagit.android2cloud.result_receiver", mReceiver);
		intent.putExtra("com.suchagit.android2cloud.host", host_input.getText().toString());
		startService(intent);
    }
    
    public Bundle getDeviceTime() {
    	Bundle deviceTime = new Bundle();
    	deviceTime.putLong("currentTime", System.currentTimeMillis());
    	deviceTime.putString("timezone", Time.getCurrentTimezone());
    	deviceTime.putString("friendlyTime", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis())));
    	return deviceTime;
    }
    
    public boolean correctTime(Long serverTime) {
    	Bundle device = getDeviceTime();
    	long diff = device.getLong("currentTime") - serverTime;
    	long acceptableDiff = 1800000; // thirty minutes in milliseconds
    	Log.d("OAuthActivity", device.getLong("currentTime") + "");
    	return (diff < acceptableDiff && diff > 0);
    }

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		if(resultCode == HttpClient.STATUS_COMPLETE && resultData.getInt("response_code") == 200 && !correctTime(resultData.getLong("currentTime"))) {
			DialogFragment errorFragment = IncorrectTimeDialogFragment.newInstance(getDeviceTime());
    	    errorFragment.show(getSupportFragmentManager(), "dialog");
		} else {
			// display the OAuthCommunicationError we swallowed
			Bundle error_data = mReceiver.getPassThrough();
			DialogFragment errorFragment = OAuthCommunicationExceptionDialogFragment.newInstance(error_data);
    	    errorFragment.show(getSupportFragmentManager(), "dialog");
		}
	}
}