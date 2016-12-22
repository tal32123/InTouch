package tk.talcharnes.intouch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import tk.talcharnes.intouch.data.ContactsContract;

/**
 * Credit to skyfishjy
 */
public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder> {
    private Context mContext;

    public MyListCursorAdapter(Context context,Cursor cursor){
        super(context,cursor);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView mTextView;
        TextView contactName;
        ImageButton callButton;
        ImageButton textButton;
        public ViewHolder(View view) {
            super(view);
            contactName = (TextView) view.findViewById(R.id.contact_name);
            callButton = (ImageButton) view.findViewById(R.id.call_button);
            textButton = (ImageButton) view.findViewById(R.id.send_text_button);        }
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

        int nameIndex = cursor.getColumnIndex(ContactsContract.ContactsEntry.COLUMN_NAME);
        int photoThumbnailUriIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHOTO_THUMBNAIL_URI);
        int phoneNumberIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_PHONE_NUMBER);
        int messageListIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_MESSAGE_LIST);
        int textCounterIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_TEXT_NOTIFICATION_COUNTER);
        int callCounterIndex = cursor.getColumnIndex(tk.talcharnes.intouch.data.ContactsContract.ContactsEntry.COLUMN_CALL_NOTIFICATION_COUNTER);

        String name  = cursor.getString(nameIndex);
        String photoThumbnailUri = cursor.getString(photoThumbnailUriIndex);
        final String phoneNumber = cursor.getString(phoneNumberIndex);
        String messageList = cursor.getString(messageListIndex);
        int textCounter = cursor.getInt(textCounterIndex);
        int callCounter = cursor.getInt(callCounterIndex);

        viewHolder.contactName.setText(name);

        // Sends a random text
        viewHolder.textButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {

                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber, null, "TEXTING WORKS! ", null, null);
                            Toast.makeText(mContext, "Message Sent",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(mContext,ex.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                            ex.printStackTrace();


                            //attempt to send text another way (through the user's text messaging app) if permissions were not accepted
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("smsto:" + phoneNumber));  // This ensures only SMS apps respond
                            intent.putExtra("sms_body", "TESTING TEXT MESSAGE! IT WORKS!");
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





    }
}