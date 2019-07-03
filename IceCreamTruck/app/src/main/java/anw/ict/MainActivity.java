package anw.ict;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

import anw.ict.chat.fragments.ChatFrag;
import anw.ict.chat.objects.ChatMessage;
import anw.ict.chat.objects.ChatNotification;
import anw.ict.home.HomeFrag;

import static android.content.ContentValues.TAG;
import static anw.ict.utils.Constants.CHAT_LOG;
import static anw.ict.utils.Constants.IMAGE_PHOTO;
import static anw.ict.utils.Constants.MSG;
import static anw.ict.utils.Constants.NOTIFICATIONS;
import static anw.ict.utils.Constants.PHOTOS;
import static anw.ict.utils.Constants.PIC;
import static anw.ict.utils.Constants.TOKENS;

public class MainActivity extends AppCompatActivity {
    String userRole, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "getInstanceId failed", task.getException());
                return;
            }

            // Get new Instance ID token
            String token = Objects.requireNonNull(task.getResult()).getToken();
            FirebaseDatabase.getInstance().getReference(TOKENS + "/" + FirebaseAuth.getInstance().getUid()).setValue(token);
        });

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userRole = sharedPreferences.getString("userRole", "null");
        userId = sharedPreferences.getString("userId", "null");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("msg", "");
        editor.apply();

        NotificationManager mNotificationManager = (NotificationManager) Objects.requireNonNull(getApplicationContext()).getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        FragmentTransaction ftMain = getSupportFragmentManager().beginTransaction();
        ftMain.replace(R.id.frag, new ChatFrag());
        ftMain.commit();

        navView.setOnNavigationItemSelectedListener(item -> {
            // Begin the transaction
            switch (item.getItemId()) {
                case R.id.nav_chat:
                    FragmentTransaction ftChat = getSupportFragmentManager().beginTransaction();
                    ftChat.replace(R.id.frag, new ChatFrag());
                    ftChat.commit();
                    return true;
                case R.id.nav_home:
                    FragmentTransaction ftHome = getSupportFragmentManager().beginTransaction();
                    ftHome.replace(R.id.frag, new HomeFrag());
                    ftHome.commit();
                    return true;
            }
            return false;
        });


        if (Intent.ACTION_SEND.equals(action) && type != null) {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if(auth.getCurrentUser() == null){
                Intent i = new Intent(this, SplashActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            else{
                if ("text/plain".equals(type)) {
                    handleSendText(intent); // Handle text being sent
                } else if (type.startsWith("image/")) {
                    handleSendImage(intent); // Handle single image being sent
                }
            }
        }
    }

    void handleSendText(Intent intent) {
        String msg = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (msg != null) {
            Log.e("String", msg);
            // Update UI to reflect text being shared
            Long time = new Date().getTime();
            //String timestamp = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(time);
            if (!msg.equals("")) {
                ChatMessage data = new ChatMessage(getApplicationContext(), msg,MSG, String.valueOf(time), userRole);
                FirebaseDatabase.getInstance().getReference(CHAT_LOG).child(String.valueOf(time)).setValue(data);

                ChatNotification notif = new ChatNotification(data);
                FirebaseDatabase.getInstance().getReference(NOTIFICATIONS + "/" + userId + "/" + time).setValue(notif);
            }
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Log.e("Image", imageUri.toString());
            uploadImage(imageUri);
        }
    }
    private void uploadImage(Uri file) {
        long fileSize = 0;
        try {
            AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(file, "r");
            assert afd != null;
            fileSize = afd.getLength();
            afd.close();
        } catch (Exception e) {
            Log.e("Invalid", "File not valid");
        }

        if (fileSize > IMAGE_PHOTO || fileSize <= 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("File too large");
            builder.setMessage("Image size must be less than 10MB");

            // Set up the buttons
            builder.setPositiveButton("Optimize Image", (dialog, which) -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ezgif.com")));
                dialog.dismiss();
            });
            builder.setNegativeButton("OK", (dialog, which) -> dialog.cancel());

            builder.show();
        } else {
            String name = String.valueOf(new Date().getTime());

            UploadTask ut = FirebaseStorage.getInstance().getReference(PHOTOS).child(name).putFile(file);
            ut.addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), "Photo not uploaded. :(", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getApplicationContext(), "Photo sent!", Toast.LENGTH_SHORT).show();

                        ChatMessage data = new ChatMessage(getApplicationContext(), name, PIC, name, userRole);
                        FirebaseDatabase.getInstance().getReference(CHAT_LOG).child(name).setValue(data);

                        ChatNotification notif = new ChatNotification(data);
                        FirebaseDatabase.getInstance().getReference(NOTIFICATIONS + "/" + userId + "/" + name).setValue(notif);
                    });
        }
    }
}
