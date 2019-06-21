package anw.icecreamtruck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static anw.icecreamtruck.utils.Constants.STICKERS;
import static anw.icecreamtruck.utils.Constants.IMAGE_GIF;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;
    StorageReference stickerStorage;
    FirebaseDatabase db;
    FirebaseStorage st;
    String userId;

    ValueEventListener stickerListener, userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        st = FirebaseStorage.getInstance();

        if(auth.getCurrentUser() == null)
            login();
        else
            load();
    }

    private void login(){
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v-> {
            if (!etUsername.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
                auth.signInWithEmailAndPassword(etUsername.getText().toString().trim(), etPassword.getText().toString()).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        load();
                    } else {
                        if (task.getException() != null) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(getApplicationContext(), "Login error! Please check your username and password!", Toast.LENGTH_LONG).show();
                            } catch (FirebaseNetworkException e) {
                                Toast.makeText(getApplicationContext(), "Connection error! Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.e("DEBUG", e.getMessage());
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Username or password missing!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void load(){
        userId = auth.getUid();
        LinearLayoutCompat login_details = findViewById(R.id.login_details);
        login_details.setVisibility(View.GONE);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        initListeners();

        // Download All Stickers
        db.getReference(STICKERS).addValueEventListener(stickerListener);
    }

    private void initListeners() {
        stickerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren())
                    downloadSticker(child.getKey());

                // Init userrole
                db.getReference("users/" + userId).addValueEventListener(userListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("role").getValue() != null) {
                    String userRole = dataSnapshot.child("role").getValue().toString();

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userRole", userRole);
                    editor.putString("userId", userId);
                    editor.apply();

                    db.getReference(STICKERS).removeEventListener(stickerListener);
                    db.getReference("users/" + userId).removeEventListener(userListener);

                    Log.e("Initialization", "Complete");

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
    private void downloadSticker(String filename) {
        try {
            FileInputStream file = getApplicationContext().openFileInput(filename);
        } catch (Exception e) {
            stickerStorage = st.getReference(STICKERS).child(filename);
            stickerStorage.getBytes(IMAGE_GIF).addOnSuccessListener(bytes -> {
                try {
                    FileOutputStream outputStream = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(bytes);
                    outputStream.close();
                    Log.e("Downloaded", filename);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }).addOnFailureListener(exception -> {
                Log.e("Error", "Failed to get sticker");
            });
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        db.getReference(STICKERS).removeEventListener(stickerListener);
        db.getReference("users/" + userId).removeEventListener(userListener);
    }
}
