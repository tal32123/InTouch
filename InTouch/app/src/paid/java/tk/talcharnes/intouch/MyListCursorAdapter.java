package tk.talcharnes.intouch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * Created by Tal on 1/11/2017.
 */

public class MyListCursorAdapter extends ListCursorAdapter {
    Context mContext;
    private static final String LOG_TAG = MyListCursorAdapter.class.getSimpleName();
    private String firebaseContactKey;
    boolean googlePlayServicesApiValid;

    public MyListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;

        if (GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE >= 10084000) {
            googlePlayServicesApiValid = true;
            Log.d(LOG_TAG, "GOOGLE PLAY SERVICES VERSION = " + GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE);
        } else {
            Log.d(LOG_TAG, "GOOGLE PLAY SERVICES VERSION is too low. Version = " + GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE);
            googlePlayServicesApiValid = false;
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        super.onBindViewHolder(viewHolder, cursor);
        if (googlePlayServicesApiValid) {
            int firebaseContactKeyIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_FIREBASE_CONTACT_KEY);
            firebaseContactKey = cursor.getString(firebaseContactKeyIndex);
            Intent intent = new Intent(mContext, UpdateContactInfoActivity.class);
            intent.putExtra("firebaseContactKey", firebaseContactKey);
        }
    }
}


