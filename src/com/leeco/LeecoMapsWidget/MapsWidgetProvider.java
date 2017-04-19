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
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dingli on 17-3-23.
 */
public class MapsWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "MapsWidgetProvider";
    private static Context mContext;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        getShared();
        final NotificationManager nmr = context.getSystemService(NotificationManager.class);
        nmr.registerNotificationInterceptListener(mNotificationListener);
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        receive(context,intent);
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

    private void fillHandlerData(Context context, String title0, String title1) {
        String title = title0;
        String text = title1;
//        Icon iconSmall = n.getSmallIcon();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.maps_appwidget);
        linkButtons(context, views, false /* not playing */);
        if (title != null) {
            views.setTextViewText(R.id.maps_title, title);
            views.setTextViewText(R.id.maps_text, text);

        }
        appWidgetManager.updateAppWidget(new ComponentName(context, MapsWidgetProvider.class), views);
    }

    private void fillWidgetData(Notification n) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.maps_appwidget);
        views = n.contentView;

        ViewGroup viewGroup = (ViewGroup) views.apply(mContext, null);
        ArrayList<String> strArrayList = new ArrayList<String>();

        int childSize = viewGroup.getChildCount();
        for (int i = 0; i < childSize; i++) {
            View view1 = viewGroup.getChildAt(i);
            if (i == 0) {
                ImageView imageView = (ImageView) view1;
            }
            if (i == 1) {
                ViewGroup viewGroup1 = (ViewGroup) view1;
                int childSize1 = viewGroup1.getChildCount();
                if (childSize1 > 0) {
                    for (int i1 = 0; i1 < childSize1; i1++) {
                        ViewGroup viewGroup2 = (ViewGroup) viewGroup1.getChildAt(i1);
                        int childSize2 = viewGroup2.getChildCount();
                        if (childSize2 > 0) {
                            for (int i2 = 0; i2 < childSize2; i2++) {
                                View view3 = viewGroup2.getChildAt(i2);
                                TextView textView = (TextView) view3;
                                String str = textView.getText().toString();
                                strArrayList.add(str);
                            }
                            mHandler.obtainMessage(MSG_SETWIDGETDATA, strArrayList).sendToTarget();
                        }
                    }
                }

            }
        }
    }

    private ArrayList<ViewGroup> viewArrayList = new ArrayList<ViewGroup>();

    private ArrayList<ViewGroup> getView(ViewGroup viewGroup) {
        int childSize = viewGroup.getChildCount();
        if (childSize > 0) {
            for (int i = 0; i < childSize; i++) {
                ViewGroup viewGroup1 = (ViewGroup) (viewGroup.getChildAt(i));
                if (viewGroup1.getChildCount() > 0) {
                    getView(viewGroup1);
                } else {
                    viewArrayList = new ArrayList<ViewGroup>();
                    viewArrayList.add(viewGroup1);
                }
            }
        }
        return viewArrayList;
    }

    private static final int MSG_SETWIDGETDATA = 1, MSG_GETWIDGETDATA = 2;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SETWIDGETDATA:
                    setShared((ArrayList<String>) msg.obj);
                    break;
                case MSG_GETWIDGETDATA:
                    getShared();
                    break;
            }
        }
    };

    public void setShared(final ArrayList<String> strs) {
        String title0 = "";
        String title1 = "";
        SharedPreferences sp = mContext.getSharedPreferences("googleMapNotificationData", mContext.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < strs.size(); i++) {
            String str = strs.get(i);
            if (str != null && str.length() > 0) {
                editor.putString("title" + i, str);
                editor.commit();
                if (i == 0) {
                    title0 = str;
                }
                if (i == 1) {
                    title1 = str;
                }
            }
        }
        fillHandlerData(mContext, title0, title1);
    }

    private void getShared() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = mContext.getSharedPreferences("googleMapNotificationData", mContext.MODE_APPEND);
                String title0 = sp.getString("title0", mContext.getResources().getString(R.string.city_name));
                String title1 = sp.getString("title1", mContext.getResources().getString(R.string.street_name));
                fillHandlerData(mContext, title0, title1);
            }
        });

    }
}