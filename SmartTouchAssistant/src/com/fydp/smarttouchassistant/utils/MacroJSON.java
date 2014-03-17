package com.fydp.smarttouchassistant.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class MacroJSON {

	private JSONObject object;
	private JSONObject macroArray;
	
	public MacroJSON(String profileName){
		this.object = new JSONObject();
		try {
			object.put("profile", profileName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.macroArray = new JSONObject();
		
	}
	
	public void addMacro(int index, String macro){
		try {
			macroArray.put(""+index, macro);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void putArray(){
		try {
			object.put("macros", macroArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return object.toString();
	}
	
}
