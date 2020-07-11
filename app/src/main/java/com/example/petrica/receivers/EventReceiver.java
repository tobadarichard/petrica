package com.example.petrica.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.petrica.R;
import com.example.petrica.activities.BaseContentActivity;
import com.example.petrica.activities.EventDetailsActivity;

import java.util.Date;


public class EventReceiver extends BroadcastReceiver {
    // Receiver listening for event near broadcast
    // It shows a notification
    public static final String EVENT_NEAR = "com.example.petrica.action.EVENT_NEAR";
    public static final String EVENT_FINISHED = "com.example.petrica.action.EVENT_FINISHED";
    @Override
    public void onReceive(Context context, Intent intent) {
        // Creating channel (for api >26)
        long date_event = intent.getLongExtra(BaseContentActivity.EXTRA_DATE,0);
        long date_now = (new Date()).getTime();
        if ((date_now > date_event && intent.getAction().equals(EVENT_NEAR)) ||
                (date_now < date_event && intent.getAction().equals(EVENT_FINISHED)))
            return; // Do nothing

        String CHANNEL_ID = "Event channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID
                    ,context.getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Creating pending intent
        Intent i = new Intent(context,EventDetailsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra(BaseContentActivity.EXTRA_MUST_RETRIEVE_EVENT,true);
        i.putExtra(BaseContentActivity.EXTRA_ID_EVENT,intent.getStringExtra(BaseContentActivity.EXTRA_ID_EVENT));
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Creating notification
        Notification notif;
        if (intent.getAction().equals(EVENT_NEAR)){
            notif = (new NotificationCompat.Builder(context, CHANNEL_ID))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.baseline_calendar_today_black_18)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(context.getString(R.string.notif_event_coming_title))
                    .setContentText(context.getString(R.string.notif_event_coming_body,intent.getStringExtra(BaseContentActivity.EXTRA_NAME))).build();
        }
        else{
            notif = (new NotificationCompat.Builder(context, CHANNEL_ID))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.baseline_calendar_today_black_18)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(context.getString(R.string.notif_event_finished_title))
                    .setContentText(context.getString(R.string.notif_event_finished_body,intent.getStringExtra(BaseContentActivity.EXTRA_NAME))).build();
        }
        NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
        int NOTIF_ID = 5;
        nmc.notify(intent.getStringExtra(BaseContentActivity.EXTRA_ID_EVENT), NOTIF_ID,notif);

    }
}
