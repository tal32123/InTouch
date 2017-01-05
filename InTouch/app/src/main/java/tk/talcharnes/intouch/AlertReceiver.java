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
    String contactID;
    String action;
    int notificationID;



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
        contactID = intent.getStringExtra("contactID");
        action = intent.getAction();

        extrasBundle = intent.getExtras();


        if(action.equals(ACTION_CALL_NOTIFICATION)){
            // *1 in front is for calls so that ID is positive.
            // We add this in front of the contact ID to have a unique notification
            notificationID = 1 * Integer.parseInt(contactID);
            createNotification(context, "Reminder to call " + name, "Call reminder!", name, action);

        }
        else if(action.equals(ACTION_SEND_TEXT)){
            //-1 in front is for texts.
            // We add this in front of the contact ID to have a unique notification
            notificationID = -1 * Integer.parseInt(contactID);
            createNotification(context, "Reminder to text " + name, "Text reminder!", name, action);

        }




    }
    public void createNotification(Context context, String message, String messageText, String messageAlert, String action){
        Intent testIntent = new Intent(context, NotificationReceiver.class);
        testIntent.putExtras(extrasBundle);
        testIntent.setAction(action);

        PendingIntent notificIntent = PendingIntent.getBroadcast(context, 0, testIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(message)
                .setTicker(messageText)
                .setContentText(messageAlert);
        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.build());
    }


}

