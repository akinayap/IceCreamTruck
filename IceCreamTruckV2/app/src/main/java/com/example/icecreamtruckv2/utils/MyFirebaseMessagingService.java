package com.example.icecreamtruckv2.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.icecreamtruckv2.MainActivity;
import com.example.icecreamtruckv2.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

public class MyFirebaseMessagingService  extends FirebaseMessagingService {

    class Message
    {
        CharSequence msg;
        Long time;
        Person sender;

        Message(CharSequence m, Long t, Person s)
        {
            msg = m;
            time = t;
            sender = s;
        }

        CharSequence getText(){return msg;}

        Long getTime() {return time;}

        Person getSender() {return sender;}
    }

    List<Message> messages = new ArrayList<>();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("DEBUG", "onMessageReceived triggered!");

        testNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        //inboxNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        //defaultNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        //generateNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void testNotification(String sender, String msg) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "YOUR_CHANNEL_ID";

        Bitmap tiger = BitmapFactory.decodeResource(getResources(), R.drawable.tiger);
        Bitmap otter = BitmapFactory.decodeResource(getResources(), R.drawable.otter);

        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.view_expanded_notification);
        expandedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        expandedView.setTextViewText(R.id.notification_title, sender);
        expandedView.setTextViewText(R.id.notification_message, msg);
        expandedView.setImageViewBitmap(R.id.big_icon, sender.equals("Ah Girl") ? otter : tiger);
        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        collapsedView.setTextViewText(R.id.expand_text, sender + " has sent you a love letter.");
        collapsedView.setImageViewBitmap(R.id.big_icon, sender.equals("Ah Girl") ? otter : tiger);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setAutoCancel(true)
                .setCustomBigContentView(expandedView)
                .setCustomContentView(collapsedView)
                .setCustomHeadsUpContentView(collapsedView)

                .setContentTitle(sender)
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_pawprint);

        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channelId);

        notificationManager.notify((int)SystemClock.uptimeMillis(), builder.build());
    }

    private void inboxNotification(String sender, String msg) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "YOUR_CHANNEL_ID";

        Person.Builder p = new Person.Builder()
                .setName(sender)
                .setIcon(IconCompat.createWithResource(this, R.drawable.tiger));

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ict)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationCompat.MessagingStyle ms = new NotificationCompat.MessagingStyle(p.build());
        messages.add(new Message(msg, System.currentTimeMillis(), p.build()));
        for(Message m : messages)
        {
            NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(m.getText(), m.getTime(), m.getSender());
            ms.addMessage(message);
        }
        notification.setStyle(ms);//.setCustomContentView(collapsedView).setCustomBigContentView(expandedView);

        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        notification.setChannelId(channelId);

        notificationManager.notify((int)SystemClock.uptimeMillis(), notification.build());
    }

    private void defaultNotification(String sender, String msg) {
        Intent chatIntent  = new Intent(this, MainActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        chatIntent.putExtra("FROM", "NOTIF");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap tiger = BitmapFactory.decodeResource(getResources(), R.drawable.tiger);
        Bitmap otter = BitmapFactory.decodeResource(getResources(), R.drawable.otter);

        String channelId = "YOUR_CHANNEL_ID";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ict)
                .setContentTitle(sender)
                .setContentText(msg)
                .setLargeIcon(sender.equals("Ah Girl") ? otter : tiger)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, chatIntent, 0));

        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        notificationBuilder.setChannelId(channelId);

        notificationManager.notify((int)SystemClock.uptimeMillis(), notificationBuilder.build());
    }

    private void generateNotification(String sender, String msg) {

        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.view_expanded_notification);
        expandedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        expandedView.setTextViewText(R.id.content_title, sender);
        expandedView.setTextViewText(R.id.notification_message, msg);
/*
        // adding action to left button
        Intent leftIntent = new Intent(this, NotificationIntentService.class);
        leftIntent.setAction("left");
        expandedView.setOnClickPendingIntent(R.id.left_button, PendingIntent.getService(this, 0, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        // adding action to right button
        Intent rightIntent = new Intent(this, NotificationIntentService.class);
        rightIntent.setAction("right");
        expandedView.setOnClickPendingIntent(R.id.right_button, PendingIntent.getService(this, 1, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT));
*/

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        collapsedView.setTextViewText(R.id.expand_text, sender + " has sent you a love letter.");

        Intent chatIntent  = new Intent(this, MainActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        chatIntent.putExtra("FROM", "NOTIF");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "YOUR_CHANNEL_ID";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(sender)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ict)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, chatIntent, 0))
                // setting the custom collapsed and expanded views
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setGroup("TOGETHER");

        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        notificationBuilder.setChannelId(channelId);

        notificationManager.notify((int)SystemClock.uptimeMillis(), notificationBuilder.build());
    }
}