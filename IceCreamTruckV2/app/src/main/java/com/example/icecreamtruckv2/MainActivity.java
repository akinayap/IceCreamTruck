package com.example.icecreamtruckv2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.icecreamtruckv2.Chat.ChatFrag;
import com.example.icecreamtruckv2.Home.HomeFrag;
import com.example.icecreamtruckv2.Money.MoneyFrag;
import com.example.icecreamtruckv2.Notification.NotificationFrag;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private FirebaseInstanceId fid;
    private String userRole, userUID;
    private SharedPreferences sharedPreferences;


    private static final String TAG = "MainActivity";
    private static final int SIGN_IN_REQUEST_CODE = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Begin the transaction
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_home:
                    ft.replace(R.id.frag, new HomeFrag());
                    ft.commit();
                    return true;
                case R.id.nav_money:
                    ft.replace(R.id.frag, new MoneyFrag());
                    ft.commit();
                    return true;
                case R.id.nav_chat:
                    ft.replace(R.id.frag, new ChatFrag());
                    ft.commit();
                    return true;
                case R.id.nav_notifications:
                    ft.replace(R.id.frag, new NotificationFrag());
                    ft.commit();
                    return true;
            }
            ft.commit();
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("Home");
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        fid = FirebaseInstanceId.getInstance();
        userUID = auth.getCurrentUser().getUid();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.activity_main);

        setMyInfo();

    }

    private void setMyInfo() {
        DatabaseReference root;
        root = db.getReference("users/" + userUID);
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("role").getValue() != null) {
                    userRole = dataSnapshot.child("role").getValue().toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userRole", userRole);
                    editor.apply();
                }

                BottomNavigationView navigation = findViewById(R.id.navigation);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frag, new HomeFrag());
                ft.commit();

                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DEBUG", databaseError.toException().toString());
            }
        });
    }
}
