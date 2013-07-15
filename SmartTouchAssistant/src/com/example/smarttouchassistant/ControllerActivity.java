package com.example.smarttouchassistant;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ControllerActivity extends Activity implements SensorEventListener{

	private static final float ALPHA = 0.9f;
	
	private SensorManager sm = null;
	private float[] rotation_vector = new float[9];
	private float[] orientation = new float[3];
	private int[] origin_orientation = new int[3];

	private Button button1;
	private TextView textview1;
	private ImageView imageMoveMe;
	
	List<Sensor> rv;
	private MarginLayoutParams marginLayoutParams;
	private int x,y;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		// Show the Up button in the action bar.
		setupActionBar();
		
		LocalBroadcastManager.getInstance(this).registerReceiver(
	            mMessageReceiver, new IntentFilter("foregroundSwitch"));
		
		
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        button1 = (Button) findViewById(R.id.button1);
        
        button1.setOnClickListener (new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		        textview1.setText("CALIBRATE!");
		        marginLayoutParams.leftMargin = x;
		        marginLayoutParams.bottomMargin = y;
	        	origin_orientation[0] = (int) (((orientation[1]*180)/Math.PI));
	        	origin_orientation[1] = (int) (((orientation[2]*180)/Math.PI));
	        	origin_orientation[2] = (int) (((orientation[0]*180)/Math.PI));
		        
			};
        });
        
        rv = sm.getSensorList(Sensor.TYPE_ROTATION_VECTOR);
        textview1 = (TextView) findViewById(R.id.textView1);
        imageMoveMe = (ImageView) findViewById(R.id.imageMoveMe);
        marginLayoutParams = (MarginLayoutParams) imageMoveMe.getLayoutParams();
        x = marginLayoutParams.leftMargin;
        y = marginLayoutParams.bottomMargin;
	}

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
    	
    	if (rv == null || rv.size() <= 0){
    		//error
    	}

        resumeListener();
	    Log.d("DEBUG", "Controller resumed");
	}
	
    private void resumeListener(){
    	sm.registerListener(this, rv.get(0),SensorManager.SENSOR_DELAY_UI);
    	button1.performClick();
    }
    
    private void pauseListener(){
    	sm.unregisterListener(this);
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

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		rotation_vector[ 0] = 1;
		rotation_vector[ 4] = 1;
		rotation_vector[ 8] = 1;
		SensorManager.getRotationMatrixFromVector(rotation_vector, event.values);
		SensorManager.getOrientation(rotation_vector, orientation);
		updateJoyStick();		
	}
	
	 public void updateJoyStick() {
	    	
        int x_angle =   (int) (((orientation[1]*180)/Math.PI));
        TextView textView = (TextView) findViewById(R.id.currentPitch);
        textView.setText("Pitch: "+ String.valueOf(x_angle));
        textView = (TextView) findViewById(R.id.textPitch);
        int x_diff = origin_orientation[0] - x_angle;
        textView.setText("Pitch: "+ String.valueOf(x_diff));
        
        
        int y_angle =  (int) (((orientation[2]*180)/Math.PI));
        textView = (TextView) findViewById(R.id.currentRoll);
        textView.setText("Roll: "+ String.valueOf(y_angle));
        textView = (TextView) findViewById(R.id.textRoll);
        int y_diff = origin_orientation[1] - y_angle;
        textView.setText("Roll: "+ String.valueOf(y_diff));
        
        int x_offset = x_diff;        
        int y_offset = y_diff;
        
    	marginLayoutParams.leftMargin = (int) (marginLayoutParams.leftMargin - y_offset);
        marginLayoutParams.bottomMargin= (int) (marginLayoutParams.bottomMargin - x_offset);
        imageMoveMe.requestLayout();
        
        int z_angle =  (int) (((orientation[0]*180)/Math.PI));
        textView = (TextView) findViewById(R.id.currentAzimuth);
        textView.setText("Azimuth: "+ String.valueOf(z_angle));
        textView = (TextView) findViewById(R.id.textAzimuth);
        textView.setText("Azimuth: "+ String.valueOf(origin_orientation[2] - z_angle));
        return;
    }

}
