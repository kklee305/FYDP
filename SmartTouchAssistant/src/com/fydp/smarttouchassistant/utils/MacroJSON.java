package com.fydp.smarttouchassistant.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MacroJSON {

	private JSONObject object;
	private JSONArray macroArray;

	public MacroJSON(String profileName) {
		this.object = new JSONObject();
		try {
			object.put("profile", profileName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.macroArray = new JSONArray();

	}

	public void addMacro(int index, String macro) {
		macroArray.put(macro);
	}

	public void putArray() {
		try {
			object.put("macros", macroArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getMacroObject() {
		return object;
	}

	public String toString() {
		return object.toString();
	}

}
