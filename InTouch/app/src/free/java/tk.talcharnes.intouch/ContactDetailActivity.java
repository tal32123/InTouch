package tk.talcharnes.intouch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
import android.widget.LinearLayout;
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
import java.util.GregorianCalendar;


public class ContactDetailActivity extends AppCompatActivity {
    private final String LOG_TAG = ContactDetailActivity.class.getSimpleName();
    private String name;
    private String phone_number;
    private int call_frequency;
    private int text_frequency;
    private String notification_time;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        ACTION_CALL_NOTIFICATION = "action_call";
        ACTION_SEND_TEXT = "action_send_text";
        ACTION_NOTIFICATION = "action_notification";


        nameView = (EditText)findViewById(R.id.contact_name);
        phoneNumberView = (EditText)findViewById(R.id.contact_phone_number);
        callFrequencyView = (EditText)findViewById(R.id.contact_call_frequency);
        textFrequencyView = (EditText)findViewById(R.id.contact_text_frequency);
        addMessageEditText = (EditText) findViewById(R.id.add_message_edittext);

        hourPicker = (Spinner) findViewById(R.id.hour_picker);
        String[] hourArray = new String[12];
        for (int i = 1; i< 13; i++){
            if(i-1 < 9){
                hourArray[i-1] = "0" + i + " :";

            }
            else {hourArray[i-1] = i + ":";}
        }
        SpinnerAdapter hourAdapter = new ArrayAdapter<String>(this, R.layout.time_spinner, hourArray);
        hourPicker.setAdapter(hourAdapter);

        minutePicker = (Spinner) findViewById(R.id.minute_picker);
        String[] minuteArray = new String[60];
        minuteArray[0] = "00";
        for (int i = 1; i< 60; i++){
            minuteArray[i] = Integer.toString(i);
        }
        SpinnerAdapter minuteAdapter = new ArrayAdapter<String>(this, R.layout.time_spinner, minuteArray);
        minutePicker.setAdapter(minuteAdapter);


        am_pm_spinner = (Spinner) findViewById(R.id.am_pm_spinner);

        String[] sortingCriteria = {"A.M.", "P.M."};
         am_pm_spinnerAdapter = new ArrayAdapter<String>(this, R.layout.time_spinner, sortingCriteria);
        am_pm_spinner.setAdapter(am_pm_spinnerAdapter);

        message_list_recycler_view = (RecyclerView) findViewById(R.id.message_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        message_list_recycler_view.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        String[] preSetMessages = new String[]{getString(R.string.message_1), getString(R.string.message_2), getString(R.string.message_3), getString(R.string.message_4)};
        myDataset = new ArrayList<String>();
        myDataset.addAll(Arrays.asList(preSetMessages));

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
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(myDataset.size()<6) {
                        myDataset.add(addMessageEditText.getText().toString());

                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        addMessageEditText.setText("");
                    }
                    else {

                        //snackbar code from: http://www.androidhive.info/2015/09/android-material-design-snackbar-example/
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_contact_detail);
                        Snackbar snackbar = Snackbar
                                .make(linearLayout, R.string.upgrade_for_more_messages_string, Snackbar.LENGTH_LONG)
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



        //interstitial ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });


//// TODO: 1/3/2017 refactor interstitial code, most likely can remove some of the code/move it around especially in regards to requestnewinterstitial
        //todo also if ad isn't shown save anyways
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
        myDataset.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void saveData(View view) {
        boolean emptyField = false;

        if (nameView.getText().toString() != null && !nameView.getText().toString().equals("") && !nameView.getText().toString().isEmpty()) {
            name = nameView.getText().toString();
        }
        else {emptyField = true;}


        if (phoneNumberView.getText().toString() != null && !phoneNumberView.getText().toString().equals("") && !phoneNumberView.getText().toString().isEmpty()) {
            phone_number = phoneNumberView.getText().toString();
        }
        else {emptyField = true;}

        if (callFrequencyView.getText().toString() != null && !callFrequencyView.getText().toString().equals("") && !callFrequencyView.getText().toString().isEmpty()) {
            call_frequency = Integer.parseInt(callFrequencyView.getText().toString());
        }
        else {emptyField = true;}
        if(textFrequencyView.getText().toString() != null && !textFrequencyView.getText().toString().equals("") && !textFrequencyView.getText().toString().isEmpty()){
            text_frequency = Integer.parseInt(textFrequencyView.getText().toString());
        }
        else {emptyField = true;}
        if(!myDataset.isEmpty()){

            messageArrayListString = Utility.createStringFromArrayList(myDataset);
            Log.d(LOG_TAG, "arrayList String = " + messageArrayListString);

        }

        int  minutes = minutePicker.getSelectedItemPosition();

        if(!emptyField) {

         // Defines an object to contain the new values to insert
            ContentValues mNewValues = new ContentValues();
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_NAME, name);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER, phone_number);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY, call_frequency);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY, text_frequency);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_NOTIFICATION_TIME, minutes);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER, 0);
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER, 0);
            if(photo_uri != null){
                if(!photo_uri.equals(null) && !photo_uri.equals("")) {
                    mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI, photo_uri);
                }
            }
            mNewValues.put(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST, messageArrayListString);



            Uri mNewUri = getApplicationContext().getContentResolver().insert(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.CONTENT_URI, mNewValues);

            contactID = ContentUris.parseId(mNewUri);

            Utility.updateWidgets(getApplicationContext());

            requestNewInterstitial();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            else {
                Log.d(LOG_TAG, "Interstitial not loaded");
                // up button navigation
            NavUtils.navigateUpFromSameTask(this);



                createNotifications(ACTION_SEND_TEXT);
                createNotifications(ACTION_CALL_NOTIFICATION);
            }
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                    Intent upIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(upIntent);



                    createNotifications(ACTION_SEND_TEXT);
                    createNotifications(ACTION_CALL_NOTIFICATION);


                }
            });



        }
        else{
            Toast.makeText(this, R.string.fill_out_empty_fields, Toast.LENGTH_SHORT).show();
        }

    }


    public void deleteData(View view){
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
                if(number != null && !number.equals("")){
                    phoneNumberView.setText(number);
                }
                contact_name = cursor.getString(nameIndex);
                if(contact_name != null && !contact_name.equals("")){
                    nameView.setText(contact_name);
                }
                photo_uri = cursor.getString(photoIndex);


            }
        }
    }



    public void readFromDB(View view){
        Cursor cursor = getContentResolver().query(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.CONTENT_URI, null, null, null, null, null);
        if(cursor.moveToFirst()){
          String cursorString =  DatabaseUtils.dumpCursorToString(cursor);
            Log.d(LOG_TAG, cursorString);
        }
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setContentTitle("Title")
//                .setContentText("Text")
//                .setTicker("Alert new message!")
//                .setSmallIcon(R.mipmap.ic_launcher);
//        Intent moreInfoIntent = new Intent(this, Notifications.class);
//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
//        taskStackBuilder.addParentStack(Notifications.class);
//        taskStackBuilder.addNextIntent(moreInfoIntent);
//        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        notificationBuilder.setContentIntent(pendingIntent);
//        NotificationManager notificationManager;
//        boolean isNotificationActive = false;
//        int notifID = 33;
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(notifID, notificationBuilder.build());
//        isNotificationActive = true;



//        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this, 1, alertIntent, 0));
    }
    private void createNotifications(String actionType){

        //alarm notification

        Long alertTime = new GregorianCalendar().getTimeInMillis()+5*1000;
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        alertIntent.putExtra("name", name);
        alertIntent.putExtra("number", number);
        alertIntent.putExtra("messageList", messageArrayListString);
        alertIntent.putExtra("contactID", contactID.toString());
        alertIntent.setAction(actionType);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alertIntent, 0);

        Calendar cal = Calendar.getInstance();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 3 * 60 * 1000, pendingIntent);

    }
    //request new interstitial ads
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

}