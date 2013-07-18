package com.example.smarttouchassistant;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;

import com.example.smarttouchassistant.BluetoothConnectionService.LocalBinder;

public class BluetoothConnectionActivity extends Activity {

    BluetoothConnectionService btService;
    boolean isBound = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_connection);
		Intent intent = new Intent(this, BluetoothConnectionService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);    
      
	}	
	
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
	   
	public void startLeon(View view) {
		Intent intent = new Intent(this, DisplayOptionsActivity.class);    	
    	btService.connectToDevice("LEONZHANG-MSI", "40:61:86:42:38:14");
		startActivity(intent);
	}
	
	public void startChris(View view) {
		Intent intent = new Intent(this, DisplayOptionsActivity.class);    	
    	btService.connectToDevice("CHRIS-PC", "00:26:83:14:69:43");
		startActivity(intent);
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bluetooth_connection, menu);
		return true;
	}
	
	
	

}
