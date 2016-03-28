package com.jevon.comp3275a2.pojo;

/**
 * Created by jevon on 27-Mar-16.
 */
public class CustomLocation {
    private double latitude, longitude, altitude;
    private String time;

    public CustomLocation(double latitude, double longitude, double altitude, String time) {
        this.time = time;
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getTime() {
        return time;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
