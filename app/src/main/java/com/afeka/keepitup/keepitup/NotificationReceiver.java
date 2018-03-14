package com.afeka.keepitup.keepitup;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


class NotificationReceiver extends BroadcastReceiver{
    private Database db ;
    private static final String NOTIFICATION_ID = "notification_id";
    private static final int ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        int trandID = intent.getExtras().getInt("ID");
        //get transaction by Id from database
        String textToShow = "hello"; //need to be changed!!

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context,NOTIFICATION_ID).
                setContentTitle(context.getResources().getString(R.string.notification_title)).
                setContentText(textToShow);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID,nBuilder.build());
    }
}
