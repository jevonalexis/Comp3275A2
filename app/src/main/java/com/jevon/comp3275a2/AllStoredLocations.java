package com.jevon.comp3275a2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import com.jevon.comp3275a2.adapters.LocationDataAdapter;
import com.jevon.comp3275a2.models.DBhelper;
import com.jevon.comp3275a2.models.LocationContract;
import com.jevon.comp3275a2.pojo.CustomLocation;

public class AllStoredLocations extends AppCompatActivity {
    private static ListView lv_locs;
    private static ArrayAdapter<CustomLocation> adapter;
    private static ArrayList<CustomLocation> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stored_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpList();
        loadData();
    }

    private void setUpList(){
        locations = new ArrayList<>();
        lv_locs = (ListView) findViewById(R.id.lv_locs);
        adapter = new LocationDataAdapter(this,locations);
        lv_locs.setAdapter(adapter);
    }

    private void loadData(){

        final Context context = this;

        (new Thread(new Runnable() {
            @Override
            public void run() {
                final SQLiteDatabase db = (new DBhelper(context)).getReadableDatabase();
                Cursor c = db.query(LocationContract.LocationEntry.TABLE_NAME, new String[]{LocationContract.LocationEntry.LAT,
                        LocationContract.LocationEntry.LNG,LocationContract.LocationEntry.ALT,
                        LocationContract.LocationEntry.TIME_ADDED},null,null,null,null, LocationContract.LocationEntry.TIME_ADDED + " DESC");

                while (c.moveToNext()){
                    double lat = c.getDouble(c.getColumnIndex(LocationContract.LocationEntry.LAT));
                    double lng = c.getDouble(c.getColumnIndex(LocationContract.LocationEntry.LNG));
                    double alt = c.getDouble(c.getColumnIndex(LocationContract.LocationEntry.ALT));
                    String time = c.getString(c.getColumnIndex(LocationContract.LocationEntry.TIME_ADDED));
                    locations.add(new CustomLocation(lat, lng, alt, time));
                }
                lv_locs.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        })).start();

    }

}
