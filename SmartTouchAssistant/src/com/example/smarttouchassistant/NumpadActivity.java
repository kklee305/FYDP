package com.example.smarttouchassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class NumpadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_numpad);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	public void inputNumber(View view) {
		String message = null;
		
    	switch (view.getId()) {
	    	case R.id.tap0: 
	    		message = "0";
	    		Log.d("DEBUG", "0 tapped");
	    		break;
	    	case R.id.tap1: 
	    		message = "1";
	    		Log.d("DEBUG", "1 tapped");
	    		break;
	    	case R.id.tap2:
	    		message = "2";
	    		Log.d("DEBUG", "2 tapped");
				break;
	    	case R.id.tap3: 
	    		message = "3";
	    		Log.d("DEBUG", "3 tapped");
				break;
	    	case R.id.tap4: 
	    		message = "4";
	    		Log.d("DEBUG", "4 tapped");
				break;
	    	case R.id.tap5: 
	    		message = "5";
	    		Log.d("DEBUG", "5 tapped");
				break;
	    	case R.id.tap6: 
	    		message = "6";
	    		Log.d("DEBUG", "6 tapped");
	    		break;
	    	case R.id.tap7:
	    		message = "7";
	    		Log.d("DEBUG", "7 tapped");
				break;
	    	case R.id.tap8: 
	    		message = "8";
	    		Log.d("DEBUG", "8 tapped");
				break;
	    	case R.id.tap9: 
	    		message = "9";
	    		Log.d("DEBUG", "9 tapped");
				break;
    	}	
    	BluetoothConnectionService.sendMessage(message);		 	
    }
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.numpad, menu);
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
