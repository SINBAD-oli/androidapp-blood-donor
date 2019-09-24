package com.google.firebase.project.blooddonor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.project.blooddonor.DonorDetailActivity;
import com.google.firebase.project.blooddonor.PostDetailActivity;
import com.google.firebase.project.blooddonor.R;
import com.google.firebase.project.blooddonor.messaging.RealtimeDbChatActivity;
import com.google.firebase.project.blooddonor.models.Donor;
import com.google.firebase.project.blooddonor.models.Post;
import com.google.firebase.project.blooddonor.viewholder.DonorViewHolder;


public abstract class DonorListFragment extends Fragment {

    private static final String TAG = "DonorListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Donor, DonorViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public DonorListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_donors, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.donors_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Donor>()
                .setQuery(postsQuery, Donor.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Donor, DonorViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull DonorViewHolder viewHolder, int position, @NonNull Donor model) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), DonorDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });
                // Bind Post to ViewHolder, setting OnClickListener for the star button
                //viewHolder.bindToPost(model, getContext());
                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model,new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalDonorRef = mDatabase.child("donors").child(postRef.getKey());
                       // DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), RealtimeDbChatActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                        // Run two transactions
                        //onStarClicked(globalPostRef);
                        //onStarClicked(userPostRef);
                    }
                });

            }

            @Override
            public DonorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new DonorViewHolder(inflater.inflate(R.layout.item_donor, viewGroup, false));
            }

            /*@Override
            protected void onBindViewHolder(PostViewHolder viewHolder, int position, final Donor model) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                });
            }*/
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "donorTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
