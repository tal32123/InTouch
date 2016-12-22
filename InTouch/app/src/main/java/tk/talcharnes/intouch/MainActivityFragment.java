package tk.talcharnes.intouch;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    RecyclerView contacts_list_recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
//    ContactsListAdapter mAdapter;
    MyListCursorAdapter mAdapter;


    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getSupportLoaderManager().initLoader(1, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new MyListCursorAdapter(getContext(), null);
        contacts_list_recycler_view = (RecyclerView) rootView.findViewById(R.id.contact_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());

        contacts_list_recycler_view.setLayoutManager(mLayoutManager);
        contacts_list_recycler_view.setAdapter(mAdapter);


        return rootView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        Uri uri = tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.ContactsEntry._ID,
                tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI,
                tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_NAME,
                tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER,
                tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST,
                tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER,
                tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER
        };

        CursorLoader cursorLoader = new CursorLoader(getContext(), uri, projection,
                null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        cursor.moveToFirst();
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

