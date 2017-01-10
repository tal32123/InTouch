package tk.talcharnes.intouch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tk.talcharnes.intouch.paid.Contact;

/**
 * Created by Tal on 1/1/2017.
 */

public class BackupDB {
    public Context mContext;
    Cursor mCursor;
    private static final String LOG_TAG = BackupDB.class.getSimpleName();


    public BackupDB(Context context) {
        mContext = context;
//        mCursor = mContext.getContentResolver().query(ContactsContract.ContactsEntry.CONTENT_URI, null, null, null, null);


    }

    public void deleteContactFromFirebase(String deleteKey) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseDatabaseReference = mFirebaseDatabase.getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            String uid = firebaseUser.getUid();
            if(uid != null) {
                Log.d(LOG_TAG, "uid = " + uid);
                Log.d(LOG_TAG, "deleteKey = " + deleteKey);

                 mFirebaseDatabaseReference.child(uid).child(deleteKey).removeValue();
            }
            else{
                Log.d(LOG_TAG, "uid = null");
            }
        }
        else{
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        }
    }

    public void updateFirebaseContact(String firebaseContactKey, Contact contact){
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseDatabaseReference = mFirebaseDatabase.getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            String uid = firebaseUser.getUid();
            if(uid != null) {
                Log.d(LOG_TAG, "uid = " + uid);
                Log.d(LOG_TAG, "firebaseContactKey = " + firebaseContactKey);

                mFirebaseDatabaseReference.child(uid).child(firebaseContactKey).setValue(contact);
                
            }
            else{
                Log.d(LOG_TAG, "uid = null");
            }
        }
        else{
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        }

    }

    public void restoreFromDrive() {
        //todo get file from drive
        //todo go over values, put into content values, and bulkinsert them all into db.
    }

//
//    private JSONArray getResults() {
//
//        JSONArray resultSet = new JSONArray();
//
//        mCursor.moveToFirst();
//        while (mCursor.isAfterLast() == false) {
//
//            int totalColumn = mCursor.getColumnCount();
//            JSONObject rowObject = new JSONObject();
//
//            for (int i = 0; i < totalColumn; i++) {
//                if (mCursor.getColumnName(i) != null) {
//                    try {
//                        if (mCursor.getString(i) != null) {
//                            Log.d("TAG_NAME", mCursor.getString(i));
//                            rowObject.put(mCursor.getColumnName(i), mCursor.getString(i));
//                        } else {
//                            rowObject.put(mCursor.getColumnName(i), "");
//                        }
//                    } catch (Exception e) {
//                        Log.d("TAG_NAME", e.getMessage());
//                    }
//                }
//            }
//            resultSet.put(rowObject);
//            mCursor.moveToNext();
//        }
//        mCursor.close();
//        Log.d("TAG_NAME", resultSet.toString());
//        return resultSet;
//    }
}