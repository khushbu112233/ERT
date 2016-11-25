package com.aip.dailyestimation.Notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.view.activity.SplashActivity;
import com.google.android.gms.gcm.GcmListenerService;


import java.util.List;

/**
 * Created by dhimant on 15/12/15.
 */
public class PushNotificationService extends GcmListenerService {

    int notificationID = 0;

    String sender_id;
    String push_status;
    String push_activity;
    long timestamp1;
    Context mContext;

    @Override
    public void onMessageReceived(String from, Bundle data) {

        mContext = getApplicationContext();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sender_id = sharedPreferences.getString("sp_sender_id", null);

        String message = data.getString("message");

        Intent notificationIntent = new Intent(PushNotificationService.this, PushNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification;
        if (isRunningInForeground()) {
            notification = new NotificationCompat.Builder(this)
                    .setCategory(Notification.CATEGORY_PROMO)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(uri)
                    .setVibrate(new long[]{0}).build();
        } else {
            Intent resultIntent = new Intent(this, SplashActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            notification = new NotificationCompat.Builder(this)
                    .setCategory(Notification.CATEGORY_PROMO)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(uri)
                    .setVibrate(new long[]{0}).build();
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int notification_id = (int) System.currentTimeMillis();
        notificationManager.notify(notification_id, notification);

//        createNotification(mTitle, push_msg);

    }

    protected boolean isRunningInForeground() {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if (tasks.isEmpty()) {
            return false;
        }
        String topActivityName = tasks.get(0).topActivity.getPackageName();
        return topActivityName.equalsIgnoreCase(getPackageName());
    }

}
