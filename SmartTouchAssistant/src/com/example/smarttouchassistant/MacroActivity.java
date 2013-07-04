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
import android.view.View;
import android.widget.EditText;

public class MacroActivity extends Activity {

	public final static String[] SHORTCUTS = {"com.example.smarttouchassistant.SHORCUT1", "com.example.smarttouchassistant.SHORCUT2",
		"com.example.smarttouchassistant.SHORCUT3", "com.example.smarttouchassistant.SHORCUT4"};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_macro);
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
	        if(!newForeground.equals("macro")) {
	        	Intent next = null;
		        if(newForeground.equals("mouse")) {
		        	next = new Intent(context, MouseActivity.class);
		        } else if(newForeground.equals("controller")) {
		        	next = new Intent(context, ControllerActivity.class);
		        } else if(newForeground.equals("numpad")) {
		        	next = new Intent(context, NumpadActivity.class);
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
	
    public void confirmMacros(View view) {
    	Intent intent = new Intent(this, MacroControllerActivity.class);
    	int[] ids = {R.id.shortcut1, R.id.shortcut2, R.id.shortcut3, R.id.shortcut4};
    	
    	for(int i = 0; i < ids.length; i++) {
    		EditText editText = (EditText) findViewById(ids[i]);
    		String message = editText.getText().toString();
    		intent.putExtra(SHORTCUTS[i], message);
    	}

    	startActivity(intent);
    }    
    
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first

	    Log.d("DEBUG", "Macro paused");
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

	    Log.d("DEBUG", "Macro resumed");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.macro, menu);
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
