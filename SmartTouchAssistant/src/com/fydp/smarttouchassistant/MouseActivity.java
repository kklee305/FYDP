package com.fydp.smarttouchassistant;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
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
	}

	@Override
	protected void bluetoothBounded() {
		mouseLayout = (RelativeLayout) findViewById(R.id.mouse_background);
		MouseGenstureListener gestureListener = new MouseGenstureListener(
				btService, mouseLayout);
		mDetector = new GestureDetectorCompat(getBaseContext(), gestureListener);
	}

	public boolean onTouchEvent(MotionEvent event) {
		boolean retVal = mDetector.onTouchEvent(event);
		return retVal || super.onTouchEvent(event);
	}

}
