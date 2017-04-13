package com.leeco.LeecoMapsWidget;

import android.app.Notification;
import android.app.IInterceptNotification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Created by dingli on 17-3-23.
 */
public class MapsWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "MapsWidgetProvider";
    private Context mContext;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        defaultAppWidget(context, appWidgetIds);
        final NotificationManager nmr = context.getSystemService(NotificationManager.class);
        nmr.registerNotificationInterceptListener(mNotificationListener);
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        receive(context,intent);
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
        views.setViewVisibility(R.id.maps_background, View.GONE);
        views.setViewVisibility(R.id.maps_linear, View.VISIBLE);
        views.setViewVisibility(R.id.maps_icon, View.VISIBLE);
    }

    private void receive(Context context, Intent intent) {
        String action = intent.getAction();
        StatusBarNotification notification = intent.getParcelableExtra("n");
        if (notification == null) {
            return;
        }
        String pkg = notification.getPackageName();
        if (pkg.equals("com.leeco.notificationTestData") || pkg.equals("com.google.android.apps.maps")) {

            Notification n = notification.getNotification();
            if (n == null) {
                return;
            }

            fillReceiveData(context, n, action);
        }

    }

    private void fillReceiveData(Context context, Notification n, String action) {
        String title = n.extras.getString(Notification.EXTRA_TITLE);
        String text = n.extras.getString(Notification.EXTRA_TEXT);
        Icon iconSmall = n.getSmallIcon();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.maps_appwidget);

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            if (title != null) {
                views.setTextViewText(R.id.maps_title, title);
                views.setTextViewText(R.id.maps_text, text);
                views.setImageViewIcon(R.id.maps_icon, iconSmall);
                appWidgetManager.updateAppWidget(new ComponentName(context, MapsWidgetProvider.class), views);
            }

        }
    }

    /**
     * Implementation of {@link android.app.IInterceptNotification} to listen notification changes from
     * NotificationManagerService.
     * sendNotificationCallback(in Notification notification, String title, String text);
     */
    private IInterceptNotification.Stub mNotificationListener = new IInterceptNotification.Stub() {
        @Override
        public void sendNotificationCallback(StatusBarNotification notification) throws RemoteException {
            if (notification == null) {
                return;
            }
            String pkg = notification.getPackageName();

            if (pkg.equals("com.leeco.notificationTestData") || pkg.equals("com.google.android.apps.maps")) {
                final Notification n = notification.getNotification();
                if (n == null) {
                    return;
                }
                fillWidgetData(n);
            }

        }
    };

    private void fillWidgetData(Notification n) {
        String title = n.extras.getString(Notification.EXTRA_TITLE);
        String text = n.extras.getString(Notification.EXTRA_TEXT);
        Icon iconSmall = n.getSmallIcon();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        final RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.maps_appwidget);
        views.setViewVisibility(R.id.maps_background, View.GONE);
        views.setViewVisibility(R.id.maps_linear, View.VISIBLE);
        views.setViewVisibility(R.id.maps_icon, View.VISIBLE);
        if (title != null) {
            views.setTextViewText(R.id.maps_title, title);
            views.setTextViewText(R.id.maps_text, text);
            views.setImageViewIcon(R.id.maps_icon, iconSmall);
            appWidgetManager.updateAppWidget(new ComponentName(mContext, MapsWidgetProvider.class), views);
        }
    }
}