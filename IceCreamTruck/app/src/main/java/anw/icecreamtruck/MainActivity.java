package anw.icecreamtruck;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import anw.icecreamtruck.chat.fragments.ChatFrag;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
