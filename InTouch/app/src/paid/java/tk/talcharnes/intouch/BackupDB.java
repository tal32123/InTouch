package tk.talcharnes.intouch;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

import tk.talcharnes.intouch.data.ContactsContract;
import tk.talcharnes.intouch.paid.Contact;

/**
 * Created by Tal on 1/1/2017.
 * Class to help manage database backup and restore
 */

public class BackupDB {
    public Context mContext;
    Cursor mCursor;
    private static final String LOG_TAG = BackupDB.class.getSimpleName();

    public BackupDB(Context context) {
        mContext = context;


    }

    public void deleteContactFromFirebase(String deleteKey) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseDatabaseReference = mFirebaseDatabase.getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            if (uid != null) {
                Log.d(LOG_TAG, "uid = " + uid);
                Log.d(LOG_TAG, "deleteKey = " + deleteKey);

                mFirebaseDatabaseReference.child(uid).child(deleteKey).removeValue();
            } else {
                Log.d(LOG_TAG, "uid = null");
            }
        } else {
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        }
    }

    public void updateFirebaseContact(String firebaseContactKey, Contact contact) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseDatabaseReference = mFirebaseDatabase.getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            if (uid != null) {
                Log.d(LOG_TAG, "uid = " + uid);
                Log.d(LOG_TAG, "firebaseContactKey = " + firebaseContactKey);

                mFirebaseDatabaseReference.child(uid).child(firebaseContactKey).setValue(contact);

            } else {
                Log.d(LOG_TAG, "uid = null");
            }
        } else {
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        }

    }


    public void restoreDB() {

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseDatabaseReference = mFirebaseDatabase.getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            if (uid != null) {
                Log.d(LOG_TAG, "uid = " + uid);

                mFirebaseDatabaseReference = mFirebaseDatabaseReference.child(uid);

                mFirebaseDatabaseReference.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Vector<ContentValues> cVVector = new Vector<ContentValues>();

                                //Get map of users in datasnapshot
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                    ContentValues contactValues = new ContentValues();
                                    String key = childDataSnapshot.getKey().toString(); //displays the key for the node


                                    Cursor cursor = mContext.getContentResolver().query(
                                            ContactsContract.ContactsEntry.CONTENT_URI,
                                            new String[]{
                                                    ContactsContract.ContactsEntry._ID,
                                                    ContactsContract.ContactsEntry.COLUMN_FIREBASE_CONTACT_KEY},
                                            ContactsContract.ContactsEntry.COLUMN_FIREBASE_CONTACT_KEY + " = ?",
                                            new String[]{key},
                                            null
                                    );

                                    if (!cursor.moveToFirst()) {
                                        String name = childDataSnapshot.child("name").getValue().toString();   //gives the value for given keyname
                                        String number = childDataSnapshot.child("number").getValue().toString();

                                        int callFrequency = Integer.parseInt(childDataSnapshot.child("callFrequency").getValue().toString());
                                        int textFrequency = Integer.parseInt(childDataSnapshot.child("textFrequency").getValue().toString());
                                        String messageListJsonString = childDataSnapshot.child("messageListJsonString").getValue().toString();
                                        Long notificationTime = Long.parseLong(childDataSnapshot.child("notificationTime").getValue().toString());

                                        contactValues.put(ContactsContract.ContactsEntry.COLUMN_NAME, name);
                                        contactValues.put(ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER, number);
                                        contactValues.put(ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST, messageListJsonString);
                                        contactValues.put(ContactsContract.ContactsEntry.COLUMN_NOTIFICATION_TIME, notificationTime);
                                        contactValues.put(ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY, textFrequency);
                                        contactValues.put(ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY, callFrequency);
                                        contactValues.put(ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER, 0);
                                        contactValues.put(ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER, 0);
                                        contactValues.put(ContactsContract.ContactsEntry.COLUMN_FIREBASE_CONTACT_KEY, key);

                                        Uri contactUri = mContext.getContentResolver().insert(ContactsContract.ContactsEntry.CONTENT_URI, contactValues);

                                        Long contactID = ContentUris.parseId(contactUri);

                                        PendingIntent callPendingIntent = Utility.createNotificationPendingIntent(name, number, messageListJsonString, ("" + contactID), null, Utility.ACTION_CALL_NOTIFICATION, mContext);
                                        PendingIntent textPendingIntent = Utility.createNotificationPendingIntent(name, number, messageListJsonString, ("" + contactID), null, Utility.ACTION_SEND_TEXT, mContext);

                                        Utility.createNotifications(callPendingIntent, mContext, notificationTime, callFrequency);
                                        Utility.createNotifications(textPendingIntent, mContext, notificationTime, textFrequency);
                                        Utility.updateWidgets(mContext);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });


            } else {
                Log.d(LOG_TAG, "uid = null");
            }
        } else {
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        }
    }
}