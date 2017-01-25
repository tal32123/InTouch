package tk.talcharnes.intouch;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

import tk.talcharnes.intouch.paid.Contact;


public class ContactDetailActivity extends AppCompatActivity {
    private final String LOG_TAG = ContactDetailActivity.class.getSimpleName();
    private String name;
    private int call_frequency;
    private int text_frequency;
    EditText nameView;
    EditText phoneNumberView;
    EditText callFrequencyView;
    EditText textFrequencyView;
    EditText addMessageEditText;
    Spinner hourPicker;
    Spinner minutePicker;
    Spinner am_pm_spinner;
    ArrayAdapter<String> am_pm_spinnerAdapter;
    String number;
    String photo_uri;
    String contact_name;
    String messageArrayListString;
    RecyclerView message_list_recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<String> myDataset;
    String ACTION_SEND_TEXT;
    String ACTION_CALL_NOTIFICATION;
    String ACTION_NOTIFICATION;
    Long contactID;
    int minutes;
    int hour;
    int am_pm;
    long notificationTime;
    String mUserID;
    boolean googlePlayServicesApiValid;


    //Needed for firebase
    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        if (GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE >= 10084000) {
            googlePlayServicesApiValid = true;
            Log.d(LOG_TAG, "GOOGLE PLAY SERVICES VERSION = " + GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE);
        } else {
            Log.d(LOG_TAG, "GOOGLE PLAY SERVICES VERSION is too low. Version = " + GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE);
            googlePlayServicesApiValid = false;
        }

        ACTION_CALL_NOTIFICATION = "action_call";
        ACTION_SEND_TEXT = "action_send_text";
        ACTION_NOTIFICATION = "action_notification";


        nameView = (EditText) findViewById(R.id.contact_name);
        phoneNumberView = (EditText) findViewById(R.id.contact_phone_number);
        callFrequencyView = (EditText) findViewById(R.id.contact_call_frequency);
        textFrequencyView = (EditText) findViewById(R.id.contact_text_frequency);
        addMessageEditText = (EditText) findViewById(R.id.add_message_edittext);
        ImageButton addMessageButton = (ImageButton) findViewById(R.id.add_message_button);
        addMessageButton.setContentDescription(getString(R.string.add_message_to_list_description));


        hourPicker = (Spinner) findViewById(R.id.hour_picker);
        String[] hourArray = new String[12];
        hourArray[0] = "12";
        for (int i = 1; i < 12; i++) {
            if (i < 10) {
                hourArray[i] = "0" + i;
            } else {
                hourArray[i] = Integer.toString(i);
            }
        }
        SpinnerAdapter hourAdapter = new ArrayAdapter<String>(this, R.layout.time_spinner, hourArray);
        hourPicker.setAdapter(hourAdapter);

        minutePicker = (Spinner) findViewById(R.id.minute_picker);
        String[] minuteArray = new String[60];
        for (int i = 0; i < 60; i++) {
            if (i > 9) {
                minuteArray[i] = Integer.toString(i);
            } else {
                minuteArray[i] = "0" + Integer.toString(i);
            }
        }
        SpinnerAdapter minuteAdapter = new ArrayAdapter<String>(this, R.layout.time_spinner, minuteArray);
        minutePicker.setAdapter(minuteAdapter);


        am_pm_spinner = (Spinner) findViewById(R.id.am_pm_spinner);

        String[] sortingCriteria = {getString(R.string.AM), getString(R.string.PM)};
        am_pm_spinnerAdapter = new ArrayAdapter<String>(this, R.layout.time_spinner, sortingCriteria);
        am_pm_spinner.setAdapter(am_pm_spinnerAdapter);

        message_list_recycler_view = (RecyclerView) findViewById(R.id.message_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        message_list_recycler_view.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        if (savedInstanceState != null) {
            myDataset = savedInstanceState.getStringArrayList("myDataset");
        } else {
            String[] preSetMessages = new String[]{getString(R.string.message_1), getString(R.string.message_2), getString(R.string.message_3), getString(R.string.message_4)};
            myDataset = new ArrayList<String>();
            myDataset.addAll(Arrays.asList(preSetMessages));
        }

        addMessageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {

//                      Hide keyboard
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    addMessage();

                    handled = true;

                }
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                      Hide keyboard
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    addMessage();

                    handled = true;
                }
                return handled;
            }
        });


        mAdapter = new MessageListAdapter(myDataset);
        message_list_recycler_view.setAdapter(mAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(message_list_recycler_view);

        if (googlePlayServicesApiValid) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
//                    //User is signed in
                mUsername = user.getDisplayName();
                mUserID = user.getUid();
            } else {
//                    //User is signed out
                Toast.makeText(this, R.string.please_log_in, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

        }
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


    private void moveItem(int oldPos, int newPos) {

    }

    private void deleteItem(final int position) {
        myDataset.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void saveData(View view) {
        boolean emptyField = false;

        if (nameView.getText().toString() != null && !nameView.getText().toString().equals("") && !nameView.getText().toString().isEmpty()) {
            name = nameView.getText().toString();
        } else {
            emptyField = true;
        }


        if (phoneNumberView.getText().toString() != null && !phoneNumberView.getText().toString().equals("") && !phoneNumberView.getText().toString().isEmpty()) {
            number = phoneNumberView.getText().toString();
        } else {
            emptyField = true;
        }

        if (callFrequencyView.getText().toString() != null && !callFrequencyView.getText().toString().equals("") && !callFrequencyView.getText().toString().isEmpty()) {
            if (!callFrequencyView.getText().toString().equals("0")) {
                call_frequency = Integer.parseInt(callFrequencyView.getText().toString());
            } else {
                emptyField = true;
                callFrequencyView.setError(getString(R.string.call_frequency_0_error));
            }
        } else {
            emptyField = true;
        }
        if (textFrequencyView.getText().toString() != null && !textFrequencyView.getText().toString().equals("") && !textFrequencyView.getText().toString().isEmpty()) {
            if (!textFrequencyView.getText().toString().equals("0")) {
                text_frequency = Integer.parseInt(textFrequencyView.getText().toString());
            } else {
                emptyField = true;
                textFrequencyView.setError(getString(R.string.text_frequency_0_error));
            }
        } else {
            emptyField = true;
        }
        if (!myDataset.isEmpty()) {
            messageArrayListString = Utility.createStringFromArrayList(myDataset);
            Log.d(LOG_TAG, "arrayList String = " + messageArrayListString);
        } else {
            emptyField = true;
            Toast.makeText(this, R.string.message_list_empty_error, Toast.LENGTH_SHORT).show();
        }

        minutes = minutePicker.getSelectedItemPosition();
        hour = hourPicker.getSelectedItemPosition();
        am_pm = am_pm_spinner.getSelectedItemPosition();
        notificationTime = Utility.getTimeForNotification(hour, minutes, am_pm);

        if (!emptyField) {


            // Defines an object to contain the new values to insert
            ContentValues mNewValues = new ContentValues();
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_NAME, name);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER, number);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY, call_frequency);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY, text_frequency);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_NOTIFICATION_TIME, notificationTime);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER, 0);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER, 0);

            if (photo_uri != null) {
                if (!photo_uri.equals(null) && !photo_uri.equals("")) {
                    mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI, photo_uri);
                }
            }
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST, messageArrayListString);


            if (googlePlayServicesApiValid) {
                Contact contact = new Contact();
                contact.setCallFrequency(call_frequency);
                contact.setName(name);
                contact.setTextFrequency(text_frequency);
                contact.setNumber(number);
                contact.setMessageListJsonString(messageArrayListString);
                contact.setNotificationTime(notificationTime);

                mDatabaseReference = mDatabaseReference.child(mUserID);

                DatabaseReference db_ref = mDatabaseReference.push();  //creates blank record in db
                String firebaseContactKey = db_ref.getKey();             //the UniqueID/key
                db_ref.setValue(contact);
                Log.d(LOG_TAG, "firebase key = " + firebaseContactKey);
                if (googlePlayServicesApiValid) {
                    mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_FIREBASE_CONTACT_KEY, firebaseContactKey);
                }

            }


            Uri mNewUri = getApplicationContext().getContentResolver().insert(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.CONTENT_URI, mNewValues);
            contactID = ContentUris.parseId(mNewUri);

            Utility.updateWidgets(getApplicationContext());


            createNotifications(ACTION_SEND_TEXT, text_frequency);
            createNotifications(ACTION_CALL_NOTIFICATION, call_frequency);

            NavUtils.navigateUpFromSameTask(this);


        } else {
            Toast.makeText(this, getString(R.string.fill_out_empty_fields), Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteData(View view) {
        NavUtils.navigateUpFromSameTask(this);

    }

    public void chooseContact(View view) {
        selectContact();

    }

    static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    public void selectContact() {
        // Start an activity for the user to pick a phone number from contacts
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
        }
    }

    //    Add message to list of random messages to send contact
    public void addMessageButton(View view) {
        addMessage();
    }

    public void addMessage() {
        String message = addMessageEditText.getText().toString();
        if (message != null && !message.equals("")) {
            myDataset.add(message);
            addMessageEditText.setText("");
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), R.string.message_empty_string, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);


                number = cursor.getString(numberIndex);
                if (number != null && !number.equals("")) {
                    phoneNumberView.setText(number);
                }
                contact_name = cursor.getString(nameIndex);
                if (contact_name != null && !contact_name.equals("")) {
                    nameView.setText(contact_name);
                }
                photo_uri = cursor.getString(photoIndex);


            }
        }
    }


    private void createNotifications(String actionType, int frequencyInDays) {

        //alarm notification

        PendingIntent pendingIntent = Utility.createNotificationPendingIntent(name, number, messageArrayListString, contactID.toString(), photo_uri, actionType, getApplicationContext());
        Utility.createNotifications(pendingIntent, getApplicationContext(), notificationTime, frequencyInDays);


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.re_enter_up_out, R.anim.re_enter_up_in);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("myDataset", myDataset);
    }

}