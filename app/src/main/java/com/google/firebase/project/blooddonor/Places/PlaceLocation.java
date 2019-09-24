package com.google.firebase.project.blooddonor.Places;

/**
 * Created by ASIM on 2/14/2018.
 */

public class PlaceLocation {
    /** Latitude of the location */
    private double mLat;

    /** Longitude of the location */
    private double mLng;

    public PlaceLocation() {
    }

    public PlaceLocation(double mLat, double mLng) {
        this.mLat = mLat;
        this.mLng = mLng;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLng() {
        return mLng;
    }

    public void setmLng(double mLng) {
        this.mLng = mLng;
    }
}
