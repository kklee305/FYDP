package com.example.smarttouchassistant;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;

public class MultiMediaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_media);
		// Show the Up button in the action bar.
		setupActionBar();
		
		LocalBroadcastManager.getInstance(this).registerReceiver(
	            mMessageReceiver, new IntentFilter("foregroundSwitch"));
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String newForeground = intent.getStringExtra("foreground");
	        Log.d("DEBUG", this.toString() + " received foreground switch request to " + newForeground);
	        if(!newForeground.equals("multimedia")) {
	        	Intent next = null;
		        if(newForeground.equals("mouse")) {
		        	next = new Intent(getApplicationContext(), MouseActivity.class);
		        } else if(newForeground.equals("controller")) {
		        	next = new Intent(getApplicationContext(), ControllerActivity.class);
		        } else if(newForeground.equals("macro")) {
		        	next = new Intent(getApplicationContext(), MacroActivity.class);
		        } else if(newForeground.equals("numpad")) {
		        	next = new Intent(getApplicationContext(), NumpadActivity.class);
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
	
	public void inputCommand(View view) {
		Context context = getApplicationContext();
		CharSequence text = "";
		int duration = Toast.LENGTH_SHORT;
    	switch (view.getId()) {
	    	case R.id.rewind: text = "Rewind";
	    					break;
	    	case R.id.play: text = "Play/Pause";
	    					break;
	    	case R.id.forward: text = "Forward";
							break;
	    	case R.id.volumeUp: text = "+";
							break;
	    	case R.id.volumeDown: text = "-";
							break;
    	}	
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();    	
    }

	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first

	    Log.d("DEBUG", "Multimedia paused");
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

	    Log.d("DEBUG", "Multimedia resumed");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.multi_media, menu);
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
