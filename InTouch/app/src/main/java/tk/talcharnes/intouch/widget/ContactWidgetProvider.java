package tk.talcharnes.intouch.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

import tk.talcharnes.intouch.MainActivity;
import tk.talcharnes.intouch.R;
import tk.talcharnes.intouch.Utility;

/**
 * Implementation of App Widget functionality.
 * Credit for Udacity for help!
 */
public class ContactWidgetProvider extends AppWidgetProvider {
    private static final String CallOnClick = "callOnClick";
    private static final String TextOnClick = "textOnClick";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);

            //This intent launches main activity (MyStocksActivity) when widget_bar is clicked
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_bar, pendingIntent);

            // Set up the collection
            setRemoteAdapter(context, views);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            Intent pIntent = new Intent(context, ContactWidgetProvider.class);
            PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(context, 0,
                    pIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, pendingIntentTemplate);


            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Utility.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }

        if (CallOnClick.equals(intent.getAction())) {
            String number = intent.getStringExtra("number");


//                        Get preferences to see if User wants to use standard text message app or alternative app
            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean altCallApp = getPrefs.getBoolean("checkbox_call_preference", false);
            try {
                if (!altCallApp) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + number));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(callIntent);
                    }
                } else {
                    Utility.callImmediately(context, number);
                }
            } catch (Exception e) {

            }
        }

        if (TextOnClick.equals(intent.getAction())) {

            String number = intent.getStringExtra("number");
            String messageList = intent.getStringExtra("message_list");
            //Turn string of all messages into an ArrayList in order to get one specific message at random
            ArrayList<String> messagesArrayList = null;
            try {
                messagesArrayList = Utility.getArrayListFromJSONString(messageList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Random rand = new Random();

            int n = rand.nextInt(messagesArrayList.size());


            String message = messagesArrayList.get(n);


//      Get preferences to see if User wants to use standard text message app or alternative app
            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean altTXTApp = getPrefs.getBoolean("checkbox_text_preference", false);

            //send text message


//          If user doesn't want to use the standard text message application
            if (!altTXTApp) {
                Intent textIntent = new Intent(Intent.ACTION_SENDTO);
                textIntent.setData(Uri.parse("smsto:" + number));  // This ensures only SMS apps respond
                textIntent.putExtra("sms_body", message);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    textIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(textIntent);
                }
            } else {
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, message);
                textIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(textIntent);
            }
        }
    }


    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetRemoteViewsService.class));
    }

}

