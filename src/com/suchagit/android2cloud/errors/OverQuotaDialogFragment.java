package com.suchagit.android2cloud.errors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.suchagit.android2cloud.Billing;
import com.suchagit.android2cloud.PostLinkActivity;
import com.secondbit.debug2cloud.R;

public class OverQuotaDialogFragment extends DialogFragment {
	
    public static OverQuotaDialogFragment newInstance() {
        OverQuotaDialogFragment frag = new OverQuotaDialogFragment();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.over_quota_error_title;
        int body = R.string.over_quota_error;
        int yesButton = R.string.default_error_ok;
        int noButton = R.string.over_quota_error_pay_button;

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
            ).setNegativeButton(noButton,
            	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
		    			Intent i = new Intent(getActivity(), Billing.class);
		    			getActivity().startActivityForResult(i, PostLinkActivity.BILLING_INTENT_CODE);
	    			}
				})
            .create();
    }
}