package tk.talcharnes.intouch.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.BufferedInputStream;
import java.io.InputStream;

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
    private static final String TextOnClick = "textOnClick";


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


                Bundle extras = new Bundle();
                Intent fillInIntent = new Intent();
                fillInIntent.setAction(CallOnClick);
                fillInIntent.putExtras(extras);
                fillInIntent.putExtra("number", number);
                views.setOnClickFillInIntent(R.id.call_button, fillInIntent);


                Bundle textExtras = new Bundle();
                Intent textFillInIntent = new Intent();
                textFillInIntent.setAction(TextOnClick);
                textFillInIntent.putExtras(textExtras);
                textFillInIntent.putExtra("number", number);
                textFillInIntent.putExtra("message_list", messageList);
                views.setOnClickFillInIntent(R.id.send_text_button, textFillInIntent);



                views.setTextViewText(R.id.contact_name, name);

                if(photoThumbnailUri!= null && !photoThumbnailUri.equals(null) && !photoThumbnailUri.equals("")){
                    try {
                        InputStream photo_stream = android.provider.ContactsContract.Contacts.openContactPhotoInputStream(getApplicationContext().getContentResolver(), Uri.parse(photoThumbnailUri.substring(0, photoThumbnailUri.length() - 6)));
                        BufferedInputStream buf = new BufferedInputStream(photo_stream);
                        Bitmap my_btmp = BitmapFactory.decodeStream(buf);
                        views.setImageViewBitmap(R.id.contact_image, getCircleBitmap(my_btmp));


                    }
                    catch (Exception ex){
                        //if permission to get contact photos wasn't granted then use default photo
                        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.mipmap.contact_photo);
                        views.setImageViewBitmap(R.id.contact_image,
                                icon);
                    }

                }
                else {
                    //if permission to get contact photos wasn't granted then use default photo
                    Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.contact_photo);
                        views.setImageViewBitmap(R.id.contact_image,
                                icon);
                }

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



    private Bitmap getCircleBitmap(Bitmap bitmap) {

        // class from http://curious-blog.blogspot.co.il/2014/05/create-circle-bitmap-in-android.html
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}
