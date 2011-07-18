package com.suchagit.android2cloud;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
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
	
	private static final int BILLING_INTENT_CODE = 0x1079;

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
	
	private final int EDIT_SETTINGS_REQ_CODE = 0x1234;
	
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
	        		if(link == null || link.trim().equals("")) {
	        			popup = true;
	    	        	AlertDialog.Builder builder = new AlertDialog.Builder(PostLinkActivity.this);
	    	        	builder.setMessage("Please enter a link.")
	    	        		.setNegativeButton("OK", new DialogInterface.OnClickListener() {
	    	        			public void onClick(DialogInterface dialog, int id) {
	    	        				dialog.cancel();
	    	        			}
	    	        		});
	    	        	AlertDialog alert = builder.create();
	    	        	alert.show();
	        		} else {
	        			receiver = device_entry.getText().toString();
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
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage("You don't appear to have an account setup. You need to set one up before you can use the app.")
	        		.setCancelable(false)
	        		.setPositiveButton("Set One Up", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int id) {
	    	    			Intent i = new Intent(PostLinkActivity.this, OAuthActivity.class);
	    	    			startActivity(i); 
	        			}
	        		});
	        	AlertDialog alert = builder.create();
	        	alert.show();
			} else {
				popup = true;
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("I'm not sure what account you want to use. Please select one.")
				.setCancelable(false)
				.setPositiveButton("Select Account", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(PostLinkActivity.this, Preferences.class);
						startActivity(i);
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
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
					resp = "There was an error understanding the result from the server. Please try again.";
					Toast.makeText(this, resp, Toast.LENGTH_LONG).show();
				}
			} else if(code == 401) {
				resp = "You need to log in before your link will be stored.";
				Toast.makeText(this, resp, Toast.LENGTH_LONG).show();
			} else if(code == 503) {
				resp = "The server is over quota. Your link ";
				resp += "was stored and will be sent to Chrome tomorrow. ";
				resp += "Alternatively, you can pay $1 to get around the quota ";
				resp += "for the rest of the day.";
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage(resp)
	        		.setPositiveButton("Pay $1", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int id) {
	    	    			Intent i = new Intent(PostLinkActivity.this, Billing.class);
	    	    			startActivityForResult(i, BILLING_INTENT_CODE); 
	        			}
	        		})
	        		.setNegativeButton("Wait Until Tomorrow", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int id) {
	        				dialog.cancel();
	        			}
	        		});
	        	AlertDialog alert = builder.create();
	        	alert.show();
			}
			break;
		case HttpClient.STATUS_ERROR:
			Toast.makeText(this, "Oops! Error processing your request.", Toast.LENGTH_LONG).show();
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