package tk.talcharnes.intouch.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import tk.talcharnes.intouch.R;
import tk.talcharnes.intouch.data.ContactsContract;


/**
 * Created by Tal on 9/5/2016.
 * This will be a remote views that controls the data being shown in the widget
 * Credit for Udacity for help
 */
public class WidgetRemoteViewsService extends RemoteViewsService {
    public String LOG_TAG = WidgetRemoteViewsService.class.getSimpleName();
    private static final String CallOnClick = "callOnClick";


    //define necessary columns

    String[] COLUMNS = new String[]{
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


    private static final int INDEX_CONTACT_ID = 0;
    private static final int INDEX_PHOTO_URI = 1;
    private static final int INDEX_NAME = 2;
    private static final int INDEX_NUMBER = 3;
    private static final int INDEX_MESSAGE_LIST = 4;
    private static final int INDEX_TEXT_FREQUENCY = 5;
    private static final int INDEX_CALL_FREQUENCY = 6;
    private static final int INDEX_TEXT_NOTIFICATION_COUNTER = 7;
    private static final int INDEX_CALL_NOTIFICATION_COUNTER = 8;

    private Cursor data;


    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {

            @Override
            public void onCreate() {
                //nothing to do here
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }


                //Get data from content provider
                final long identityToken = Binder.clearCallingIdentity();

                Uri uri = ContactsContract.ContactsEntry.CONTENT_URI;
                data = getContentResolver().query(
                        uri,
                        COLUMNS,
                        null,
                        null,
                        null
                );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }

            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_contact_list_file);


                final String name = data.getString(INDEX_NAME);
                final String photoThumbnailUri = data.getString(INDEX_PHOTO_URI);
                final String number = data.getString(INDEX_NUMBER);
                final String messageList = data.getString(INDEX_MESSAGE_LIST);
                final int textFrequency = data.getInt(INDEX_TEXT_FREQUENCY);
                final int callFrequency = data.getInt(INDEX_CALL_FREQUENCY);
                final int contact_id = data.getInt(INDEX_CONTACT_ID);
                final int callCounter = data.getInt(INDEX_CALL_NOTIFICATION_COUNTER);
                final int textCounter = data.getInt(INDEX_TEXT_NOTIFICATION_COUNTER);


//                set content description here
//                setRemoteContentDescription(views, description);

                //// TODO: 12/27/2016 Add photo, call, and text functions below 


                views.setOnClickPendingIntent(R.id.call_button, getPendingSelfIntent(getApplicationContext(), CallOnClick, number));


                views.setTextViewText(R.id.contact_name, name);

//                set onclick intent for view
                final Intent intentt = new Intent();
                intentt.putExtra("test", "test");
                intentt.setData(ContactsContract.ContactsEntry.CONTENT_URI);
                views.setOnClickFillInIntent(R.id.contact_list_file, intentt);


                return views;
            }
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.layout.widget_contact_list_file, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_contact_list_file);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {

                if (data.moveToPosition(position))
                    return data.getLong(INDEX_CONTACT_ID);
                return position;
            }


            @Override
            public boolean hasStableIds() {
                return true;
            }
        };


    }
    protected PendingIntent getPendingSelfIntent(Context context, String action, String number) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra("number", number);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(Context context, Intent intent) {

        if (CallOnClick.equals(intent.getAction())){
            Log.d("PENDINGINTENT", "call called");
            String number = intent.getStringExtra("number");
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + number));
                if (callIntent.resolveActivity(context.getPackageManager()) != null) {
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(callIntent);
                }

        }
    };


}
