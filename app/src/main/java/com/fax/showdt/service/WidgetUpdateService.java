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

import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.manager.location.LocationManager;
import com.fax.showdt.manager.widget.WidgetManager;
import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.GsonUtils;

import androidx.annotation.IntDef;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class WidgetUpdateService extends Service {
    private static final int ALARM_DURATION = 5 * 60 * 1000;
    private static final int DELAY_TIME = 1000;
    public static final int REFRESH_WITH_ONE_SECOND = 0;//每1秒更新一次
    public static final int REFRESH_WITH_SIXTY_SECOND = 1;//每一分钟更新一次
    public static final int REFRESH_WITH_JUST_ONCE = 2;//只更新一次
    private boolean isAllowRefreshAllWidget = true;
    private List<String> widgetIdWithOneSec = new ArrayList<>();
    private @RefreshGap
    int mCurrentRefreshGap = REFRESH_WITH_ONE_SECOND;
    private Context context;
    private Handler mHandler;
    private HandlerThread handlerThread;
    private String name = "countDown";
    public final static String WIDGET_CONFIG_CHANGED = "widget_config_changed";
    private WidgetUpdateReceiver mWidgetUpdateReceiver;
    private volatile static WidgetUpdateService service;

    @IntDef({REFRESH_WITH_ONE_SECOND, REFRESH_WITH_SIXTY_SECOND, REFRESH_WITH_JUST_ONCE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RefreshGap {
    }

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
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
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
            Log.i("test_widget:", "handlerThread被杀");
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
    private void updateWidget(String widgetId) {
        try {

            WidgetManager.getInstance().updateAppWidget(this, widgetId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class mRunnable implements Runnable {
        @Override
        public void run() {
            if(isAllowRefreshAllWidget) {
                int allWidgetId[] = WidgetManager.getAllProviderWidgetIds(WidgetUpdateService.this);
                for(int i =0;i<allWidgetId.length;i++) {
                    Log.i("test_draw_bitmap:", "秒刷新所有插件:"+allWidgetId[i]);
                    updateWidget(String.valueOf(allWidgetId[i]));
                }
            }else {
                for(int i =0;i<widgetIdWithOneSec.size();i++) {
                    Log.i("test_draw_bitmap:", "秒刷新匹配的插件:"+widgetIdWithOneSec.get(i));
                    updateWidget(String.valueOf(widgetIdWithOneSec.get(i)));
                }
            }
            mHandler.postDelayed(this, DELAY_TIME);
        }
    }

    private List<String> getWidgetIdsRefreshWithOneSec(){
        List<String> result =new ArrayList<>();
        HashMap<String, String> map = WidgetManager.getInstance().getAllBindDataWidgetIds();
        Iterator map1it = map.entrySet().iterator();
        Log.i("test_draw_bitmap:", "秒刷新map:" + map.size());
        while (map1it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) map1it.next();
            CustomWidgetConfig config = GsonUtils.parseJsonWithGson(entry.getValue(), CustomWidgetConfig.class);
            if(isAllowRefreshAllWidget){
                result.add(entry.getKey());
            }else {
                mCurrentRefreshGap = CustomPlugUtil.getWidgetRefreshGap(config);
                if (mCurrentRefreshGap == REFRESH_WITH_ONE_SECOND) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

    private void updateWidgetRefreshWithSixtySec(){
        HashMap<String, CustomWidgetConfig> map = WidgetManager.getInstance().getCustomWidgetConfig();
        Iterator map1it = map.entrySet().iterator();
        while (map1it.hasNext()) {
            Map.Entry<String, CustomWidgetConfig> entry = (Map.Entry<String, CustomWidgetConfig>) map1it.next();
            mCurrentRefreshGap = CustomPlugUtil.getWidgetRefreshGap(entry.getValue());
            if (mCurrentRefreshGap == REFRESH_WITH_SIXTY_SECOND) {
                Log.i("test_draw_bitmap:", "分钟刷新");
                updateWidget(entry.getKey());
            }
        }
    }

    class WidgetUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WIDGET_CONFIG_CHANGED.equals(action)) {
                Log.i("test_draw_bitmap:", "配置更新");
                WidgetManager.getInstance().changeWidgetInfo();
                isAllowRefreshAllWidget = true;
                //当更换配置时,调用每秒更新的方式,避免之前更新间隔时间大,导致更新不及时问题
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                //熄屏下不更新widget更加省电
                WidgetManager.getInstance().pause();
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                WidgetManager.getInstance().reStart();
                updateWidgetRefreshWithSixtySec();
            } else if (Intent.ACTION_TIME_TICK.equals(action)) {
                Log.i("test_draw_bitmap:", "接收到分钟刷新广播");
                isAllowRefreshAllWidget = false;
                widgetIdWithOneSec = getWidgetIdsRefreshWithOneSec();
                updateWidgetRefreshWithSixtySec();
            }
        }
    }


}
