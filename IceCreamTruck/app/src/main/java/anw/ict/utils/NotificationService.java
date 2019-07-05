package anw.ict.utils;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Objects;

import anw.ict.MainActivity;
import anw.ict.R;
import anw.ict.chat.objects.ChatNotification;

import static android.content.ContentValues.TAG;
import static anw.ict.utils.Constants.GIF;
import static anw.ict.utils.Constants.MSG;
import static anw.ict.utils.Constants.PIC;
import static anw.ict.utils.Constants.TOKENS;

public class NotificationService extends FirebaseMessagingService {
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);
        FirebaseDatabase.getInstance().getReference(TOKENS + "/" + FirebaseAuth.getInstance().getUid()).setValue(token);
    }

    private static boolean isForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : tasks) {
            if (ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == appProcess.importance && packageName.equals(appProcess.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (isForeground(getApplicationContext())) {
            //if in forground then your operation
            // if app is running
        } else {
            //if in background then perform notification operation

            if (remoteMessage.getData().size() > 0) {
                ChatNotification cn = new ChatNotification(remoteMessage.getData().get("sender"), remoteMessage.getData().get("type"), remoteMessage.getData().get("msg"), remoteMessage.getData().get("time"));
                inboxStyle(cn);
            }

            // Check if msg contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }
        }
    }

    private void inboxStyle(ChatNotification cn) {
        switch (cn.getType()){
            case GIF:
                cn.msg = "sent a GIF";
                break;
            case PIC:
                cn.msg = "sent a photo";
                break;
            case MSG:
                break;
        }

        cn.sender = cn.sender.equals("ahgirl") ? "Ah Girl" : "Ah Boy";

        Intent chatIntent  = new Intent(this, MainActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        chatIntent.putExtra("FROM", "NOTIF");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "YOUR_CHANNEL_ID";
        NotificationCompat.Builder builder;

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle("Letters from "+ cn.sender + ":");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String prevMsg = sharedPreferences.getString("msg", "");
        editor.putString("msg", prevMsg + "|" + cn.msg);
        editor.apply();
        String newMsg = sharedPreferences.getString("msg", "");

        String[] messages = Objects.requireNonNull(newMsg).split("\\|");

        // Moves events into the expanded layout
        for (String msg : messages) {
            inboxStyle.addLine(msg);
        }

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_paw)
                .setContentTitle("Love Letter Received")
                .setContentText(cn.sender + " has sent you a love letter.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 200, 300, 400, 500})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(CHANNEL_ID)
                .setStyle(inboxStyle);

        NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
        if (mChannel == null) {
            mChannel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableVibration(false);
            mChannel.setVibrationPattern(new long[]{1000, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(0, builder.build());

    }
}
