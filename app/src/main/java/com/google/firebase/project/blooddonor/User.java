package com.google.firebase.project.blooddonor;

/**
 * Created by Oliver Cimafranca on 2/23/2018.
 */

public class User {
    private String mName;
    private String mEmail;
    private String mPhoneNumber;
    private String mBloodGroup;
    private Boolean mEligible;
    private String mphotoUrl;

    public User(){

    }
    public User(String mName, String mEmail, String mPhoneNumber, String mBloodGroup, Boolean mEligible, String mphotoUrl) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.mPhoneNumber = mPhoneNumber;
        this.mBloodGroup = mBloodGroup;
        this.mEligible = mEligible;
        this.mphotoUrl = mphotoUrl;
    }


    public User(String mName, String mEmail, String mPhoneNumber, String mBloodGroup, Boolean mEligible) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.mPhoneNumber = mPhoneNumber;
        this.mBloodGroup = mBloodGroup;
        this.mEligible = mEligible;
    }


    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmBloodGroup() {
        return mBloodGroup;
    }

    public void setmBloodGroup(String mBloodGroup) {
        this.mBloodGroup = mBloodGroup;
    }

    public Boolean getmEligible() {
        return mEligible;
    }

    public void setmEligible(Boolean mEligible) {
        this.mEligible = mEligible;
    }

    public String getMphotoUrl() {
        return mphotoUrl;
    }

    public void setMphotoUrl(String mphotoUrl) {
        this.mphotoUrl = mphotoUrl;
    }
}
