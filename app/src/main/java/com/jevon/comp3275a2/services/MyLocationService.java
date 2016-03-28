package com.jevon.comp3275a2.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.jevon.comp3275a2.models.DBhelper;
import com.jevon.comp3275a2.models.LocationContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jevon on 27-Mar-16.
 */
public class MyLocationService extends Service {
    private static boolean isRunning  = false;
    private static final String TAG = "SERVICE";
    private static LocationManager locationManager;
    private static final String ACTION_LOCATION_RECEIVED = "com.jevon.comp3275a2.LOCATION_RECEIVED";
    private static LocationListener locationListener;
    private static int result = Activity.RESULT_CANCELED;
    private static boolean gotLocation = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service Started");
        setUpListener();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MyLocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MyLocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Criteria c = new Criteria();
            c.setAccuracy(Criteria.ACCURACY_FINE);
            String pro = locationManager.getBestProvider(c,true);
            Log.e(TAG,"provider: "+pro);
            locationManager.requestLocationUpdates(pro, 0, 0, locationListener);
        }
        return Service.START_STICKY;
    }


    private void setUpListener() {
        Log.e(TAG,"listener setup");
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                if (!gotLocation) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //instantiate helper and get db
                            final SQLiteOpenHelper helper = new DBhelper(MyLocationService.this);
                            SQLiteDatabase db = helper.getWritableDatabase();

                            //put values into contentvalues map to insert into DB
                            ContentValues cv = new ContentValues();
                            cv.put(LocationContract.LocationEntry.LAT, location.getLatitude());
                            cv.put(LocationContract.LocationEntry.LNG, location.getLongitude());
                            cv.put(LocationContract.LocationEntry.ALT, location.getAltitude());
                            //format date
                            SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss a 'on' E dd MMM yyyy", Locale.ENGLISH);
                            Date date = new Date();
                            cv.put(LocationContract.LocationEntry.TIME_ADDED, df.format(date));
                            db.insert(LocationContract.LocationEntry.TABLE_NAME, null, cv);
                            Log.e("DB","lat: "+location.getLatitude()+"   lng: "+location.getLongitude()+"  time: "+date.toString());
                            gotLocation = true;

                            //mandated to check permission. However the calling activity already handled obtaining permission
                            if (ActivityCompat.checkSelfPermission(MyLocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyLocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }

                            //stop location updates and stop service after we received a location
                            locationManager.removeUpdates(locationListener);
                            result = Activity.RESULT_OK;
                            Intent intent = new Intent();
                            intent.setAction(ACTION_LOCATION_RECEIVED);
                            sendBroadcast(intent);
                            Log.e(TAG,"service stopped");
                            stopSelf();
                        }
                    }).start();

                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    }



    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        isRunning = true;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Log.i(TAG, "Service onDestroy");
    }
}
