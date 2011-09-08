package com.suchagit.android2cloud.errors;

import com.suchagit.android2cloud.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class IntentWithoutLinkDialogFragment extends DialogFragment {
	
    public static IntentWithoutLinkDialogFragment newInstance() {
        IntentWithoutLinkDialogFragment frag = new IntentWithoutLinkDialogFragment();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.intent_without_link_error;
        int yesButton = R.string.default_error_ok;

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
            .create();
    }
}