package anw.ict;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

import anw.ict.chat.fragments.ChatFrag;

import static android.content.ContentValues.TAG;
import static anw.ict.utils.Constants.TOKENS;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frag, new ChatFrag());
        ft.commit();

        navView.setOnNavigationItemSelectedListener(item -> {
            // Begin the transaction
            switch (item.getItemId()) {
                case R.id.nav_chat:
                    Log.e("Nav", "chat");
                    return true;
                case R.id.nav_home:
                    Log.e("Nav", "home");
                    return true;
            }
            return false;
        });
    }
}
