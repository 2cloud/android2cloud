package com.suchagit.android2cloud.util;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.app.Fragment;

public class ErrorMethods {
	public static Intent getEmailIntent(Fragment context, String type, String message) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"android@2cloudproject.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "In-App Error Report");
		String message_prefix = "Error:\n";
		message_prefix += "Type: "+type+"\n";
		try {
			message_prefix += "Version: " + context.getActivity().getPackageManager().getPackageInfo(context.getActivity().getPackageName(), 0 ).versionCode + "\n";
		} catch (NameNotFoundException e) {
			message_prefix += "\n";
		}
		message_prefix += "Android Version: " + android.os.Build.VERSION.SDK + "\n";
		message_prefix += "Phone: " + android.os.Build.MODEL + "\n";
		message = message_prefix + message;
		i.putExtra(Intent.EXTRA_TEXT   , message);
		return i;
	}
}
