package com.suchagit.android2cloud.errors;

import com.suchagit.android2cloud.util.ErrorMethods;
import com.suchagit.android2cloud.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class IllegalStateExceptionDialogFragment extends DialogFragment {
	
    public static IllegalStateExceptionDialogFragment newInstance(Bundle args) {
        IllegalStateExceptionDialogFragment frag = new IllegalStateExceptionDialogFragment();
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.illegal_state_exception_error;
        int yesButton = R.string.default_error_ok;
        int noButton = R.string.report_error_button;
        
        final String host = getArguments().getString("host");
        final String request_host = getArguments().getString("request_host");
        final String request_type = getArguments().getString("request_type");
        final String stacktrace = getArguments().getString("stacktrace");
        
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
                		String message = "Host: " + host + "\n";
                		message += "Request Host: " + request_host + "\n";
                		message += "Request Type: " + request_type + "\n";
                		message += "Stacktrace: " + stacktrace + "\n";
                		Intent report = ErrorMethods.getEmailIntent(IllegalStateExceptionDialogFragment.this, "illegal_state_exception_error", message);
                		getActivity().startActivity(report);
                    }
                }
            )
            .create();
    }
}