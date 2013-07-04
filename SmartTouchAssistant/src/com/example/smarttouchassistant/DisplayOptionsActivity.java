package com.example.smarttouchassistant;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayOptionsActivity extends Activity {

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_options);

		
	}
	
	public void macroOption(View view) {
    	Intent intent = new Intent(this, MacroActivity.class);
    	startActivity(intent);
    }
	
	public void mouseOption(View view) {
    	Intent intent = new Intent(this, MouseActivity.class);
    	startActivity(intent);
    }
	
	public void controllerOption(View view) {
    	Intent intent = new Intent(this, ControllerActivity.class);
    	startActivity(intent);
    }
	
	public void numpadOption(View view) {
    	Intent intent = new Intent(this, NumpadActivity.class);
    	startActivity(intent);
    }
	
	public void multiMediaOption(View view) {
    	Intent intent = new Intent(this, MultiMediaActivity.class);
    	startActivity(intent);
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
