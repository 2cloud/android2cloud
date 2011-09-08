/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.suchagit.android2cloud;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.suchagit.android2cloud.BillingService.RequestPurchase;
import com.suchagit.android2cloud.Consts.PurchaseState;
import com.suchagit.android2cloud.Consts.ResponseCode;
import com.suchagit.android2cloud.errors.BillingCannotConnectDialogFragment;
import com.suchagit.android2cloud.errors.BillingNotSupportedDialogFragment;
import com.suchagit.android2cloud.util.OAuthAccount;
import com.suchagit.android2cloud.util.PaymentNotificationResponse;

/**
 * A sample application that demonstrates in-app billing.
 */
public class Billing extends FragmentActivity implements PaymentNotificationResponse.Receiver {
    private static final String TAG = "Billing";

    /**
     * The SharedPreferences key for recording whether we initialized the
     * database.  If false, then we perform a RestoreTransactions request
     * to get all the purchases for this user.
     */

    private BillingPurchaseObserver mBillingPurchaseObserver;
    private Handler mHandler;

    private BillingService mBillingService;
    private TextView statusText;
    private ProgressBar throbber;

    /**
     * The developer payload that is sent with subsequent
     * purchase requests.
     */
    private String mPayloadContents = null;

    private static final int DIALOG_CANNOT_CONNECT_ID = 1;
    private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;
    

	public PaymentNotificationResponse mReceiver;

    /**
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
    private class BillingPurchaseObserver extends PurchaseObserver {
        public BillingPurchaseObserver(Handler handler) {
            super(Billing.this, handler);
        }

        @Override
        public void onBillingSupported(boolean supported) {
            if (Consts.DEBUG) {
                Log.i(TAG, "supported: " + supported);
            }
            if (!supported) {
                showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
            }
        }

        @Override
        public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
                int quantity, long purchaseTime, String developerPayload, String orderId) {
            if (Consts.DEBUG) {
                Log.i(TAG, "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
            }

    		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    		SharedPreferences accounts_preferences = getSharedPreferences("android2cloud-accounts", 0);
    		
    		String account_name = settings.getString("account", "");
    		OAuthAccount account = new OAuthAccount(account_name, accounts_preferences);
    		
    		Log.d("Billing", purchaseState.toString());
            if(purchaseState.equals(Consts.PurchaseState.PURCHASED)) {
                statusText.setText("Contacting server...");
        		Intent intent = new Intent();
        		intent.setComponent(new ComponentName("com.suchagit.android2cloud", "com.suchagit.android2cloud.HttpService"));
        		intent.setAction("com.suchagit.android2cloud.PaymentNotification");
        		intent.putExtra("com.suchagit.android2cloud.result_receiver", mReceiver);
        		intent.putExtra("com.suchagit.android2cloud.host", account.getHost());
        		intent.putExtra("com.suchagit.android2cloud.oauth_token", account.getToken());
        		intent.putExtra("com.suchagit.android2cloud.oauth_secret", account.getKey());
        		intent.putExtra("com.suchagit.android2cloud.item_id", itemId);
        		intent.putExtra("com.suchagit.android2cloud.order_number", orderId);
        		startService(intent);
            }
        }

        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                ResponseCode responseCode) {
            if (Consts.DEBUG) {
                Log.d(TAG, request.mProductId + ": " + responseCode);
            }
            if (responseCode == ResponseCode.RESULT_OK) {
                if (Consts.DEBUG) {
                    Log.i(TAG, "purchase was successfully sent to server");
                }
            } else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
                if (Consts.DEBUG) {
                    Log.i(TAG, "user canceled purchase");
                    statusText.setText("Payment cancelled.");
                    throbber.setVisibility(View.GONE);
                }
            } else {
                if (Consts.DEBUG) {
                    Log.i(TAG, "purchase failed");
                }
                statusText.setText("Payment failed. Please try again.");
                throbber.setVisibility(View.GONE);
            }
        }
    }

    private String mSku;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billing);
        mSku = "quota_immunity_day";

        mHandler = new Handler();
        mBillingPurchaseObserver = new BillingPurchaseObserver(mHandler);
        mBillingService = new BillingService();
        mBillingService.setContext(this);
        
        statusText = (TextView) findViewById(R.id.status);
        throbber = (ProgressBar) findViewById(R.id.throbber);

        // Check if billing is supported.
        ResponseHandler.register(mBillingPurchaseObserver);
        statusText.setText("Purchasing Quota Exemption");
        if (!mBillingService.checkBillingSupported()) {
            showDialog(DIALOG_CANNOT_CONNECT_ID);
        }
        if (!mBillingService.requestPurchase(mSku, mPayloadContents)) {
            showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
		mReceiver = new PaymentNotificationResponse(new Handler());
		mReceiver.setReceiver(this);
    }

	public void onPause() {
		super.onPause();
		mReceiver.setReceiver(null);
	}
	
    /**
     * Called when this activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        ResponseHandler.register(mBillingPurchaseObserver);
    }

    /**
     * Called when this activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
        ResponseHandler.unregister(mBillingPurchaseObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBillingService.unbind();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_CANNOT_CONNECT_ID:
    	    DialogFragment cannotConnectFragment = BillingCannotConnectDialogFragment.newInstance();
    	    cannotConnectFragment.show(getSupportFragmentManager(), "dialog");
        case DIALOG_BILLING_NOT_SUPPORTED_ID:
    	    DialogFragment unsupportedFragment = BillingNotSupportedDialogFragment.newInstance();
    	    unsupportedFragment.show(getSupportFragmentManager(), "dialog");
        default:
            return null;
        }
    }

	public void onReceiveResult(int resultCode, Bundle resultData) {
		statusText.setText("Purchase completed.");
		throbber.setVisibility(View.GONE);
		finish();
	}
}
