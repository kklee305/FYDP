package com.fydp.smarttouchassistant.listeners;

import android.view.MotionEvent;
import android.view.View;

import com.fydp.smarttouchassistant.ControllerActivity;
import com.fydp.smarttouchassistant.ControllerActivity.ButtonEvent;
import com.fydp.smarttouchassistant.ControllerActivity.ButtonType;

public class ViewOnTouchListener implements View.OnTouchListener {

	private ButtonType type;
	private ControllerActivity controller;
	private final String stateTag = "#";

	public ViewOnTouchListener(ButtonType type, ControllerActivity controller) {
		this.type = type;
		this.controller = controller;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {

	    case MotionEvent.ACTION_DOWN:
	        v.setPressed(true);
	        controller.sendMessage(type.toString()+stateTag+ButtonEvent.PRESSED);
	        break;
	    case MotionEvent.ACTION_UP:
	    case MotionEvent.ACTION_OUTSIDE:
	        v.setPressed(false);
	        controller.sendMessage(type.toString()+stateTag+ButtonEvent.RELEASED);
	        break;
	    default:
	        break;
	    }

	    return true;
	}

}
