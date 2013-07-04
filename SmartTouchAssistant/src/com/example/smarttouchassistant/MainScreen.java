package com.example.smarttouchassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

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
    	Intent intent = new Intent(this, BluetoothConnectionActivity.class);
    	startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
