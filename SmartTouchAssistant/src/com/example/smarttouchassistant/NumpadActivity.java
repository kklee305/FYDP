package com.example.smarttouchassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
		Context context = getApplicationContext();
		CharSequence text = "";
		int duration = Toast.LENGTH_SHORT;
    	switch (view.getId()) {
	    	case R.id.tap0: text = "0 tapped";
	    					break;
	    	case R.id.tap1: text = "1 tapped";
	    					break;
	    	case R.id.tap2: text = "2 tapped";
							break;
	    	case R.id.tap3: text = "3 tapped";
							break;
	    	case R.id.tap4: text = "4 tapped";
							break;
	    	case R.id.tap5: text = "5 tapped";
							break;
	    	case R.id.tap6: text = "6 tapped";
	    					break;
	    	case R.id.tap7: text = "7 tapped";
							break;
	    	case R.id.tap8: text = "8 tapped";
							break;
	    	case R.id.tap9: text = "9 tapped";
							break;
    	}	
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();    	
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
