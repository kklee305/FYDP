package com.fydp.smarttouchassistant;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fydp.service.BluetoothConnectionService;
import com.fydp.service.BluetoothConnectionService.LocalBinder;

public class MacroControllerActivity extends Activity {
	private static final String MACRO_HEADER = "macro#";
	BluetoothConnectionService btService;
	boolean isBound = false;
	private TextView profileTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_macro_controller);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = new Intent(this, BluetoothConnectionService.class);
		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unbindService(myConnection);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				mMessageReceiver);
	}

	private void init() {
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(mMessageReceiver, new IntentFilter("foregroundSwitch"));
		bluetoothBounded();
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newForeground = intent.getStringExtra("foreground");
			Log.d("MacroControllerActivity", this.toString() + " received foreground switch request to " + newForeground);
			checkProfile(newForeground);
		}
	};

	// Bind service to BT connection
	protected ServiceConnection myConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			btService = binder.getService();
			isBound = true;
			Log.d("", "Service is bound");
			init();
		}

		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}
	};

	protected void bluetoothBounded() {
		// Get the message from the intent
		Intent intent = getIntent();
		
		profileTitle = (TextView) findViewById(R.id.profile_title);
		profileTitle.setText(intent.getStringExtra("profileTitle"));
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.macroLayout);

		int buttonYPosition = 0;
		for (String s : MacroActivity.SHORTCUTS) {
			String intentMessage = intent.getStringExtra(s);
			final String message = intentMessage.replaceAll("[+]", "&");
			Log.d("DEBUG", "replaced " + intentMessage + " with " + message);
			Button b = new Button(this);

			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btService.sendMessage(MACRO_HEADER + message);
				}
			});

			b.setText(intentMessage);
			b.setWidth(400);
			b.setHeight(40);
			b.setX(0);
			b.setY(buttonYPosition);
			layout.addView(b);
			buttonYPosition += 50;
		}
	}
	
	public void checkProfile(String newForeground) {
		JSONArray profiles = null;
		JSONObject macroSaveFile = null;
		
		try {
			macroSaveFile = new JSONObject(readFile());
			profiles = macroSaveFile.getJSONArray("profiles");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < profiles.length(); i++) {
			try {
				if (profiles.getJSONObject(i).getString("profile").equalsIgnoreCase(newForeground)) {
					swapProfile(profiles.getJSONObject(i).getJSONArray("macros"));
					profileTitle.setText(profiles.getJSONObject(i).getString("profile"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void swapProfile(final JSONArray macros) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.macroLayout);
		layout.removeAllViews();
		
		int buttonYPosition = 0;
		if (macros != null && macros.length() == 4) {
			for (int i = 0; i < macros.length(); i++) {
				try {
					Button b = new Button(this);
					final String macroString = macros.getString(i);
					b.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							btService.sendMessage(MACRO_HEADER + macroString);
						}
					});
					
					b.setText(macros.getString(i));
					b.setWidth(400);
					b.setHeight(40);
					b.setX(0);
					b.setY(buttonYPosition);
					layout.addView(b);
					buttonYPosition += 50;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String readFile() {
		String temp = "";
		try {
			FileInputStream inputStream = openFileInput("macroSave.txt");
			BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			r.close();
			inputStream.close();
			temp = "" + total;
		} catch (Exception e) {
			Log.d("Exception", "File read failed: " + e.toString());
			e.printStackTrace();
		}

		if (temp.isEmpty()) {
			JSONObject newMacroFile = new JSONObject();
			JSONArray profiles = new JSONArray();
			try {
				newMacroFile.put("profiles", profiles);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			temp = newMacroFile.toString();
		}

		return temp;
	}

}
