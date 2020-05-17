package com.fax.showdt.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
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
import android.os.Vibrator;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.fax.showdt.AppContext;
import com.fax.showdt.manager.musicPlug.KLWPSongUpdateManager;
import com.fax.showdt.manager.musicPlug.PackageHelper;
import com.fax.showdt.manager.widget.WidgetMusicActionType;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.IntentUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


@TargetApi(Build.VERSION_CODES.KITKAT)
public class NLService extends NotificationListenerService implements OnPlaybackPositionUpdateListener, OnClientUpdateListener {
    static final String TAG = "NLService";
    public static final String NOTIFY_REFRESH_AUDIO_INFO = "notify_refresh_audio_info";
    public static final String NOTIFY_CONTROL_MUSIC = "notify_control_music";
    private static NLService sInstance;
    private Callback mMediaCallback;
    private MediaController mMediaController;
    private String curMediaPkgName;
    private MediaSessionManager mMediaSessionManager;
    private StatusBarNotification[] mNotificationsCache = null;
    private boolean mNotificationsCacheDirty = true;
    private static HashMap<String, Notification> mNotificationsCounter = new HashMap();
    private boolean mMediaSwitch = false;
    private boolean mIsSleep = false;
    private RemoteController mRemoteController;
    private KLWPSongUpdateManager mKLWPSongUpdateManager;
    private NotificationListenerReceiver mRefreshAudioReceiver;
    private SessionListener mSessionListener;


    public static void startSelf(Context context) {
        if (isNotificationListenerEnabled(context) && !CommonUtils.isServiceRunning(context, NLService.class.getName())) {
            toggleNotificationListenerService(context);
        }
    }

    public static void stopSelf(Context context) {
        context.stopService(new Intent(context, NLService.class));
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
                    if (!mIsSleep) {
                        mKLWPSongUpdateManager.refreshMusicInfoWith21();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void onPlaybackStateChanged(@NonNull PlaybackState playbackState) {
            try {
                Log.i(TAG, "播放状态：" + playbackState.getState());
                Log.i(TAG, "动作：" + playbackState.getActions());
                Log.i(TAG, "进度：" + playbackState.getPosition());
                if (!mIsSleep) {
                    mKLWPSongUpdateManager.refreshMusicInfoWith21();
                }

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
                    Log.i(NLService.TAG, "音频焦点包名：：" + mediaController.getPackageName());
                    Log.i(NLService.TAG, "当前音频焦点包名：：" + curMediaPkgName);
                    if ((mMediaSwitch && mediaController.getPackageName().equals(curMediaPkgName)) || (!mMediaSwitch && !TextUtils.isEmpty(PackageHelper.getBroadReceiverPackage(NLService.this, mediaController.getPackageName())))) {
                        try {

                            if (!(NLService.this.mMediaController == null || NLService.this.mMediaCallback == null)) {
                                NLService.this.mMediaController.unregisterCallback(NLService.this.mMediaCallback);

                            }
                            Log.i(NLService.TAG, "当前音频焦点：" + mediaController.getPackageName());
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
                        } catch (Exception e) {
                        }
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


    public static ArrayList<Notification> getAllNotifications() {
        Collection<Notification> map = mNotificationsCounter.values();
        ArrayList<Notification> valueList = new ArrayList<Notification>(map);
        return valueList;
    }

    public NLService() {
        sInstance = this;
    }

    public void onClientChange(boolean z) {

    }

    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        return Service.START_STICKY;
    }

    public void onCreate() {
        super.onCreate();
        mKLWPSongUpdateManager = new KLWPSongUpdateManager(this);
        start();
    }

    public void onDestroy() {
        if (VERSION.SDK_INT < 21) {
            destroyRemoteController();
        } else {
            destroyMediaSessionManager();

        }
        if (mRefreshAudioReceiver != null) {
            unregisterReceiver(mRefreshAudioReceiver);
        }
        mKLWPSongUpdateManager.disposeRx();
        super.onDestroy();
    }

    public void onClientMetadataUpdate(MetadataEditor metadataEditor) {
        try {

        } catch (Throwable e) {
            Log.i(TAG, "Failed to update", e);
        }
        metadataEditor.clear();
    }

    public void onClientPlaybackStateUpdate(int i) {
        Log.i(TAG, "onClientPlaybackStateUpdate : " + i);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClientPlaybackStateUpdate(int i, long j, long j2, float f) {
        try {
            onClientPlaybackStateUpdate(i);
            onPlaybackPositionUpdate(j2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClientTransportControlUpdate(int i) {
    }

    public void onPlaybackPositionUpdate(long j) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        String packageName = statusBarNotification.getPackageName();
        Log.d(TAG, "NotificationPosted:" + packageName);

        synchronized (TAG) {
            List<MediaController> mediaControllers = this.mMediaSessionManager.getActiveSessions(new ComponentName(this, NLService.class));
            for (MediaController mediaController : mediaControllers) {
                if (mediaController.getPackageName().equals(packageName)) {
                    mMediaSwitch = true;
                    if (mSessionListener != null) {
                        curMediaPkgName = packageName;
                        mSessionListener.onActiveSessionsChanged(mediaControllers);
                    }
                }
            }
            this.mNotificationsCounter.put(packageName, statusBarNotification.getNotification());
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        int i = 0;
        String packageName = statusBarNotification.getPackageName();
        Log.d(TAG, "NotificationRemoved: %s" + packageName);
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
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
    }

    private synchronized void start() {
        if (VERSION.SDK_INT < 21) {
            registerRemoteController();
        } else {
            try {
                addSessionListener();
            } catch (SecurityException e) {

            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(NOTIFY_REFRESH_AUDIO_INFO);
            intentFilter.addAction(NOTIFY_CONTROL_MUSIC);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            mRefreshAudioReceiver = new NotificationListenerReceiver();
            this.registerReceiver(mRefreshAudioReceiver, intentFilter);
        }
    }


    @TargetApi(21)
    private void addSessionListener() {
        ComponentName componentName = new ComponentName(this, NLService.class);
        this.mMediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
        this.mSessionListener = new SessionListener();
        this.mMediaSessionManager.addOnActiveSessionsChangedListener(this.mSessionListener, componentName);
        List<MediaController> mediaControllers = this.mMediaSessionManager.getActiveSessions(componentName);
        mSessionListener.onActiveSessionsChanged(mediaControllers);
    }

    private void registerRemoteController() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (this.mRemoteController == null) {
            RemoteController remoteController = new RemoteController(this, this);
            Point point = new Point();
            ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealSize(point);
            int max = Math.max(point.x, point.y);
            remoteController.setArtworkConfiguration(max, max);
            if (audioManager.registerRemoteController(remoteController)) {
                this.mRemoteController = remoteController;
            } else {
                Log.i(TAG, "Failed to register");
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

    public class NotificationListenerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("test_widget:", "action:" + action);
            if (NOTIFY_REFRESH_AUDIO_INFO.equals(action)) {
                KLWPSongUpdateManager.isEditWidget = intent.getBooleanExtra("switch_flag", false);
                ComponentName componentName = new ComponentName(NLService.this, NLService.class);
                if (mMediaSessionManager != null) {
                    List<MediaController> mediaControllers = mMediaSessionManager.getActiveSessions(componentName);
                    mSessionListener.onActiveSessionsChanged(mediaControllers);
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mIsSleep = true;
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                mIsSleep = false;
            } else if (NOTIFY_CONTROL_MUSIC.equals(action)) {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(30);
                String musicControl = intent.getStringExtra("music_control");
                Log.i("test_music_control:", musicControl);
                MediaController mediaController = getMediaController();

                if (mediaController != null) {
                    switch (musicControl) {
                        case WidgetMusicActionType.PLAY_OR_PAUSE: {
                            PlaybackState playbackState = mediaController.getPlaybackState();
                            Log.i("test_music_control:", "playbackState:" + playbackState);
                            Log.i("test_music_control:", "mediaController pkg:" + mediaController.getPackageName());

                            if (playbackState != null) {
                                if (playbackState.getState() == PlaybackState.STATE_PLAYING) {
                                    mediaController.getTransportControls().pause();
                                    Log.i("test_music_control:", "playbackState:" + "暂停");
                                } else {
                                    mediaController.getTransportControls().play();
                                    Log.i("test_music_control:", "playbackState:" + "播放");
                                }
                            }
                            break;
                        }
                        case WidgetMusicActionType.NEXT: {
                            mediaController.getTransportControls().skipToNext();
                            break;
                        }
                        case WidgetMusicActionType.PREVIOUS: {
                            mediaController.getTransportControls().skipToPrevious();
                            break;
                        }
                        case WidgetMusicActionType.VOICE_ADD: {
                            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                            break;
                        }
                        case WidgetMusicActionType.VOICE_MULTI: {
                            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                            break;
                        }
                        case WidgetMusicActionType.OPEN_APP: {
                            Intent packageIntent = IntentUtils.getAppLaunchIntent(AppContext.get(), mediaController.getPackageName());
                            if (packageIntent != null) {
                                packageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(AppContext.get(), 0, packageIntent, PendingIntent.FLAG_ONE_SHOT);
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {

                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}