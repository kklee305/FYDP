package com.fydp.smarttouchassistant.listeners;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fydp.service.BluetoothConnectionService;
import com.fydp.smarttouchassistant.R;

public class MouseGenstureListener extends SimpleOnGestureListener {

	BluetoothConnectionService btService;
	private static final String MOUSE_HEADER = "mouse#";
	private RelativeLayout parentLayout;
	private TextView clickTypeView, scrollXView, scrollYView;

	public MouseGenstureListener(BluetoothConnectionService btService, RelativeLayout parentLayout) {
		this.btService = btService;
		this.parentLayout = parentLayout;
		
		if (btService == null) {
			Log.e("Mouse Gesture Listener", "Error btService is null");
		}

		initUI();
	}

	private void initUI() {
		clickTypeView = (TextView) parentLayout.findViewById(R.id.click_type);
		scrollXView = (TextView) parentLayout.findViewById(R.id.textScrollx);
		scrollYView = (TextView) parentLayout.findViewById(R.id.textScrolly);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		clickTypeView.setText("Double Click");
		btService.sendMessage(MOUSE_HEADER + "DoubleClick");
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		clickTypeView.setText("Single Click");
		btService.sendMessage(MOUSE_HEADER + "SingleClick");
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		scrollXView.setText("x: " + String.valueOf(distanceX));
		scrollYView.setText("y: " + String.valueOf(distanceY));
		clickTypeView.setText("Mouse Scroll");
		btService.sendMessage(MOUSE_HEADER + String.valueOf(distanceX) + "/" + String.valueOf(distanceY));
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return true;
	}
	
	public void onDestroy() {
		btService = null;
	}
}
