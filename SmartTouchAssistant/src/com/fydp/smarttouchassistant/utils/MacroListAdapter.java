package com.fydp.smarttouchassistant.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fydp.smarttouchassistant.R;

public class MacroListAdapter extends ArrayAdapter<String> {
	private Context context;
	private String[] profileNames;

	public MacroListAdapter(Context context, String[] profileNames) {
		super(context, R.layout.macro_listitem, profileNames);
		this.context = context;
		this.profileNames = profileNames;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View listView = inflater.inflate(R.layout.macro_listitem, parent, false);
		TextView profileName = (TextView) listView.findViewById(R.id.profile_name_list);
		profileName.setText(profileNames[position]);
		return listView;
	}

}
