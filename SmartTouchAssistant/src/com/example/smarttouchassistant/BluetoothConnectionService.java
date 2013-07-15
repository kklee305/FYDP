package com.example.smarttouchassistant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class BluetoothConnectionService extends Service{	
	
    private final IBinder mBinder = new LocalBinder();
    private static String inputFromPC;
    private static String[] contextTest = {"macro", "mouse", "numpad", "multimedia", "controller"};
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    public static final String DEBUG_TAG = "DEBUG";
    public static final String ERROR_TAG = "ERROR";
    
	private static BluetoothAdapter mBluetoothAdapter; 
	public static ConnectThread mConnectThread;
	public static ConnectedThread mConnectedThread;
	private static Set<BluetoothDevice> pairedDevices;
	//private final Handler mHandler;
    
	
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	BluetoothConnectionService getService() {
            return BluetoothConnectionService.this;
        }
    }
    
    /**
     * Constructor. Prepares a new Bluetooth session
     */
    public void onCreate() {
        //mHandler = handler;
    	inputFromPC = null;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null) {
			Log.e(ERROR_TAG, "No default adapter");
		}
		
        //TestThread tt = new TestThread(this);
        //tt.start();
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
			if(device.getName().equals(name) && device.getAddress().equals(address)) {
				mConnectThread = new ConnectThread(device);
				mConnectThread.start();
				Log.d(DEBUG_TAG,"Bluetooth Connection found");
			}
		}
		if(mConnectThread == null) {
			Log.e(ERROR_TAG, "Bluetooth Connection not established!");
		}
	}
	
	protected void establishComm(BluetoothSocket mmSocket) {
		mConnectedThread = new ConnectedThread(mmSocket);
		mConnectedThread.start();
		Log.d(DEBUG_TAG, "Bluetooth Connection established");
	}
	
	public void sendMessage(String message) {
		Log.d(DEBUG_TAG, "Sending " + message);
		mConnectedThread.write(message.getBytes());
	}
  
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"));
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	        	Log.d(DEBUG_TAG, "Attempting to connect through socket...");
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	        Log.d(DEBUG_TAG, "Attempting to start managing connection...");
	        // Do work to manage the connection (in a separate thread)
	        establishComm(mmSocket);
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;

	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	 
	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()
	        
	        Log.d(DEBUG_TAG, "Connected thread started");
	        
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	        	try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
	                // Send the obtained bytes to the UI activity
	                
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
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
			while(true) {
	        	Intent intent = new Intent("foregroundSwitch");
	
	        	
	        	intent.putExtra("foreground", contextTest[i]);
	        	
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        
				Log.d(DEBUG_TAG, "sent foreground switch to " +contextTest[i]);
	            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	            i++;
	        	if(i == 5) {
	        		i = 0;
	        	}
			}
		}
	}
}
