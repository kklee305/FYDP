package com.fydp.smarttouchassistant;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.fydp.service.BluetoothConnectionService;
import com.fydp.service.BluetoothConnectionService.LocalBinder;
import com.fydp.smarttouchassistant.listeners.RotationSensorEventListener;

public class ControllerActivity extends Activity {
	private static final String CONTROLLER_HEADER = "controller#";
	BluetoothConnectionService btService;
    boolean isBound = false;
	
	private SensorManager sensorManager;

	private Button button1;

	private RelativeLayout controllerLayout;
	
	private Sensor sensor;
	private RotationSensorEventListener listener; //Change to a base when gyro is added
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Intent intent = new Intent(this, BluetoothConnectionService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);   
		// Show the Up button in the action bar.
		setupActionBar();
		
		LocalBroadcastManager.getInstance(this).registerReceiver(
	            mMessageReceiver, new IntentFilter("foregroundSwitch"));
		
		
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        if (1==0) { //gyro exists
//        	//use gyroscope
//        } else {
//        	//use rotation vector
//        }
        
        controllerLayout = (RelativeLayout) findViewById(R.id.controllerLayout);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        listener = new RotationSensorEventListener(controllerLayout);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener (new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.calibrate();
			};
        });
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
	
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String newForeground = intent.getStringExtra("foreground");
	        Log.d("DEBUG", this.toString() + " received foreground switch request to " + newForeground);
	        if(!newForeground.equals("controller")) {
	        	Intent next = null;
		        if(newForeground.equals("mouse")) {
		        	next = new Intent(context, MouseActivity.class);
		        } else if(newForeground.equals("numpad")) {
		        	next = new Intent(context, NumpadActivity.class);
		        } else if(newForeground.equals("macro")) {
		        	next = new Intent(context, MacroActivity.class);
		        } else if(newForeground.equals("multimedia")) {
		        	next = new Intent(context, MultiMediaActivity.class);
		        }		        
		        startActivity(next); 
		        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
	        }	        
	    }
	};
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.controller, menu);
		return true;
	}
	
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    pauseListener();
	    Log.d("DEBUG", "Controller paused");
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

        resumeListener();
	    Log.d("DEBUG", "Controller resumed");
	}
	
    private void resumeListener(){
    	sensorManager.registerListener(listener, sensor,SensorManager.SENSOR_DELAY_UI);
    	button1.performClick();
    }
    
    private void pauseListener(){
    	sensorManager.unregisterListener(listener);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
