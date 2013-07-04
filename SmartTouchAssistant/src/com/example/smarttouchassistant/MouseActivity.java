package com.example.smarttouchassistant;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class MouseActivity extends Activity {

	private static int oldX;
	private static int oldY;
	private static int xChange;
	private static int yChange;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mouse);
		// Show the Up button in the action bar.
		setupActionBar();
		
		oldX = -1;
		oldY = -1;
		xChange = -1;
		yChange = -1;

		LocalBroadcastManager.getInstance(this).registerReceiver(
	            mMessageReceiver, new IntentFilter("foregroundSwitch"));
	}
	
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
	public boolean onTouchEvent(MotionEvent event){ 

	    String DEBUG_TAG = "DEBUG_TAG";
	    int newX = Math.round(event.getX() * event.getXPrecision());
	    int newY = Math.round(event.getY() * event.getYPrecision());
	    
	    if(oldY < 0 || oldX < 0) { // new instance, reset history
	    	oldX = newX;
	    	oldY = newY;
	    }
	    
	    xChange = newX - oldX;
	    yChange = newY - oldY;
	    
	    oldY = newY;
	    oldX = newX;
	    
    	Log.d(DEBUG_TAG, "X DIFF: " + Float.toString(xChange));
    	Log.d(DEBUG_TAG, "Y DIFF: " + Float.toString(yChange));
    	return true;   
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

}
