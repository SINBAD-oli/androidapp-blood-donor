package com.google.firebase.project.blooddonor;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.project.blooddonor.models.Donor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ASIM on 2/23/2018.
 */

public class RegisterUser extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    ImageView mUserProfilePicture;

    private EditText inputName, inputEmail, inputPhone;
    private Button btnSignUp;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user_activity);

        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        mUserProfilePicture = (ImageView) findViewById(R.id.profile_image);
        spinner = (Spinner) findViewById(R.id.blood_group_spinner);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_eligible);


        mAuth = FirebaseAuth.getInstance();


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        populateProfile(user);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();
                String phoneNumber = inputPhone.getText().toString();
                String bloodGroup = spinner.getSelectedItem().toString();
                boolean eligibility = checkBox.isChecked();
                Uri photoUrl = user.getPhotoUrl();
                String photoString = "";
                if (photoUrl != null) {
                    photoString = photoUrl.toString();
                }
                writeNewUser(user.getUid(), name, email, phoneNumber, bloodGroup, eligibility, photoString);
                finish();
            }
        });

    }

    private void populateProfile(FirebaseUser user) {
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {

            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_anon_user_48dp)
                            .fitCenter()
                            .dontAnimate())
                    .into(mUserProfilePicture);
        }

        inputEmail.setText(
                TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());
        inputPhone.setText(
                TextUtils.isEmpty(user.getPhoneNumber()) ? "" : user.getPhoneNumber());
        inputName.setText(
                TextUtils.isEmpty(user.getDisplayName()) ? "No display name" : user.getDisplayName());


    }

    private void writeNewUser(String userId, String name, String email, String phoneNumber, String bloodGroup, Boolean
            eligibility, String photoUrl) {
        User user = new User(name, email, phoneNumber, bloodGroup, eligibility, photoUrl);

        mDatabaseReference.child("users").child(userId).setValue(user);
        if (eligibility) {
            writeNewDonor(userId, name, bloodGroup, phoneNumber, photoUrl);
        }
    }


    // [START write_fan_out]
    private void writeNewDonor(String userId, String username, String bloodGroup, String phoneNumer, String photoUrl) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabaseReference.child("donors").push().getKey();
        Donor donor = new Donor(userId, username, bloodGroup, phoneNumer, photoUrl);
        Map<String, Object> postValues = donor.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/donors/" + key, postValues);

        mDatabaseReference.updateChildren(childUpdates);
    }
    // [END write_fan_out]
}
