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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.project.blooddonor.R;

import android.widget.Toast;

import static android.app.Activity.RESULT_OK;


public class PlaceActivity extends Fragment implements LoaderManager.LoaderCallbacks<List<Place>> {
    Button locationButton,searchButton;
    /**
     * URL for earthquake data from the USGS dataset
     */
    private static String MAPS_REQUEST_URL;
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    final int PLACE_PICKER_REQUEST = 2;
    private Location mLastKnownLocation;
    private boolean mLocationPermissionGranted = false;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * Adapter for the list of earthquakes
     */
    private PlaceLocation currentLocation = new PlaceLocation();
    private PlaceAdapter mAdapter;

    public static final String LOG_TAG = PlaceActivity.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.place_activity, container, false);
        //setContentView(R.layout.place_activity);
        locationButton = (Button) rootView.findViewById(R.id.location_button);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {

                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.i(LOG_TAG, "latitude" + location.getLatitude() + " longitude " + location.getLongitude());
                                    currentLocation.setmLat(location.getLatitude());
                                    currentLocation.setmLng(location.getLongitude());
                                    MAPS_REQUEST_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + currentLocation.getmLat() + ","
                                            + currentLocation.getmLng() + "&radius=50000&keyword=blood+bank&key=AIzaSyBwkd-s-vuQiNoPN3dgWNegOrydB-Od54M";
                                    Log.i(LOG_TAG, "latitude" + currentLocation.getmLat() + " longitude " + currentLocation.getmLng());

                                    // Get a reference to the LoaderManager, in order to interact with loaders.

                                    LoaderManager loaderManager = getLoaderManager();

                                    // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                                    // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                                    // because this activity implements the LoaderCallbacks interface).
                                    loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, PlaceActivity.this);
                                    getLoaderManager().getLoader(EARTHQUAKE_LOADER_ID).startLoading();
                                }
                            }
                        });

               /* Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(PlaceActivity.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            Log.i(LOG_TAG,"Location : "+mLastKnownLocation.getLongitude()+","+mLastKnownLocation.getLatitude());
                            location.setmLat(mLastKnownLocation.getLatitude());
                            location.setmLng(mLastKnownLocation.getLongitude());


                            //textView.setText(mLastKnownLocation.getLatitude()+","+mLastKnownLocation.getLongitude());
                        } else {
                            Log.d(LOG_TAG, "Current location is null. Using defaults.");
                            Log.e(LOG_TAG, "Exception: %s", task.getException());
                        }
                    }
                });*/
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        // Create a fake list of places.
        //  ArrayList<Place> places = new ArrayList<>();
        // places.add(new Place(-33.819748,151.0195536,"Australian Red Cross Blood Service Parramatta Donor Centre","ChIJ75HJjBWjEmsRUUMiqhcKp3Y","22 Oak St, Rosehill",4.7));

        //  ArrayList<Place> places = QueryUtils.extractPlaces();
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) getActivity().findViewById(R.id.list);
        mEmptyStateTextView = (TextView) getActivity().findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new PlaceAdapter(getActivity(), new ArrayList<Place>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Place currentPlace = mAdapter.getItem(position);
                // Create a Uri from an intent string. Use the result to create an Intent.
                //Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+currentPlace.getmLat()+","
                //+currentPlace.getmLng());
                Uri gmmIntentUri = Uri.parse("geo:" + currentPlace.getmLat() + ","
                        + currentPlace.getmLng() + "?q=" + Uri.encode(currentPlace.getmName()));
                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Attempt to start an activity that can handle the Intent
                    startActivity(mapIntent);
                } else {
                    //Toast.makeText(PlaceActivity.this,"Cannot Load maps , No maps App found.",Toast.LENGTH_LONG).show();
                    Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(R.id.list),
                            R.string.unavailable_maps_app, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            getLocationPermission();
            if (mLocationPermissionGranted) {

                getDeviceLocation();


            }

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


    }

    @Override
    public Loader<List<Place>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the given URL
        Log.i(LOG_TAG, "onCreateLoader.");
        return new PlaceLoader(getActivity(), MAPS_REQUEST_URL);
    }


    @Override
    public void onLoadFinished(Loader<List<Place>> loader, List<Place> earthquakes) {
        Log.i(LOG_TAG, "onLoadFinished.");
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_earthquakes);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);

            int DISTANCE_LOADER_ID = 5;
            for (final Place place : earthquakes) {
                DISTANCE_LOADER_ID++;
                String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + currentLocation.getmLat() + "," +
                        currentLocation.getmLng() + "&destinations=place_id:" + place.getmPlaceId() + "&key=AIzaSyBwkd-s-vuQiNoPN3dgWNegOrydB-Od54M";
                final String finalURL = URL;
                LoaderManager.LoaderCallbacks<Distance> distanceLoaderListener
                        = new LoaderManager.LoaderCallbacks<Distance>() {
                    @Override
                    public Loader<Distance> onCreateLoader(int id, Bundle args) {
                        // Create a new loader for the given URL
                        Log.i(LOG_TAG, "Inner onCreateLoader.");
                        return new DistanceLoader(getActivity(), finalURL);
                    }

                    @Override
                    public void onLoadFinished(Loader<Distance> loader, Distance data) {
                        place.setDistance(data);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onLoaderReset(Loader<Distance> loader) {

                    }
                };
                LoaderManager loaderManager = getLoaderManager();

                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).

                loaderManager.initLoader(DISTANCE_LOADER_ID, null, distanceLoaderListener);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<List<Place>> loader) {
    // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "onLoaderReset.");
        mAdapter.clear();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place place = PlacePicker.getPlace(data, getActivity());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}

