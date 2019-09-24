package com.google.firebase.project.blooddonor.messaging;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.project.blooddonor.R;
import com.google.firebase.project.blooddonor.models.Donor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class demonstrating how to setup a {@link RecyclerView} with an adapter while taking sign-in
 * states into consideration. Also demonstrates adding data to a ref and then reading it back using
 * the {@link FirebaseRecyclerAdapter} to build a simple chat app.
 * <p>
 * For a general intro to the RecyclerView, see <a href="https://developer.android.com/training/material/lists-cards.html">Creating
 * Lists</a>.
 */
public class RealtimeDbChatActivity extends AppCompatActivity
        implements FirebaseAuth.AuthStateListener {
    private static final String TAG = "RealtimeDatabaseDemo";
    public static final String EXTRA_POST_KEY = "post_key";
    private String mPostKey;
    private DatabaseReference mPostReference;
    private ValueEventListener mPostListener;
    private Donor donor;
    FirebaseRecyclerOptions<Chat> options;


    public static final String ARG_CHAT_ROOMS = "chats";


    /**
     * Get the last 50 chat messages.
     */
    protected static final Query sChatQuery =
            FirebaseDatabase.getInstance().getReference().child("chats").limitToLast(50);

    @BindView(R.id.messagesList)
    RecyclerView mRecyclerView;

    @BindView(R.id.sendButton)
    Button mSendButton;

    @BindView(R.id.messageEdit)
    EditText mMessageEdit;

    @BindView(R.id.emptyTextView)
    TextView mEmptyListMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("donors").child(mPostKey);


        ImeHelper.setImeOnDoneListener(mMessageEdit, new ImeHelper.DonePressedListener() {
            @Override
            public void onDonePressed() {
                onSendClick();
            }
        });

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
                donor = dataSnapshot.getValue(Donor.class);
                // [START_EXCLUDE]
                if (isSignedIn()) {

                    attachRecyclerViewAdapter();

                }
                // [END_EXCLUDE]

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(RealtimeDbChatActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;


        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        mSendButton.setEnabled(isSignedIn());
        mMessageEdit.setEnabled(isSignedIn());

        if (isSignedIn()) {
            // attachRecyclerViewAdapter();
        } else {
            // Toast.makeText(this, R.string.signing_in, Toast.LENGTH_SHORT).show();
            // auth.signInAnonymously().addOnCompleteListener(new SignInResultNotifier(this));
        }
    }

    private boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void attachRecyclerViewAdapter() {
        /*final RecyclerView.Adapter adapter = newAdapter(getUid(), donor.uid);

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });


        mRecyclerView.setAdapter(adapter);*/
        newAdapter(getUid(), donor.uid);
    }

    @OnClick(R.id.sendButton)
    public void onSendClick() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        onAddMessage(new Chat(name, donor.name, uid, donor.uid, mMessageEdit.getText().toString()));

        mMessageEdit.setText("");
    }

    protected void newAdapter(String senderUid, String receiverUid) {

        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        final Query sChatQuery =
                FirebaseDatabase.getInstance().getReference().child("chats").child(room_type_1);
        options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(sChatQuery, Chat.class)
                .setLifecycleOwner(RealtimeDbChatActivity.this)
                .build();


        databaseReference.child(ARG_CHAT_ROOMS)
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                            final Query sChatQuery =
                                    FirebaseDatabase.getInstance().getReference().child("chats").child(room_type_1);
                            options = new FirebaseRecyclerOptions.Builder<Chat>()
                                    .setQuery(sChatQuery, Chat.class)
                                    .setLifecycleOwner(RealtimeDbChatActivity.this)
                                    .build();
                            final RecyclerView.Adapter adapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(options) {
                                @Override
                                public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    return new ChatHolder(LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.message, parent, false));
                                }

                                @Override
                                protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Chat model) {
                                    holder.bind(model);
                                }

                                @Override
                                public void onDataChanged() {
                                    // If there are no chat messages, show a view that invites the user to add a message.
                                    mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);

                                }
                            };

                            // Scroll to bottom on new messages
                            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onItemRangeInserted(int positionStart, int itemCount) {
                                    mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
                                }
                            });


                            mRecyclerView.setAdapter(adapter);
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                            final Query sChatQuery =
                                    FirebaseDatabase.getInstance().getReference().child("chats").child(room_type_2);
                            options = new FirebaseRecyclerOptions.Builder<Chat>()
                                    .setQuery(sChatQuery, Chat.class)
                                    .setLifecycleOwner(RealtimeDbChatActivity.this)
                                    .build();
                            final RecyclerView.Adapter adapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(options) {
                                @Override
                                public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    return new ChatHolder(LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.message, parent, false));
                                }

                                @Override
                                protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Chat model) {
                                    holder.bind(model);
                                }

                                @Override
                                public void onDataChanged() {
                                    // If there are no chat messages, show a view that invites the user to add a message.
                                    mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);

                                }
                            };

                            // Scroll to bottom on new messages
                            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onItemRangeInserted(int positionStart, int itemCount) {
                                    mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
                                }
                            });


                            mRecyclerView.setAdapter(adapter);
                        } else {
                            Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                            final Query sChatQuery =
                                    FirebaseDatabase.getInstance().getReference().child("chats").child(room_type_1);
                            options = new FirebaseRecyclerOptions.Builder<Chat>()
                                    .setQuery(sChatQuery, Chat.class)
                                    .setLifecycleOwner(RealtimeDbChatActivity.this)
                                    .build();
                            final RecyclerView.Adapter adapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(options) {
                                @Override
                                public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    return new ChatHolder(LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.message, parent, false));
                                }

                                @Override
                                protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Chat model) {
                                    holder.bind(model);
                                }

                                @Override
                                public void onDataChanged() {
                                    // If there are no chat messages, show a view that invites the user to add a message.
                                    mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);

                                }
                            };

                            // Scroll to bottom on new messages
                            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onItemRangeInserted(int positionStart, int itemCount) {
                                    mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
                                }
                            });


                            mRecyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to get message
                    }
                });

    }


    protected void onAddMessage(final Chat chat) {

        final String room_type_1 = chat.senderUid + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child(ARG_CHAT_ROOMS)
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                            final Query sChatQuery =
                                    FirebaseDatabase.getInstance().getReference().child("chats").child(room_type_1);
                            sChatQuery.getRef().push().setValue(chat, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference reference) {
                                    if (error != null) {
                                        Log.e(TAG, "Failed to write message", error.toException());
                                    }
                                }
                            });
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                            final Query sChatQuery =
                                    FirebaseDatabase.getInstance().getReference().child("chats").child(room_type_2);
                            sChatQuery.getRef().push().setValue(chat, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference reference) {
                                    if (error != null) {
                                        Log.e(TAG, "Failed to write message", error.toException());
                                    }
                                }
                            });
                        } else {
                            Log.e(TAG, "sendMessageToFirebaseUser: success");
                            final Query sChatQuery =
                                    FirebaseDatabase.getInstance().getReference().child("chats").child(room_type_1);
                            sChatQuery.getRef().push().setValue(chat, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference reference) {
                                    if (error != null) {
                                        Log.e(TAG, "Failed to write message", error.toException());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to send message.
                    }
                });

    }

}
