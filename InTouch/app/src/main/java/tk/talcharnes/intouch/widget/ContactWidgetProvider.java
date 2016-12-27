package tk.talcharnes.intouch.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import tk.talcharnes.intouch.MainActivity;
import tk.talcharnes.intouch.R;
import tk.talcharnes.intouch.Utility;

/**
 * Implementation of App Widget functionality.
 * Credit for Udacity for help!
 */
public class ContactWidgetProvider extends AppWidgetProvider {



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.contact_widget);

            //This intent launches main activity (MyStocksActivity) when widget_bar is clicked
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_bar, pendingIntent);

            // Set up the collection
            setRemoteAdapter(context, views);

            //todo check this out more
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);





            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Utility.ACTION_DATA_UPDATED.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
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

