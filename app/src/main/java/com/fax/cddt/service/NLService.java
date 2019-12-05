package com.fax.cddt.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.RemoteControlClient.OnPlaybackPositionUpdateListener;
import android.media.RemoteController;
import android.media.RemoteController.MetadataEditor;
import android.media.RemoteController.OnClientUpdateListener;
import android.media.session.MediaController;
import android.media.session.MediaController.PlaybackInfo;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;


@TargetApi(Build.VERSION_CODES.KITKAT)
public class NLService extends NotificationListenerService implements OnPlaybackPositionUpdateListener, OnClientUpdateListener {
    static final String TAG = "NLService";
    private static NLService sInstance;
    private Callback mMediaCallback;
    private MediaController mMediaController;
    private MediaSessionManager mMediaSessionManager;
    private StatusBarNotification[] mNotificationsCache = null;
    private boolean mNotificationsCacheDirty = true;
    private static HashMap<String, Notification> mNotificationsCounter = new HashMap();
    private RemoteController mRemoteController;
    private KLWPUpdateManager mKLWPUpdateManager;
    private KLWPSongUpdateManager mKLWPSongUpdateManager;
    private final String QQ_PKG = "com.tencent.mobileqq";
    public static final String NOTIFY_REFRESH_AUDIO_INFO = "notify_refresh_audio_info";
    private NotifyRefreshAudioReceiver mRefreshAudioReceiver;

    private SessionListener mSessionListener;


    public  static void startSelf(Context context){
        if(isNotificationListenerEnabled(context) && !CommonUtils.isServiceRunning(context,NLService.class.getName())){
            toggleNotificationListenerService(context);
        }
    }

    public static void stopSelf(Context context){
        context.stopService(new Intent(context,NLService.class));
    }

    //检测通知监听服务是否被授权
    public static boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;

    }

    //把应用的NotificationListenerService实现类disable再enable，即可触发系统rebind操作
    public static void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context, NLService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(
                new ComponentName(context, NLService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    @TargetApi(21)
    class Callback extends MediaController.Callback {
        private MediaController mController;

        public Callback(MediaController mediaController) {
            this.mController = mediaController;
        }

        public void onAudioInfoChanged(PlaybackInfo playbackInfo) {
            super.onAudioInfoChanged(playbackInfo);
        }

        public void onMetadataChanged(MediaMetadata mediaMetadata) {
            if (mediaMetadata != null) {
                try {
                    Log.i(TAG, "artist:" + mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
                    Log.i(TAG, "album:" + mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM));
                    Log.i(TAG, "title:" + mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE));
                    Log.i(TAG, "duration:" + mediaMetadata.getLong(MediaMetadata.METADATA_KEY_DURATION));
                    Log.i(TAG, "album artist:" + mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST));
                    Log.i(TAG, "writer:" + mediaMetadata.getString(MediaMetadata.METADATA_KEY_WRITER));
                    Log.i(TAG, "author:" + mediaMetadata.getString(MediaMetadata.METADATA_KEY_AUTHOR));
                    mKLWPSongUpdateManager.refreshMusicInfoWith21();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void onPlaybackStateChanged(@NonNull PlaybackState playbackState) {
            try {
                mKLWPSongUpdateManager.refreshMusicInfoWith21();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(21)
    class SessionListener implements OnActiveSessionsChangedListener {
        private SessionListener() {
        }

        public void onActiveSessionsChanged(List<MediaController> list) {
            if (list != null && list.size() != 0) {
                for (MediaController mediaController : list) {
                    if (mediaController != null && !TextUtils.isEmpty(PackageHelper.getBroadReceiverPackage(NLService.this, mediaController.getPackageName()))) {
                        if (!(NLService.this.mMediaController == null || NLService.this.mMediaCallback == null)) {
                            try {
                                NLService.this.mMediaController.unregisterCallback(NLService.this.mMediaCallback);
                            } catch (Exception e) {
                            }
                        }
                        NLService.this.mMediaController = mediaController;
                        NLService.this.mMediaCallback = new Callback(NLService.this.mMediaController);
                        NLService.this.mMediaController.registerCallback(NLService.this.mMediaCallback);
                        NLService.this.mMediaCallback.onMetadataChanged(NLService.this.mMediaController.getMetadata());
                        PlaybackState playbackState = NLService.this.mMediaController.getPlaybackState();
                        if (playbackState != null) {
                            NLService.this.mMediaCallback.onPlaybackStateChanged(playbackState);
                            return;
                        }
                        return;
                    }
                }
            }

        }
    }


    @Nullable
    public static MediaController getMediaController() {
        if (sInstance != null) {
            return sInstance.mMediaController;
        }
        return null;
    }

    @Nullable
    public static RemoteController getRemoteController() {
        if (sInstance != null) {
            return sInstance.mRemoteController;
        }
        return null;
    }

    @NonNull
    protected static StatusBarNotification[] getAllActiveNotifications() {
        if (sInstance != null) {
            return sInstance.getActiveNotifications();
        }
        return new StatusBarNotification[0];
    }

    public StatusBarNotification[] getActiveNotifications() {
        if (this.mNotificationsCache == null || this.mNotificationsCacheDirty) {
            try {
                this.mNotificationsCache = super.getActiveNotifications();
                this.mNotificationsCacheDirty = false;
            } catch (NullPointerException e) {
            } catch (Throwable e2) {
            }
        }
        if (this.mNotificationsCache == null) {
            this.mNotificationsCache = new StatusBarNotification[0];
        }
        return this.mNotificationsCache;
    }


    public static ArrayList<Notification> getAllNotifications(){
        Collection<Notification> map = mNotificationsCounter.values();
        ArrayList<Notification> valueList = new ArrayList<Notification>(map);
        return valueList;
    }

    public NLService() {
        sInstance = this;
    }

    public void onClientChange(boolean z) {
        DebugLog.i(TAG, "onClientChange : " + z, new Object[0]);

    }

    public int onStartCommand(Intent intent, int i, int i2) {
        DebugLog.i(TAG, "onStartCommand", new Object[0]);
        super.onStartCommand(intent, i, i2);
        return Service.START_STICKY;
    }

    public void onCreate() {
        DebugLog.i(TAG, "OnCreate", new Object[0]);
        super.onCreate();
        if (!CommonUtils.isServiceRunning(this, "com.maibaapp.module.main.service.BackgroundAdService")) {
            Intent intent = new Intent(this, BackgroundAdService.class);
            startService(intent);
        }
        mKLWPSongUpdateManager = new KLWPSongUpdateManager(this);
        start();
    }

    public void onDestroy() {
        DebugLog.i(TAG, "OnDestroy", new Object[0]);
        if (VERSION.SDK_INT < 21) {
            destroyRemoteController();
        } else {
            destroyMediaSessionManager();

        }
        if(mRefreshAudioReceiver != null){
            unregisterReceiver(mRefreshAudioReceiver);
        }
        mKLWPSongUpdateManager.disposeRx();
        super.onDestroy();
    }

    public void onClientMetadataUpdate(MetadataEditor metadataEditor) {
        String string = metadataEditor.getString(2, "");
        String string2 = metadataEditor.getString(7, "");
        String string3 = metadataEditor.getString(1, "");
        Long valueOf = Long.valueOf(metadataEditor.getLong(9, -1));
        Bitmap bitmap = metadataEditor.getBitmap(100, null);
        DebugLog.i(TAG, "Artist:%s, title: %s, album: %s, len: %d", string, string2, string3, valueOf);
        try {

        } catch (Throwable e) {
            DebugLog.i(TAG, "Failed to update", e);
        }
        metadataEditor.clear();
    }

    public void onClientPlaybackStateUpdate(int i) {
        DebugLog.i(TAG, "onClientPlaybackStateUpdate : " + i, new Object[0]);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClientPlaybackStateUpdate(int i, long j, long j2, float f) {
        DebugLog.i(TAG, "PlayBackUpdate: state %d, stateChangeTimeMs %d, currentPostMs %d, speed %f", Integer.valueOf(i), Long.valueOf(j), Long.valueOf(j2), Float.valueOf(f));
        try {
            onClientPlaybackStateUpdate(i);
            onPlaybackPositionUpdate(j2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClientTransportControlUpdate(int i) {
        DebugLog.i(TAG, "onClientTransportControlUpdate: %d", Integer.valueOf(i));
    }

    public void onPlaybackPositionUpdate(long j) {
        DebugLog.i(TAG, "onPlaybackPositionUpdate: %d", Long.valueOf(j));
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        String packageName = statusBarNotification.getPackageName();
        DebugLog.i(TAG, "NotificationPosted:", packageName);
        synchronized (TAG) {
            this.mNotificationsCounter.put(packageName, statusBarNotification.getNotification());
        }
        try {
            if (QQ_PKG.equals(packageName)) {
                Notification notification = mNotificationsCounter.get(QQ_PKG);
                DebugLog.i("test_qq_news：", "notification:" + notification.toString());
                Bundle bundle = notification.extras;
                if (bundle != null) {
                    String title = bundle.getString(Notification.EXTRA_TITLE, "");
                    String content = bundle.getString(Notification.EXTRA_TEXT, "");
                    Bitmap icon= bundle.getParcelable(Notification.EXTRA_LARGE_ICON);
                    long timeStamp = TimeUtils.currentTimeMillis();
                    AppNewsInfo newsInfo = new AppNewsInfo();
                    newsInfo.setSenderName(title);
                    newsInfo.setSenderNews(content);
                    newsInfo.setSenderIcon(icon);
                    newsInfo.setSendTime(timeStamp);
                    if(mKLWPUpdateManager == null){
                        mKLWPUpdateManager = new KLWPUpdateManager(this);
                    }
                    mKLWPUpdateManager.sendMsgUpdate(newsInfo);
                    DebugLog.i("test_qq_news：", "标题:" + title + "内容:" + content);
                    if(icon != null) {
                        DebugLog.i("test_qq_news：", "icon的大小:" + icon.getAllocationByteCount()/1024);
                    }
                }

            }
        } catch (Throwable e) {
            DebugLog.i(TAG, "onNotificationPosted", e);
        }

    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        int i = 0;
        String packageName = statusBarNotification.getPackageName();
        DebugLog.i(TAG, "NotificationRemoved: %s", packageName);
        synchronized (TAG) {
            if (this.mNotificationsCounter.containsKey(packageName)) {
                for (StatusBarNotification statusBarNotification2 : getAllActiveNotifications()) {
                    if (statusBarNotification2 != null && statusBarNotification2.getPackageName().equals(packageName)) {
                        i++;
                    }
                }
                this.mNotificationsCounter.put(packageName, statusBarNotification.getNotification());
            }
        }

    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        DebugLog.i(TAG,"NotificationListenerService连接成功");
//        ShowToast.Long("NotificationListenerService连接成功");

    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        DebugLog.i(TAG,"NotificationListenerService连接断开");
//        ShowToast.Long("NotificationListenerService连接断开");

    }

    private synchronized void start() {
        if (VERSION.SDK_INT < 21) {
            registerRemoteController();
        } else {
            addSessionListener();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(NOTIFY_REFRESH_AUDIO_INFO);
            mRefreshAudioReceiver = new NotifyRefreshAudioReceiver();
            this.registerReceiver(mRefreshAudioReceiver,intentFilter);
        }
    }


    @TargetApi(21)
    private void addSessionListener() {
//        if (NotificationHelper.a(this)) {
        ComponentName componentName = new ComponentName(this, NLService.class);
        this.mMediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
        this.mSessionListener = new SessionListener();
        this.mMediaSessionManager.addOnActiveSessionsChangedListener(this.mSessionListener, componentName);
        List<MediaController> mediaControllers = this.mMediaSessionManager.getActiveSessions(componentName);
        mSessionListener.onActiveSessionsChanged(mediaControllers);
//       }
    }

    private void registerRemoteController() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//        if (this.mRemoteController == null && NotificationHelper.a(this)) {
        if (this.mRemoteController == null) {
            RemoteController remoteController = new RemoteController(this, this);
            Point point = new Point();
            ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealSize(point);
            int max = Math.max(point.x, point.y);
            remoteController.setArtworkConfiguration(max, max);
            if (audioManager.registerRemoteController(remoteController)) {
                this.mRemoteController = remoteController;
            } else {
                DebugLog.i(TAG, "Failed to register");
            }
        }
    }

    @TargetApi(21)
    private void destroyMediaSessionManager() {
        if (this.mMediaSessionManager != null) {
            this.mMediaSessionManager.removeOnActiveSessionsChangedListener(this.mSessionListener);
        }
    }

    private void destroyRemoteController() {
        if (this.mRemoteController != null) {
            ((AudioManager) getSystemService(AUDIO_SERVICE)).unregisterRemoteController(this.mRemoteController);
            this.mRemoteController = null;
        }
    }

    class NotifyRefreshAudioReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            DebugLog.i("test_widget:","action:"+action);
            if(NOTIFY_REFRESH_AUDIO_INFO.equals(action)) {
                KLWPSongUpdateManager.isEditWidget = intent.getBooleanExtra("switch_flag",false);
                ComponentName componentName = new ComponentName(NLService.this, NLService.class);
                if(mMediaSessionManager != null) {
                    List<MediaController> mediaControllers = mMediaSessionManager.getActiveSessions(componentName);
                    mSessionListener.onActiveSessionsChanged(mediaControllers);
                }
            }
        }
    }
}