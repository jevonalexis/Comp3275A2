package com.jevon.comp3275a2;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Bluetooth extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 81;
    private final int REQUEST_PERMISSION_BT = 82;
    private ListView lv;
    private ArrayAdapter<String> mArrayAdapter;
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\t" + device.getAddress());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lv = (ListView) findViewById(R.id.listView);
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lv.setAdapter(mArrayAdapter);
        mArrayAdapter.clear(); //clear previous list of discovered devices
        bluetooth();
    }


    private void bluetooth() {
        //Permissions necessary for API 23+
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_PERMISSION_BT);
        }
        // If permissions are granted
        else {
            //get adapter and register receiver
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
            //if no adapter found inform user
            if (mBluetoothAdapter == null) {
                new AlertDialog.Builder(this).setTitle("Bluetooth not supported")
                        .setMessage("This device does not support bluetooth unfortunately")
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false).show();
                finish();
            }
            //if bluetooth is turned off prompt the user to turn it on
            else if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            //start scanning for devices
            else
                mBluetoothAdapter.startDiscovery();
        }
    }

    //Called when the user is prompted to turn on bluetooth. Returns result_ok if it was turned on.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                mBluetoothAdapter.startDiscovery();
            } else {
                Toast.makeText(this, "Bluetooth needs to be turned on", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_BT:
                if (grantResults.length > 0) {
                    boolean all_granted = true;
                    //check if all permissions were granted
                    for (int i : grantResults) {
                        if (i != PackageManager.PERMISSION_GRANTED)
                            all_granted = false;
                    }
                    //if all permissions were granted restart activity with permissions granted
                    if (all_granted) {
                        Intent i = getIntent();
                        finish();
                        startActivity(i);
                    } else {
                        Toast.makeText(this, "Bluetooth and Location permissions needed", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    break;
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if the receiver is registered unregister it. try catch used since there is no method to check if is registered
        try {
            unregisterReceiver(mReceiver);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }
}

