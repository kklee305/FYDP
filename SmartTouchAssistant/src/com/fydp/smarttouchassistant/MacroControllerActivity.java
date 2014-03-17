package com.fydp.smarttouchassistant;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fydp.service.BluetoothConnectionService;
import com.fydp.service.BluetoothConnectionService.LocalBinder;

public class MacroControllerActivity extends Activity {
	private static final String MACRO_HEADER = "macro#";
	BluetoothConnectionService btService;
    boolean isBound = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_macro_controller);
		
		Intent serviceIntent = new Intent(this, BluetoothConnectionService.class);
        bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE); 
		
		// Get the message from the intent
		Intent intent = getIntent();
		LinearLayout layout = (LinearLayout) findViewById(R.id.macroLayout);
		
		int buttonYPosition = 0;
		for(String s: MacroActivity.SHORTCUTS) {
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

	
	//Bind service to BT connection
	private ServiceConnection myConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
	        LocalBinder binder = (LocalBinder) service;
	        btService = binder.getService();
	        isBound = true;
	    }
	    
	    public void onServiceDisconnected(ComponentName arg0) {
	        isBound = false;
	    }    	    
	};

}
