package com.example.smarttouchassistant;

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
        
        final Intent nextActivityIntent = new Intent(this, DisplayOptionsActivity.class);
        
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
            	btService.connectToDevice(deviceNames.get(deviceNumber),deviceAddresses.get(deviceNumber));
        		startActivity(nextActivityIntent);  
	        }
        });
         
      
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
