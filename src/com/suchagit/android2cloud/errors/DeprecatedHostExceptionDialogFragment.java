package com.suchagit.android2cloud.errors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.suchagit.android2cloud.PostLinkActivity;
import com.suchagit.android2cloud.Preferences;
import com.suchagit.android2cloud.R;

public class DeprecatedHostExceptionDialogFragment extends DialogFragment {
	
    public static DeprecatedHostExceptionDialogFragment newInstance() {
        DeprecatedHostExceptionDialogFragment frag = new DeprecatedHostExceptionDialogFragment();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.deprecated_host_error;
        int yesButton = R.string.default_error_ok;

        return new AlertDialog.Builder(getActivity())
    		.setCancelable(false)
            .setTitle(title)
            .setMessage(body)
            .setPositiveButton(yesButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    	    			Intent i = new Intent(getActivity(), Preferences.class);
    	    			getActivity().startActivityForResult(i, PostLinkActivity.EDIT_SETTINGS_REQ_CODE); 
        			}
                }
            )
            .create();
    }
}