package com.google.firebase.project.blooddonor.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.project.blooddonor.R;
import com.google.firebase.project.blooddonor.models.Donor;


public class DonorViewHolder extends RecyclerView.ViewHolder {

    private TextView mName;
    private TextView mPhoneNumber;
    private TextView mBloodGroup;
    private ImageView donorPhoto;
    public ImageView messageView;
    private Context context;

    public DonorViewHolder(View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.donor_name);
        mBloodGroup = itemView.findViewById(R.id.donor_blood_group);
        donorPhoto = itemView.findViewById(R.id.donor_photo);
        messageView = itemView.findViewById(R.id.message);
        this.context =itemView.getContext();
    }

    public void bindToPost(Donor donor, View.OnClickListener messageClickListener) {
        mName.setText(donor.name);
        mBloodGroup.setText(donor.bloodGroup);
        if (donor.photoUrl != null) {

            Glide.with(context)
                    .load(donor.photoUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_anon_user_48dp)
                            .fitCenter()
                            .dontAnimate())
                    .into(donorPhoto);
        }
        messageView.setOnClickListener(messageClickListener);
    }

}
