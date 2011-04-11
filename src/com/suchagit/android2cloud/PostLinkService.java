package com.suchagit.android2cloud;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PostLinkService extends Service {
    private static final int START_STICKY = 0;
	//private NotificationManager mNM;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        PostLinkService getService() {
            return PostLinkService.this;
        }
    }

    @Override
    public void onCreate() {
        //mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        //showNotification();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        //mNM.cancel(R.string.postlinkservice_started);

        // Tell the user we stopped.
        //Toast.makeText(this, R.string.postlinkservice_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    //private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        //CharSequence text = getText(R.string.postlinkservice_started);

        // Set the icon, scrolling text and timestamp
        //Notification notification = new Notification(R.drawable.icon, text,
        //        System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        //        new Intent(this, PostLink.class), 0);

        // Set the info for the views that show in the notification panel.
        //notification.setLatestEventInfo(this, getText(R.string.app_name),
        //               text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        //mNM.notify(R.string.postlinkservice_started, notification);
    //}
    
    public void sendLink(String link, String device, OAuthConsumer consumer, OAuthProvider provider, SharedPreferences preferences){
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider
    	String consumer_key = getResources().getString(R.string.consumer_key);
    	String consumer_secret = getResources().getString(R.string.consumer_secret);
    	if(!preferences.getString("host", "error").equals("http://android2cloud.appspot.com")){
    		consumer_key = "anonymous";
    		consumer_secret = "anonymous";
    	}
		Log.i("android2cloud", "PostLinkService(119) consumer_key: "+consumer_key);
		Log.i("android2cloud", "PostLinkService(120) consumer_secret: "+consumer_secret);
		consumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
        consumer.setTokenWithSecret(preferences.getString("token", "error"), preferences.getString("secret", "error"));
        // create an HTTP request to a protected resource
        String target = preferences.getString("host", "error")+"/addlink";
		Log.i("android2cloud", "PostLinkService(125) target: "+target);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("link", link));
        formparams.add(new BasicNameValuePair("name", preferences.getString("deviceName", "Android")));
        formparams.add(new BasicNameValuePair("recipient", device));
        UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			Toast.makeText(this, "There was an error with your URL. Please report it on the project page.", Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(133) e1.getMessage(): "+e1.getMessage());
		}
		HttpPost request = new HttpPost(target);
		request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(entity);

    	HttpClient client = new DefaultHttpClient();
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	
        // sign the request
        try {
			consumer.sign(request);
		} catch (OAuthMessageSignerException e) {
			Toast.makeText(this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthMessageSignerException' to the project page.", Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(147) e.getMessage(): "+e.getMessage());
		} catch (OAuthExpectationFailedException e) {
			Toast.makeText(this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthExpectationFailedException' to the project page.", Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(150) e.getMessage(): "+e.getMessage());
		} catch (OAuthCommunicationException e) {
			Toast.makeText(this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthCommunicationException' to the project page.", Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(153) e.getMessage(): "+e.getMessage());
		}

		String returnString = "";
        // send the request
        try {
        	String response = client.execute(request, responseHandler);
			returnString = response;
		} catch (ClientProtocolException e){
			Toast.makeText(this, "There was an error sending your request Please report this: " + e.getMessage(), Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(163) e.getMessage(): "+e.getMessage());
		} catch (IOException e) {
			Toast.makeText(this, "There was an error sending your request. Please report this: " + e.getMessage(), Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(166) e.getMessage(): "+e.getMessage());
		}
		Toast.makeText(this, returnString, Toast.LENGTH_LONG).show();
		Log.i("android2cloud", "PostLinkService(170) returnString: "+returnString);
    }
    
    public void sendCredit(String payKey, boolean anonymous, String name, String link, OAuthConsumer consumer, OAuthProvider provider, SharedPreferences preferences){
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider
    	String consumer_key = getResources().getString(R.string.consumer_key);
    	String consumer_secret = getResources().getString(R.string.consumer_secret);
    	if(!preferences.getString("host", "error").equals("http://android2cloud.appspot.com")){
    		consumer_key = "anonymous";
    		consumer_secret = "anonymous";
    	}
		Log.i("android2cloud", "PostLinkService(119) consumer_key: "+consumer_key);
		Log.i("android2cloud", "PostLinkService(120) consumer_secret: "+consumer_secret);
		consumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
        consumer.setTokenWithSecret(preferences.getString("token", "error"), preferences.getString("secret", "error"));
        // create an HTTP request to a protected resource
        String target = preferences.getString("host", "error")+"/donations/add";
		Log.i("android2cloud", "PostLinkService(125) target: "+target);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("link", link));
        formparams.add(new BasicNameValuePair("anonymous", anonymous+""));
        formparams.add(new BasicNameValuePair("name", name));
        Log.i("android2cloud", payKey);
        formparams.add(new BasicNameValuePair("payKey", payKey));
        UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			Toast.makeText(this, "There was an error with your URL. Please report it on the project page.", Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(133) e1.getMessage(): "+e1.getMessage());
		}
		HttpPost request = new HttpPost(target);
		request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(entity);

    	HttpClient client = new DefaultHttpClient();
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	
        // sign the request
        try {
			consumer.sign(request);
		} catch (OAuthMessageSignerException e) {
			Toast.makeText(this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthMessageSignerException' to the project page.", Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(147) e.getMessage(): "+e.getMessage());
		} catch (OAuthExpectationFailedException e) {
			Toast.makeText(this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthExpectationFailedException' to the project page.", Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(150) e.getMessage(): "+e.getMessage());
		} catch (OAuthCommunicationException e) {
			Toast.makeText(this, "There was an error sending your request. Please remove and re-add your account, and report the error 'OAuthCommunicationException' to the project page.", Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(153) e.getMessage(): "+e.getMessage());
		}

		String returnString = "";
        // send the request
        try {
        	String response = client.execute(request, responseHandler);
			returnString = response;
		} catch (ClientProtocolException e){
			Toast.makeText(this, "There was an error sending your request Please report this: " + e.getMessage(), Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(163) e.getMessage(): "+e.getMessage());
		} catch (IOException e) {
			Toast.makeText(this, "There was an error sending your request. Please report this: " + e.getMessage(), Toast.LENGTH_LONG).show();
			Log.i("android2cloud", "PostLinkService(166) e.getMessage(): "+e.getMessage());
		}
		Toast.makeText(this, returnString, Toast.LENGTH_LONG).show();
		Log.i("android2cloud", "PostLinkService(170) returnString: "+returnString);
    }
}