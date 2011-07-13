package com.suchagit.android2cloud.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class AddLinkResponse extends ResultReceiver {
    private Receiver mReceiver;

    public AddLinkResponse(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
    		Bundle newData = new Bundle();
        	if(resultCode == HttpClient.STATUS_COMPLETE) {
        		try {
					JSONObject json = new JSONObject(resultData.getString("raw_result"));
					newData.putInt("response_code", json.getInt("code"));
					newData.putString("link", json.getString("link"));
					newData.putString("raw_result", resultData.getString("raw_result"));
				} catch (JSONException e) {
					newData.putInt("response_code", 500);
					newData.putString("type", "client_error");
				}
        	}
    		mReceiver.onReceiveResult(resultCode, newData);
        }
    }
}
