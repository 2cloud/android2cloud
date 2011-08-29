package com.suchagit.android2cloud.errors;

import com.suchagit.android2cloud.util.ErrorMethods;
import com.suchagit.android2cloud.PostLinkActivity;
import com.suchagit.android2cloud.Preferences;
import com.secondbit.debug2cloud.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PostLinkAuthErrorDialogFragment extends DialogFragment {
	
    public static PostLinkAuthErrorDialogFragment newInstance(Bundle args) {
        PostLinkAuthErrorDialogFragment frag = new PostLinkAuthErrorDialogFragment();
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.postlink_auth_error;
        int yesButton = R.string.default_error_ok;
        int noButton = R.string.report_error_button;
        int neutButton = R.string.postlink_auth_error_account_button;
        
        final String host = getArguments().getString("host");
        final String account = getArguments().getString("account");
        final String token = getArguments().getString("token");
        final String secret = getArguments().getString("secret");
		
		
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
                		Intent report = ErrorMethods.getEmailIntent(PostLinkAuthErrorDialogFragment.this, "postlink_auth_error", message);
                		getActivity().startActivity(report);
                    }
                }
            )
			.setNeutralButton(neutButton, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
        			Intent i = new Intent(getActivity(), Preferences.class);
        			getActivity().startActivityForResult(i, PostLinkActivity.EDIT_SETTINGS_REQ_CODE);
    			}
    		})
            .create();
    }
}