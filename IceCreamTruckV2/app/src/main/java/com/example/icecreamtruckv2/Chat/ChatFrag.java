package com.example.icecreamtruckv2.Chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icecreamtruckv2.Constants;
import com.example.icecreamtruckv2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFrag extends Fragment {

    /** Database instance **/
    private DatabaseReference mReference;

    /** UI Components **/
    private EditText mChatInput;
    private ChatLogAdapter mAdapter;

    public static final int isGirl = 0; // 0 means is Ah Boy chat, 1 means is Ah Girl chat
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.chat_frag, parent, false);
    }


    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        RecyclerView chat = view.findViewById(R.id.list_of_messages);
        chat.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ChatLogAdapter();
        chat.setAdapter(mAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mReference = database.getReference(Constants.CHAT_DB);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(Constants.CHAT_LOG_TAG, "SUCCESS!");
                mAdapter.clearData();

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatMessage data = item.getValue(ChatMessage.class);
                    mAdapter.addData(data);
                }

                RecyclerView rv = view.findViewById(R.id.list_of_messages);
                if(mAdapter.getItemCount() >= 1)
                {
                    rv.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(Constants.CHAT_LOG_TAG, "ERROR: " + databaseError.getMessage());
                Toast.makeText(getContext(), R.string.chat_init_error, Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                mChatInput = view.findViewById(R.id.input);
                ChatMessage data = new ChatMessage();
                data.setMessage(mChatInput.getText().toString());
                data.setId("0");

                if (isGirl == 1) {
                    data.setIcon(R.drawable.girl);
                    data.setName("Ah Girl");
                } else {
                    data.setIcon(R.drawable.boy);
                    data.setName("Ah Boy");
                }

                mReference.child(String.valueOf(new Date().getTime())).setValue(data);
                mChatInput.setText("");
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
    }
}
