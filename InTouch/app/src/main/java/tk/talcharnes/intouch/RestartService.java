//package tk.talcharnes.intouch;
//
//import android.app.IntentService;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.database.Cursor;
//
//import tk.talcharnes.intouch.data.ContactsContract;
//
///**
// * Created by Tal on 1/19/2017.
// */
//
//public class RestartService extends IntentService {
//
//
//    public RestartService(String name) {
//        super(name);
//    }
//
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        final String ACTION_CALL_NOTIFICATION = "action_call";
//        final String ACTION_SEND_TEXT = "action_send_text";
//        final String ACTION_NOTIFICATION = "action_notification";
//
//
//
//        Cursor cursor = getApplicationContext().getContentResolver().query(ContactsContract.ContactsEntry.CONTENT_URI, null, null, null, null);
//
//        int contact_idIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry._ID);
//        int nameIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_NAME);
//        int photoThumbnailUriIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI);
//        int phoneNumberIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER);
//        int messageListIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST);
//        int textFrequencyIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY);
//        int callFrequencyIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY);
//        int callCounterIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER);
//        int textCounterIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER);
//        int notificationTimeIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_NOTIFICATION_TIME);
//
//
//        String name = cursor.getString(nameIndex);
//        String photoThumbnailUri = cursor.getString(photoThumbnailUriIndex);
//        String phoneNumber = cursor.getString(phoneNumberIndex);
//        String messageList = cursor.getString(messageListIndex);
//        int textFrequency = cursor.getInt(textFrequencyIndex);
//        int callFrequency = cursor.getInt(callFrequencyIndex);
//        int contact_id = cursor.getInt(contact_idIndex);
//        int callCounter = cursor.getInt(callCounterIndex);
//        int textCounter = cursor.getInt(textCounterIndex);
//        long notificationTime = cursor.getLong(notificationTimeIndex);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            while (cursor.moveToNext()) {
//
//                PendingIntent callIntent = Utility.createNotificationPendingIntent(name, phoneNumber, messageList, Integer.toString(contact_id),
//                        photoThumbnailUri, ACTION_CALL_NOTIFICATION, getApplicationContext());
//
//                Utility.createNotifications(callIntent, getApplicationContext(), notificationTime, callFrequency);
//
//                PendingIntent textIntent = Utility.createNotificationPendingIntent(name, phoneNumber, messageList, Integer.toString(contact_id),
//                        photoThumbnailUri, ACTION_CALL_NOTIFICATION, getApplicationContext());
//
//                Utility.createNotifications(textIntent, getApplicationContext(), notificationTime, textFrequency);
//            }
//        }
//    }
//}