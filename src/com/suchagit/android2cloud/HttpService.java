package com.suchagit.android2cloud;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpRequestBase;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.suchagit.android2cloud.util.AddLinkRequest;
import com.suchagit.android2cloud.util.CheckTimeRequest;
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
			String oauth_token = intent.getStringExtra("com.suchagit.android2cloud.oauth_token");
			String oauth_secret = intent.getStringExtra("com.suchagit.android2cloud.oauth_secret");
			client = new HttpClient(oauth_token, oauth_secret);
		}
		String requestType = intent.getAction();
		String host = intent.getStringExtra("com.suchagit.android2cloud.host");
		final ResultReceiver result = intent.getParcelableExtra("com.suchagit.android2cloud.result_receiver");
		HttpRequestBase request = null;
		Bundle b = new Bundle();
		try {
			if(requestType.equals("AddLink")) {
				String link = intent.getStringExtra("com.suchagit.android2cloud.link");
				String receiver = intent.getStringExtra("com.suchagit.android2cloud.receiver");
				String sender = intent.getStringExtra("com.suchagit.android2cloud.sender");
				request = new AddLinkRequest(host, receiver, sender, link);
			} else if (requestType.equals("PaymentNotification")) {
				String itemId = intent.getStringExtra("com.suchagit.android2cloud.item_id");
				String orderNumber = intent.getStringExtra("com.suchagit.android2cloud.order_number");
				request = new PaymentNotificationRequest(host, orderNumber, itemId);
			} else if (requestType.equals("CheckTime")) {
				request = new CheckTimeRequest(host);
			}
			String response = client.exec(request);
			b.putString("raw_result", response);
			result.send(HttpClient.STATUS_COMPLETE, b);
		} catch (UnsupportedEncodingException e) {
			b.putInt("response_code", 600);
			b.putString("type", "unsupported_encoding_exception_error");
			result.send(HttpClient.STATUS_ERROR, b);
		} catch (IllegalStateException e) {
			b.putInt("response_code", 600);
			b.putString("type", "illegal_state_exception_error");
			b.putString("request_type", requestType);
			if(request != null)
				b.putString("host", request.getURI().getHost());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			b.putString("stacktrace", sw.toString());
			result.send(HttpClient.STATUS_ERROR, b);
		} catch (IllegalArgumentException e) {
			b.putInt("response_code", 600);
			b.putString("type", "illegal_argument_exception_error");
			b.putString("request_type", requestType);
			if(request != null)
				b.putString("host", request.getURI().getHost());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			b.putString("stacktrace", sw.toString());
			result.send(HttpClient.STATUS_ERROR, b);
		}
	}
}