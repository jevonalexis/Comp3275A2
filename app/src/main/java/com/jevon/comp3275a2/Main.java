package com.jevon.comp3275a2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jevon.comp3275a2.adapters.MainAdapter;
import com.jevon.comp3275a2.services.MyLocationService;

import java.util.ArrayList;
import java.util.Arrays;

public class Main extends AppCompatActivity {
    private static final String ACTION_LOCATION_RECEIVED = "com.jevon.comp3275a2.LOCATION_RECEIVED";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1 ;
    private static final int REQUEST_ENABLE_LOC= 2;
    private static LocationManager locationManager;
    private static final String TAG = "main";
    private ListView lv;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_LOCATION_RECEIVED.equals(action)){
                Toast.makeText(Main.this,"Location saved in database",Toast.LENGTH_LONG).show();
                Log.e(TAG,"location saved to DB");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpListView();

        //register receiver with intentfilter
        IntentFilter filter = new IntentFilter(ACTION_LOCATION_RECEIVED);
        registerReceiver(mReceiver, filter);
        getLocation();

    }

    private void setUpListView(){
        String[] items = {"Accelerometer","Gyroscope","Proximity", "Location", "Bluetooth", "View All Stored"};

        lv = (ListView) findViewById(R.id.lv_main);
        MainAdapter adapter = new MainAdapter(Main.this, new ArrayList<>(Arrays.asList(items)));
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent i = new Intent(Main.this,SensorResults.class);
                    i.putExtra("SENSOR_TYPE",Sensor.TYPE_ACCELEROMETER);
                    startActivity(i);
                }
                else if(position == 1){
                    Intent i = new Intent(Main.this,SensorResults.class);
                    i.putExtra("SENSOR_TYPE",Sensor.TYPE_GYROSCOPE);
                    startActivity(i);
                }
                else if(position == 2){
                    Intent i = new Intent(Main.this,SensorResults.class);
                    i.putExtra("SENSOR_TYPE",Sensor.TYPE_PROXIMITY);
                    startActivity(i);
                }
                else if(position == 3)
                    startActivity(new Intent(Main.this,LocationResults.class));
                else if(position == 4)
                    startActivity(new Intent(Main.this,Bluetooth.class));
                else if(position == 5)
                    startActivity(new Intent(Main.this,AllStoredLocations.class));
            }
        });
    }

    private void getLocation() {
        Log.e(TAG, "getlocation() called");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //check for permissions
        if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else if(isLocationOn()){
            Log.e(TAG,"service called");
            startService(new Intent(Main.this, MyLocationService.class));
        }
    }



    //if location services are not enabled prompt the user to enable it via a dialog
    private boolean isLocationOn(){
        boolean location_enabled = false;
        try{
            location_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){ ex.printStackTrace();}
        if(!location_enabled){
            new AlertDialog.Builder(this).setMessage("Location services need to be enabled")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_LOC);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Snackbar.make(findViewById(R.id.lv_main), "Could not save location. Location Services need to be turned on", Snackbar.LENGTH_LONG)
                                    .setAction("Turn on", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_LOC);
                                        }
                                    })
                                    .show();
                        }
                    }).show();
        }
        return location_enabled;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0) {
                    boolean any_granted = false;
                    //check if any permissions were granted
                    for (int i : grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED)
                            any_granted = true;
                    }
                    if (any_granted && isLocationOn())
                        startService(new Intent(Main.this, MyLocationService.class));
                }
                else {
                    Toast.makeText(this, "Location permission needed to save location", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //Call when the user is prompted to turn on location.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_LOC) {
            if (isLocationOn()) {
                startService(new Intent(Main.this, MyLocationService.class));
            } else {
                Toast.makeText(this, "Location service needs to be turned on", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
