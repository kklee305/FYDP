package com.fydp.smarttouchassistant;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.fydp.smarttouchassistant.listeners.ViewOnTouchListener;

public class ControllerActivity extends BaseBluetoothActivity {
	private static final String CONTROLLER_HEADER = "controller#";

	public static enum ButtonType {
		UP, DOWN, LEFT, RIGHT, X, Y, A, B
	};

	public static enum ButtonEvent {
		PRESSED, RELEASED
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	protected void bluetoothBounded() {
		sendMessage("open#");
		initButtonUI();
	}

	private void initButtonUI() {
		Button dpadUp = (Button) findViewById(R.id.dpad_up);
		Button dpadDown = (Button) findViewById(R.id.dpad_down);
		Button dpadLeft = (Button) findViewById(R.id.dpad_left);
		Button dpadRight = (Button) findViewById(R.id.dpad_right);
		Button actionA = (Button) findViewById(R.id.action_pad_a);
		Button actionB = (Button) findViewById(R.id.action_pad_b);
		Button actionX = (Button) findViewById(R.id.action_pad_x);
		Button actionY = (Button) findViewById(R.id.action_pad_y);

		dpadUp.setOnTouchListener(new ViewOnTouchListener(ButtonType.UP, this));
		dpadDown.setOnTouchListener(new ViewOnTouchListener(ButtonType.DOWN,
				this));
		dpadLeft.setOnTouchListener(new ViewOnTouchListener(ButtonType.LEFT,
				this));
		dpadRight.setOnTouchListener(new ViewOnTouchListener(ButtonType.RIGHT,
				this));
		actionA.setOnTouchListener(new ViewOnTouchListener(ButtonType.A, this));
		actionB.setOnTouchListener(new ViewOnTouchListener(ButtonType.B, this));
		actionX.setOnTouchListener(new ViewOnTouchListener(ButtonType.X, this));
		actionY.setOnTouchListener(new ViewOnTouchListener(ButtonType.Y, this));

		Button extraButtons = (Button) findViewById(R.id.controller_extra);
		final Context activity = this;
		extraButtons.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(activity);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.controller_extra_buttons);

				Button startButton = (Button) dialog
						.findViewById(R.id.controller_start);
				startButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						sendMessage("start");
					}
				});

				Button optionsButton = (Button) dialog
						.findViewById(R.id.controller_options);
				optionsButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						sendMessage("options");
					}
				});

				dialog.show();

			}
		});
	}

	public void sendMessage(String message) {
		Log.d("Controller Messages", CONTROLLER_HEADER + message.toLowerCase());
		if (isBound) {
			btService.sendMessage(CONTROLLER_HEADER + message.toLowerCase());
		}
	}

	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first
		Log.d("DEBUG", "Controller paused");
		sendMessage("close#");
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first
		Log.d("DEBUG", "Controller resumed");
	}

}
