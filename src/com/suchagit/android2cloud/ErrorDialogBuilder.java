package com.suchagit.android2cloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import com.secondbit.debug2cloud.R;

public class ErrorDialogBuilder extends AlertDialog.Builder {
	private Activity activity;
	private Bundle data;
	
	private DialogInterface.OnClickListener default_ok_listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			dialog.cancel();
		}
	};
	
	private DialogInterface.OnClickListener default_ok_kill_listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			dialog.cancel();
			ErrorDialogBuilder.this.activity.finish();
		}
	};
	
	public ErrorDialogBuilder(Context context) {
		super(context);
		this.activity = (Activity) context;
		this.buildDefault();
	}
	
	public ErrorDialogBuilder(Context context, Bundle bundle) {
		super(context);
		this.activity = (Activity) context;
		this.buildDefault();
		if(bundle != null)
			this.data = bundle;
	}
	
	public ErrorDialogBuilder(Context context, String message) {
		super(context);
		this.activity = (Activity) context;
		this.buildDefault();
		this.setMessage(message);
	}
	
	public ErrorDialogBuilder(Context context, int message) {
		super(context);
		this.activity = (Activity) context;
		this.buildDefault();
		this.setMessage(message);
	}
	
	public ErrorDialogBuilder buildDefault() {
    	this.setMessage(R.string.default_error_message)
    	.setTitle(R.string.default_error_title)
    		.setPositiveButton(R.string.default_error_ok, default_ok_listener)
    		.setCancelable(false);
    	this.data = new Bundle();
    	return this;
	}
	
	public ErrorDialogBuilder build(int error) {
		this.setMessage(error);
		switch(error) {
		case R.string.oauthactivity_null_uri_error:
			this.setPositiveButton(R.string.default_error_ok, default_ok_kill_listener);
			break;
		case R.string.oauthwebview_null_intent_error:
			this.setPositiveButton(R.string.default_error_ok, default_ok_kill_listener);
			break;
		case R.string.postlink_null_link_error:
			this.setCancelable(true);
			break;
		case R.string.postlink_null_receiver_error:
			this.setCancelable(true);
			break;
		case R.string.postlink_auth_error:
			this.setNegativeButton(R.string.postlink_auth_error_account_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
	    			Intent i = new Intent(ErrorDialogBuilder.this.activity, Preferences.class);
	    			ErrorDialogBuilder.this.activity.startActivityForResult(i, PostLinkActivity.EDIT_SETTINGS_REQ_CODE); 
    			}
    		})
    		.setNeutralButton(R.string.report_error_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				String message = "Error:\n";
    				message += "Type: postlink_auth_error\n";
    				try {
						message += "Version: " + ErrorDialogBuilder.this.activity.getPackageManager().getPackageInfo(ErrorDialogBuilder.this.activity.getPackageName(), 0 ).versionCode + "\n";
					} catch (NameNotFoundException e) {
					}
    				message += "Account: " + ErrorDialogBuilder.this.data.getString("account") +"\n";
    				message += "Host: " + ErrorDialogBuilder.this.data.getString("host") + "\n";
    				message += "Token: " + ErrorDialogBuilder.this.data.getString("token") + "\n";
    				message += "Secret: " + ErrorDialogBuilder.this.data.getString("secret") + "\n";
    				Intent report = getEmailIntent(message);
    				ErrorDialogBuilder.this.activity.startActivity(report);
    			}
    		}).setCancelable(true);
			break;
		case R.string.no_accounts_error:
			this.setPositiveButton(R.string.no_accounts_error_account_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
	    			Intent i = new Intent(ErrorDialogBuilder.this.activity, OAuthActivity.class);
	    			ErrorDialogBuilder.this.activity.startActivity(i); 
    			}
    		});
			break;
		case R.string.no_account_selected_error:
			this.setPositiveButton(R.string.no_account_selected_error_account_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
	    			Intent i = new Intent(ErrorDialogBuilder.this.activity, Preferences.class);
	    			ErrorDialogBuilder.this.activity.startActivityForResult(i, PostLinkActivity.EDIT_SETTINGS_REQ_CODE); 
    			}
    		});
			break;
		case R.string.intent_without_link_error:
			this.setCancelable(true);
			break;
		case R.string.http_client_error:
			this.setNegativeButton(R.string.report_error_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				String message = "Error:\n";
    				message += "Type: http_client_error\n";
    				try {
						message += "Version: " + ErrorDialogBuilder.this.activity.getPackageManager().getPackageInfo(ErrorDialogBuilder.this.activity.getPackageName(), 0 ).versionCode + "\n";
					} catch (NameNotFoundException e) {
					}
    				message += "Account: " + ErrorDialogBuilder.this.data.getString("account") +"\n";
    				message += "Host: " + ErrorDialogBuilder.this.data.getString("host") + "\n";
    				message += "Token: " + ErrorDialogBuilder.this.data.getString("token") + "\n";
    				message += "Secret: " + ErrorDialogBuilder.this.data.getString("secret") + "\n";
    				message += "Raw Data: " + ErrorDialogBuilder.this.data.getString("raw_data") + "\n";
    				Intent report = getEmailIntent(message);
    				ErrorDialogBuilder.this.activity.startActivity(report);
    			}
    		}).setCancelable(true);
			break;
		case R.string.over_quota_error:
			this.setTitle(R.string.over_quota_error_title)
			.setNegativeButton(R.string.over_quota_error_pay_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
	    			Intent i = new Intent(ErrorDialogBuilder.this.activity, Billing.class);
	    			ErrorDialogBuilder.this.activity.startActivityForResult(i, PostLinkActivity.BILLING_INTENT_CODE); 
    			}
    		});
			break;
		case R.string.oauth_message_signer_exception_error:
			this.setNegativeButton(R.string.report_error_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				String message = "Error:\n";
    				message += "Type: oauth_message_signer_exception_error\n";
    				try {
						message += "Version: " + ErrorDialogBuilder.this.activity.getPackageManager().getPackageInfo(ErrorDialogBuilder.this.activity.getPackageName(), 0 ).versionCode + "\n";
					} catch (NameNotFoundException e) {
					}
    				message += "Account: " + ErrorDialogBuilder.this.data.getString("account") +"\n";
    				message += "Host: " + ErrorDialogBuilder.this.data.getString("host") + "\n";
    				if(ErrorDialogBuilder.this.data.get("request_url") != null)
    					message += "Request URL: " + ErrorDialogBuilder.this.data.getString("request_url") + "\n";
    				else if(ErrorDialogBuilder.this.data.get("verifier") != null)
    					message += "Verifier: " + ErrorDialogBuilder.this.data.getString("verifier") + "\n";
    				message += "Stacktrace: \n";
    				message += ErrorDialogBuilder.this.data.getString("stacktrace") + "\n";
    				Intent report = getEmailIntent(message);
    				ErrorDialogBuilder.this.activity.startActivity(report);
    			}
    		}).setCancelable(true);
			break;
		case R.string.oauth_not_authorized_exception_error:
			this.setNegativeButton(R.string.report_error_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				String message = "Error:\n";
    				message += "Type: oauth_not_authorized_exception_error\n";
    				try {
						message += "Version: " + ErrorDialogBuilder.this.activity.getPackageManager().getPackageInfo(ErrorDialogBuilder.this.activity.getPackageName(), 0 ).versionCode + "\n";
					} catch (NameNotFoundException e) {
					}
    				message += "Account: " + ErrorDialogBuilder.this.data.getString("account") +"\n";
    				message += "Host: " + ErrorDialogBuilder.this.data.getString("host") + "\n";
    				if(ErrorDialogBuilder.this.data.get("request_url") != null)
    					message += "Request URL: " + ErrorDialogBuilder.this.data.getString("request_url") + "\n";
    				else if(ErrorDialogBuilder.this.data.get("verifier") != null)
    					message += "Verifier: " + ErrorDialogBuilder.this.data.getString("verifier") + "\n";
    				message += "Stacktrace: \n";
    				message += ErrorDialogBuilder.this.data.getString("stacktrace") + "\n";
    				Intent report = getEmailIntent(message);
    				ErrorDialogBuilder.this.activity.startActivity(report);
    			}
    		}).setCancelable(true);
			break;
		case R.string.oauth_expectation_failed_exception_error:
			this.setNegativeButton(R.string.report_error_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				String message = "Error:\n";
    				message += "Type: oauth_expectation_failed_exception_error\n";
    				try {
						message += "Version: " + ErrorDialogBuilder.this.activity.getPackageManager().getPackageInfo(ErrorDialogBuilder.this.activity.getPackageName(), 0 ).versionCode + "\n";
					} catch (NameNotFoundException e) {
					}
    				message += "Account: " + ErrorDialogBuilder.this.data.getString("account") +"\n";
    				message += "Host: " + ErrorDialogBuilder.this.data.getString("host") + "\n";
    				if(ErrorDialogBuilder.this.data.get("request_url") != null)
    					message += "Request URL: " + ErrorDialogBuilder.this.data.getString("request_url") + "\n";
    				else if(ErrorDialogBuilder.this.data.get("verifier") != null)
    					message += "Verifier: " + ErrorDialogBuilder.this.data.getString("verifier") + "\n";
    				message += "Stacktrace: \n";
    				message += ErrorDialogBuilder.this.data.getString("stacktrace") + "\n";
    				Intent report = getEmailIntent(message);
    				ErrorDialogBuilder.this.activity.startActivity(report);
    			}
    		}).setCancelable(true);
			break;
		case R.string.oauth_communication_exception_error:
			this.setNegativeButton(R.string.report_error_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				String message = "Error:\n";
    				message += "Type: oauth_communication_exception_error\n";
    				try {
						message += "Version: " + ErrorDialogBuilder.this.activity.getPackageManager().getPackageInfo(ErrorDialogBuilder.this.activity.getPackageName(), 0 ).versionCode + "\n";
					} catch (NameNotFoundException e) {
					}
    				message += "Account: " + ErrorDialogBuilder.this.data.getString("account") +"\n";
    				message += "Host: " + ErrorDialogBuilder.this.data.getString("host") + "\n";
    				if(ErrorDialogBuilder.this.data.get("request_url") != null)
    					message += "Request URL: " + ErrorDialogBuilder.this.data.getString("request_url") + "\n";
    				else if(ErrorDialogBuilder.this.data.get("verifier") != null)
    					message += "Verifier: " + ErrorDialogBuilder.this.data.getString("verifier") + "\n";
    				message += "Stacktrace: \n";
    				message += ErrorDialogBuilder.this.data.getString("stacktrace") + "\n";
    				Intent report = getEmailIntent(message);
    				ErrorDialogBuilder.this.activity.startActivity(report);
    			}
    		})
			.setNeutralButton(R.string.oauth_communication_exception_error_networking_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
	    			Intent i = new Intent(Intent.ACTION_MAIN);
	    			i.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
	    			ErrorDialogBuilder.this.activity.startActivity(i);
    			}
    		}).setCancelable(true);
			break;
		case R.string.unsupported_encoding_exception_error:
			this.setNegativeButton(R.string.report_error_button, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				String message = "Error:\n";
    				message += "Type: unsupported_encoding_exception_error\n";
    				try {
						message += "Version: " + ErrorDialogBuilder.this.activity.getPackageManager().getPackageInfo(ErrorDialogBuilder.this.activity.getPackageName(), 0 ).versionCode + "\n";
					} catch (NameNotFoundException e) {
					}
    				message += "Account: " + ErrorDialogBuilder.this.data.getString("account") +"\n";
    				message += "Host: " + ErrorDialogBuilder.this.data.getString("host") + "\n";
    				message += "URL: " + ErrorDialogBuilder.this.data.getString("link") + "\n";
    				message += "Device: " + ErrorDialogBuilder.this.data.getString("device_name") + "\n";
    				message += "Recipient: " + ErrorDialogBuilder.this.data.getString("reciever") + "\n";
    				message += "Stacktrace: \n";
    				message += ErrorDialogBuilder.this.data.getString("stacktrace") + "\n";
    				Intent report = getEmailIntent(message);
    				ErrorDialogBuilder.this.activity.startActivity(report);
    			}
    		}).setCancelable(true);
			break;
		}
		return this;
	}
	
	public static Intent getEmailIntent(String message) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"android@2cloudproject.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "In-App Error Report");
		String message_prefix = "Android Version: " + android.os.Build.VERSION.SDK + "\n";
		message_prefix += "Phone: " + android.os.Build.MODEL + "\n";
		message = message_prefix + message;
		i.putExtra(Intent.EXTRA_TEXT   , message);
		return i;
	}

}
