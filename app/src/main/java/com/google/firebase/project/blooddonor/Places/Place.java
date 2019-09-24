/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.project.blooddonor.Places;

/**
 * An {@link Place} object contains information related to a single Place.
 */
public class Place extends PlaceLocation {


    /**
     * PlaceLocation of the place
     */
    private String mName;

    /**
     * PlaceId of the place
     */
    private String mPlaceId;

    /**
     * Vicinity of the place
     */
    private String mVicinity;

    private double mRating;

    private Distance mDistance;

    /**
     * Constructs a new {@link Place} object.
     *
     * @param mLat      is the Latitude of the place
     * @param mLng      is the Longitude of the place
     * @param mLat      is the Latitude (size) of the place
     * @param mName     is the Name where the place
     * @param mVicinity is the address nearby
     * @param mPlaceId  is the unique place Id
     */

    public Place(double mLat, double mLng, String mName, Distance distance, String mVicinity, double mRating) {
        super(mLat, mLng);
        this.mName = mName;
        this.mPlaceId = mPlaceId;
        this.mVicinity = mVicinity;
        this.mRating = mRating;
        this.mDistance = distance;
    }

    public Place(double mLat, double mLng, String mName, String mPlaceId, String mVicinity, double mRating) {
        super(mLat, mLng);
        this.mDistance = new Distance();
        this.mName = mName;
        this.mPlaceId = mPlaceId;
        this.mVicinity = mVicinity;
        this.mRating = mRating;
    }

    public Place(String mName, String mPlaceId, String mVicinity) {
        this.mName = mName;
        this.mPlaceId = mPlaceId;
        this.mVicinity = mVicinity;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPlaceId() {
        return mPlaceId;
    }

    public void setmPlaceId(String mPlaceId) {
        this.mPlaceId = mPlaceId;
    }

    public String getmVicinity() {
        return mVicinity;
    }


    public void setmVicinity(String mVicinity) {
        this.mVicinity = mVicinity;
    }

    public double getmRating() {
        return mRating;
    }

    public void setmRating(float mRating) {
        this.mRating = mRating;
    }

    public Distance getDistance() {
        return mDistance;
    }

    public void setDistance(Distance distance) {
        this.mDistance = distance;
    }
}
