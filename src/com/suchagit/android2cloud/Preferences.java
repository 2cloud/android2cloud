package com.suchagit.android2cloud;

import com.secondbit.debug2cloud.R;
import com.suchagit.android2cloud.util.OAuthAccount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;


public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener {
	protected String ACCOUNTS_PREFERENCES = "android2cloud-accounts";
	protected String SETTINGS_PREFERENCES = "android2cloud-settings";
	final int ACCOUNT_LIST_REQ_CODE = 0x1337;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		ListPreference accounts_pref = (ListPreference) findPreference("account");
		accounts_pref.setOnPreferenceChangeListener(this);

		Preference addNewAccount = (Preference) findPreference("addNewAccount");
		addNewAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(Preferences.this, OAuthActivity.class);
				startActivityForResult(i, ACCOUNT_LIST_REQ_CODE);
				return true;
			}
		});

		Preference deleteAccount = (Preference) findPreference("deleteAccount");
		deleteAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences accounts = getSharedPreferences("android2cloud-accounts", 0);
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				OAuthAccount account = new OAuthAccount(prefs.getString("account", ""), accounts);
				account.delete(prefs);
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		ListPreference accounts_pref = (ListPreference) findPreference("account");
		SharedPreferences accounts_prefs = getSharedPreferences(ACCOUNTS_PREFERENCES, 0);
		String[] accounts = OAuthAccount.getAccounts(accounts_prefs);
		int size = accounts.length;
		if(size == 1){
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			SharedPreferences.Editor settings_editor = settings.edit();
			settings_editor.putString("account", accounts[0]);
			settings_editor.commit();
		} else if(size > 1) {
			accounts_pref.setEnabled(true);
			accounts_pref.setEntries(accounts);
			accounts_pref.setEntryValues(accounts);
		}
	}

	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return true;
	}
}