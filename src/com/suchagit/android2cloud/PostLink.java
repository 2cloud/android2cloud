package com.suchagit.android2cloud;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PostLink extends Activity {

	//protected String SETTINGS_PREFERENCES = "android2cloud-settings";
	protected String ACCOUNTS_PREFERENCES = "android2cloud-accounts";

	protected SharedPreferences preferences;
	private static OAuthConsumer consumer;
	private static OAuthProvider provider;
	final int ACCOUNT_LIST_REQ_CODE = 0x1337;
	final int EDIT_SETTINGS_REQ_CODE = 0x1234;
	final int DONATE_REQ_CODE = 0x1111;

    private PostLinkService mBoundService;
	private boolean mIsBound;
	private String url;
	private String device;
	boolean alerted;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String account = preferences.getString("account", "error");
		Log.i("android2cloud", "PostLink(59) account: "+account);
        String host = preferences.getString("host", "error");
		Log.i("android2cloud", "PostLink(61) host: "+host);
        String token = preferences.getString("token", "error");
		Log.i("android2cloud", "PostLink(63) token: "+token);
        String secret = preferences.getString("secret", "error");
		Log.i("android2cloud", "PostLink(65) secret: "+secret);
        Log.i("android2cloud", account);
        Log.i("android2cloud", host);
        Log.i("android2cloud", token);
        Log.i("android2cloud", secret);
        String intentAction = getIntent().getAction();
        boolean silence = preferences.getBoolean("silent", false);
		Log.i("android2cloud", "PostLink(72) silence: "+silence);
    	setContentView(R.layout.main);
    	final Button go = (Button) findViewById(R.id.go);
    	final TextView device_label = (TextView) findViewById(R.id.device_label);
    	final TextView description = (TextView) findViewById(R.id.description);
    	final EditText device_entry = (EditText) findViewById(R.id.device_entry);
    	device_entry.setText("Chrome");
		device_label.setVisibility(View.VISIBLE);
		device_entry.setVisibility(View.VISIBLE);
		description.setText("Enter a URL and a Device (usually 'Chrome') and hit 'Send' to push it to the cloud.");
    	final TextView account_display = (TextView) findViewById(R.id.account_label);
        if(!silence || !Intent.ACTION_SEND.equals(intentAction)){
	        SharedPreferences accounts_prefs = getSharedPreferences(ACCOUNTS_PREFERENCES, 0);
	        Map<String, ?> accounts = accounts_prefs.getAll();
	        int size = accounts.size();
	        if(account.equals("error") && host.equals("error") && token.equals("error") && secret.equals("error")){
	        	if(size == 0) {
		        	Log.i("android2cloud", "alert");
		        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        	builder.setMessage("You don't appear to have an account setup. You need to set one up before you can use the app.")
		        		.setCancelable(false)
		        		.setPositiveButton("Let's Go", new DialogInterface.OnClickListener() {
		        			public void onClick(DialogInterface dialog, int id) {
		    	    			Intent i = new Intent(PostLink.this, Preferences.class);
		    	    			i.putExtra("addAccount", "true");
		    	    			startActivityForResult(i, ACCOUNT_LIST_REQ_CODE); 
		        			}
		        		});
		        	AlertDialog alert = builder.create();
		        	alert.show();
		        	alerted = true;
	        	}else{
		        	Log.i("android2cloud", "alert");
		        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        	builder.setMessage("Your account is setup, but I'm not sure which to use. Will you tell me?")
		        		.setCancelable(false)
		        		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        			public void onClick(DialogInterface dialog, int id) {
		    	    			Intent i = new Intent(PostLink.this, Preferences.class);
		    	    			startActivityForResult(i, ACCOUNT_LIST_REQ_CODE); 
		        			}
		        		});
		        	AlertDialog alert = builder.create();
		        	alert.show();
		        	alerted = true;
	        	}
	        }
        	if(account.equals("error") || host.equals("error") || token.equals("error") || secret.equals("error")){
        		go.setClickable(false);
        		account_display.setText("Account: There's an error with your account. Try removing it and adding it again.");
        	}else{
        		go.setClickable(true);
        		account_display.setText("Account: "+account);
        	}

	        if (Intent.ACTION_SEND.equals(intentAction)) {
	        	// Share
		        Bundle extras = getIntent().getExtras();
		        if (extras != null) {
		        	final EditText url_input = (EditText) findViewById(R.id.link_entry);
		        	url = extras.getString(Intent.EXTRA_TEXT);
		        	String regex = "\\b(\\w+)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		            Pattern patt = Pattern.compile(regex);
		            Matcher matcher = patt.matcher(url);
		            ArrayList<String> matches = new ArrayList<String>();
		            Log.i("android2cloud", "About to matcher.find()");
		            while(matcher.find()){
			            Log.i("android2cloud", "After matcher.find()");
		            	matches.add(matcher.group());
		            	//Toast.makeText(this, matcher.group(), Toast.LENGTH_LONG).show();
		            }
		            final CharSequence[] matches_cs = matches.toArray(new CharSequence[matches.size()]);
		            if(matches.size() > 1) {
		            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		            	builder.setTitle("Choose a link to share:");
		            	builder.setItems(matches_cs, new DialogInterface.OnClickListener() {
		            	    public void onClick(DialogInterface dialog, int item) {
		            	        url = (String) matches_cs[item];
		            	        url_input.setText(url);
		            	    }
		            	});
		            	AlertDialog alert = builder.create();
		            	alert.show();
		            }else if(matches.size() == 1){
		            	url = (String) matches_cs[0];
			        	url_input.setText(url);
		            }else{
			        	url_input.setText(url);
		            }
		        }
	        }
			Log.i("android2cloud", "PostLink(159) url: "+url);
	        
		    go.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View v) {
		    		/*
		    		*/
		    		doUnbindService();
		    		final EditText url_input = (EditText) findViewById(R.id.link_entry);
		    		final EditText device_input = (EditText) findViewById(R.id.device_entry);
		    		url = url_input.getText().toString();
		    		device = device_input.getText().toString();
		    		if(device == ""){
		    			device = "Chrome";
		    		}
	    			SharedPreferences.Editor settings_editor = preferences.edit();
	    			settings_editor.putString("device_name", device);
	    			settings_editor.commit();
		    		doBindService();
		    	}
		    });
        }else{
            if(account.equals("error") || host.equals("error") || token.equals("error") || secret.equals("error")){
            	Toast.makeText(this, "Error with your account. Please check your settings in the app.", Toast.LENGTH_LONG).show();
            }
            
            if (Intent.ACTION_SEND.equals(intentAction)) {
            	// Share
    	        Bundle extras = getIntent().getExtras();
    	        if (extras != null) {
    	        	url = extras.getString(Intent.EXTRA_TEXT);
    	        	String regex = "\\b(\\w+)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		            Pattern patt = Pattern.compile(regex);
		            Matcher matcher = patt.matcher(url);
		            ArrayList<String> matches = new ArrayList<String>();
		            Log.i("android2cloud", "About to matcher.find()");
		            while(matcher.find()){
			            Log.i("android2cloud", "After matcher.find()");
		            	matches.add(matcher.group());
		            	//Toast.makeText(this, matcher.group(), Toast.LENGTH_LONG).show();
		            }
		            final CharSequence[] matches_cs = matches.toArray(new CharSequence[matches.size()]);
		            if(matches.size() > 1) {
		            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		            	builder.setTitle("Choose a link to share:");
		            	builder.setItems(matches_cs, new DialogInterface.OnClickListener() {
		            	    public void onClick(DialogInterface dialog, int item) {
		            	        url = (String) matches_cs[item];
		    		    		doUnbindService();
		    		    		device = preferences.getString("device_name", "Chrome");
		    	    			SharedPreferences.Editor settings_editor = preferences.edit();
		    	    			settings_editor.putString("device_name", device);
		    	    			settings_editor.commit();
		    		    		doBindService();
		                        finish();
		            	    }
		            	});
		            	AlertDialog alert = builder.create();
		            	alert.show();
		            }else if(matches.size() == 1){
		            	url = (String) matches_cs[0];
    		    		doUnbindService();
    		    		device = preferences.getString("device_name", "Chrome");
    	    			SharedPreferences.Editor settings_editor = preferences.edit();
    	    			settings_editor.putString("device_name", device);
    	    			settings_editor.commit();
    		    		doBindService();
                        finish();
		            }else{
    		    		doUnbindService();
    		    		device = preferences.getString("device_name", "Chrome");
    	    			SharedPreferences.Editor settings_editor = preferences.edit();
    	    			settings_editor.putString("device_name", device);
    	    			settings_editor.commit();
    		    		doBindService();
                        finish();
		            }
    	        }
            }
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	final TextView device_label = (TextView) findViewById(R.id.device_label);
    	final TextView description = (TextView) findViewById(R.id.description);
    	final EditText device_entry = (EditText) findViewById(R.id.device_entry);
    	if(preferences.getString("device_name", null) != null){
    		device_entry.setText(preferences.getString("device_name", "Chrome"));
    	}
		device_label.setVisibility(View.VISIBLE);
		device_entry.setVisibility(View.VISIBLE);
		description.setText("Enter a URL and a Device (usually 'Chrome') and hit 'Send' to push it to the cloud.");
        String account = preferences.getString("account", "error");
		Log.i("android2cloud", "PostLink(239) account: "+account);
        String host = preferences.getString("host", "error");
		Log.i("android2cloud", "PostLink(241) host: "+host);
        String token = preferences.getString("token", "error");
		Log.i("android2cloud", "PostLink(243) token: "+token);
        String secret = preferences.getString("secret", "error");
		Log.i("android2cloud", "PostLink(245) secret: "+secret);
	    final Button go = (Button) findViewById(R.id.go);
	    final TextView account_display = (TextView) findViewById(R.id.account_label);
        SharedPreferences accounts_prefs = getSharedPreferences(ACCOUNTS_PREFERENCES, 0);
        Map<String, ?> accounts = accounts_prefs.getAll();
        int size = accounts.size();
		Log.i("android2cloud", "PostLink(251) size: "+size);
        if(account.equals("error") || host.equals("error") || token.equals("error") || secret.equals("error")){
        	go.setClickable(false);
        	account_display.setText("Account: There's an error with your account. Try removing it and adding it again.");
        }else{
        	go.setClickable(true);
        	account_display.setText("Account: "+account);
        }
        if(account.equals("error") && host.equals("error") && token.equals("error") && secret.equals("error") && !alerted){
        	if(size == 0) {
	        	Log.i("android2cloud", "alert");
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage("You don't appear to have an account setup. You need to set one up before you can use the app.")
	        		.setCancelable(false)
	        		.setPositiveButton("Let's Go", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int id) {
	    	    			Intent i = new Intent(PostLink.this, Preferences.class);
	    	    			startActivityForResult(i, ACCOUNT_LIST_REQ_CODE); 
	        			}
	        		});
	        	AlertDialog alert = builder.create();
	        	alert.show();
        	}else{
	        	Log.i("android2cloud", "alert");
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage("Your account is setup, but I'm not sure which to use. Will you tell me?")
	        		.setCancelable(false)
	        		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int id) {
	    	    			Intent i = new Intent(PostLink.this, Preferences.class);
	    	    			startActivityForResult(i, ACCOUNT_LIST_REQ_CODE);
	        			}
	        		});
	        	AlertDialog alert = builder.create();
	        	alert.show();
        	}
        }
        if(alerted){
        	alerted = false;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.postlink_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.edit_settings:
			Intent i = new Intent(PostLink.this, Preferences.class);
			startActivityForResult(i, EDIT_SETTINGS_REQ_CODE);
            return true;
        /*case R.id.menu_donate:
        	Intent in = new Intent(PostLink.this, Donate.class);
        	startActivityForResult(in, DONATE_REQ_CODE);
        	return true;*/
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((PostLinkService.LocalBinder)service).getService();

            // Tell the user about this for our demo.
    		mBoundService.sendLink(url, device, consumer, provider, preferences);
        	//Toast.makeText(PostLink.this, "Sent "+url+" to the cloud.", Toast.LENGTH_LONG).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(PostLink.this, 
                PostLinkService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}