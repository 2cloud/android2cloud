package com.suchagit.android2cloud;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpRequestBase;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.suchagit.android2cloud.util.AddLinkRequest;
import com.suchagit.android2cloud.util.HttpClient;
import com.suchagit.android2cloud.util.PaymentNotificationRequest;


public class HttpService extends IntentService {
	
	public HttpService() {
		super("HttpService");
	}
	
	private HttpClient client;

	@Override
	protected void onHandleIntent(Intent intent) {
		if(client == null) {
			String oauth_token = intent.getStringExtra("oauth_token");
			String oauth_secret = intent.getStringExtra("oauth_secret");
			client = new HttpClient(oauth_token, oauth_secret);
		}
		String requestType = intent.getAction();
		String host = intent.getStringExtra("host");
		final ResultReceiver result = intent.getParcelableExtra("result_receiver");
		HttpRequestBase request = null;
		Bundle b = new Bundle();
		try {
			if(requestType.equals("AddLink")) {
				String link = intent.getStringExtra("link");
				String receiver = intent.getStringExtra("receiver");
				String sender = intent.getStringExtra("sender");
				request = new AddLinkRequest(host, receiver, sender, link);
			} else if (requestType.equals("PaymentNotification")) {
				String itemId = intent.getStringExtra("item_id");
				String orderNumber = intent.getStringExtra("order_number");
				request = new PaymentNotificationRequest(host, orderNumber, itemId);
			}
			String response = client.exec(request);
			b.putString("raw_result", response);
			result.send(HttpClient.STATUS_COMPLETE, b);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			// need to build and return an error object here
			b.putInt("response_code", 600);
			b.putString("type", "unsupported_encoding_exception_error");
			result.send(HttpClient.STATUS_ERROR, b);
		}
	}
}