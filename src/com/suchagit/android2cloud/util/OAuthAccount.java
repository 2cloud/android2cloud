package com.suchagit.android2cloud.util;

import android.content.SharedPreferences;

public class OAuthAccount {
	private String account;
	private String host;
	private String token;
	private String key;
	
	public void setAccount(String newAccount) {
		this.account = newAccount;
	}
	
	public String getAccount() {
		return this.account;
	}
	
	public void setHost(String newHost) {
		this.host = newHost;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public void setToken(String newToken) {
		this.token = newToken;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public void setKey(String newKey) {
		this.key = newKey;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public OAuthAccount(String account, SharedPreferences preferences) {
		this.setAccount(account);
		this.load(preferences);
	}
	public OAuthAccount(String account, String host, String token, String key) {
		this.setAccount(account);
		this.setHost(host);
		this.setToken(token);
		this.setKey(key);
	}
	
	public OAuthAccount save(SharedPreferences preferences) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("host_"+this.account, this.host);
		editor.putString("oauth_token_"+this.account, this.token);
		editor.putString("oauth_secret_"+this.account, this.key);
		String accounts = preferences.getString("accounts", "|");
		if(accounts.indexOf("|" + this.account + "|") == -1) {
			accounts += this.account + "|";
		}
		editor.putString("accounts", accounts);
		editor.commit();
		return this;
	}
	
	public boolean delete(SharedPreferences preferences) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("host_"+this.account);
		editor.remove("oauth_token_"+this.account);
		editor.remove("oauth_secret_"+this.account);
		String accounts = preferences.getString("accounts", "|"+this.account+"|");
		accounts = accounts.replace("|"+this.account+"|", "|");
		editor.putString("accounts", accounts);
		return editor.commit();
	}
	
	public void load(SharedPreferences preferences) {
		this.setHost(preferences.getString("host_"+this.account, "error"));
		this.setToken(preferences.getString("oauth_token_"+this.account, "error"));
		this.setKey(preferences.getString("oauth_secret_"+this.account, "error"));
	}
	
	public static String[] getAccounts(SharedPreferences preferences) {
		String accountString = preferences.getString("accounts", "|");
		int chopStart = 0;
		int chopEnd = accountString.length();
		if(accountString.charAt(accountString.length() - 1) == '|') {
			chopEnd--;
		}
		if(accountString.charAt(0) == '|') {
			chopStart++;
		}
		if(chopEnd <= chopStart) {
			accountString = "";
		} else {
			accountString = accountString.substring(chopStart, chopEnd);
		}
		return accountString.split("\\|");
	}
}