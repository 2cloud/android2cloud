package com.suchagit.android2cloud;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

import com.suchagit.android2cloud.util.AddLinkResponse;
import com.suchagit.android2cloud.util.HttpClient;
import com.suchagit.android2cloud.util.OAuthAccount;

public class PostLinkActivity extends Activity implements AddLinkResponse.Receiver {
	
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
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
    	
    	settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		receiver = settings.getString("receiver", "Chrome");
    	
		if(!settings.getBoolean("silent", false)) {
			if(!contentSet) {
				setContentView(R.layout.main);
				contentSet = true;
			}
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
	        			showDialog(R.string.postlink_null_link_error);
	        		} else if(receiver == null || receiver.trim().equals("")) {
	        			popup = true;
	        			showDialog(R.string.postlink_null_receiver_error);
	        		} else {
	        			send_button.setVisibility(View.GONE);
	        			throbber.setVisibility(View.VISIBLE);
	        			sendLink();
	        		}
	        	}
	        });
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		popup = false;
		
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
			} else if(accounts.length == 1 && accounts[0].equals("")){
				popup = true;
	        	showDialog(R.string.no_accounts_error);
			} else {
				popup = true;
				showDialog(R.string.no_account_selected_error);
			}
		}
		
		if(Intent.ACTION_SEND.equals(getIntent().getAction())) {
			link = getIntent().getExtras().getString(Intent.EXTRA_TEXT);
			Log.d("PostLinkActivity", link);
        	String regex = "\\b(\\w+)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern patt = Pattern.compile(regex);
            Matcher matcher = patt.matcher(link);
            ArrayList<String> matches = new ArrayList<String>();
            while(matcher.find()){
            	matches.add(matcher.group());
    			Log.d("PostLinkActivity", matcher.group());
            }
            final CharSequence[] matches_cs = matches.toArray(new CharSequence[matches.size()]);
            if(matches.size() > 1) {
            	popup = true;
            	if(!contentSet) {
            		setContentView(R.layout.main);
            	}
    	    	url_input = (EditText) findViewById(R.id.link_entry);
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            	builder.setTitle("Choose a link to share:");
            	builder.setItems(matches_cs, new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int item) {
            	        link = (String) matches_cs[item];
            	        url_input.setText(link);
            	        if(settings.getBoolean("silent", false)) {
            	        	sendLink();
            	        	finish();
            	        }
            	    }
            	});
            	AlertDialog alert = builder.create();
            	alert.show();
            }else if(matches.size() == 1){
            	link = (String) matches_cs[0];
            } else {
            	showDialog(R.string.intent_without_link_error);
            }
		}
    	
		mReceiver = new AddLinkResponse(new Handler());
		mReceiver.setReceiver(this);
		if(settings.getBoolean("silent", false) && popup == false && link.trim() != "") {
			sendLink();
			finish();
		} else {
			if(!contentSet) {
				setContentView(R.layout.main);
			}
	    	send_button = (Button) findViewById(R.id.send);
	    	device_entry = (EditText) findViewById(R.id.device_entry);
	    	url_input = (EditText) findViewById(R.id.link_entry);
	    	account_display = (TextView) findViewById(R.id.account_label);
	    	throbber = (ProgressBar) findViewById(R.id.sendLinkThrobber);
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
	public Dialog onCreateDialog(int id, Bundle data) {
		super.onCreateDialog(id, data);
		ErrorDialogBuilder error = new ErrorDialogBuilder(PostLinkActivity.this, data);
		error.build(id);
		AlertDialog alert = error.create();
		return alert;
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
					showDialog(R.string.http_client_error, error_data);
				}
			} else if(code == 401) {
				Bundle error_data = new Bundle();
				error_data.putString("account", account.getAccount());
				error_data.putString("host", account.getHost());
				error_data.putString("token", account.getToken());
				error_data.putString("secret", account.getKey());
				showDialog(R.string.postlink_auth_error, error_data);
			} else if(code == 503) {
				showDialog(R.string.over_quota_error);
			}
			break;
		case HttpClient.STATUS_ERROR:
			int error_code = resultData.getInt("response_code");
			if(error_code == 600) {
				Bundle error_data = new Bundle();
				error_data.putString("account", account.getAccount());
				error_data.putString("host", account.getHost());
				error_data.putString("device_name", "Android");
				error_data.putString("receiver", receiver);
				error_data.putString("link", link);
				showDialog(R.string.postlink_auth_error, error_data);
				showDialog(R.string.unsupported_encoding_exception_error);
			} else {
				showDialog(R.string.default_error_message);
			}
			break;
		}
	}
	
	private void sendLink() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.suchagit.android2cloud", "com.suchagit.android2cloud.HttpService"));
		intent.setAction("AddLink");
		intent.putExtra("result_receiver", mReceiver);
		intent.putExtra("host", account.getHost());
		intent.putExtra("oauth_token", account.getToken());
		intent.putExtra("oauth_secret", account.getKey());
		intent.putExtra("link", link);
		intent.putExtra("receiver", receiver);
		intent.putExtra("sender", "Android");
		startService(intent);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("receiver", receiver);
		editor.commit();
	}
}