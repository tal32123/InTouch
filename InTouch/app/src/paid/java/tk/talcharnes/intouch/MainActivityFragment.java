package tk.talcharnes.intouch;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView contacts_list_recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    //    ContactsListAdapter mAdapter;
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
    //Needed for firebase
    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;

    private String firebaseContactKey;
    boolean googlePlayServicesApiValid;


    public MainActivityFragment() {
        ACTION_CALL_NOTIFICATION = "action_call";
        ACTION_SEND_TEXT = "action_send_text";
        ACTION_NOTIFICATION = "action_notification";
        if (GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE >= 10084000) {
            googlePlayServicesApiValid = true;
            Log.d(LOG_TAG, "GOOGLE PLAY SERVICES VERSION = " + GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE);
        } else {
            googlePlayServicesApiValid = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googlePlayServicesApiValid == true) {
            setHasOptionsMenu(true);


            //firebase
            mUsername = "ANONYMOUS";
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference();


            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        //User is signed in
                    } else {
                        //User is signed out
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            };
        }

    else
    {
        setHasOptionsMenu(false);
        Toast.makeText(getContext(), "Please upgrade Google Play Services for all features", Toast.LENGTH_SHORT).show();
    }

}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.restore_database) {
            restoreDatabase(getContext());

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER,
                ContactsContract.ContactsEntry.COLUMN_FIREBASE_CONTACT_KEY,
                ContactsContract.ContactsEntry.COLUMN_NOTIFICATION_TIME
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
        int firebaseKeyIndex = mCursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_FIREBASE_CONTACT_KEY);

        name = mCursor.getString(nameIndex);
        number = mCursor.getString(numberIndex);
        photo_uri = mCursor.getString(photo_uriIndex);
        messageArrayListString = mCursor.getString(messageListIndex);
        firebaseContactKey = mCursor.getString(firebaseKeyIndex);

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

        //Delete Contact from Firebase
        BackupDB backupDB = new BackupDB(getContext());
        backupDB.deleteContactFromFirebase(firebaseContactKey);



        Utility.updateWidgets(getContext());
        Log.d(LOG_TAG, "deleteItem position = " + position);
    }

    private PendingIntent createNotificationPendingIntent(String action){
        return Utility.createNotificationPendingIntent(name, number, messageArrayListString,contact_id, photo_uri, action, getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(googlePlayServicesApiValid) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(googlePlayServicesApiValid) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    private void restoreDatabase(Context context){
        BackupDB backupDB = new BackupDB(context);
        backupDB.restoreDB();
    }

}

