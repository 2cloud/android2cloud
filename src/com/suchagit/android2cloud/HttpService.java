package com.suchagit.android2cloud;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicResponseHandler;

import com.suchagit.android2cloud.util.HttpClient;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;


public class HttpService extends Service {
	
	final Handler mHandler = new Handler();
	
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateResults();
		}
	};
	
	public void updateResults() {
	}
	
	protected void makeHttpRequest() {
		Thread t = new Thread() {
			public void run() {
				
			}
		};
		t.start();
	}
	
	private final IBinder mBinder = new HttpServiceBinder();
	private String response = "";

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public class HttpServiceBinder extends Binder {
		HttpService getService() {
			return HttpService.this;
		}
	}
	
	private HttpClient client;
	
	@Override
	public void onCreate() {
	}
	
	public void doRequest(SharedPreferences preferences, HttpRequestBase request) {
		if(client == null) {
			client = new HttpClient(preferences);
		}
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		response = client.exec(request, responseHandler);
		Toast.makeText(this, response, Toast.LENGTH_LONG).show();
	}
}