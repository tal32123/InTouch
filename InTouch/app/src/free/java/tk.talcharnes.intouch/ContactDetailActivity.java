package tk.talcharnes.intouch;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/*
 * Credit to: http://www.androidhive.info/2015/09/android-material-design-snackbar-example/
 * Credit to: http://wiseassblog.com/tutorial/view/android/2016/06/17/how-to-build-a-recyclerview-part-5.html

 */

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
    InterstitialAd mInterstitialAd;
    Long contactID;
    int minutes;
    int hour;
    int am_pm;
    long notificationTime;
    int dayOfYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        ACTION_CALL_NOTIFICATION = "action_call";
        ACTION_SEND_TEXT = "action_send_text";
        ACTION_NOTIFICATION = "action_notification";

        //Set up views
        nameView = (EditText) findViewById(R.id.contact_name);
        phoneNumberView = (EditText) findViewById(R.id.contact_phone_number);
        callFrequencyView = (EditText) findViewById(R.id.contact_call_frequency);
        textFrequencyView = (EditText) findViewById(R.id.contact_text_frequency);
        addMessageEditText = (EditText) findViewById(R.id.add_message_edittext);
        ImageButton addMessageButton = (ImageButton) findViewById(R.id.add_message_button);
        addMessageButton.setContentDescription(getString(R.string.add_message_to_list_description));

        //set up hour picker spinner
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


        //Set up minute picker spinner
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

        //Set up spinner for am/pm hours
        am_pm_spinner = (Spinner) findViewById(R.id.am_pm_spinner);
        String[] sortingCriteria = {"A.M.", "P.M."};
        am_pm_spinnerAdapter = new ArrayAdapter<String>(this, R.layout.time_spinner, sortingCriteria);
        am_pm_spinner.setAdapter(am_pm_spinnerAdapter);

        //Set up recycler view for messages
        message_list_recycler_view = (RecyclerView) findViewById(R.id.message_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        message_list_recycler_view.setLayoutManager(mLayoutManager);


        if (savedInstanceState != null) {
            myDataset = savedInstanceState.getStringArrayList("myDataset");

        } else {
            String[] preSetMessages = new String[]{getString(R.string.message_1), getString(R.string.message_2), getString(R.string.message_3), getString(R.string.message_4)};
            myDataset = new ArrayList<String>();
            myDataset.addAll(Arrays.asList(preSetMessages));
        }
        //Messages are added to recyclerview
        addMessageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (myDataset.size() < 6) {
//                      Hide keyboard
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        addMessage();
                    } else {
                        createSnackBar(v);
                    }
                    handled = true;

                }
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (myDataset.size() < 6) {
//                      Hide keyboard
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        addMessage();
                    } else {
                        createSnackBar(v);
                    }
                    handled = true;
                }
                return handled;
            }
        });


// Connect adapter to data and initialize message list recycler view
        mAdapter = new MessageListAdapter(myDataset);
        message_list_recycler_view.setAdapter(mAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(message_list_recycler_view);


        //interstitial ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });


    }

    //Code for swipe to delete messages
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

    //    code to save contact information
    public void saveData(View view) {
        boolean emptyField = false;
        dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

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
//                emptyField = true;
//                callFrequencyView.setError(getString(R.string.call_frequency_0_error));
            }
        } else {
            emptyField = true;
        }
        if (textFrequencyView.getText().toString() != null && !textFrequencyView.getText().toString().equals("") && !textFrequencyView.getText().toString().isEmpty()) {
            if (!textFrequencyView.getText().toString().equals("0")) {
                text_frequency = Integer.parseInt(textFrequencyView.getText().toString());
            } else {
//                emptyField = true;
//                textFrequencyView.setError(getString(R.string.text_frequency_0_error));
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

//      Get data from spinners
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
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER, dayOfYear);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER, dayOfYear);
            if (photo_uri != null) {
                if (!photo_uri.equals(null) && !photo_uri.equals("")) {
                    mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI, photo_uri);
                }
            }
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST, messageArrayListString);

            Uri mNewUri = getApplicationContext().getContentResolver().insert(
                    tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.CONTENT_URI,
                    mNewValues);
            contactID = ContentUris.parseId(mNewUri);

            Utility.updateWidgets(getApplicationContext());

            requestNewInterstitial();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d(LOG_TAG, "Interstitial not loaded");
                // up button navigation
                NavUtils.navigateUpFromSameTask(this);
                if(text_frequency != 0) {
                    createNotifications(ACTION_SEND_TEXT, text_frequency);
                }
                if(call_frequency != 0) {
                    createNotifications(ACTION_CALL_NOTIFICATION, call_frequency);
                }
            }
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                    Intent upIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(upIntent);

                    if(text_frequency != 0) {
                        createNotifications(ACTION_SEND_TEXT, text_frequency);
                    }
                    if(call_frequency != 0) {
                        createNotifications(ACTION_CALL_NOTIFICATION, call_frequency);
                    }


                }
            });


        } else {
//          All fields must be filled
            Toast.makeText(this, R.string.fill_out_empty_fields, Toast.LENGTH_SHORT).show();
        }

    }


    //  Delete contact information
// (Since contact has not been saved yet here it just goes up to main activity)
    public void deleteData(View view) {
        NavUtils.navigateUpFromSameTask(this);

    }

    //    Choose contact from phone contacts
    public void chooseContact(View view) {
        selectContact();

    }

    static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    //    Choose contact from phone contacts
    public void selectContact() {
        // Start an activity for the user to pick a phone number from contacts
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
        }
    }

    //   Gets contact information from phone contacts
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


    @Override
    protected void onPause() {
        super.onPause();
//       Create transition
        overridePendingTransition(R.anim.re_enter_up_out, R.anim.re_enter_up_in);

    }

    //  Create notifications for contact
    private void createNotifications(String actionType, int frequencyInDays) {

        PendingIntent pendingIntent = Utility.createNotificationPendingIntent(name, number, messageArrayListString, contactID.toString(), photo_uri, actionType, getApplicationContext());
        Utility.createNotifications(pendingIntent, getApplicationContext(), notificationTime, frequencyInDays, dayOfYear, true);

    }

    //request new interstitial ads
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("1ECB075D2EA88E0C7DF7EA1E269DD2E2")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    //    Add message to list of random messages to send contact
    public void addMessageButton(View view) {
        if (myDataset.size() < 6) {
            addMessage();
        }
        else {
            createSnackBar(null);

        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("myDataset", myDataset);
    }

    private void createSnackBar(View v){
            if (v != null) {
//                      Hide keyboard
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_contact_detail);
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, R.string.upgrade_for_more_messages_string, Snackbar.LENGTH_LONG)
                    .setAction(R.string.ACTION_UPGRADE, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            upgradeApp();
                        }
                    });

// Changing message text color
            snackbar.setActionTextColor(Color.RED);

// Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
    }

    private void upgradeApp(){
        final String appPackageName = "tk.talcharnes.intouch.paid";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
    }
}