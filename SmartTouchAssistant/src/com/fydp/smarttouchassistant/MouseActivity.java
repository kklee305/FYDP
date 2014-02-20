package com.fydp.smarttouchassistant;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.fydp.smarttouchassistant.listeners.MouseGenstureListener;

public class MouseActivity extends BaseBluetoothActivity {
	private RelativeLayout mouseLayout;
	private GestureDetectorCompat mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mouse);
		// Show the Up button in the action bar.
		// setupActionBar();
	}
	
	@Override
	protected void bluetoothBounded() {
		mouseLayout = (RelativeLayout) findViewById(R.id.mouse_layout);
		MouseGenstureListener gestureListener = new MouseGenstureListener(btService, mouseLayout);
		mDetector = new GestureDetectorCompat(getBaseContext(), gestureListener);
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first
		Log.d("DEBUG", "Mouse resumed");
	}
	
	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first
		Log.d("DEBUG", "Mouse paused");
		unbindService(super.myConnection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mouse, menu);
		return true;
	}

	// /**
	// * Set up the {@link android.app.ActionBar}.
	// */
	// private void setupActionBar() {
	//
	// getActionBar().setDisplayHomeAsUpEnabled(true);
	//
	// }

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

	public boolean onTouchEvent(MotionEvent event) {
		boolean retVal = mDetector.onTouchEvent(event);
		return retVal || super.onTouchEvent(event);
	}

}
