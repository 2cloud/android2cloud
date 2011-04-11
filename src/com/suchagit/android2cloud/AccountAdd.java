package com.suchagit.android2cloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.suchagit.android2cloud.R;

public class AccountAdd extends Activity {
	protected static final int OAUTH_REQ_CODE = 0x1122;
	protected String ACCOUNTS_PREFERENCES = "android2cloud-accounts";
	protected String SETTINGS_PREFERENCES = "android2cloud-settings";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_account);
		final EditText account_input = (EditText) findViewById(R.id.account_entry);
		final EditText host_input = (EditText) findViewById(R.id.host_entry);
		final Intent intent = this.getIntent();
		SharedPreferences accounts = getSharedPreferences(ACCOUNTS_PREFERENCES, 0);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if(intent.getExtras() != null && intent.getExtras().getString("account") != null){
			account_input.setText((CharSequence) intent.getExtras().get("account"));
			host_input.setText((CharSequence) accounts.getString(intent.getExtras().getString("account"), "||error").split("\\|")[2]);
		}else{
			account_input.setText("Default");
			host_input.setText("http://android2cloud.appspot.com");
		}
		boolean advanced = settings.getBoolean("advanced", false);
		Button submit_button;
		if(advanced){
			submit_button = (Button) findViewById(R.id.advanced_go);
		}else{
			submit_button = (Button) findViewById(R.id.basic_go);
			((Button) findViewById(R.id.advanced_go)).setVisibility(View.GONE);
			((Button) findViewById(R.id.cancel)).setVisibility(View.GONE);
			((Button) findViewById(R.id.help)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.account_label)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.host_label)).setVisibility(View.GONE);
			((EditText) findViewById(R.id.host_entry)).setVisibility(View.GONE);
			((EditText) findViewById(R.id.account_entry)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.advanced_description)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.basic_description)).setVisibility(View.VISIBLE);
			submit_button.setVisibility(View.VISIBLE);
		}
		final Button cancel_button = (Button) findViewById(R.id.cancel);
		final Button help_button = (Button) findViewById(R.id.help);
		submit_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String account_text = account_input.getText().toString();
				Log.i("android2cloud", "AccountAdd(41) account_text: "+account_text);
				String host_text = host_input.getText().toString();
				Log.i("android2cloud", "AccountAdd(43) host_text: "+host_text);
    			Intent i = new Intent(AccountAdd.this, OAuth.class);
    			i.putExtra("account", account_text);
    			i.putExtra("host", host_text);
    			startActivityForResult(i, OAUTH_REQ_CODE);
			}
		});
		help_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
	        	AlertDialog.Builder builder = new AlertDialog.Builder(AccountAdd.this);
	        	builder.setMessage("Each account has two parts. The \"Account\" field just serves as a name for you to recognise the account; you can enter anything in here. The \"Host\" field tells the application which server to store its data on. It should include the http://, and should not have a trailing /. If you haven't set up your own server, and just want to use the default server, you can leave that setting at the default.")
	        		.setCancelable(true);
	        	AlertDialog alert = builder.create();
	        	alert.show();
			}
		});
		cancel_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(0);
				finish();
			}
		});
	}
	
    @Override
	protected void onActivityResult(int req_code, int res_code, Intent intent) {
    	super.onActivityResult(req_code, res_code, intent);
    	if(res_code == 1){
			SharedPreferences accounts = getSharedPreferences(ACCOUNTS_PREFERENCES, 0);
			SharedPreferences.Editor editor = accounts.edit();
			if(intent.getExtras() != null && intent.getExtras().getString("account") != null){
				editor.remove(intent.getExtras().getString("account"));
			}
			editor.putString(intent.getExtras().getString("account"), intent.getExtras().getString("token")+"|"+intent.getExtras().getString("secret")+"|"+intent.getExtras().getString("host"));
			editor.commit();
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor settings_editor = settings.edit();
			settings_editor.putString("account", intent.getExtras().getString("account"));
			Log.i("android2cloud", "AccountAdd(81) account: "+intent.getExtras().getString("account"));
			settings_editor.putString("host", intent.getExtras().getString("host"));
			Log.i("android2cloud", "AccountAdd(83) host: "+intent.getExtras().getString("host"));
			settings_editor.putString("token", intent.getExtras().getString("token"));
			Log.i("android2cloud", "AccountAdd(85) token: "+intent.getExtras().getString("token"));
			settings_editor.putString("secret", intent.getExtras().getString("secret"));
			Log.i("android2cloud", "AccountAdd(87) secret: "+intent.getExtras().getString("secret"));
			settings_editor.commit();
			//Toast.makeText(AccountAdd.this, "Adding "+account_text+" on "+host_text+" to accounts.", Toast.LENGTH_LONG).show();
			Toast.makeText(AccountAdd.this, "Successfully added account!", Toast.LENGTH_LONG).show();
			setResult(1);
			finish();
    	}
    }
}