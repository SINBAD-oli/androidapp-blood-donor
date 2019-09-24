/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.project.blooddonor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.project.blooddonor.Places.PlaceActivity;
import com.google.firebase.project.blooddonor.fragment.DonorsFragment;
import com.google.firebase.project.blooddonor.fragment.MyTopPostsFragment;
import com.google.firebase.project.blooddonor.fragment.RecentPostsFragment;


import java.util.Arrays;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    View mRootView;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // by default, it automatically sets focus to the first failed input field after validation is triggered
        // you can disable this behavior by
        // Step 3: set a trigger
        mRootView = findViewById(R.id.root);
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        /*inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);*/


        mFirebaseAuth = FirebaseAuth.getInstance();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed In
                    // Toast.makeText(MainActivity.this,"Welcome you are signed in to Friendly chat",Toast.LENGTH_LONG).show();
                    OnSignedInIntialize(user);

                } else {
                    //user is signed Out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    //.setIsSmartLockEnabled(false)
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                                    .setLogo(R.drawable.ic_launcher_round)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


        // Button launches NewPostActivity
        findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
            }
        });

    }

    private void OnSignedInIntialize(FirebaseUser user) {
        mDatabaseReference = mFirebaseDatabase.getReference().child("users").child(user.getUid());
        attachDatabaseReadListner();
        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new RecentPostsFragment(),
                    new DonorsFragment(),
                    new PlaceActivity(),
                    new MyTopPostsFragment(),

            };
            private final String[] mFragmentNames = new String[]{
                    getString(R.string.heading_recent),
                    getString(R.string.heading_donors),
                    getString(R.string.heading_nearby),
                    getString(R.string.heading_my_top_posts)
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void attachDatabaseReadListner() {

        if (userListener == null) {
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    if (dataSnapshot.getValue() != null) {
                        User user = dataSnapshot.getValue(User.class);
                        // ...
                    } else {
                        Intent intent = new Intent(MainActivity.this, RegisterUser.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "User is not exist ", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            mDatabaseReference.addValueEventListener(userListener);
            //populateProfile(user);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                //sign out
                AuthUI.getInstance().signOut(this);
                OnSignedOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    finish();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    finish();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    finish();
                    return;
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);

        }
    }

    private void OnSignedOut() {
        detachDatabaseReadListner();

    }

    private void detachDatabaseReadListner() {
        if (userListener != null) {
            mDatabaseReference.removeEventListener(userListener);
            userListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFirebaseAuth != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

}
