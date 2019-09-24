package com.google.firebase.project.blooddonor.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Donor {

    public String uid;
    public String name;
    public String bloodGroup;
    public String phoneNumber;
    public String photoUrl;

    /*
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();*/
    public Donor() {

    }

    public Donor(String uid, String name, String bloodGroup, String phoneNumber, String photoUrl) {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        this.uid = uid;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.phoneNumber = phoneNumber;
        this.photoUrl = photoUrl;

    }

    public Donor(String uid, String name, String bloodGroup, String phoneNumber) {
        this.uid = uid;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.phoneNumber = phoneNumber;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("bloodGroup", bloodGroup);
        result.put("phoneNumber", phoneNumber);
        result.put("photoUrl", photoUrl);
        //result.put("starCount", starCount);
        //result.put("stars", stars);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
