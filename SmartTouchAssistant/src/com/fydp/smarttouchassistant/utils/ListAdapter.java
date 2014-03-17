package com.fydp.smarttouchassistant.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fydp.smarttouchassistant.FileExplorerActivity.FILETYPE;
import com.fydp.smarttouchassistant.R;

public class ListAdapter extends ArrayAdapter<String> {
	private Context context;
	private String[] files; 
	private FILETYPE[] fileType;
	
	public ListAdapter(Context context, String[] files, FILETYPE[] fileType){
		super(context,  R.layout.fe_listitem, files);
		this.context = context;
		this.files = files;
		this.fileType = fileType;
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View listView = inflater.inflate(R.layout.fe_listitem, parent, false);
	    TextView fileName = (TextView) listView.findViewById(R.id.fe_name);
	    ImageView imageView = (ImageView) listView.findViewById(R.id.fe_icon);
//	    Log.d("","@@@ position:" + position + ", title: " + files[position]);
	    fileName.setText(files[position]);
	    if (fileType[position] == FILETYPE.FOLDER) {
	    	imageView.setImageResource(R.drawable.fe_folder_icon);
	    } else if(fileType[position] == FILETYPE.BACK) {
	    	imageView.setImageResource(R.drawable.fe_back_icon);
	    } else {
	    	imageView.setImageResource(R.drawable.fe_file_icon);    	
	    }

	    return listView;
	  }
	
}
