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
import android.content.pm.ActivityInfo;
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
import android.widget.ProgressBar;

import com.fydp.service.BluetoothConnectionService;
import com.fydp.service.BluetoothConnectionService.LocalBinder;
import com.fydp.smarttouchassistant.utils.DirectoryParser;
import com.fydp.smarttouchassistant.utils.FileDirectoryTree;
import com.fydp.smarttouchassistant.utils.FileDirectoryTree.Node;
import com.fydp.smarttouchassistant.utils.FileExplorerListAdapter;

public class FileExplorerActivity extends Activity {

	public static enum FILETYPE {
		FILE, FOLDER, BACK
	};

	public static enum FILEOPERATIONS {
		REQUEST, LAUNCH
	};

	private static final String FILEDIRECTORY_HEADER = "filedirectory#";

	// private FileDirectoryTree testDirectory;
	private FileDirectoryTree directory;

	private String rootDirectory = "C:";
	private Node currentLocation;
	private ListView listView;
	private ProgressBar spinner;

	private List<Button> navigationBarList = new ArrayList<Button>();

	private boolean requestFlag = false;

	BluetoothConnectionService btService;
	boolean isBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_explorer);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unbindService(myConnection);
		directory = null;
		while (navigationBarList.size() != 0){			
			removeCurrentDirectoryBar();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = new Intent(this, BluetoothConnectionService.class);
		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
	}

	protected void init() {
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver, new IntentFilter("foregroundSwitch"));
		listView = (ListView) findViewById(R.id.fe_directory_list);
		spinner = (ProgressBar) findViewById(R.id.progressspinner);
		// initTestTree();
		initTree();
	}

	private void initTree() {
		directory = new FileDirectoryTree(rootDirectory);
		currentLocation = directory.getRoot();
		insertCurrentToDirectoryBar();
		requestDirectoryFromPC(currentLocation.getDirectory());
	}

	private void requestDirectoryFromPC(String directory) {
		Log.d("", FILEDIRECTORY_HEADER + directory + "#");
		btService.sendMessage(FILEDIRECTORY_HEADER + directory + "#");
		requestFlag = true;
		listView.setVisibility(View.INVISIBLE);
		spinner.setVisibility(View.VISIBLE);
		// responseDirectoryFromPC("filedirectory?folder1?folder2*file1");
	}

	private void responseDirectoryFromPC(String message) {
		List<Node> children = DirectoryParser.parse(message, currentLocation);
		currentLocation.insertChildren(children);
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
				while (!directoryBar.getChildAt(navigationBarList.size() - 1)
						.equals(v)) {
					removeCurrentDirectoryBar();
					currentLocation = currentLocation.getParent();
				}
				requestDirectoryFromPC(currentLocation.getDirectory());
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
		spinner.setVisibility(View.GONE);
	}

	private void showList(List<String> fileName, List<FILETYPE> fileType,
			final List<Node> children) {
		String[] fileNameArray = new String[fileName.size()];
		FILETYPE[] fileTypeArray = new FILETYPE[fileType.size()];
		FileExplorerListAdapter adapter = new FileExplorerListAdapter(this,
				fileName.toArray(fileNameArray),
				fileType.toArray(fileTypeArray));

		listView.setAdapter(adapter);
		listView.invalidate();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > children.size() - 1) { // back
					removeCurrentDirectoryBar();
					currentLocation = currentLocation.getParent();
				} else {
					currentLocation = children.get(position);
					if (currentLocation.getDataType() == FILETYPE.FOLDER) {
						insertCurrentToDirectoryBar();
					}
				}

				if (currentLocation.getDataType() == FILETYPE.FOLDER) {
					requestDirectoryFromPC(currentLocation.getDirectory());
				} else {
					btService.sendMessage(FILEOPERATIONS.LAUNCH.toString()
							.toLowerCase()
							+ "#"
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
			Log.d("DEBUG", this.toString()
					+ " received foreground switch request to " + message);
			Intent next = null;
			if (message.contains("filedirectory#") && requestFlag) {
				Log.d("requestCallback", message);
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
			} else {
				return;
			}
			startActivity(next);
			LocalBroadcastManager.getInstance(context).unregisterReceiver(
					mMessageReceiver);
		}
	};

	// private void initTestTree() {
	// testDirectory = new FileDirectoryTree(rootDirectory);
	// currentLocation = testDirectory.getRoot();
	//
	// currentLocation.insert("C:", FILETYPE.FOLDER);
	// currentLocation.insert("D:", FILETYPE.FOLDER);
	// currentLocation.insert("E:", FILETYPE.FOLDER);
	//
	// currentLocation = currentLocation.getChildren().get(0);
	// currentLocation.insert("Bob", FILETYPE.FOLDER);
	// currentLocation.insert("Public", FILETYPE.FOLDER);
	//
	// currentLocation = currentLocation.getChildren().get(0);
	// currentLocation.insert("My Music", FILETYPE.FOLDER);
	// currentLocation.insert("My Documents", FILETYPE.FOLDER);
	// currentLocation.insert("SmartTouchAssistant.txt", FILETYPE.FILE);
	//
	// currentLocation = currentLocation.getChildren().get(0);
	// currentLocation.insert("OMG.mp3", FILETYPE.FILE);
	// currentLocation.insert("WTF.mp3", FILETYPE.FILE);
	// currentLocation.insert("BBQ.mp3", FILETYPE.FILE);
	//
	// currentLocation = currentLocation.getParent();
	// currentLocation = currentLocation.getChildren().get(1);
	// currentLocation.insert("This.docx", FILETYPE.FILE);
	// currentLocation.insert("is.txt", FILETYPE.FILE);
	// currentLocation.insert("rad.lol", FILETYPE.FILE);
	//
	// currentLocation = null;
	// }

}
