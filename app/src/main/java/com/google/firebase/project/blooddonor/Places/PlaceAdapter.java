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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.project.blooddonor.R;

import java.util.List;

/**
 * An {@link PlaceAdapter} knows how to create a list item layout for each place
 * in the data source (a list of {@link Place} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class PlaceAdapter extends ArrayAdapter<Place> {

    /**
     * Constructs a new {@link PlaceAdapter}.
     *
     * @param context of the app
     * @param places  is the list of earthquakes, which is the data source of the adapter
     */
    public PlaceAdapter(Context context, List<Place> places) {
        super(context, 0, places);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.place_list_item, parent, false);
        }

        Place currentPlace = getItem(position);

        // Find the TextView with view ID magnitude
        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.magnitude);
        // Format the magnitude to show 1 decimal place
        // String formattedMagnitude = formatMagnitude(currentEarthquake.getMagnitude());
        // Display the magnitude of the current earthquake in that TextView
        if (TextUtils.isEmpty(currentPlace.getDistance().getmtext())) {
            magnitudeView.setText("N/A");
        }else {
            magnitudeView.setText(currentPlace.getDistance().getmtext());
        }


        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getdistanceColor(currentPlace.getDistance().getmValue());
        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        TextView placeNameView = (TextView) listItemView.findViewById(R.id.place_name);
        placeNameView.setText(currentPlace.getmName());

        TextView placeVicinityView = (TextView) listItemView.findViewById(R.id.place_vicinity);
        placeVicinityView.setText(currentPlace.getmVicinity());

        TextView placeIdView = (TextView) listItemView.findViewById(R.id.place_id);
        placeIdView.setText(currentPlace.getmPlaceId());


        TextView latitudeView = (TextView) listItemView.findViewById(R.id.latitude);
        latitudeView.setText(Double.toString(currentPlace.getmLat()));

        TextView longitudeView = (TextView) listItemView.findViewById(R.id.longitude);
        longitudeView.setText(Double.toString(currentPlace.getmLng()));

        TextView ratingView = (TextView) listItemView.findViewById(R.id.rating);
        ratingView.setText(Double.toString(currentPlace.getmRating()));
        // Return the list item view that is now showing the appropriate data
        RatingBar ratingBar = (RatingBar) listItemView.findViewById(R.id.ratingBar);
        ratingBar.setRating((float) currentPlace.getmRating());
        return listItemView;
    }

    /**
     * Return the color for the magnitude circle based on the intensity of the earthquake.
     *
     * @param distanceFloor of the earthquake
     */
    private int getdistanceColor(int distanceFloor) {
        int distanceColorResourceId;

        if (distanceFloor <= 1000) {
            distanceColorResourceId = R.color.distance1;
        } else if (distanceFloor <= 5000) {
            distanceColorResourceId = R.color.distance2;
        } else if (distanceFloor <= 10000) {
            distanceColorResourceId = R.color.distance3;
        } else if (distanceFloor <= 15000) {
            distanceColorResourceId = R.color.distance4;
        } else if (distanceFloor <= 20000) {
            distanceColorResourceId = R.color.distance5;
        } else if (distanceFloor <= 25000) {
            distanceColorResourceId = R.color.distance6;
        } else if (distanceFloor <= 30000) {
            distanceColorResourceId = R.color.distance7;
        } else if (distanceFloor <= 350000) {
            distanceColorResourceId = R.color.distance8;
        } else if (distanceFloor <= 400000) {
            distanceColorResourceId = R.color.distance9;
        } else if (distanceFloor <= 450000) {
            distanceColorResourceId = R.color.distance10plus;
        } else {
            distanceColorResourceId = R.color.distance10plus;
        }

        return ContextCompat.getColor(getContext(), distanceColorResourceId);
    }
}
