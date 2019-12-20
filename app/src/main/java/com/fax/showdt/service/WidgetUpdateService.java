package com.fax.showdt.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.fax.showdt.manager.location.LocationManager;
import com.fax.showdt.manager.widget.WidgetManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


public class WidgetUpdateService extends Service {
    private static final int ALARM_DURATION = 5 * 60 * 1000;
    private static final int DELAY_TIME = 1000;
    private Context context;
    private Handler mHandler;
    private HandlerThread handlerThread;
    private String name = "countDown";
    public final static String WIDGET_CONFIG_CHANGED = "widget_config_changed";
    private WidgetUpdateReceiver mWidgetUpdateReceiver;
    private volatile static WidgetUpdateService service;

    public static void startSelf(Context context) {
        Intent intent = new Intent(context, WidgetUpdateService.class);
        ContextCompat.startForegroundService(context, intent);
    }

    public static void stopSelf(Context context) {
        context.stopService(new Intent(context, WidgetUpdateService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        improveForeground();
        initHandler();
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void initHandler() {
        if (handlerThread == null) {
            handlerThread = new HandlerThread(name);
            handlerThread.start();
        }
        if (mHandler == null) {
            mHandler = new Handler(handlerThread.getLooper());
            mHandler.post(new mRunnable());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void improveForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.fax.showdt";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.logo_small_icon)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.umeng_push_notification_default_large_icon))
                .setContentTitle("秀桌面正在运行")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
        context = getApplicationContext();
        improveForeground();
        initAlarm();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIDGET_CONFIG_CHANGED);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        mWidgetUpdateReceiver = new WidgetUpdateReceiver();
        this.registerReceiver(mWidgetUpdateReceiver, intentFilter);
        //当服务开启后通知 通知监听器刷新歌曲信息
        Intent update_intent = new Intent();
        update_intent.setAction(NLService.NOTIFY_REFRESH_AUDIO_INFO);
        sendBroadcast(update_intent);
    }

    public static boolean isServiceRunning() {
        return service != null;
    }

    private PendingIntent getPendingIntent() {
        Intent alarmIntent = new Intent(context, WidgetUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 123456789,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service = null;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent());
        if (handlerThread != null) {
            handlerThread.quit();
            Log.i("test_widget:","handlerThread被杀");
        }
        //当服务被杀后需要关掉定位服务,避免消耗资源
        LocationManager.getInstance().stopLocation();
        unregisterReceiver(mWidgetUpdateReceiver);
    }

    private void initAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + ALARM_DURATION, getPendingIntent());
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void updateWidget() {
        try {
            WidgetManager.getInstance().updateAppWidget(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class mRunnable implements Runnable {
        @Override
        public void run() {
            updateWidget();
            mHandler.postDelayed(this, DELAY_TIME);
        }
    }

    class WidgetUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WIDGET_CONFIG_CHANGED.equals(action)) {
                WidgetManager.getInstance().changeWidgetInfo();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                //熄屏下不更新widget更加省电
                WidgetManager.getInstance().pause();
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                WidgetManager.getInstance().reStart();
            }
        }
    }


}
