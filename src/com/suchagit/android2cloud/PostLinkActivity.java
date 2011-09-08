package com.suchagit.android2cloud;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.suchagit.android2cloud.errors.DefaultErrorDialogFragment;
import com.suchagit.android2cloud.errors.DeprecatedHostExceptionDialogFragment;
import com.suchagit.android2cloud.errors.HttpClientErrorDialogFragment;
import com.suchagit.android2cloud.errors.IllegalArgumentExceptionDialogFragment;
import com.suchagit.android2cloud.errors.IllegalStateExceptionDialogFragment;
import com.suchagit.android2cloud.errors.IntentWithoutLinkDialogFragment;
import com.suchagit.android2cloud.errors.NoAccountSelectedDialogFragment;
import com.suchagit.android2cloud.errors.NoAccountsDialogFragment;
import com.suchagit.android2cloud.errors.OverQuotaDialogFragment;
import com.suchagit.android2cloud.errors.PostLinkAuthErrorDialogFragment;
import com.suchagit.android2cloud.errors.PostLinkNullLinkDialogFragment;
import com.suchagit.android2cloud.errors.PostLinkNullReceiverDialogFragment;
import com.suchagit.android2cloud.errors.SelectLinkDialogFragment;
import com.suchagit.android2cloud.errors.UnsupportedEncodingExceptionDialogFragment;
import com.suchagit.android2cloud.util.AddLinkResponse;
import com.suchagit.android2cloud.util.HttpClient;
import com.suchagit.android2cloud.util.OAuthAccount;

public class PostLinkActivity extends FragmentActivity implements AddLinkResponse.Receiver {
	
	public static final int BILLING_INTENT_CODE = 0x1079;

	public AddLinkResponse mReceiver;
    
	private OAuthAccount account;
	private SharedPreferences settings;
	private SharedPreferences accounts_preferences;
	
	private EditText device_entry;
	private EditText url_input;
	private TextView account_display;
	private ProgressBar throbber;
	private Button send_button;
	
	private String link = "";
	private String receiver = "";
	private boolean popup = false;
	private boolean contentSet = false;
	
	public final static int EDIT_SETTINGS_REQ_CODE = 0x1234;

	private static final int NO_ACCOUNT = 0;
	private static final int NO_ACCOUNT_SELECTED = 2;
	private static final int ACCOUNT = 1;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
    	
    	settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		receiver = settings.getString("receiver", "Chrome");
    	
		if(!settings.getBoolean("silent", false)) {
			render();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();

    	settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		//check to make sure they have an account
		popup = false;
		int accountStatus = checkAccount();
		switch(accountStatus) {
		case NO_ACCOUNT:
			popup = true;
			render();
    	    DialogFragment noAccountFragment = NoAccountsDialogFragment.newInstance();
    	    noAccountFragment.show(getSupportFragmentManager(), "dialog");
    	    break;
		case NO_ACCOUNT_SELECTED:
			popup = true;
			render();
    	    DialogFragment selectAccountFragment = NoAccountSelectedDialogFragment.newInstance();
    	    selectAccountFragment.show(getSupportFragmentManager(), "dialog");
    	    break;
		}
		
		try {
			checkHost(account.getHost());
		} catch(DeprecatedHostException e) {
			popup = true;
			render();
			account.delete(accounts_preferences);
			DialogFragment deprecatedHostFragment = DeprecatedHostExceptionDialogFragment.newInstance();
			deprecatedHostFragment.show(getSupportFragmentManager(), "dialog");
		}
		
		// pull the URL from the intent
		if(Intent.ACTION_SEND.equals(getIntent().getAction())) {
			String intentText = getStringFromIntent(getIntent());
			try{
				link = getLinkFromString(intentText);
			} catch(NoLinkFoundException e){
				popup = true;
				render();
	    	    DialogFragment errorFragment = IntentWithoutLinkDialogFragment.newInstance();
	    	    errorFragment.show(getSupportFragmentManager(), "dialog");
			} catch(TooManyLinksException e) {
	        	popup = true;
	        	render();
		    	Bundle data = new Bundle();
		    	data.putCharSequenceArray("choices", e.getLinks());
	    	    DialogFragment errorFragment = SelectLinkDialogFragment.newInstance(data);
	    	    errorFragment.show(getSupportFragmentManager(), "dialog");
			}
		}
    	
		mReceiver = new AddLinkResponse(new Handler());
		mReceiver.setReceiver(this);
		if(settings.getBoolean("silent", false) && popup == false && link != null && link.trim() != "") {
			sendLink();
			finish();
		} else {
			render();
	    	url_input.setText(link);
	    	device_entry.setText(receiver);
	    	account_display.setText("Account: "+account.getAccount());
		}
	}
	
	public void onPause() {
		super.onPause();
		mReceiver.setReceiver(null);
		contentSet = false;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_link, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.edit_settings:
			Intent i = new Intent(PostLinkActivity.this, Preferences.class);
			startActivityForResult(i, EDIT_SETTINGS_REQ_CODE);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	public void onReceiveResult(int resultCode, Bundle resultData) {
		throbber.setVisibility(View.GONE);
		send_button.setVisibility(View.VISIBLE);
		switch (resultCode) {
		case HttpClient.STATUS_COMPLETE:
			int code = resultData.getInt("response_code");
			String resp = "";
			if(code == 200) {
				resp = "Successfully sent " + resultData.getString("link") + " to the cloud.";
				Toast.makeText(this, resp, Toast.LENGTH_LONG).show();
			} else if(code == 500) {
				if(resultData.getString("type") == "client_error") {
    				Bundle error_data = new Bundle();
    				error_data.putString("account", account.getAccount());
    				error_data.putString("host", account.getHost());
    				error_data.putString("token", account.getToken());
    				error_data.putString("secret", account.getKey());
    				error_data.putString("raw_data", resultData.getString("raw_result"));
	        	    DialogFragment errorFragment = HttpClientErrorDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
				}
			} else if(code == 401) {
				Bundle error_data = new Bundle();
				error_data.putString("account", account.getAccount());
				error_data.putString("host", account.getHost());
				error_data.putString("token", account.getToken());
				error_data.putString("secret", account.getKey());
        	    DialogFragment errorFragment = PostLinkAuthErrorDialogFragment.newInstance(error_data);
        	    errorFragment.show(getSupportFragmentManager(), "dialog");
			} else if(code == 503) {
        	    DialogFragment errorFragment = OverQuotaDialogFragment.newInstance();
        	    errorFragment.show(getSupportFragmentManager(), "dialog");
			}
			break;
		case HttpClient.STATUS_ERROR:
			int error_code = resultData.getInt("response_code");
			if(error_code == 600) {
				if(resultData.getString("type").equals("unsupported_encoding_exception_error")) {
					Bundle error_data = new Bundle();
					error_data.putString("account", account.getAccount());
					error_data.putString("host", account.getHost());
					error_data.putString("device_name", "Android");
					error_data.putString("receiver", receiver);
					error_data.putString("link", link);
	        	    DialogFragment errorFragment = UnsupportedEncodingExceptionDialogFragment.newInstance(error_data);
	        	    errorFragment.show(getSupportFragmentManager(), "dialog");
				} else if(resultData.getString("type").equals("illegal_state_exception_error")) {
					Bundle error_data = new Bundle();
					error_data.putString("host", account.getHost());
					error_data.putString("request_type", resultData.getString("request_type"));
					error_data.putString("request_host", resultData.getString("host"));
					error_data.putString("stacktrace", resultData.getString("stacktrace"));
					DialogFragment illegalStateFragment = IllegalStateExceptionDialogFragment.newInstance(error_data);
					illegalStateFragment.show(getSupportFragmentManager(), "dialog");
				} else if(resultData.getString("type").equals("illegal_argument_exception_error")) {
					Bundle error_data = new Bundle();
					error_data.putString("host", account.getHost());
					error_data.putString("request_type", resultData.getString("request_type"));
					error_data.putString("request_host", resultData.getString("host"));
					error_data.putString("stacktrace", resultData.getString("stacktrace"));
					DialogFragment illegalArgFragment = IllegalArgumentExceptionDialogFragment.newInstance(error_data);
					illegalArgFragment.show(getSupportFragmentManager(), "dialog");
				}
			} else {
        	    DialogFragment errorFragment = DefaultErrorDialogFragment.newInstance();
        	    errorFragment.show(getSupportFragmentManager(), "dialog");
			}
			break;
		}
	}
	
	private void render() {
		if(!contentSet) {
			setContentView(R.layout.main);
			contentSet = true;
			
	    	send_button = (Button) findViewById(R.id.send);
	    	device_entry = (EditText) findViewById(R.id.device_entry);
	    	url_input = (EditText) findViewById(R.id.link_entry);
	    	account_display = (TextView) findViewById(R.id.account_label);
	    	throbber = (ProgressBar) findViewById(R.id.sendLinkThrobber);
	    	
	    	
	    	send_button.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		link = url_input.getText().toString();
	    			receiver = device_entry.getText().toString();
	        		if(link == null || link.trim().equals("")) {
	        			popup = true;
		        	    DialogFragment errorFragment = PostLinkNullLinkDialogFragment.newInstance();
		        	    errorFragment.show(getSupportFragmentManager(), "dialog");
	        		} else if(receiver == null || receiver.trim().equals("")) {
	        			popup = true;
		        	    DialogFragment errorFragment = PostLinkNullReceiverDialogFragment.newInstance();
		        	    errorFragment.show(getSupportFragmentManager(), "dialog");
	        		} else {
	        			send_button.setVisibility(View.GONE);
	        			throbber.setVisibility(View.VISIBLE);
	        			sendLink();
	        		}
    			}
	    	});
		}
	}
	
	private int checkAccount() {
		settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		accounts_preferences = getSharedPreferences("android2cloud-accounts", 0);
		
		String account_name = settings.getString("account", "");
		account = new OAuthAccount(account_name, accounts_preferences);
		String[] accounts = OAuthAccount.getAccounts(accounts_preferences);
		if(account_name.equals("") || accounts.length == 0) {
			if(accounts.length == 1 && !account_name.equals("")){
				SharedPreferences.Editor settings_editor = settings.edit();
				settings_editor.putString("account", accounts[0]);
				settings_editor.commit();
				return ACCOUNT;
			} else if(accounts.length == 1 && accounts[0].equals("")){
				return NO_ACCOUNT;
			} else {
				return NO_ACCOUNT_SELECTED;
			}
		}
		return ACCOUNT;
	}
	
	private String getStringFromIntent(Intent intent) {
		return intent.getExtras().getString(Intent.EXTRA_TEXT);
	}
	
	private String getLinkFromString(String string) throws NoLinkFoundException, TooManyLinksException {
		Log.d("PostLinkActivity", "getLinkFromString: "+string);
    	String regex = "\\b(\\w+)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern patt = Pattern.compile(regex);
        Matcher matcher = patt.matcher(string);
        ArrayList<String> matches = new ArrayList<String>();
        while(matcher.find()){
        	matches.add(matcher.group());
			Log.d("PostLinkActivity", "Match: "+matcher.group());
        }
        final CharSequence[] matches_cs = matches.toArray(new CharSequence[matches.size()]);
        if(matches.size() > 1) {
        	throw new TooManyLinksException(matches_cs);
        }else if(matches.size() == 1){
        	return (String) matches_cs[0];
        } else {
        	throw new NoLinkFoundException();
        }
	}
	private void sendLink() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.suchagit.android2cloud", "com.suchagit.android2cloud.HttpService"));
		intent.setAction("com.suchagit.android2cloud.AddLink");
		intent.putExtra("com.suchagit.android2cloud.result_receiver", mReceiver);
		intent.putExtra("com.suchagit.android2cloud.host", account.getHost());
		intent.putExtra("com.suchagit.android2cloud.oauth_token", account.getToken());
		intent.putExtra("com.suchagit.android2cloud.oauth_secret", account.getKey());
		intent.putExtra("com.suchagit.android2cloud.link", link);
		intent.putExtra("com.suchagit.android2cloud.receiver", receiver);
		intent.putExtra("com.suchagit.android2cloud.sender", "Android");
		startService(intent);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("receiver", receiver);
		editor.commit();
	}
	
	public void linkChosen(String chosenLink) {
    	settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        url_input.setText(chosenLink);
        if(settings.getBoolean("silent", false)) {
        	link = chosenLink;
        	sendLink();
        	finish();
        }
	}
	
	public void checkHost(String host) throws DeprecatedHostException {
		if(host == null) {
			host = "";
		}
		String domain = host.replace("http://", "");
		domain = domain.replace("https://", "");
		domain = domain.replace("/", "");
		if(domain.equals("android2cloud.appspot.com")) {
			account.delete(accounts_preferences);
			throw new DeprecatedHostException(domain);
		}
	}
	
	@SuppressWarnings("serial")
	private class TooManyLinksException extends Exception {
		CharSequence[] matches;
		
		TooManyLinksException(CharSequence[] links) {
			this.matches = links;
		}
		
		private CharSequence[] getLinks() {
			return this.matches;
		}
	}
	
	
	@SuppressWarnings("serial")
	public class DeprecatedHostException extends Exception {
		String domain;
		
		DeprecatedHostException(String host) {
			this.domain = host;
		}
		
		public String getDomain() {
			return this.domain;
		}
	}
	
	@SuppressWarnings("serial")
	private class NoLinkFoundException extends Exception {
		// just defining the class
		// no real data needed
	}
}