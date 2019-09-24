package com.google.firebase.project.blooddonor.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class DonorsFragment extends DonorListFragment {

    public DonorsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All users
        return databaseReference.child("donors");
    }
}
