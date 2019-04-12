package com.example.icecreamtruckv2.Utils;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.icecreamtruckv2.MainActivity;
import com.example.icecreamtruckv2.R;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Created by sherryy on 4/3/17.
 */

public class NotificationIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationIntentService() {
        super("notificationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (intent.getAction()) {
            case "left":
                Handler leftHandler = new Handler(Looper.getMainLooper());
                leftHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "You clicked the left button", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case "right":
                Handler rightHandler = new Handler(Looper.getMainLooper());
                rightHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "You clicked the right button", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case "chat":
                Handler chatHandler = new Handler(Looper.getMainLooper());
                chatHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent newIntent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(newIntent);
                        Toast.makeText(getBaseContext(), "You clicked the right button", Toast.LENGTH_LONG).show();
                    }
                });

        }
    }
}