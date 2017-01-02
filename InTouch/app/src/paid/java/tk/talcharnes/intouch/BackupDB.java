package tk.talcharnes.intouch;

import android.content.Context;
import android.database.Cursor;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * Created by Tal on 1/1/2017.
 */

public class BackupDB {
    public Context mContext;
    Cursor cursor;


    public BackupDB(Context context){
        mContext = context;
        cursor =  mContext.getContentResolver().query(ContactsContract.ContactsEntry.CONTENT_URI, null, null, null, null);


    }

    public void uploadToDrive(){}{
        int contact_idIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry._ID);
        int nameIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_NAME);
        int photoThumbnailUriIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI);
        int phoneNumberIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER);
        int messageListIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST);
        int textFrequencyIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY);
        int callFrequencyIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY);
        int callCounterIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER);
        int textCounterIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER);

    if(cursor.getCount() > 0 )
    {
        for (int i = 0; i < cursor.getCount(); i++) {
            final String name = cursor.getString(nameIndex);
            final String photoThumbnailUri = cursor.getString(photoThumbnailUriIndex);
            final String phoneNumber = cursor.getString(phoneNumberIndex);
            final String messageList = cursor.getString(messageListIndex);
            final int textFrequency = cursor.getInt(textFrequencyIndex);
            final int callFrequency = cursor.getInt(callFrequencyIndex);
            final int contact_id = cursor.getInt(contact_idIndex);
            final int callCounter = cursor.getInt(callCounterIndex);
            final int textCounter = cursor.getInt(textCounterIndex);
            //todo add to a csv file or something
        }
        //todo upload to google docs

    }
    }


    public void restoreFromDrive(){
        //todo get file from drive
        //todo go over values, put into content values, and bulkinsert them all into db.
    }

}
