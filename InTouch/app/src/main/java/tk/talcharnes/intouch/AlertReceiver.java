package tk.talcharnes.intouch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.InputStream;

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
    String photo_uri;



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
        photo_uri = intent.getStringExtra("photo_uri");

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
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(getBMP(photo_uri))
                .setContentTitle(message)
                .setTicker(messageText)
                .setContentText(messageAlert);
        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.build());
    }
    private Bitmap getBMP(String photo_uri){
    if(photo_uri!= null && !photo_uri.equals(null) && !photo_uri.equals("")) {
        InputStream photo_stream = android.provider.ContactsContract.Contacts.openContactPhotoInputStream(mContext.getContentResolver(), Uri.parse(photo_uri.substring(0, photo_uri.length() - 6)));
        BufferedInputStream buf = new BufferedInputStream(photo_stream);
        Bitmap my_btmp = BitmapFactory.decodeStream(buf);

        return getCircleBitmap(my_btmp);
    }
        else return BitmapFactory.decodeResource(mContext.getResources(),
            R.mipmap.contact_photo);
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {

        // class from http://curious-blog.blogspot.co.il/2014/05/create-circle-bitmap-in-android.html
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }


}

