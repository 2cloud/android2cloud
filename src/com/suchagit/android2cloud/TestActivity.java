package com.suchagit.android2cloud;

import com.suchagit.android2cloud.R;
import com.suchagit.android2cloud.util.AddLinkRequest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;


public class TestActivity extends Activity {
	private boolean mIsBound;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
    	setContentView(R.layout.main);
    	final Button go = (Button) findViewById(R.id.go);
    	
    	go.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		doUnbindService();
	    		doBindService();
	    	}
	    });
	}
	
	private HttpService mBoundService;
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((HttpService.HttpServiceBinder)service).getService();
			AddLinkRequest req = new AddLinkRequest("https://2cloudapp.appspot.com/", "Web", "Android", "http://www.wired.com/");
			mBoundService.doRequest(PreferenceManager.getDefaultSharedPreferences(getBaseContext()), req);
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}};
	
    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(TestActivity.this, 
                HttpService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
}