package com.fydp.smarttouchassistant;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MacroActivity extends Activity {

	public final static String[] SHORTCUTS = {
			"com.fydp.smarttouchassistant.SHORCUT1",
			"com.fydp.smarttouchassistant.SHORCUT2",
			"com.fydp.smarttouchassistant.SHORCUT3",
			"com.fydp.smarttouchassistant.SHORCUT4" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_macro);

		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver, new IntentFilter("foregroundSwitch"));
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newForeground = intent.getStringExtra("foreground");
			Log.d("DEBUG", this.toString()
					+ " received foreground switch request to " + newForeground);
			if (!newForeground.equals("macro")) {
				Intent next = null;
				if (newForeground.equals("mouse")) {
					next = new Intent(context, MouseActivity.class);
				} else if (newForeground.equals("controller")) {
					next = new Intent(context, ControllerActivity.class);
				} else if (newForeground.equals("numpad")) {
					next = new Intent(context, NumpadActivity.class);
				} else if (newForeground.equals("multimedia")) {
					next = new Intent(context, MultiMediaActivity.class);
				} else {
					return;
				}
				startActivity(next);
				LocalBroadcastManager.getInstance(context).unregisterReceiver(
						mMessageReceiver);
			}
		}
	};

	public void saveMacros(View view) {

		Intent intent = new Intent(this, MacroControllerActivity.class);
		int[] ids = { R.id.shortcut1, R.id.shortcut2, R.id.shortcut3,
				R.id.shortcut4 };

		EditText profileNameView = (EditText) findViewById(R.id.profile_name);
		intent.putExtra("profile", profileNameView.getText().toString());
//		MacroJSON macroJSON = new MacroJSON(profileNameView.getText()
//				.toString());

		for (int i = 0; i < ids.length; i++) {
			EditText editText = (EditText) findViewById(ids[i]);
			String message = editText.getText().toString();
			intent.putExtra(SHORTCUTS[i], message);
//			macroJSON.addMacro(i, editText.getText().toString());EditText editText
		}
//		macroJSON.putArray();
//		JSONObject macroFile = null;
//		try {
//			macroFile = new JSONObject(readFile());
//			macroFile.put("profiles",macroJSON);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		writeToFile(macroFile.toString());
		startActivity(intent);
	}

	public void loadMacros(View view) {
//		String JSONString = readFile();
		Log.d("",readFile());
	}

	private void writeToFile(String data) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					openFileOutput("macroSave.txt", Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
			Log.d("", "Attempt to write to file " + data);
		} catch (IOException e) {
			Log.d("Exception", "File write failed: " + e.toString());
		}
	}

	private String readFile() {
		String temp = "";
		try {
			FileInputStream inputStream = openFileInput("macroSave.txt");
			BufferedReader r = new BufferedReader(new InputStreamReader(
					inputStream));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			r.close();
			inputStream.close();
			temp = "" + total;
		} catch (Exception e) {
			Log.d("Exception", "File read failed: " + e.toString());
			e.printStackTrace();
		}

		if (temp.isEmpty()) {
			JSONObject newMacroFile = new JSONObject();
			try {
				newMacroFile.put("profiles", new JSONObject());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			temp = newMacroFile.toString();
		}

		return temp;
	}

}
