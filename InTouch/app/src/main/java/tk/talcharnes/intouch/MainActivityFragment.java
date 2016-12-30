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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    Cursor mCursor;


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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(contacts_list_recycler_view);


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
                ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY,
                ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY,
                ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER,
                ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER
        };


        CursorLoader cursorLoader = new CursorLoader(getContext(), uri, projection,
                null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        mCursor = cursor;
        cursor.moveToFirst();
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }



    //code from http://wiseassblog.com/tutorial/view/android/2016/06/17/how-to-build-a-recyclerview-part-5.html
    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        deleteItem(viewHolder.getAdapterPosition());
                    }
                };
        return simpleItemTouchCallback;
    }
    private void moveItem(int oldPos, int newPos){

    }

    private void deleteItem(final int position){

        int contact_idIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry._ID);
        mCursor.moveToPosition(position);
        int deletePosition = mCursor.getInt(contact_idIndex);
                getContext().getContentResolver().delete(ContactsContract.ContactsEntry.CONTENT_URI,
                "_ID = ?",
                        new String[]{""+deletePosition});
        mAdapter.notifyItemRemoved(position);

        Utility.updateWidgets(getContext());
        Log.d(LOG_TAG, "deleteItem position = " + position);
    }


}

