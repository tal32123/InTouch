package tk.talcharnes.intouch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * Credit to skyfishjy
 */
public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder> {
    private Context mContext;
    private final String LOG_TAG = MyListCursorAdapter.class.getSimpleName();

    public MyListCursorAdapter(Context context,Cursor cursor){
        super(context,cursor);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView mTextView;
        TextView contactName;
        ImageButton callButton;
        ImageButton textButton;
        CardView contactCardView;
        TextView frequencyView;
        ImageView contactPhotoView;

        public ViewHolder(View view) {
            super(view);
            contactName = (TextView) view.findViewById(R.id.contact_name);
            callButton = (ImageButton) view.findViewById(R.id.call_button);
            textButton = (ImageButton) view.findViewById(R.id.send_text_button);
            contactCardView = (CardView) view.findViewById(R.id.contact_card_view);
            frequencyView = (TextView) view.findViewById(R.id.frequency);
            contactPhotoView = (ImageView) view.findViewById(R.id.contact_image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_file, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        int contact_idIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry._ID);
        int nameIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_NAME);
        int photoThumbnailUriIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI);
        int phoneNumberIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER);
        int messageListIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST);
        int textFrequencyIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_TEXT_FREQUENCY);
        int callFrequencyIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_CALL_FREQUENCY);
        int callCounterIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER);
        int textCounterIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER);

        final String name = cursor.getString(nameIndex);
        final String photoThumbnailUri = cursor.getString(photoThumbnailUriIndex);
        final String phoneNumber = cursor.getString(phoneNumberIndex);
        final String messageList = cursor.getString(messageListIndex);
        final int textFrequency = cursor.getInt(textFrequencyIndex);
        final int callFrequency = cursor.getInt(callFrequencyIndex);
        final int contact_id = cursor.getInt(contact_idIndex);
        final int callCounter = cursor.getInt(callCounterIndex);
        final int textCounter = cursor.getInt(textCounterIndex);

        viewHolder.contactName.setText(name);

        // Sends a random text
        viewHolder.textButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {

                        //Turn string of all messages into an ArrayList in order to get one specific message at random
                        ArrayList<String> messagesArrayList = null;
                        try {
                            messagesArrayList = Utility.getArrayListFromJSONString(messageList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Random rand = new Random();

                        int  n = rand.nextInt(messagesArrayList.size());

                        Log.d(LOG_TAG,
                                "n = " + n
                                        + "MessagesArrayList size = " + messagesArrayList.size()
                                        + "message = " + messagesArrayList.get(n));

                        String message = messagesArrayList.get(n);

                        try {

                            //send text message
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                            Toast.makeText(mContext, "Message Sent",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            //If text message isn't sent show why
                            Toast.makeText(mContext,ex.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                            ex.printStackTrace();


                            //If text message wasn't sent attempt to send text another way (through the user's text messaging app)
                            // Most likely due to text message permissions not being accepted by user
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("smsto:" + phoneNumber));  // This ensures only SMS apps respond
                            intent.putExtra("sms_body", message);
                            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                                mContext.startActivity(intent);
                            }
                        }
                    }
                }
        );

        viewHolder.callButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                            mContext.startActivity(intent);
                        }
                    }
                }
        );

        viewHolder.contactCardView.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Edit field", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, UpdateContactInfoActivity.class);
                        intent.putExtra("messageList", messageList);
                        intent.putExtra("name", name);
                        intent.putExtra("number", phoneNumber);
                        intent.putExtra("contact_id", "" + contact_id);
                        intent.putExtra("textFequency", textFrequency);
                        intent.putExtra("callFrequency", callFrequency);
                        intent.putExtra("photo_uri", photoThumbnailUri);

                        mContext.startActivity(intent);


                    }
                }
        );

        if (callCounter > textCounter){
            viewHolder.frequencyView.setText(textCounter + " days since last text");
        }
        else if (callCounter == textCounter){
            viewHolder.frequencyView.setText("Call or text " + name);
        }
        else {
            viewHolder.frequencyView.setText(callCounter + " days since last call");
        }

        if(photoThumbnailUri!= null && !photoThumbnailUri.equals(null) && !photoThumbnailUri.equals("")){
//            Utility.getContactBitmapFromURI(mContext, Uri.parse(photoThumbnailUri));
//            Picasso.with(mContext).load(photoThumbnailUri).into(viewHolder.contactPhotoView);

        }

    }


}