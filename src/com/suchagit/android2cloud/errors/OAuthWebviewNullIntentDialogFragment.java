package com.suchagit.android2cloud.errors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.suchagit.android2cloud.R;

public class OAuthWebviewNullIntentDialogFragment extends DialogFragment {
	
    public static OAuthWebviewNullIntentDialogFragment newInstance() {
        OAuthWebviewNullIntentDialogFragment frag = new OAuthWebviewNullIntentDialogFragment();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.oauthwebview_null_intent_error;
        int yesButton = R.string.default_error_ok;

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
            .create();
    }
}