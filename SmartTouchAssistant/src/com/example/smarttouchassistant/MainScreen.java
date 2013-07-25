package com.example.smarttouchassistant;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainScreen extends Activity {
	
    BluetoothConnectionService btService;
    boolean isBound = false;
    
	public final static String EXTRA_MESSAGE = "com.example.smarttouchassistant.MESSAGE";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);    
       
    }
    
	
	   
    public void chooseBluetooth(View view) {
    	//ready the intent to go to next activity
    	final Intent intent = new Intent(this, BluetoothConnectionActivity.class); 
    	
    	Context context = getApplicationContext();
    	CharSequence toastText = "";
    	int duration = Toast.LENGTH_SHORT;

    	
    	
    	final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if (mBluetoothAdapter == null) {
    		toastText = "This device does not support BlueTooth!";
    	    Toast toast = Toast.makeText(context, toastText, duration);
        	toast.show();
    	} else {
    	    if (!mBluetoothAdapter.isEnabled()) {
    	    	// custom dialog
    			final Dialog dialog = new Dialog(this);
    			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    			dialog.setContentView(R.layout.dialog_bluetooth_turned_on);
     
    			Button dialogButtonYes = (Button) dialog.findViewById(R.id.bluetoothButtonOK);
    			// if button is clicked, close the custom dialog
    			dialogButtonYes.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog.dismiss();
    					mBluetoothAdapter.enable();    					
    	            	startActivity(intent);	
    				}
    			});
    			
    			Button dialogButtonNo = (Button) dialog.findViewById(R.id.bluetoothButtonNo);
    			// if button is clicked, close the custom dialog
    			dialogButtonNo.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog.dismiss();
    				}
    			});
     
    			dialog.show();
    	    } else {
            	startActivity(intent);	
    	    }    	    
    	}    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
