package com.suchagit.android2cloud.errors;

import com.suchagit.android2cloud.util.ErrorMethods;
import com.suchagit.android2cloud.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class OAuthExpectationFailedExceptionDialogFragment extends DialogFragment {
	
    public static OAuthExpectationFailedExceptionDialogFragment newInstance(Bundle args) {
        OAuthExpectationFailedExceptionDialogFragment frag = new OAuthExpectationFailedExceptionDialogFragment();
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.oauth_expectation_failed_exception_error;
        int yesButton = R.string.default_error_ok;
        int noButton = R.string.report_error_button;
        
        final String stacktrace = getArguments().getString("stacktrace");
        final String host = getArguments().getString("host");
        final String account = getArguments().getString("account");
        final String verifier = getArguments().getString("verifier");
        final String requestUrl = getArguments().getString("request_url");

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
                		message += "Stacktrace: \n";
                		message += stacktrace + "\n";
                		Intent report = ErrorMethods.getEmailIntent(OAuthExpectationFailedExceptionDialogFragment.this, "oauth_expectation_failed_exception_error", message);
                		getActivity().startActivity(report);
                    }
                }
            )
            .create();
    }
}