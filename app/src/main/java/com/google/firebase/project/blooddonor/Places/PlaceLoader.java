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

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class PlaceLoader extends AsyncTaskLoader<List<Place>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = PlaceLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link PlaceLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public PlaceLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        Log.i(LOG_TAG, "On Start Loading ...");
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Place> loadInBackground() {
        Log.i(LOG_TAG, "loadInBackground.");
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Place> places = QueryUtils.fetchEarthquakeData(mUrl);
        return places;
    }
}
