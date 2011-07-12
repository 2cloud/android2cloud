package com.suchagit.android2cloud;

import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;


public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener {
	protected String ACCOUNTS_PREFERENCES = "android2cloud-accounts";
	protected String SETTINGS_PREFERENCES = "android2cloud-settings";
	final int ACCOUNT_LIST_REQ_CODE = 0x1337;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    		addPreferencesFromResource(R.xml.preferences);
            ListPreference accounts_pref = (ListPreference) findPreference("account");
            CheckBoxPreference advancedCheckbox = (CheckBoxPreference) findPreference("advanced");
            accounts_pref.setOnPreferenceChangeListener(this);
            advancedCheckbox.setOnPreferenceChangeListener(this);
    		PreferenceCategory advancedPreferences = (PreferenceCategory) findPreference("advancedCategory");
    		advancedPreferences.setEnabled(advancedCheckbox.isChecked());
    		if(getIntent() != null){
    			if(getIntent().getStringExtra("addAccount") != null){
		            String intentAction = getIntent().getStringExtra("addAccount");
		            if(intentAction.equals("true")){
		            	Intent i = new Intent(Preferences.this, OAuthActivity.class);
		            	startActivityForResult(i, ACCOUNT_LIST_REQ_CODE);
		            }
    			}
    		}
            
            Preference addNewAccount = (Preference) findPreference("addNewAccount");
            addNewAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                                    public boolean onPreferenceClick(Preference preference) {
	                    	    			Intent i = new Intent(Preferences.this, OAuthActivity.class);
	                    	    			startActivityForResult(i, ACCOUNT_LIST_REQ_CODE);
                                            return true;
                                    }

                            });
    }
    
    public boolean onPreferenceChange(Preference preference, Object newValue) {
    	/*if(preference.getKey().equals("account") || preference.getKey().equals("host")){
    		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    		Editor settingsEditor = settings.edit();
    		settingsEditor.putString(preference.getKey(), (String) newValue);
    		settingsEditor.commit();
    	}*/
    	if(preference.getKey().equals("advanced")){
    		PreferenceCategory advancedPreferences = (PreferenceCategory) findPreference("advancedCategory");
    		advancedPreferences.setEnabled(newValue.equals(true));
    		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    		Editor settingsEditor = settings.edit();
    		settingsEditor.putBoolean(preference.getKey(), newValue.equals(true));
    		settingsEditor.commit();
    	}
    	return true;
    }
    
    @Override
    protected void onResume() {
            super.onResume();
    		SharedPreferences accounts_prefs = getSharedPreferences(ACCOUNTS_PREFERENCES, 0);
            Map<String, ?> accounts = accounts_prefs.getAll();
            int size = accounts.size();
            Object[] account_objects_array = accounts.keySet().toArray();
            if(size == 1){
        		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            	Log.i("android2cloud", account_objects_array[0].toString());
    			SharedPreferences.Editor settings_editor = settings.edit();
    			String[] tmpToken = accounts.get(account_objects_array[0].toString()).toString().split("\\|");
    			settings_editor.putString("account", account_objects_array[0].toString());
    			settings_editor.putString("host", tmpToken[2]);
    			settings_editor.putString("token", tmpToken[0]);
    			settings_editor.putString("secret", tmpToken[1]);
    			settings_editor.commit();
    			Log.i("android2cloud", "account: "+account_objects_array[0].toString());
    			Log.i("android2cloud", "host: "+tmpToken[2]);
    			Log.i("android2cloud", "token: "+tmpToken[0]);
    			Log.i("android2cloud", "secret: "+tmpToken[1]);
            }
            Log.i("android2cloud", "account_objects_array: "+account_objects_array.length);
           	String[] accountsAsStrings = new String[account_objects_array.length];
           	for(int x=0; x < account_objects_array.length; x++)
           		accountsAsStrings[x] = (String) account_objects_array[x];
           	
            ListPreference accounts_pref = (ListPreference) findPreference("account");
            accounts_pref.setEntries(accountsAsStrings);
            accounts_pref.setEntryValues(accountsAsStrings);
    }
    

    @Override
	protected void onActivityResult(int req_code, int res_code, Intent intent) {
        super.onActivityResult(req_code, res_code, intent);
        if(res_code == 1){
	        SharedPreferences accounts_prefs = getSharedPreferences(ACCOUNTS_PREFERENCES, 0);
	        Map<String, ?> accounts = accounts_prefs.getAll();
	        int size = accounts.size();
	        Object[] account_objects_array = accounts.keySet().toArray();
	        String[] accounts_array = new String[size];
	        Log.i("android2cloud", "account_objects_array: "+account_objects_array.length);
	       	for(int x=0; x<account_objects_array.length; x++){
	        	Log.i("android2cloud", "index: "+x);
	        	accounts_array[x] = (String) account_objects_array[x];
	        }
            ListPreference accounts_pref = (ListPreference) findPreference("account");
            accounts_pref.setEntries(accounts_array);
            accounts_pref.setEntryValues(accounts_array); 
        }
    }
}