package com.leeco.mapswidget;

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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import org.json.JSONException;
import utils.JSONHelper;
import utils.Utilities;

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

    private void fillHandlerData(Context context, final MapsEntity mapsEntity) {
        String title = mapsEntity.getTitle_first();
        String text = mapsEntity.getTitle_second();
        String text1 = mapsEntity.getTitle_third();
        Drawable iconDraw = mapsEntity.getArrow();
        if (iconDraw == null) {
            return;
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.maps_appwidget);
        linkButtons(context, views, false /* not playing */);
        if (title != null) {
            views.setTextViewText(R.id.maps_title, title);
            views.setTextViewText(R.id.maps_text, text);
            views.setTextViewText(R.id.maps_text1, text1);
            Bitmap iconSmall = Utilities.drawableToBitmap(mContext, iconDraw);
            if (iconSmall != null) {
                views.setImageViewBitmap(R.id.maps_icon, iconSmall);
            } else {
                views.setImageViewResource(R.id.maps_icon, R.drawable.ic_launcher);
            }
        }
        appWidgetManager.updateAppWidget(new ComponentName(context, MapsWidgetProvider.class), views);
    }

    private void fillWidgetData(final Notification n) {
        RemoteViews views = n.bigContentView;
        if (views == null) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) views.apply(mContext, null);
        getRemoteView(viewGroup);
    }

    private void getRemoteView(ViewGroup viewGroup) {
        int childSize = viewGroup.getChildCount();
        MapsEntity mapsEntity = new MapsEntity();
        for (int i = 0; i < childSize; i++) {
            View view0 = viewGroup.getChildAt(i);
            if (i == 0) {
                if (!(view0 instanceof ImageView)) {
                    return;
                }
                ImageView imageView = (ImageView) view0;
                mapsEntity.setArrow(imageView.getDrawable());
            }
            if (i == 1) {
                ViewGroup viewGroup1 = (ViewGroup) view0;
                int childSize1 = viewGroup1.getChildCount();
                for (int i1 = 0; i1 < childSize1; i1++) {
                    View view1 = viewGroup1.getChildAt(i1);
                    if (i1 == 0) {
                        ViewGroup viewGroup2 = (ViewGroup) view1;
                        int childSize2 = viewGroup2.getChildCount();
                        for (int i2 = 0; i2 < childSize2; i2++) {
                            View view2 = viewGroup2.getChildAt(i2);
                            if (!(view2 instanceof TextView)) {
                                return;
                            }
                            TextView textView = (TextView) view2;
                            String str = textView.getText().toString();
                            if (str != null && str.length() > 0) {
                                switch (i2) {
                                    case 0:
                                        mapsEntity.setTitle_first(str);
                                        break;
                                    case 1:
                                        mapsEntity.setTitle_second(str);
                                        break;
                                    case 2:
                                        mapsEntity.setTitle_third(str);
                                        break;
                                }
                            }
                        }
                        if (mapsEntity.getArrow() != null) {
                            mHandler.obtainMessage(MSG_SETWIDGETDATA, mapsEntity).sendToTarget();
                        }
                    }
                    break;
                }
            }
        }
    }

    private ArrayList<ViewGroup> fillView(ViewGroup viewGroup) {
        ArrayList<ViewGroup> viewGroupsArrayList = new ArrayList<ViewGroup>();
        getView(viewGroup, viewGroupsArrayList);
        return viewGroupsArrayList;

    }

    private void parserView(ArrayList<ViewGroup> viewGroupsArrayList) {
        int arraySize = viewGroupsArrayList.size();
        for (int i = 0; i < arraySize; i++) {
            View view = viewGroupsArrayList.get(i);
        }
    }

    private void getView(ViewGroup viewGroup, ArrayList<ViewGroup> viewGroupsArrayList) {
        int childSize = viewGroup.getChildCount();
        if (childSize > 0) {
            for (int i = 0; i < childSize; i++) {
                ViewGroup viewGroup1 = (ViewGroup) (viewGroup.getChildAt(i));
                if (viewGroup1.getChildCount() > 0) {
                    getView(viewGroup1, viewGroupsArrayList);
                } else {
                    viewGroupsArrayList.add(viewGroup1);
                }
            }
        }
    }

    private static final int MSG_SETWIDGETDATA = 1, MSG_GETWIDGETDATA = 2;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SETWIDGETDATA:
                    setShared((MapsEntity) msg.obj);
                    break;
                case MSG_GETWIDGETDATA:
                    getShared();
                    break;
            }
        }
    };

    public void setShared(final MapsEntity mapsEntity) {
        String jsonMaps = JSONHelper.toJSON(mapsEntity);
        SharedPreferences sp = mContext.getSharedPreferences("googleMapNotificationData", mContext.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("jsonMaps", jsonMaps);
        editor.commit();
        fillHandlerData(mContext, mapsEntity);
    }

    private void getShared() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MapsEntity mapsEntity = new MapsEntity();
                mapsEntity.setTitle_first(mContext.getResources().getString(R.string.city_name));
                mapsEntity.setTitle_second(mContext.getResources().getString(R.string.street_name));
                mapsEntity.setArrow(mContext.getResources().getDrawable(R.drawable.ic_launcher, null));
                String jsonDefaultStrMaps = JSONHelper.toJSON(mapsEntity);
                SharedPreferences sp = mContext.getSharedPreferences("googleMapNotificationData", mContext.MODE_APPEND);
                String jsonMaps = sp.getString("jsonMaps", jsonDefaultStrMaps);
                try {
                    mapsEntity = JSONHelper.parseObject(jsonMaps, MapsEntity.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fillHandlerData(mContext, mapsEntity);
            }
        });

    }
}