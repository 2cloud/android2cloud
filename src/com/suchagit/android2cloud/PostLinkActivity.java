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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.suchagit.android2cloud.util.AddLinkResponse;
import com.suchagit.android2cloud.util.HttpClient;
import com.suchagit.android2cloud.util.OAuthAccount;

public class PostLinkActivity extends Activity implements AddLinkResponse.Receiver {
	
	public AddLinkResponse mReceiver;
    
	private OAuthAccount account;
	private SharedPreferences settings;
	private SharedPreferences accounts_preferences;
	private EditText device_entry;
	private EditText url_input;
	private TextView account_display;
	
	private String link = "";
	private String receiver = "";
	private boolean popup = false;
	
	private final int EDIT_SETTINGS_REQ_CODE = 0x1234;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
    	
    	settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		receiver = settings.getString("receiver", "Chrome");
    	
		if(!settings.getBoolean("silent", false)) {
	    	setContentView(R.layout.main);
	    	final Button send = (Button) findViewById(R.id.send);
	    	device_entry = (EditText) findViewById(R.id.device_entry);
	    	url_input = (EditText) findViewById(R.id.link_entry);
	    	account_display = (TextView) findViewById(R.id.account_label);
	    	
	    	url_input.setText(link);
	    	device_entry.setText(receiver);
	    	
	    	send.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		link = url_input.getText().toString();
	        		receiver = device_entry.getText().toString();
	        		sendLink();
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
		
		String account_name = settings.getString("account", "error");
		if(account_name.equals("error")) {
			String[] accounts = OAuthAccount.getAccounts(accounts_preferences);
			if(accounts.length == 1){
				SharedPreferences.Editor settings_editor = settings.edit();
				settings_editor.putString("account", accounts[0]);
				settings_editor.commit();
			} else {
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
			}
		}
		
		if(Intent.ACTION_SEND.equals(getIntent().getAction())) {
			link = getIntent().getExtras().getString(Intent.EXTRA_TEXT);
        	String regex = "\\b(\\w+)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern patt = Pattern.compile(regex);
            Matcher matcher = patt.matcher(link);
            ArrayList<String> matches = new ArrayList<String>();
            while(matcher.find()){
            	matches.add(matcher.group());
            }
            final CharSequence[] matches_cs = matches.toArray(new CharSequence[matches.size()]);
            if(matches.size() > 1) {
            	popup = true;
    	    	setContentView(R.layout.main);
    	    	url_input = (EditText) findViewById(R.id.link_entry);
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            	builder.setTitle("Choose a link to share:");
            	builder.setItems(matches_cs, new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int item) {
            	        link = (String) matches_cs[item];
            	        url_input.setText(link);
            	        sendLink();
            	        finish();
            	    }
            	});
            	AlertDialog alert = builder.create();
            	alert.show();
            }else if(matches.size() == 1){
            	link = (String) matches_cs[0];
            }
		}
		
		account = new OAuthAccount(settings.getString("account", "error"), accounts_preferences);
    	
		mReceiver = new AddLinkResponse(new Handler());
		mReceiver.setReceiver(this);
		if(settings.getBoolean("silent", false)) {
			if(link.trim() != "" && popup == false) {
				sendLink();
				finish();
			}
		} else {
	    	account_display.setText("Account: "+account.getAccount());
		}
	}
	
	public void onPause() {
		super.onPause();
		mReceiver.setReceiver(null);
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
				resp += resultData.getString("link");
				resp += " was stored and will be sent to Chrome tomorrow. ";
				resp += "Alternatively, you can pay $1 to get around the quota ";
				resp += "for the rest of the day.";
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage(resp)
	        		.setPositiveButton("Pay $1", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int id) {
	    	    			Intent i = new Intent(PostLinkActivity.this, OAuthActivity.class);
	    	    			startActivity(i); 
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
	}
}