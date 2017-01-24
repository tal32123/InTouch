package tk.talcharnes.intouch;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

/*
 * Credit to: http://www.androidhive.info/2015/09/android-material-design-snackbar-example/
 * Credit to: http://wiseassblog.com/tutorial/view/android/2016/06/17/how-to-build-a-recyclerview-part-5.html
 */
public class UpdateContactInfoActivity extends AppCompatActivity {
    private final String LOG_TAG = ContactDetailActivity.class.getSimpleName();
    private String name;
    private String phone_number;
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
    String contact_id;
    String ACTION_CALL_NOTIFICATION;
    String ACTION_SEND_TEXT;
    String ACTION_NOTIFICATION;
    int hour;
    int minutes;
    int am_pm;
    long notificationTimeInMillis;

    @Override
    protected void onPause() {
        super.onPause();
//      Create exit transition
        overridePendingTransition(R.anim.re_enter_slide_out, R.anim.re_enter_slide);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        Intent intent = getIntent();
        messageArrayListString = intent.getStringExtra("messageList");
        number = intent.getStringExtra("number");
        name = intent.getStringExtra("name");
        contact_id = intent.getStringExtra("contact_id");
        text_frequency = intent.getIntExtra("textFequency", 0);
        call_frequency = intent.getIntExtra("callFrequency", 0);
        notificationTimeInMillis = intent.getLongExtra("notificationTime", 0);


        String photoUri = intent.getStringExtra("photo_uri");
        if (photoUri != null) {
            photo_uri = photoUri;
        }

        ACTION_CALL_NOTIFICATION = "action_call";
        ACTION_SEND_TEXT = "action_send_text";
        ACTION_NOTIFICATION = "action_notification";

//        Get contact notification times to place in respective spinners
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTimeInMillis(notificationTimeInMillis);
        hour = timeCal.get(Calendar.HOUR);
        minutes = timeCal.get(Calendar.MINUTE);
        am_pm = timeCal.get(Calendar.AM_PM);

//      Get views
        nameView = (EditText) findViewById(R.id.contact_name);
        nameView.setText(name);
        phoneNumberView = (EditText) findViewById(R.id.contact_phone_number);
        phoneNumberView.setText(number);
        callFrequencyView = (EditText) findViewById(R.id.contact_call_frequency);
        callFrequencyView.setText("" + call_frequency, TextView.BufferType.EDITABLE);
        textFrequencyView = (EditText) findViewById(R.id.contact_text_frequency);
        textFrequencyView.setText("" + text_frequency, TextView.BufferType.EDITABLE);
        addMessageEditText = (EditText) findViewById(R.id.add_message_edittext);
        ImageButton addMessageButton = (ImageButton) findViewById(R.id.add_message_button);
        addMessageButton.setContentDescription(getString(R.string.add_message_to_list_description));

//      Set up hour picker spinner
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
        hourPicker.setSelection(hour);

//        Set up minute picker spinner
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
        minutePicker.setSelection(minutes);

//      Set up AM/PM picker spinner
        am_pm_spinner = (Spinner) findViewById(R.id.am_pm_spinner);
        String[] sortingCriteria = {getString(R.string.AM), getString(R.string.PM)};
        am_pm_spinnerAdapter = new ArrayAdapter<String>(this, R.layout.time_spinner, sortingCriteria);
        am_pm_spinner.setAdapter(am_pm_spinnerAdapter);
        am_pm_spinner.setSelection(am_pm);

//      Set up recycler view for message list
        message_list_recycler_view = (RecyclerView) findViewById(R.id.message_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        message_list_recycler_view.setLayoutManager(mLayoutManager);
        try {
            myDataset = Utility.getArrayListFromJSONString(messageArrayListString);
        } catch (JSONException e) {
            e.printStackTrace();
            myDataset = new ArrayList<String>();
        }

//      Set up add message edit text functionality
        addMessageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    myDataset.add(addMessageEditText.getText().toString());
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    handled = true;
                }
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //If it is the free version of the app user has a limited amount of messages they can have
                    if (myDataset.size() < 6 || getString(R.string.version).equals(getString(R.string.paid_version))) {
                        myDataset.add(addMessageEditText.getText().toString());

                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        addMessageEditText.setText("");
                    } else {
                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_contact_detail);
                        Snackbar snackbar = Snackbar
                                .make(relativeLayout, R.string.upgrade_for_more_messages_string, Snackbar.LENGTH_LONG)
                                .setAction(R.string.ACTION_UPGRADE, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
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
                    handled = true;
                }
                return handled;
            }
        });


        mAdapter = new MessageListAdapter(myDataset);
        message_list_recycler_view.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(message_list_recycler_view);
    }

    //    Recyclerview touch functions
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
//  Put code here if implementing move messages
    }

    //    Swipe to delete messages
    private void deleteItem(final int position) {
        myDataset.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    //  Save contact data
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

        am_pm = am_pm_spinner.getSelectedItemPosition();
        minutes = minutePicker.getSelectedItemPosition();
        hour = hourPicker.getSelectedItemPosition();

        notificationTimeInMillis = Utility.getTimeForNotification(hour, minutes, am_pm);

        if (!emptyField) {

            // Defines an object to contain the new values to insert
            ContentValues mNewValues = new ContentValues();
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_NAME, name);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER, number);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY, call_frequency);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY, text_frequency);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_NOTIFICATION_TIME, notificationTimeInMillis);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER, 0);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER, 0);
            if (photo_uri != null) {
                if (!photo_uri.equals(null) && !photo_uri.equals("")) {
                    mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI, photo_uri);
                }
            }
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST, messageArrayListString);

            int updateArray = getApplicationContext().getContentResolver().update(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.CONTENT_URI,
                    mNewValues,
                    "_ID = ?",
                    new String[]{contact_id});
            Log.d(LOG_TAG, "Updated row " + updateArray);
            Utility.updateWidgets(getApplicationContext());

            //Create text notifications
            createNotifications(ACTION_SEND_TEXT, text_frequency);
            createNotifications(ACTION_CALL_NOTIFICATION, call_frequency);

            NavUtils.navigateUpFromSameTask(this);
        } else {
//          Not all fields are filled
            Toast.makeText(this, R.string.fill_out_empty_fields, Toast.LENGTH_SHORT).show();
        }

    }

    //    Delete contact from list
    public void deleteData(View view) {

        //Delete contact from database
        getContentResolver().delete(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.CONTENT_URI,
                "_ID = ?",
                new String[]{contact_id});

        //Cancel current notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Integer.parseInt(contact_id));
        notificationManager.cancel(-1 * Integer.parseInt(contact_id));

        //Cancel future notifications
        PendingIntent textPendingIntent =
                Utility.createNotificationPendingIntent(
                        name,
                        number,
                        messageArrayListString,
                        contact_id,
                        photo_uri,
                        ACTION_SEND_TEXT,
                        getApplicationContext());

        PendingIntent callPendingIntent =
                Utility.createNotificationPendingIntent(
                        name,
                        number,
                        messageArrayListString,
                        contact_id,
                        photo_uri,
                        ACTION_CALL_NOTIFICATION,
                        getApplicationContext());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(textPendingIntent);
        alarmManager.cancel(callPendingIntent);

        Utility.updateWidgets(getApplicationContext());

        //Go back to home screen
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

    //  Add message to list
    public void addMessage(View view) {
        String message = addMessageEditText.getText().toString();
        if (message != null && !message.equals("")) {
            myDataset.add(message);
            addMessageEditText.setText("");
            mAdapter.notifyDataSetChanged();
        } else
            Toast.makeText(getApplicationContext(), getString(R.string.message_empty_string), Toast.LENGTH_SHORT).show();
    }

    private void createNotifications(String actionType, int frequencyInDays) {
        PendingIntent pendingIntent = Utility.createNotificationPendingIntent(name, number, messageArrayListString, contact_id, photo_uri, actionType, getApplicationContext());
        Utility.createNotifications(pendingIntent, getApplicationContext(), notificationTimeInMillis, frequencyInDays);
    }

}