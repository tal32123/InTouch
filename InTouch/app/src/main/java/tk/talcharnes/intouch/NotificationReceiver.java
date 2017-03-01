package tk.talcharnes.intouch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    Context mContext;
    String name;
    String number;
    String ACTION_NOTIFICATION;
    String ACTION_CALL_NOTIFICATION;
    String ACTION_SEND_TEXT;
    String contactID;
    String message;

    final static String LOG_TAG = NotificationReceiver.class.getSimpleName();

    public NotificationReceiver() {

        ACTION_CALL_NOTIFICATION = "action_call";
        ACTION_SEND_TEXT = "action_send_text";
        ACTION_NOTIFICATION = "action_notification";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        contactID = intent.getStringExtra("contactID");
        message = intent.getStringExtra("message");
        Log.d("NOTIFICATIONRECEIVER ", "name " + name + "number " + number + " message " + message);

        if (ACTION_SEND_TEXT.equals(intent.getAction())) {
            sendText();
        }
        if (ACTION_CALL_NOTIFICATION.equals(intent.getAction())) {
            sendCall();
        }


        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void sendText() {

//      Get preferences to see if User wants to use standard text message app or alternative app
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean altTXTApp = getPrefs.getBoolean("checkbox_text_preference", false);

//                      If user doesn't want to use the standard text message application
        if (!altTXTApp) {
            try {
                //send text message
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, message, null, null);
                Toast.makeText(mContext, mContext.getString(R.string.message_sent_string),
                        Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {

                //If text message wasn't sent attempt to send text another way (through the user's text messaging app)
                // Most likely due to text message permissions not being accepted by user
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + number));  // This ensures only SMS apps respond
                intent.putExtra("sms_body", message);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

        }

    }

    public void sendCall() {
//                        Get preferences to see if User wants to use standard text message app or alternative app
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean altCallApp = getPrefs.getBoolean("checkbox_call_preference", false);
        try {
            if (!altCallApp) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + number));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (callIntent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(callIntent);
                }
            } else {
                Utility.callImmediately(mContext, number);
            }
        } catch (Exception e) {

        }
    }
}
