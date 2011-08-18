package com.suchagit.android2cloud.errors;

import com.suchagit.android2cloud.util.ErrorMethods;
import com.suchagit.android2cloud.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class OAuthCommunicationExceptionDialogFragment extends DialogFragment {
	
    public static OAuthCommunicationExceptionDialogFragment newInstance(Bundle args) {
        OAuthCommunicationExceptionDialogFragment frag = new OAuthCommunicationExceptionDialogFragment();
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.oauth_communication_exception_error;
        int yesButton = R.string.default_error_ok;
        int noButton = R.string.report_error_button;
        int neutButton = R.string.oauth_communication_exception_error_networking_button;
        
        final String stacktrace = getArguments().getString("stacktrace");
        final String host = getArguments().getString("host");
        final String account = getArguments().getString("account");
        final String verifier = getArguments().getString("verifier");
        final String requestUrl = getArguments().getString("request_url");
        final String responseBody = getArguments().getString("response_body");

        return new AlertDialog.Builder(getActivity())
    		.setCancelable(false)
            .setTitle(title)
            .setMessage(body)
            .setPositiveButton(yesButton,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	dialog.cancel();
                        	getActivity().finish();
                        }
                    }
                )
            .setNegativeButton(noButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                		String message = "Account: " + account +"\n";
                		message += "Host: " + host + "\n";
                		if(requestUrl != null)
                			message += "Request URL: " + requestUrl + "\n";
                		else if(verifier != null)
                			message += "Verifier: " + verifier + "\n";
                		message += "Response Body: \n";
                		message += responseBody + "\n";
                		message += "Stacktrace: \n";
                		message += stacktrace + "\n";
                		Intent report = ErrorMethods.getEmailIntent(OAuthCommunicationExceptionDialogFragment.this, "oauth_communication_exception_error", message);
                		getActivity().startActivity(report);
                    }
                }
            )
			.setNeutralButton(neutButton, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
	    			Intent i = new Intent(Intent.ACTION_MAIN);
	    			i.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
	    			getActivity().startActivity(i);
    			}
    		})
            .create();
    }
}