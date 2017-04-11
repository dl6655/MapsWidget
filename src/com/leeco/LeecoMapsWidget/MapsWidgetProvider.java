package com.leeco.LeecoMapsWidget;

import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
/**
 * Created by root on 17-3-23.
 */
public class MapsWidgetProvider extends AppWidgetProvider  {
    private static final String TAG = "MapsWidgetProvider";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        defaultAppWidget(context, appWidgetIds);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        receive(context,intent);

    }
    private void defaultAppWidget(Context context, int[] appWidgetIds) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.maps_appwidget);
        linkButtons(context, views, false /* not playing */);
        pushUpdate(context, appWidgetIds, views);
    }
    private void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        } else {
            appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), views);
        }
    }
    private void linkButtons(Context context, RemoteViews views, boolean playerActive) {
        Intent intent;
        PendingIntent pendingIntent;
        intent = new Intent();
//        com.google.android.maps.MapsActivity   com.google.android.apps.gmm
        intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
        pendingIntent = PendingIntent.getActivity(context,
                0 /* no requestCode */, intent, 0 /* no flags */);
        views.setOnClickPendingIntent(R.id.maps_google, pendingIntent);
    }

    private void receive(Context context, Intent intent){
        String action = intent.getAction();
        String title = intent.getStringExtra(Notification.EXTRA_TITLE);
        String text = intent.getStringExtra(Notification.EXTRA_TEXT);
        String icon = intent.getStringExtra(Notification.EXTRA_SMALL_ICON);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews rvs = new RemoteViews(context.getPackageName(), R.layout.maps_appwidget);
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            if(title != null){
                rvs.setTextViewText(R.id.maps_title, title);
                rvs.setTextViewText(R.id.maps_text, text);
                rvs.setImageViewResource(R.id.maps_icon, R.drawable.ic_launcher);
                appWidgetManager.updateAppWidget(new ComponentName(context, MapsWidgetProvider.class), rvs);
            }

        }

    }
}
