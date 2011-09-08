package com.suchagit.android2cloud.errors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.suchagit.android2cloud.R;

public class IncorrectTimeDialogFragment extends DialogFragment {
	
    public static IncorrectTimeDialogFragment newInstance(Bundle args) {
        IncorrectTimeDialogFragment frag = new IncorrectTimeDialogFragment();
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.incorrect_time_error;
        int yesButton = R.string.default_error_ok;
        int noButton = R.string.incorrect_time_error_time_button;
        
        //final String timezone = getArguments().getString("timezone");
        //final String friendlyTime = getArguments().getString("friendlyTime");

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
    			public void onClick(DialogInterface dialog, int id) {
	    			Intent i = new Intent(Intent.ACTION_MAIN);
	    			i.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
	    			getActivity().startActivity(i);
    			}
    		})
            .create();
    }
}