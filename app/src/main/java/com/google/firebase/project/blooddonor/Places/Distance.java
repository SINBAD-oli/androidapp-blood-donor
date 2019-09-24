package com.google.firebase.project.blooddonor.Places;

/**
 * Created by ASIM on 2/17/2018.
 */

public class Distance {
    private String mtext;
    private int mValue;

    public Distance() {

    }

    public Distance(String mtext, int mValue) {
        this.mtext = mtext;
        this.mValue = mValue;
    }

    public String getmtext() {
        return mtext;
    }

    public void setMtext(String mtext) {
        this.mtext = mtext;
    }

    public int getmValue() {
        return mValue;
    }

    public void setmValue(int mValue) {
        this.mValue = mValue;
    }
}
