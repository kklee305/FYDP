package com.fydp.smarttouchassistant;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.fydp.service.BluetoothConnectionService;

public class MainScreen extends Activity {

    BluetoothConnectionService btService;
    boolean isBound = false;

    public final static String EXTRA_MESSAGE = "com.fydp.smarttouchassistant.MESSAGE";

    private Button useBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initUI() {
        useBluetooth = (Button) findViewById(R.id.use_bluetooth);
        useBluetooth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseBluetooth();
            }
        });
    }

    public void chooseBluetooth() {
        //ready the intent to go to next activity
        final Intent intent = new Intent(this, BluetoothConnectionActivity.class);

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "This device does not support BlueTooth!", Toast.LENGTH_SHORT);
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                // custom dialog
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_bluetooth_turned_on);

                Button dialogButtonYes = (Button) dialog.findViewById(R.id.bluetoothButtonOK);
                // if button is clicked, close the custom dialog
                dialogButtonYes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        mBluetoothAdapter.enable();
                        startActivity(intent);
                    }
                });

                Button dialogButtonNo = (Button) dialog.findViewById(R.id.bluetoothButtonNo);
                // if button is clicked, close the custom dialog
                dialogButtonNo.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            } else {
                startActivity(intent);
            }
        }
    }


}
