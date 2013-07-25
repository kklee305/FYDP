package com.example.smarttouchassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class DisplayOptionsActivity extends Activity {
	private static final int RESULT_SETTINGS = 1;
    
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
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
		 case R.id.menu_settings:
	            Intent i = new Intent(this, UserSettingsActivity.class);
	            startActivityForResult(i, RESULT_SETTINGS);
	            break;
	 
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        
        switch (requestCode) {
        case RESULT_SETTINGS:
        	Context context = getApplicationContext();
        	CharSequence text = "Automatic Context Switching set to: " + sharedPrefs.getBoolean("auto_switching", false);
        	int duration = Toast.LENGTH_SHORT;

        	Toast toast = Toast.makeText(context, text, duration);
        	toast.show();
            break; 
        }
 
    }
		
	

}
