package com.fydp.smarttouchassistant.utils;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.fydp.smarttouchassistant.FileExplorerActivity.FILETYPE;
import com.fydp.smarttouchassistant.utils.FileDirectoryTree.Node;

public class DirectoryParser {
	public static List<Node> parse(String raw, Node directory) {
		Log.d("parsing", raw);
		if (!raw.contains("filedirectory#") || raw.isEmpty()) {
			return null;
		}
		List<Node> children = new ArrayList<Node>();
		String temp = raw.replaceFirst("filedirectory#", "");
		int tempIndex;
		FILETYPE fileType;
		while (!temp.isEmpty()) {
			if (temp.indexOf("?", 1) == temp.indexOf("*", 1)) {
				tempIndex = temp.length();
			} else if (temp.indexOf("*", 1) == -1) {
				tempIndex = temp.indexOf("?", 1);
			} else if (temp.indexOf("?", 1) == -1) {
				tempIndex = temp.indexOf("*", 1);
			} else {
				tempIndex = temp.indexOf("?", 1) < temp.indexOf("*", 1) ? temp.indexOf("?", 1) : temp.indexOf("*", 1);
			}

			if (temp.substring(0, 1).equals("?")) {
				fileType = FILETYPE.FOLDER;
			} else if (temp.substring(0, 1).equals("*")) {
				fileType = FILETYPE.FILE;
			} else {
				Log.d("parser wtf", "firstIndex not type " + temp.substring(0, 1));
				break;
			}
			children.add(new Node(temp.substring(1, tempIndex), fileType, directory));
			temp = temp.substring(tempIndex);
		}

		return children;
	}
}
