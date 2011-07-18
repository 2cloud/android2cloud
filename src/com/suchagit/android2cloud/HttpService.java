package com.suchagit.android2cloud;

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
		Bundle b = new Bundle();
		String response = client.exec(request);
		b.putString("raw_result", response);
		result.send(HttpClient.STATUS_COMPLETE, b);
	}
}