package com.example.icecreamtruckv2.Chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icecreamtruckv2.Utils.Constants;
import com.example.icecreamtruckv2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFrag extends Fragment {

    /** Database instance **/
    private DatabaseReference root, notif;

    /** UI Components **/
    private RecyclerView chat;
    private EditText mChatInput;
    private ChatLogAdapter mAdapter;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private FirebaseInstanceId fid;

    private SharedPreferences sharedPreferences;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userRole = sharedPreferences.getString("userRole", "null");
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        fid = FirebaseInstanceId.getInstance();
        return inflater.inflate(R.layout.chat_frag, parent, false);
    }


    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        chat = view.findViewById(R.id.list_of_messages);
        mAdapter = new ChatLogAdapter();
        chat.setAdapter(mAdapter);
        chat.setLayoutManager(new LinearLayoutManager(getContext()));

        try {
            root = db.getReference(Constants.CHAT_DB);
            root.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
                    mAdapter.addData(data);
                    chat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                mChatInput = view.findViewById(R.id.input);
                ChatMessage data = new ChatMessage();
                data.setMessage(mChatInput.getText().toString());
                data.setId("0");

                userRole = "ahgirl";
                if (userRole == "ahgirl") {
                    data.setIcon(R.drawable.girl);
                    data.setName("Ah Girl");
                    notif = db.getReference(Constants.CHAT_DB + "boy");
                    FirebaseMessaging.getInstance().subscribeToTopic("pushGirlNotifications");
                } else {
                    data.setIcon(R.drawable.boy);
                    data.setName("Ah Boy");
                    notif = db.getReference(Constants.CHAT_DB + "girl");
                    FirebaseMessaging.getInstance().subscribeToTopic("pushBoyNotifications");
                }

                notif.child(String.valueOf(new Date().getTime())).setValue(data);
                root.child(String.valueOf(new Date().getTime())).setValue(data);
                mChatInput.setText("");
            }
        });

    }

}
