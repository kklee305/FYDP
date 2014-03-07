package com.fydp.smarttouchassistant;

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

import com.fydp.service.BluetoothConnectionService;
import com.fydp.service.BluetoothConnectionService.LocalBinder;

public abstract class BaseBluetoothActivity extends Activity {

	BluetoothConnectionService btService;
	boolean isBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(this, BluetoothConnectionService.class);
		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
	}

	protected void init() {
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(mMessageReceiver, new IntentFilter("foregroundSwitch"));
		bluetoothBounded();
	}
	
	protected abstract void bluetoothBounded();

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

	//TODO rework this
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newForeground = intent.getStringExtra("foreground");
			Log.d("DEBUG", this.toString() + " received foreground switch request to " + newForeground);
			if (!newForeground.equals("mouse")) {
				Intent next = null;
				if (newForeground.equals("numpad")) {
					next = new Intent(context, NumpadActivity.class);
				} else if (newForeground.equals("controller")) {
					next = new Intent(context, ControllerActivity.class);
				} else if (newForeground.equals("macro")) {
					next = new Intent(context, MacroActivity.class);
				} else if (newForeground.equals("multimedia")) {
					next = new Intent(context, MultiMediaActivity.class);
				}
				startActivity(next);
				LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
			}
		}
	};


}
