package com.fydp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.fydp.smarttouchassistant.R;

/**
 * Created by Keith on 14/12/13.
 */
public class EnableBluetoothDialogFragment extends DialogFragment {

    private BluetoothAdapter bluetoothAdapter;
    private Intent intent;

    public EnableBluetoothDialogFragment(BluetoothAdapter bluetoothAdapter, Intent intent) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.intent = intent;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Dialog);
        builder.setTitle("Turn on Bluetooth?")
                .setIcon(R.drawable.bluetooth_not_turned_on_icon)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bluetoothAdapter.enable();
                        startActivity(intent);
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        return builder.create();
    }
}
