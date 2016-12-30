package tk.talcharnes.intouch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Tal on 12/29/2016.
 */

public class AlertReceiver extends BroadcastReceiver {
    Context mContext;
    String number;
    String messageList;
    String name;
    String ACTION_CALL_NOTIFICATION;
    String ACTION_SEND_TEXT;
    String ACTION_NOTIFICATION;
    Intent mIntent;
    Bundle extrasBundle;



    public AlertReceiver(){
        ACTION_CALL_NOTIFICATION = "action_call";
        ACTION_SEND_TEXT = "action_send_text";
        ACTION_NOTIFICATION = "action_notification";



    }
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        name = intent.getStringExtra("name");
        messageList = intent.getStringExtra("messageList");
        number = intent.getStringExtra("number");

        extrasBundle = intent.getExtras();

            createNotification(context, "times up " + name, "5 seconds passed!", "alert", intent.getAction());

    }
    public void createNotification(Context context, String message, String messageText, String messageAlert, String action){
        Intent testIntent = new Intent(context, NotificationReceiver.class);
        testIntent.putExtras(extrasBundle);
        testIntent.setAction(action);

        PendingIntent notificIntent = PendingIntent.getService(context, 0, testIntent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(message)
                .setTicker(messageText)
                .setContentText(messageAlert);
        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }


}

