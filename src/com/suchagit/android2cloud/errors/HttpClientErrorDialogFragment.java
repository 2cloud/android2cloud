package com.suchagit.android2cloud.errors;

import com.suchagit.android2cloud.util.ErrorMethods;
import com.secondbit.debug2cloud.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class HttpClientErrorDialogFragment extends DialogFragment {
	
    public static HttpClientErrorDialogFragment newInstance(Bundle args) {
        HttpClientErrorDialogFragment frag = new HttpClientErrorDialogFragment();
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.http_client_error;
        int yesButton = R.string.default_error_ok;
        int noButton = R.string.report_error_button;
        
        final String host = getArguments().getString("host");
        final String account = getArguments().getString("account");
        final String token = getArguments().getString("token");
        final String secret = getArguments().getString("secret");
        final String raw_data = getArguments().getString("raw_data");

        return new AlertDialog.Builder(getActivity())
    		.setCancelable(true)
            .setTitle(title)
            .setMessage(body)
            .setPositiveButton(yesButton,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	dialog.cancel();
                        }
                    }
                )
            .setNegativeButton(noButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                		String message = "Account: " + account +"\n";
                		message += "Host: " + host + "\n";
                		message += "Token: " + token + "\n";
                		message += "Secret: " + secret + "\n";
                		message += "Raw Result Data: \n";
                		message += raw_data + "\n";
                		Intent report = ErrorMethods.getEmailIntent(HttpClientErrorDialogFragment.this, "http_client_error", message);
                		getActivity().startActivity(report);
                    }
                }
            )
            .create();
    }
}