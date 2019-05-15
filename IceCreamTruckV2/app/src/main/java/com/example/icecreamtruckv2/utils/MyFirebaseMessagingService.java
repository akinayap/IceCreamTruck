package com.example.icecreamtruckv2.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.example.icecreamtruckv2.MainActivity;
import com.example.icecreamtruckv2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

public class MyFirebaseMessagingService  extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("DEBUG", "onMessageReceived triggered!");
        newNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTag());
    }

    private void newNotification(String sender, String msg, String msgType) {
        msg = msgType.equals("GIF") ? "sent a GIF" : msg;
        Log.e("sender", sender);
        Log.e("msg", msg);
        Log.e("test", msgType);
        Intent chatIntent  = new Intent(this, MainActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        chatIntent.putExtra("FROM", "NOTIF");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "YOUR_CHANNEL_ID";
        NotificationCompat.Builder builder;

        Bitmap tiger = BitmapFactory.decodeResource(getResources(), R.drawable.tiger);
        Bitmap otter = BitmapFactory.decodeResource(getResources(), R.drawable.otter);

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        collapsedView.setTextViewText(R.id.expand_text, sender + " has sent you a love letter.");
        collapsedView.setImageViewBitmap(R.id.big_icon, sender.equals("Ah Girl") ? otter : tiger);

        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.view_expanded_notification);
        expandedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        expandedView.setTextViewText(R.id.notification_title, sender);
        expandedView.setTextViewText(R.id.notification_message, msg);
        expandedView.setTextViewText(R.id.expand_text, sender + " has sent you a love letter.");
        expandedView.setImageViewBitmap(R.id.big_icon, sender.equals("Ah Girl") ? otter : tiger);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pawprint)
                .setContentTitle(sender)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setContentIntent(PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 200, 300, 400, 500})
                .setChannelId(CHANNEL_ID);;


        NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
        if (mChannel == null) {
            mChannel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.enableVibration(false);
            mChannel.setVibrationPattern(new long[]{1000, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify((int)SystemClock.uptimeMillis(), builder.build());
    }
}