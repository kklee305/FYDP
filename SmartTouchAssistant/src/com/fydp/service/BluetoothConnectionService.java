package com.fydp.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import com.fydp.smarttouchassistant.DisplayOptionsActivity;
import com.fydp.smarttouchassistant.config.CommonConfig;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class BluetoothConnectionService extends Service {
	private static final String AUTO_SWITCHING_PREF = "auto_switching";
	private final IBinder mBinder = new LocalBinder();
	private static String[] contexts = { "macro", "mouse", "numpad", "multimedia", "controller", "filedirectory" };
	private static String[] foregrounds = { "WINWORD", "explorer", "calc", "wmplayer", "controller", "filedirectory" };

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Well known SPP UUID
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public static final String DEBUG_TAG = "DEBUG";
	public static final String ERROR_TAG = "ERROR";

	private static BluetoothAdapter mBluetoothAdapter;
	public static ConnectThread mConnectThread;
	public static ConnectedThread mConnectedThread;
	private static Set<BluetoothDevice> pairedDevices;

	// private final Handler mHandler;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public BluetoothConnectionService getService() {
			return BluetoothConnectionService.this;
		}
	}

	/**
	 * Constructor. Prepares a new Bluetooth session
	 */
	public void onCreate() {
		// mHandler = handler;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(ERROR_TAG, "No default adapter");
		}

		// TestThread tt = new TestThread(this);
		// tt.start();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		mConnectThread = null;
		mConnectedThread = null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Tell the user we stopped.
		Toast.makeText(this, "Bluetooth Service Stopped", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void connectToDevice(String name, String address) {
		for (BluetoothDevice device : pairedDevices) {
			if (device.getName().equals(name) && device.getAddress().equals(address)) {
				mConnectThread = new ConnectThread(device);
				mConnectThread.start();
				Log.d(DEBUG_TAG, "Bluetooth Connection found");
			}
		}
		if (mConnectThread == null) {
			Log.e(ERROR_TAG, "Bluetooth Connection not established!");
		}
	}

	protected void establishComm(BluetoothSocket mmSocket) {
		mConnectedThread = new ConnectedThread(mmSocket, this);
		mConnectedThread.start();
		Log.d(DEBUG_TAG, "Bluetooth Connection established");
	}

	public void sendMessage(String message) {
		String temp = message + "#";
		if (mConnectedThread == null) {
			Log.e(DEBUG_TAG, "mConnectedThread is null");
		} else {
			mConnectedThread.write(temp.getBytes());
		}
	}

	private class ConnectThread extends Thread {
		private BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();

			if (mmDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
				// Connect to device automatically
				try {
					mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
				} catch (IOException e) {
					Log.e(DEBUG_TAG, "socket not created");
				}
				try {
					// Connect the device through the socket. This will block
					// until it succeeds or throws an exception
					Log.d(DEBUG_TAG, "Attempting to connect through socket...");
					mmSocket.connect();
					Log.d(DEBUG_TAG, "Connected!!");
				} catch (IOException connectException) {
					// Unable to connect; close the socket and get out
					try {
						mmSocket.close();
					} catch (IOException closeException) {
					}
					return;
				}
				Log.d(DEBUG_TAG, "Attempting to start managing connection...");
				// Do work to manage the connection (in a separate thread)
				establishComm(mmSocket);
			}
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private final Context context;

		public ConnectedThread(BluetoothSocket socket, Context origContext) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			context = origContext;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[1024]; // buffer store for the stream
			int bytes; // bytes returned from read()

			Log.d(DEBUG_TAG, "Connected thread started");
			if (!CommonConfig.aCommonConfig().isDebuggable()) {
				Intent nextActivityIntent = new Intent(getBaseContext(), DisplayOptionsActivity.class);
				nextActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplication().startActivity(nextActivityIntent);
			}
			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					String received = new String(buffer, 0, bytes, "UTF-8");
					// Send the obtained bytes to the UI activity
					Log.d(DEBUG_TAG, "Received: " + received);
					
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
					if (sharedPrefs.getBoolean(AUTO_SWITCHING_PREF, false)) {
						String broadcast = received;
						Intent intent = new Intent("foregroundSwitch");
						for (int i = 0; i < foregrounds.length; i++) {
							if (received.contains(foregrounds[i])) {
								broadcast =  contexts[i];
								Log.d(DEBUG_TAG, "sent foreground switch to " + contexts[i]);
								break;
							}
						}
						intent.putExtra("foreground", broadcast);
						CommonConfig.aCommonConfig().setCurrentContext(broadcast);
						LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
					} else {
						Log.d(DEBUG_TAG, "Auto switching off, ignored!");
					}
				} catch (IOException e) {
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(byte[] bytes) {
			try {
				String received = new String(bytes);
				String[] input = received.split("\r");

				Log.d(DEBUG_TAG, "sending " + input[0]);
				mmOutStream.write(bytes);
			} catch (IOException e) {
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class TestThread extends Thread {
		private final Context context;

		public TestThread(Context origContext) {
			context = origContext;
		}

		int i = 0;

		public void run() {
			Log.d(DEBUG_TAG, "started test thread");
			while (true) {
				Intent intent = new Intent("foregroundSwitch");

				intent.putExtra("foreground", contexts[i]);

				try {
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d(DEBUG_TAG, "sent foreground switch to " + contexts[i]);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				i++;
				if (i == 5) {
					i = 0;
				}
			}
		}
	}
}
