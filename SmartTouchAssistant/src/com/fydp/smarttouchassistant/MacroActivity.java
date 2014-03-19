package com.fydp.smarttouchassistant;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fydp.smarttouchassistant.config.CommonConfig;
import com.fydp.smarttouchassistant.utils.MacroJSON;
import com.fydp.smarttouchassistant.utils.MacroListAdapter;

public class MacroActivity extends Activity {

	public final static String[] SHORTCUTS = { "com.fydp.smarttouchassistant.SHORCUT1",
			"com.fydp.smarttouchassistant.SHORCUT2", "com.fydp.smarttouchassistant.SHORCUT3",
			"com.fydp.smarttouchassistant.SHORCUT4" };

	private JSONArray profiles = null;
	private JSONObject macroSaveFile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_macro);

		LocalBroadcastManager.getInstance(this)
				.registerReceiver(mMessageReceiver, new IntentFilter("foregroundSwitch"));

		if (CommonConfig.aCommonConfig().isDebuggable()) {
			Button clearButton = (Button) findViewById(R.id.clear_button);
			clearButton.setVisibility(View.VISIBLE);
		}

		if (CommonConfig.aCommonConfig().getCurrentContext().isEmpty()) {
			setEditProfileBoxText("Enter Profile Name");
		} else {
			setEditProfileBoxText(CommonConfig.aCommonConfig().getCurrentContext());
		}

		try {
			macroSaveFile = new JSONObject(readFile());
			Log.d("save file", macroSaveFile.toString());
			profiles = macroSaveFile.getJSONArray("profiles");
			Log.d("profile", profiles.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void saveMacros(View view) {

		for (int i = 0; i < profiles.length(); i++) {
			try {
				if (profiles.getJSONObject(i).getString("profile").equalsIgnoreCase(getEditProfileBoxText())) {
					Toast.makeText(this, "Conflicting Profile Name", Toast.LENGTH_LONG).show();
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		Intent intent = new Intent(this, MacroControllerActivity.class);
		int[] ids = { R.id.shortcut1, R.id.shortcut2, R.id.shortcut3, R.id.shortcut4 };

		intent.putExtra("profile", getEditProfileBoxText());
		MacroJSON macroJSON = new MacroJSON(getEditProfileBoxText());

		for (int i = 0; i < ids.length; i++) {
			EditText editText = (EditText) findViewById(ids[i]);
			String message = editText.getText().toString();
			intent.putExtra(SHORTCUTS[i], message);
			macroJSON.addMacro(i, editText.getText().toString());
		}
		macroJSON.putArray();
		// Log.d("save current macros", macroJSON.getMacroArray().toString());
		profiles.put(macroJSON.getMacroObject());
		// Log.d("save profiles", profiles.toString());
		try {
			macroSaveFile.put("profiles", profiles);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeToFile(macroSaveFile.toString());
		try {
			macroSaveFile = new JSONObject(readFile());
			profiles = macroSaveFile.getJSONArray("profiles");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		intent.putExtra("profileTitle", getEditProfileBoxText());
		startActivity(intent);
	}

	public void loadMacros(View view) {
		try {
			macroSaveFile = new JSONObject(readFile());
			profiles = macroSaveFile.getJSONArray("profiles");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String[] profileNames = new String[profiles.length()];
		for (int i = 0; i < profiles.length(); i++) {
			try {
				Log.d("", profiles.getJSONObject(i).getString("profile"));
				profileNames[i] = profiles.getJSONObject(i).getString("profile");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		showProfiles(profileNames);
	}

	private void showProfiles(String[] profileNames) {
		if (profileNames.length == 0) {
			Toast.makeText(this, "No Profiles Saved", Toast.LENGTH_LONG).show();
			return;
		}

		AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
		builderSingle.setTitle("Select a Profile");
		final MacroListAdapter listAdapter = new MacroListAdapter(this, profileNames);
		builderSingle.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final int index = which;
				final String strName = listAdapter.getItem(which);
				AlertDialog.Builder builderInner = new AlertDialog.Builder(MacroActivity.this);
				builderInner.setMessage(strName);
				builderInner.setPositiveButton("Load", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						JSONArray macros = null;
						try {
							macros = profiles.getJSONObject(index).getJSONArray("macros");
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						Intent intent = new Intent(getBaseContext(), MacroControllerActivity.class);
						if (macros != null && macros.length() == 4) {
							for (int i = 0; i < macros.length(); i++) {
								Log.d("macro loading", "" + macros.length());
								try {
									intent.putExtra(SHORTCUTS[i], macros.getString(i));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} else {
							Toast.makeText(getBaseContext(), "Error loading profile", Toast.LENGTH_LONG).show();
							clearMacros(null);
						}
						intent.putExtra("profileTitle", strName);
						startActivity(intent);
						dialog.dismiss();
					}
				});

				builderInner.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						JSONArray newArray = new JSONArray();
						if (profiles != null) {
							for (int i = 0; i < profiles.length(); i++) {
								if (i != index) {
									try {
										newArray.put(profiles.get(i));
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						}
						try {
							macroSaveFile.put("profiles", newArray);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						writeToFile(macroSaveFile.toString());
						try {
							macroSaveFile = new JSONObject(readFile());
							profiles = macroSaveFile.getJSONArray("profiles");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				});
				builderInner.show();
			}
		});
		builderSingle.show();
	}

	private void writeToFile(String data) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("macroSave.txt",
					Context.MODE_PRIVATE));
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
			BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
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
			JSONArray profiles = new JSONArray();
			try {
				newMacroFile.put("profiles", profiles);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			temp = newMacroFile.toString();
		}

		return temp;
	}

	private void setEditProfileBoxText(String text) {
		EditText profileNameView = (EditText) findViewById(R.id.profile_name);
		profileNameView.setText(text);
	}

	private String getEditProfileBoxText() {
		EditText profileNameView = (EditText) findViewById(R.id.profile_name);
		return profileNameView.getText().toString();
	}

	public void clearMacros(View view) {
		writeToFile("");
		try {
			macroSaveFile = new JSONObject(readFile());
			profiles = macroSaveFile.getJSONArray("profiles");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newForeground = intent.getStringExtra("foreground");
			Log.d("DEBUG", this.toString() + " received foreground switch request to " + newForeground);

			setEditProfileBoxText(CommonConfig.aCommonConfig().getCurrentContext());

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
				LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
			}
		}
	};

}
