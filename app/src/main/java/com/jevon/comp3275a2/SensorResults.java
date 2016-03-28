package com.jevon.comp3275a2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SensorResults extends AppCompatActivity implements SensorEventListener {
    private static SensorManager mSensorManager;
    private static Sensor mAccelerometer, mGyroscope, mProximity;
    private static TextView tv_x, tv_y, tv_z, tv_x_name, tv_y_name, tv_z_name;
    private static final int SENSOR_DELAY = 1500000;
    private static int sensor_type;
    private static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUI();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        /*based on SENSOR_TYPE that was passed to the activity we will know which sensor to demo
        * No need to do anything here since onResume() is always called after onCreate() in life cycle*/
        sensor_type = getIntent().getIntExtra("SENSOR_TYPE", -1);

    }

    private void setUpUI() {
        setContentView(R.layout.activity_sensor_results);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_x_name = (TextView) findViewById(R.id.tv_x_name);
        tv_y_name = (TextView) findViewById(R.id.tv_y_name);
        tv_z_name = (TextView) findViewById(R.id.tv_z_name);

        tv_x = (TextView) findViewById(R.id.tv_x_val);
        tv_y = (TextView) findViewById(R.id.tv_y_val);
        tv_z = (TextView) findViewById(R.id.tv_z_val);

        //make text views invisible initially and show only the ones we need depending on the number of values the sensor has
        makeInvisible(new View[]{tv_x, tv_x_name, tv_y, tv_y_name, tv_z, tv_z_name});
    }

    private void Accelerometer() {
        //get the sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        String found = " not found.";
        //if we have an accelerometer sensor make text views visible and register a listener for the sensor
        if (mAccelerometer != null) {
            makeVisible(new View[]{tv_x, tv_x_name, tv_y, tv_y_name, tv_z, tv_z_name});
            tv_x_name.setText("x:  ");
            mSensorManager.registerListener(this, mAccelerometer, SENSOR_DELAY);
            found = " found.";
        }
        Snackbar.make(tv_x, "Accelerometer sensor" + found, Snackbar.LENGTH_INDEFINITE).show();
    }

    private void Gyroscope() {
        //follows same process as accelerometer
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        String found = " not found.";
        if (mGyroscope != null) {
            makeVisible(new View[]{tv_x, tv_x_name, tv_y, tv_y_name, tv_z, tv_z_name});
            tv_x_name.setText("x:  ");
            mSensorManager.registerListener(this, mGyroscope, SENSOR_DELAY);
            found = " found.";
        }
        Snackbar.make(tv_x, "Gyroscope sensor" + found, Snackbar.LENGTH_INDEFINITE).show();

    }

    private void Proximity() {
//        follows same process as other 2 sensors except that it returns 1 value
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        String found = " not found.";
        if (mProximity != null) {
            makeVisible(new View[]{tv_x, tv_x_name});
            tv_x_name.setText("Poximity:  ");
            mSensorManager.registerListener(this, mProximity, SENSOR_DELAY);
            found = " found.";
        }
        Snackbar.make(tv_x, "Proximity sensor" + found, Snackbar.LENGTH_INDEFINITE).show();
    }

    private void makeInvisible(View[] views) {
        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    private void makeVisible(View[] views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) { //called every sensor_delay seconds
        if (event.sensor == mAccelerometer || event.sensor == mGyroscope) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            tv_x.setText(x + "");
            tv_y.setText(y + "");
            tv_z.setText(z + "");
        } else if (event.sensor == mProximity) {
            float val = event.values[0];
            if(val == 0)
                tv_x.setText(val + " (near)");
            else
                tv_x.setText(val + " (far)");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensor_type == Sensor.TYPE_ACCELEROMETER) {
            toolbar.setTitle("Accelerometer Results");
            Accelerometer();
        } else if (sensor_type == Sensor.TYPE_GYROSCOPE) {
            toolbar.setTitle("Gyroscope Results");
            Gyroscope();
        } else if (sensor_type == Sensor.TYPE_PROXIMITY) {
            toolbar.setTitle("Proximity Sensor Results");
            Proximity();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}

