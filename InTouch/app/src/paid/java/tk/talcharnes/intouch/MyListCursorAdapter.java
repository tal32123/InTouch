package tk.talcharnes.intouch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * Created by Tal on 1/11/2017.
 */

public class MyListCursorAdapter extends ListCursorAdapter {
    Context mContext;
    private static final String LOG_TAG = MyListCursorAdapter.class.getSimpleName();
    private String firebaseContactKey;

    public MyListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        super.onBindViewHolder(viewHolder, cursor);
            int firebaseContactKeyIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_FIREBASE_CONTACT_KEY);
            firebaseContactKey = cursor.getString(firebaseContactKeyIndex);
        Intent intent = new Intent(mContext, UpdateContactInfoActivity.class);
        intent.putExtra("firebaseContactKey", firebaseContactKey);
                    }
    }


