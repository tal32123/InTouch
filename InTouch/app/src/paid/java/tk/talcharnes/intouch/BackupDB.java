package tk.talcharnes.intouch;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * Created by Tal on 1/1/2017.
 */

public class BackupDB {
    public Context mContext;
    Cursor mCursor;


    public BackupDB(Context context) {
        mContext = context;
        mCursor = mContext.getContentResolver().query(ContactsContract.ContactsEntry.CONTENT_URI, null, null, null, null);


    }

    public void uploadToDrive() {
    }

    {
        int contact_idIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry._ID);
        int nameIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_NAME);
        int photoThumbnailUriIndex = mCursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI);
        int phoneNumberIndex = mCursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER);
        int messageListIndex = mCursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST);
        int textFrequencyIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY);
        int callFrequencyIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY);
        int callCounterIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER);
        int textCounterIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER);

        if (mCursor.getCount() > 0) {
            for (int i = 0; i < mCursor.getCount(); i++) {
                final String name = mCursor.getString(nameIndex);
                final String photoThumbnailUri = mCursor.getString(photoThumbnailUriIndex);
                final String phoneNumber = mCursor.getString(phoneNumberIndex);
                final String messageList = mCursor.getString(messageListIndex);
                final int textFrequency = mCursor.getInt(textFrequencyIndex);
                final int callFrequency = mCursor.getInt(callFrequencyIndex);
                final int contact_id = mCursor.getInt(contact_idIndex);
                final int callCounter = mCursor.getInt(callCounterIndex);
                final int textCounter = mCursor.getInt(textCounterIndex);
                //todo add to a csv file or something
            }
            //todo upload to google docs

        }
    }


    public void restoreFromDrive() {
        //todo get file from drive
        //todo go over values, put into content values, and bulkinsert them all into db.
    }


    private JSONArray getResults() {

        JSONArray resultSet = new JSONArray();

        mCursor.moveToFirst();
        while (mCursor.isAfterLast() == false) {

            int totalColumn = mCursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (mCursor.getColumnName(i) != null) {
                    try {
                        if (mCursor.getString(i) != null) {
                            Log.d("TAG_NAME", mCursor.getString(i));
                            rowObject.put(mCursor.getColumnName(i), mCursor.getString(i));
                        } else {
                            rowObject.put(mCursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            mCursor.moveToNext();
        }
        mCursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }
}