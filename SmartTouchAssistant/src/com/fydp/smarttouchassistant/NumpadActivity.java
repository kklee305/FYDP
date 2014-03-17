package com.fydp.smarttouchassistant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class NumpadActivity extends BaseBluetoothActivity {
	private static final String NUMPAD_HEADER = "numpad#";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_numpad);
	}
	
	public void inputNumber(View view) {
		String message = null;
		if (!isBound) {
			return;
		}
		
    	switch (view.getId()) {
	    	case R.id.tap0: 
	    		message = "0";
	    		Log.d("DEBUG", "0 tapped");
	    		break;
	    	case R.id.tap1: 
	    		message = "1";
	    		Log.d("DEBUG", "1 tapped");
	    		break;
	    	case R.id.tap2:
	    		message = "2";
	    		Log.d("DEBUG", "2 tapped");
				break;
	    	case R.id.tap3: 
	    		message = "3";
	    		Log.d("DEBUG", "3 tapped");
				break;
	    	case R.id.tap4: 
	    		message = "4";
	    		Log.d("DEBUG", "4 tapped");
				break;
	    	case R.id.tap5: 
	    		message = "5";
	    		Log.d("DEBUG", "5 tapped");
				break;
	    	case R.id.tap6: 
	    		message = "6";
	    		Log.d("DEBUG", "6 tapped");
	    		break;
	    	case R.id.tap7:
	    		message = "7";
	    		Log.d("DEBUG", "7 tapped");
				break;
	    	case R.id.tap8: 
	    		message = "8";
	    		Log.d("DEBUG", "8 tapped");
				break;
	    	case R.id.tap9: 
	    		message = "9";
	    		Log.d("DEBUG", "9 tapped");
				break;
	    	case R.id.tapAdd: 
	    		message = "+";
	    		Log.d("DEBUG", "+ tapped");
				break;
	    	case R.id.tapMinus: 
	    		message = "-";
	    		Log.d("DEBUG", "- tapped");
				break;
	    	case R.id.tapMultiply: 
	    		message = "*";
	    		Log.d("DEBUG", "* tapped");
				break;
	    	case R.id.tapDivide: 
	    		message = "/";
	    		Log.d("DEBUG", "/ tapped");
				break;
	    	case R.id.tapDot: 
	    		message = ".";
	    		Log.d("DEBUG", ". tapped");
				break;
	    	case R.id.tapEnter: 
	    		message = "enter";
	    		Log.d("DEBUG", "Enter tapped");
				break;
    	}	
    	btService.sendMessage(NUMPAD_HEADER + message);		 	
    }

	@Override
	protected void bluetoothBounded() {
	}

}
