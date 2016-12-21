package tk.talcharnes.intouch;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    RecyclerView contacts_list_recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    String[] myDataSet;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        contacts_list_recycler_view = (RecyclerView) rootView.findViewById(R.id.contact_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        contacts_list_recycler_view.setLayoutManager(mLayoutManager);
        String[] projection = new String[]{ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI,
                ContactsContract.ContactsEntry.COLUMN_NAME,
                ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER,
                ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST,
                ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER,
                ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER
        };


        Cursor cursor = getContext().getContentResolver().query(ContactsContract.ContactsEntry.CONTENT_URI, projection, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()){
            int nameIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_NAME);



            int cursorSize = cursor.getCount();
            myDataSet = new String[cursorSize];
            for(int i = 0; i < cursorSize; i++){
                String name  = cursor.getString(nameIndex);
                myDataSet[i] = name;
                cursor.moveToNext();
            }

        }
        mAdapter = new ContactsListAdapter(myDataSet);
        contacts_list_recycler_view.setAdapter(mAdapter);


        return rootView;
    }
}
