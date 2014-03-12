package com.fydp.smarttouchassistant;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.fydp.service.BluetoothConnectionService;
import com.fydp.service.BluetoothConnectionService.LocalBinder;
import com.fydp.smarttouchassistant.utils.DirectoryParser;
import com.fydp.smarttouchassistant.utils.FileDirectoryTree;
import com.fydp.smarttouchassistant.utils.FileDirectoryTree.Node;
import com.fydp.smarttouchassistant.utils.ListAdapter;

public class FileExplorerActivity extends Activity {

	public static enum FILETYPE {
		FILE, FOLDER, BACK
	};

	public static enum FILEOPERATIONS {
		REQUEST, LAUNCH, RENAME
	};

	private static final String FILEDIRECTORY_HEADER = "filedirectory#";

	private FileDirectoryTree testDirectory;
	private FileDirectoryTree directory;

	private String rootDirectory = "C:\\";
	private Node currentLocation;
	ListView listView;

	private List<Button> navigationBarList = new ArrayList<Button>();

	private boolean requestFlag = false;

	BluetoothConnectionService btService;
	boolean isBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_explorer);

		Intent intent = new Intent(this, BluetoothConnectionService.class);
		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
	}

	protected void init() {
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(mMessageReceiver, new IntentFilter("foregroundSwitch"));
		listView = (ListView) findViewById(R.id.fe_directory_list);
		// initTestTree();
		initTree();
//		String testDirectory = "filedirectory?C:\\folder1?C:\\folder2*C:\\file1";
//		FileDirectoryTree testTree = new FileDirectoryTree(rootDirectory);
//		List<Node> children = DirectoryParser.parse(testDirectory, rootDirectory, testTree.getRoot());
//		Log.d("WTF",children.toString() + children.size());
	}

	private void initTree() {
		directory = new FileDirectoryTree(rootDirectory);
		currentLocation = directory.getRoot();
		requestDirectoryFromPC(currentLocation.getDirectory());
	}

	private void requestDirectoryFromPC(String directory) {
		// if (directory.equalsIgnoreCase("?root")) {
		// currentLocation = testDirectory.getRoot();
		// }
		Log.d("", FILEDIRECTORY_HEADER + FILEOPERATIONS.REQUEST.toString() + "#" + directory);
		btService.sendMessage(FILEDIRECTORY_HEADER + FILEOPERATIONS.REQUEST.toString() + "#" + directory);
		requestFlag = true;
		listView.setVisibility(View.INVISIBLE);
	}

	private void responseDirectoryFromPC(String message) {
		List<Node> children = DirectoryParser.parse(message, currentLocation.getDirectory(), currentLocation);
		insertCurrentToDirectoryBar();
		populateList(children);
	}

	private void insertCurrentToDirectoryBar() {
		final LinearLayout directoryBar = (LinearLayout) findViewById(R.id.fe_directory_bar);

		Button button = new Button(this);
		button.setText(currentLocation.getData());
		directoryBar.addView(button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				while (!directoryBar.getChildAt(navigationBarList.size() - 1).equals(v)) {
					removeCurrentDirectoryBar();
					currentLocation = currentLocation.getParent();
				}
				responseDirectoryFromPC(currentLocation.getDirectory());
			}
		});

		navigationBarList.add(button);

	}

	private void removeCurrentDirectoryBar() {
		LinearLayout directoryBar = (LinearLayout) findViewById(R.id.fe_directory_bar);

		directoryBar.removeViewAt(navigationBarList.size() - 1);

		navigationBarList.remove(navigationBarList.size() - 1);

	}

	private void populateList(List<Node> children) {
		List<String> fileName = new ArrayList<String>();
		List<FILETYPE> fileType = new ArrayList<FILETYPE>();

		if (!children.isEmpty()) {
			for (Node node : children) {
				fileName.add(node.getData());
				fileType.add(node.getDataType());
			}
		}
		if (currentLocation.getParent() != null) {
			fileName.add("");
			fileType.add(FILETYPE.BACK);
		}

		showList(fileName, fileType, children);
		listView.setVisibility(View.VISIBLE);
	}

	private void showList(List<String> fileName, List<FILETYPE> fileType, final List<Node> children) {
		String[] fileNameArray = new String[fileName.size()];
		FILETYPE[] fileTypeArray = new FILETYPE[fileType.size()];
		ListAdapter adapter = new ListAdapter(this, fileName.toArray(fileNameArray), fileType.toArray(fileTypeArray));

		listView.setAdapter(adapter);
		listView.invalidate();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position > children.size() - 1) {
					removeCurrentDirectoryBar();
					currentLocation = currentLocation.getParent();
				} else {
					currentLocation = children.get(position);
					if (currentLocation.getDataType() == FILETYPE.FOLDER) {
						insertCurrentToDirectoryBar();
					}
				}

				if (currentLocation.getDataType() == FILETYPE.FOLDER) {
					requestDirectoryFromPC(currentLocation.getData());
				} else {
					Toast.makeText(getApplicationContext(), "Launch " + currentLocation.getData(), Toast.LENGTH_SHORT)
							.show();
					btService.sendMessage(FILEDIRECTORY_HEADER + FILEOPERATIONS.LAUNCH.toString() + "#"
							+ currentLocation.getDirectory());
					currentLocation = currentLocation.getParent();
				}
			}
		});
	}

	// Bind service to BT connection
	protected ServiceConnection myConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			btService = binder.getService();
			isBound = true;
			Log.d("", "Service is bound");
			init();
		}

		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}
	};

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String message = intent.getStringExtra("foreground");
			Log.d("DEBUG", this.toString() + " received foreground switch request to " + message);
			Intent next = null;
			// TODO remove me
			message = "filedirectory?C:\folder1?C:\folder1*C:\file1";
			if (message.contains("filedirectory") && requestFlag) {
				Log.d("", message);
				requestFlag = false;
				responseDirectoryFromPC(message);
				return;
			}

			if (message.equals("mouse")) {
				next = new Intent(context, MouseActivity.class);
			} else if (message.equals("numpad")) {
				next = new Intent(context, NumpadActivity.class);
			} else if (message.equals("controller")) {
				next = new Intent(context, ControllerActivity.class);
			} else if (message.equals("macro")) {
				next = new Intent(context, MacroActivity.class);
			} else if (message.equals("multimedia")) {
				next = new Intent(context, MultiMediaActivity.class);
			}
			startActivity(next);
			LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
		}
	};

	private void initTestTree() {
		testDirectory = new FileDirectoryTree(rootDirectory);
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
		currentLocation.insert("SmartTouchAssistant.txt", FILETYPE.FILE);

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
