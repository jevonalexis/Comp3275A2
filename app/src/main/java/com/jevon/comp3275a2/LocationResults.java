package com.jevon.comp3275a2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class LocationResults extends AppCompatActivity {
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int REQUEST_ENABLE_LOC = 2;
    private static TextView tv_lat, tv_lng, tv_alt;
    private static ProgressDialog dialog;
    private static final String TAG = "Location Results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_lat = (TextView) findViewById(R.id.tv_lat_val);
        tv_lng = (TextView) findViewById(R.id.tv_lng_val);
        tv_alt = (TextView) findViewById(R.id.tv_alt_val);
    }

    private void setUpListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (dialog.isShowing()) dialog.dismiss();
                tv_lat.setText(location.getLatitude() + "");
                tv_lng.setText(location.getLongitude() + "");
                tv_alt.setText(location.getAltitude() + "");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }

    private void Location() {
        //get location manager which is the access point to access location data
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //check for permissions
        if (ContextCompat.checkSelfPermission(LocationResults.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(LocationResults.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        if (isLocationOn()) {
            dialog = dialog.show(LocationResults.this, "Please Wait", "Getting Location");
            Criteria c = new Criteria();
            c.setAccuracy(Criteria.ACCURACY_FINE);
            String pro = locationManager.getBestProvider(c, true);
            Log.e(TAG,"provider: "+ pro);
            locationManager.requestLocationUpdates(pro, 0, 0, locationListener);
        }
    }

    //check if user has location enabled
    private boolean isLocationOn() {
        boolean location_enabled = false;
        try {
            location_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!location_enabled) {
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
                            Toast.makeText(getApplicationContext(), "Location Services need to be turned on", Toast.LENGTH_LONG).show();
                            finish();
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
                    //if permissions were granted restart activity with permissions granted
                    if (any_granted) {
                        Log.e(TAG,"permission granted: ");
                        Intent i = getIntent();
                        finish();
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(this, "Location permission needed", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    //Called when the user is prompted to turn on location.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_LOC) {
            //we then check if the user has enabled location services
            if (isLocationOn()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Criteria c = new Criteria();
                c.setAccuracy(Criteria.ACCURACY_FINE);
                String pro = locationManager.getBestProvider(c, true);
                Log.e(TAG, "provider: "+pro);
                locationManager.requestLocationUpdates(pro, 0, 0, locationListener);
            } else {
                Toast.makeText(this, "Location service needs to be turned on", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpListener();
        Location();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.removeUpdates(locationListener);
    }
}
