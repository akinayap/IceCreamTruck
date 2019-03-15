package com.akina.icecreamtruck;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;


public class MainActivity extends AppCompatActivity {

    /** Database instance **/
    private DatabaseReference mReference;

    /** UI Components **/
    private EditText mChatInput;
    private ChatLogAdapter mAdapter;

    private static final String TAG = "MainActivity";
    private static final int SIGN_IN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen

        setContentView(R.layout.activity_main);


        // Initialize Chat Database
        RecyclerView chat = findViewById(R.id.list_of_messages);
        chat.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatLogAdapter();
        chat.setAdapter(mAdapter);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //To do//
                            return;
                        }

                        // Get the Instance ID token//
                        String token = task.getResult().getToken();
                        String msg = getString(R.string.fcm_token, token);
                        Log.d(TAG, msg);

                    }
                });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            mReference = database.getReference(Constants.CHAT_DB);
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(Constants.CHAT_LOG_TAG,"SUCCESS!");
                    mAdapter.clearData();

                    for(DataSnapshot item : dataSnapshot.getChildren()) {
                        ChatMessage data = item.getValue(ChatMessage.class);
                        mAdapter.addData(data);
                    }

                    RecyclerView rv = findViewById(R.id.list_of_messages);
                    rv.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(Constants.CHAT_LOG_TAG,"ERROR: " + databaseError.getMessage());
                    Toast.makeText(getBaseContext(), R.string.chat_init_error, Toast.LENGTH_SHORT).show();
                }
            });

            FloatingActionButton fab = findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mChatInput = findViewById(R.id.input);
                    ChatMessage data = new ChatMessage();
                    data.setMessage(mChatInput.getText().toString());
                    data.setIcon(R.drawable.tiger);
                    data.setId("0");
                    data.setName("Ah Boy");

                    mReference.child(String.valueOf(new Date().getTime())).setValue(data);
                    mChatInput.setText("");
                }
            });

            FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        } else {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        }
    }

}
