package com.suchagit.android2cloud.errors;

import com.suchagit.android2cloud.PostLinkActivity;
import com.secondbit.debug2cloud.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectLinkDialogFragment extends DialogFragment {
	
    public static SelectLinkDialogFragment newInstance(Bundle args) {
        SelectLinkDialogFragment frag = new SelectLinkDialogFragment();
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.select_link_title;
        final CharSequence[] choices = getArguments().getCharSequenceArray("choices");

        return new AlertDialog.Builder(getActivity())
    		.setCancelable(false)
            .setTitle(title)
            .setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                    	((PostLinkActivity) getActivity()).linkChosen((String) choices[item]);
                    }
                }
            )
            .create();
    }
}