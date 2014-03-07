package com.fydp.smarttouchassistant;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.fydp.smarttouchassistant.utils.FileDirectoryTree;
import com.fydp.smarttouchassistant.utils.FileDirectoryTree.Node;
import com.fydp.smarttouchassistant.utils.ListAdapter;

public class FileExplorerActivity extends BaseBluetoothActivity {

	public static enum FILETYPE {
		FILE, FOLDER, BACK
	};

	public static enum FILEOPERATIONS {
		LAUNCH, RENAME
	};

	private FileDirectoryTree<String> testDirectory;

	private String rootDirectory = "My Computer";
	private Node<String> currentLocation;

	private List<Button> navigationBarList = new ArrayList<Button>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_explorer);
	}

	@Override
	protected void bluetoothBounded() {
		initTestTree();
		initTree();
	}

	private void initTree() {
		requestDirectoryFromPC("?root");
		insertCurrentToDirectoryBar();
		populateList();
	}

	private void requestDirectoryFromPC(String directory) {
		if (directory.equalsIgnoreCase("?root")) {
			currentLocation = testDirectory.getRoot();
		}
	}

	private void insertCurrentToDirectoryBar() {
		final LinearLayout directoryBar = (LinearLayout) findViewById(R.id.fe_directory_bar);
		
		Button button = new Button(this);
		button.setText(currentLocation.getData());
		directoryBar.addView(button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				while(!directoryBar.getChildAt(navigationBarList.size()-1).equals(v)) {
					removeCurrentDirectoryBar();
					currentLocation = currentLocation.getParent();
				}
				populateList();
			}
		});
		
		navigationBarList.add(button);

	}
	
	private void removeCurrentDirectoryBar() {
		LinearLayout directoryBar = (LinearLayout) findViewById(R.id.fe_directory_bar);

		directoryBar.removeViewAt(navigationBarList.size()-1);

		navigationBarList.remove(navigationBarList.size()-1);

	}

	private void populateList() {
		List<Node<String>> children = currentLocation.getChildren();
		//TODO get children from PC

		List<String> fileName = new ArrayList<String>();
		List<FILETYPE> fileType = new ArrayList<FILETYPE>();

		if (!children.isEmpty()) {
			for (Node<String> node : children) {
				fileName.add(node.getData());
				fileType.add(node.getDataType());
			}
		}
		if (currentLocation.getParent() != null) {
			fileName.add("");
			fileType.add(FILETYPE.BACK);
		}

		showList(fileName, fileType, children);
	}

	private void showList(List<String> fileName, List<FILETYPE> fileType, final List<Node<String>> children) {
		String[] fileNameArray = new String[fileName.size()];
		FILETYPE[] fileTypeArray = new FILETYPE[fileType.size()];
		ListAdapter adapter = new ListAdapter(this, fileName.toArray(fileNameArray), fileType.toArray(fileTypeArray));

		ListView listView = (ListView) findViewById(R.id.fe_directory_list);
		listView.setAdapter(adapter);
		listView.invalidate();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position > children.size()-1) {
					removeCurrentDirectoryBar();
					currentLocation = currentLocation.getParent();
				} else {
					currentLocation = children.get(position);
					if (currentLocation.getDataType() == FILETYPE.FOLDER) {
						insertCurrentToDirectoryBar();
					}
				}

				if (currentLocation.getDataType() == FILETYPE.FOLDER) {
					populateList();
				} else {
					Toast.makeText(getApplicationContext(), "Launch " + currentLocation.getData(), Toast.LENGTH_SHORT)
							.show();
					currentLocation = currentLocation.getParent();
				}
			}
		});
	}

	private void initTestTree() {
		testDirectory = new FileDirectoryTree<String>(rootDirectory);
		currentLocation = testDirectory.getRoot();

		currentLocation.insert("C:", FILETYPE.FOLDER);
		currentLocation.insert("D:", FILETYPE.FOLDER);
		currentLocation.insert("E:", FILETYPE.FOLDER);

		currentLocation = currentLocation.getChildren().get(0);
		currentLocation.insert("Bob", FILETYPE.FOLDER);
		currentLocation.insert("Public", FILETYPE.FOLDER);

		currentLocation = currentLocation.getChildren().get(0);
		currentLocation.insert("My Music", FILETYPE.FOLDER);
		currentLocation.insert("My Documents", FILETYPE.FOLDER);
		currentLocation.insert("dickbutt.png", FILETYPE.FILE);

		currentLocation = currentLocation.getChildren().get(0);
		currentLocation.insert("OMG.mp3", FILETYPE.FILE);
		currentLocation.insert("WTF.mp3", FILETYPE.FILE);
		currentLocation.insert("BBQ.mp3", FILETYPE.FILE);

		currentLocation = currentLocation.getParent();
		currentLocation = currentLocation.getChildren().get(1);
		currentLocation.insert("This.docx", FILETYPE.FILE);
		currentLocation.insert("is.txt", FILETYPE.FILE);
		currentLocation.insert("rad.lol", FILETYPE.FILE);

		currentLocation = null;
	}

}
