package com.suchagit.android2cloud.errors;

import com.suchagit.android2cloud.OAuthActivity;
import com.suchagit.android2cloud.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CorruptedAccountDialogFragment extends DialogFragment {
	
    public static CorruptedAccountDialogFragment newInstance() {
        CorruptedAccountDialogFragment frag = new CorruptedAccountDialogFragment();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.corrupted_account_error;
        int yesButton = R.string.corrupted_account_error_account_button;

        return new AlertDialog.Builder(getActivity())
    		.setCancelable(false)
            .setTitle(title)
            .setMessage(body)
            .setPositiveButton(yesButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    	    			Intent i = new Intent(getActivity(), OAuthActivity.class);
    	    			getActivity().startActivityForResult(i, 0x1234); 
                    }
                }
            )
            .create();
    }
}