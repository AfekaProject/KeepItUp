package com.afeka.keepitup.keepitup;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


public class NotificationReceiver extends BroadcastReceiver{
    private static final String NOTIFICATION_ID = "notification_id";
    private static final int REQUEST_CODE = 0;


    @Override
    public void onReceive(Context context, Intent intent) {
        int transID = intent.getExtras().getInt("ID");
        String transName = intent.getExtras().getString("NAME");

        Intent menuIntent = new Intent(context, MenuActivity.class);
        menuIntent.putExtra("SHOW",transID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,REQUEST_CODE,menuIntent,0);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context,NOTIFICATION_ID).
                setContentTitle(context.getResources().getString(R.string.notification_title)).
                setContentText(transName).
                setContentIntent(pendingIntent).
                setSmallIcon(R.drawable.insurnece_icon).
                setAutoCancel(true).setChannelId("0");


        NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(context);
        notificationManager.notify(transID,nBuilder.build());


    }
}
