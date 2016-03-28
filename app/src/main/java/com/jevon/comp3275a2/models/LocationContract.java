package com.jevon.comp3275a2.models;

import android.provider.BaseColumns;

/**
 * Created by jevon on 27-Mar-16.
 */
public final class LocationContract {

    private static final String INT = " INTEGER";
    private static final String REAL = " REAL";

    public static final String CREATE_TABLE =
            "CREATE TABLE "+ LocationEntry.TABLE_NAME+ " (" +
                    LocationEntry._ID + INT + " PRIMARY KEY AUTOINCREMENT, " +
                    LocationEntry.LAT + REAL + " NOT NULL, " +
                    LocationEntry.LNG + REAL + " NOT NULL, " +
                    LocationEntry.ALT + REAL + " NOT NULL, " +
                    LocationEntry.TIME_ADDED + " NOT NULL);" ;


    public static abstract class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "storedlocations";
        public static final String LAT = "latitude";
        public static final String LNG = "longitude";
        public static final String ALT = "altitude";
        public static final String TIME_ADDED = "timeadded";
    }
}