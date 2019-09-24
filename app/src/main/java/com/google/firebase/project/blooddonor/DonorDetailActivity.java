package com.google.firebase.project.blooddonor;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.project.blooddonor.models.Donor;

public class DonorDetailActivity extends BaseActivity {

    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mPostReference;

    private ValueEventListener mPostListener;
    private String mPostKey;


    private TextView mNameView;
    private TextView mPhoneNumberView;
    private TextView mBloodGroupView;
    private ImageView mDonorPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_detail);

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("donors").child(mPostKey);

        // Initialize Views
        mNameView = findViewById(R.id.name);
        mBloodGroupView = findViewById(R.id.blood_group);
        mPhoneNumberView = findViewById(R.id.phone_number);
        mDonorPhoto = findViewById(R.id.donor_photo);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Donor donor = dataSnapshot.getValue(Donor.class);
                // [START_EXCLUDE]
                mNameView.setText(donor.name);
                mPhoneNumberView.setText(donor.phoneNumber);
                mBloodGroupView.setText(donor.bloodGroup);
                if (donor.photoUrl != null) {

                    Glide.with(DonorDetailActivity.this)
                            .load(donor.photoUrl)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_anon_user_48dp)
                                    .fitCenter()
                                    .dontAnimate())
                            .into(mDonorPhoto);
                }
                // [END_EXCLUDE]

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(DonorDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }
    }
}
