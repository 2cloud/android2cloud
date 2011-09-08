package com.suchagit.android2cloud.errors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.suchagit.android2cloud.R;
import com.suchagit.android2cloud.util.ErrorMethods;

public class UnsupportedEncodingExceptionDialogFragment extends DialogFragment {
	
    public static UnsupportedEncodingExceptionDialogFragment newInstance(Bundle args) {
        UnsupportedEncodingExceptionDialogFragment frag = new UnsupportedEncodingExceptionDialogFragment();
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.unsupported_encoding_exception_error;
        int yesButton = R.string.default_error_ok;
        int noButton = R.string.report_error_button;
        
        final String host = getArguments().getString("host");
        final String account = getArguments().getString("account");
        final String device = getArguments().getString("device_name");
        final String receiver = getArguments().getString("receiver");
        final String link = getArguments().getString("link");
        
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
                		message += "Device Name: " + device + "\n";
                		message += "Receiver: " + receiver + "\n";
                		message += "Link: " + link + "\n";
                		Intent report = ErrorMethods.getEmailIntent(UnsupportedEncodingExceptionDialogFragment.this, "unsupported_encoding_exception_error", message);
                		getActivity().startActivity(report);
                    }
                }
            )
            .create();
    }
}