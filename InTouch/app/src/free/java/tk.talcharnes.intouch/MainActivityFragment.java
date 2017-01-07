package tk.talcharnes.intouch;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.GregorianCalendar;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    RecyclerView contacts_list_recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    AdView mAdView;
    MyListCursorAdapter mAdapter;
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    Cursor mCursor;
    String contact_id;
    String name;
    String number;
    String photo_uri;
    String messageArrayListString;
    String ACTION_SEND_TEXT;
    String ACTION_CALL_NOTIFICATION;
    String ACTION_NOTIFICATION;


    public MainActivityFragment() {
        ACTION_CALL_NOTIFICATION = "action_call";
        ACTION_SEND_TEXT = "action_send_text";
        ACTION_NOTIFICATION = "action_notification";
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




        // Load an ad into the AdMob banner view.
        mAdView = (AdView) rootView.findViewById(R.id.fragment_main_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();

        mAdView.loadAd(adRequest);

        //todo Next version/update add reward video after third contact addition?
        // https://firebase.google.com/docs/admob/android/rewarded-video


        return rootView;
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        Uri uri = ContactsContract.ContactsEntry.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.ContactsEntry._ID,
                ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI,
                ContactsContract.ContactsEntry.COLUMN_NAME,
                ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER,
                ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST,
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
//        cursor.moveToFirst();
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


        int nameIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_NAME);
        int numberIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER);
        int photo_uriIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI);
        int messageListIndex = mCursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST);


        name = mCursor.getString(nameIndex);
        number = mCursor.getString(numberIndex);
        photo_uri = mCursor.getString(photo_uriIndex);
        messageArrayListString = mCursor.getString(messageListIndex);



        int deletePosition = mCursor.getInt(contact_idIndex);
        contact_id = ""+(deletePosition);

        getContext().getContentResolver().delete(ContactsContract.ContactsEntry.CONTENT_URI,
                "_ID = ?",
                        new String[]{""+deletePosition});
        mAdapter.notifyItemRemoved(position);


        //Cancel current notifications
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(deletePosition);
        notificationManager.cancel(-1 * deletePosition);

        //Cancel future notifications


        PendingIntent textPendingIntent = createNotificationPendingIntent(ACTION_SEND_TEXT);
        PendingIntent callPendingIntent = createNotificationPendingIntent(ACTION_CALL_NOTIFICATION);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(textPendingIntent);
        alarmManager.cancel(callPendingIntent);



        Utility.updateWidgets(getContext());
        Log.d(LOG_TAG, "deleteItem position = " + position);
    }

    private PendingIntent createNotificationPendingIntent(String action){

        Long alertTime = new GregorianCalendar().getTimeInMillis()+5*1000;
        Intent alertIntent = new Intent(getContext(), AlertReceiver.class);
        alertIntent.putExtra("name", name);
        alertIntent.putExtra("number", number);
        alertIntent.putExtra("messageList", messageArrayListString);
        alertIntent.putExtra("contactID", contact_id);
        alertIntent.putExtra("photo_uri", photo_uri);
        alertIntent.setAction(action);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), Integer.parseInt(contact_id), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }



}

