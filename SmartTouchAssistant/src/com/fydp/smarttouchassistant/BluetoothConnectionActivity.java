package com.fydp.smarttouchassistant;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.fydp.service.BluetoothConnectionService;
import com.fydp.service.BluetoothConnectionService.LocalBinder;

public class BluetoothConnectionActivity extends Activity {

    BluetoothConnectionService btService;
    boolean isBound = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_connection);
		Intent intent = new Intent(this, BluetoothConnectionService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
        
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        while(!mBluetoothAdapter.isEnabled()) { };
        
        final ArrayList<String> deviceNames= new ArrayList<String>();       
        final ArrayList<String> deviceAddresses = new ArrayList<String>();
        
        for(BluetoothDevice bd: mBluetoothAdapter.getBondedDevices()) {
        	deviceNames.add(bd.getName());
        	deviceAddresses.add(bd.getAddress());
        }
        final List<String> list= new ArrayList<String>(deviceNames);

        
        Button b=(Button) findViewById(R.id.startBluetooth);
        final RelativeLayout bluetoothSelectionLayout = (RelativeLayout) findViewById(R.id.bluetoothSelectorLayout);
               
        final Spinner selectorSpinner = new Spinner(BluetoothConnectionActivity.this);
        selectorSpinner.setBackgroundColor(getResources().getColor(R.color.darkwhite));
        
        final ArrayAdapter<String> adp= new ArrayAdapter<String>(this,
                                        android.R.layout.simple_list_item_1,list);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        selectorSpinner.setAdapter(adp);
        bluetoothSelectionLayout.addView(selectorSpinner); 
        
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deviceNumber = selectorSpinner.getSelectedItemPosition();
                btService.connectToDevice(deviceNames.get(deviceNumber), deviceAddresses.get(deviceNumber));
                findViewById(R.id.bb_connection_spinner).setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);
            }
        });
	}	
	
	@Override
	protected void onPause() {
		super.onPause();
		findViewById(R.id.bb_connection_spinner).setVisibility(View.GONE);
		findViewById(R.id.startBluetooth).setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onStop() {
//		unbindService(myConnection);
		super.onStop();
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
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bluetooth_connection, menu);
		return true;
	}

}
