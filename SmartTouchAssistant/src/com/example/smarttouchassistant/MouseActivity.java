package com.example.smarttouchassistant;

import com.example.smarttouchassistant.BluetoothConnectionService.LocalBinder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

public class MouseActivity extends Activity{
	
	private TextView textview5, textScrollx, textScrolly;	
	private GestureDetectorCompat mDetector;
	BluetoothConnectionService btService;
    boolean isBound = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mouse);
		Intent intent = new Intent(this, BluetoothConnectionService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);   
		// Show the Up button in the action bar.
		setupActionBar();
		
		mDetector = new GestureDetectorCompat(getBaseContext(),mGestureListener);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		LocalBroadcastManager.getInstance(this).registerReceiver(
	            mMessageReceiver, new IntentFilter("foregroundSwitch"));
		
        textview5 = (TextView) findViewById(R.id.textView5);
        textScrollx = (TextView) findViewById(R.id.textScrollx);
        textScrolly = (TextView) findViewById(R.id.textScrolly);
	}
	//Bind service to BT connection	
	private ServiceConnection myConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
	        LocalBinder binder = (LocalBinder) service;
	        btService = binder.getService();
	        isBound = true;
	    }
	    
	    public void onServiceDisconnected(ComponentName arg0) {
	        isBound = false;
	    }    	    
	};
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String newForeground = intent.getStringExtra("foreground");
	        Log.d("DEBUG", this.toString() + " received foreground switch request to " + newForeground);
	        if(!newForeground.equals("mouse")) {
	        	Intent next = null;
		        if(newForeground.equals("numpad")) {
		        	next = new Intent(context, NumpadActivity.class);
		        } else if(newForeground.equals("controller")) {
		        	next = new Intent(context, ControllerActivity.class);
		        } else if(newForeground.equals("macro")) {
		        	next = new Intent(context, MacroActivity.class);
		        } else if(newForeground.equals("multimedia")) {
		        	next = new Intent(context, MultiMediaActivity.class);
		        }		        
		        startActivity(next);
		        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
	        }	        
	    }
	};
	

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	

	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first

	    Log.d("DEBUG", "Mouse paused");
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

	    Log.d("DEBUG", "Mouse resumed");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mouse, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	
	public boolean onTouchEvent(MotionEvent event){ 
    	boolean retVal = mDetector.onTouchEvent(event);
        return retVal || super.onTouchEvent(event);    
    }
    
    private final GestureDetector.SimpleOnGestureListener mGestureListener
    = new GestureDetector.SimpleOnGestureListener() {
    	
        @Override
        public boolean onDoubleTap(MotionEvent e) {
        	textview5.setText("Double Click");
        	btService.sendMessage("Double Click");
            return true;
        }
        
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
        	textview5.setText("Single Click");
        	btService.sendMessage("Single Click");
            return true;
        }        


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        	textScrollx.setText("x: "+ String.valueOf(distanceX));
        	textScrolly.setText("y: "+ String.valueOf(distanceY));
        	btService.sendMessage(String.valueOf(distanceX));
        	btService.sendMessage(String.valueOf(distanceY));
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    };   
    

}
