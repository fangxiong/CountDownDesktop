package com.fax.showdt.manager.widget;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.bean.BasePlugBean;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.DrawablePlugBean;
import com.fax.showdt.bean.PlugLocation;
import com.fax.showdt.bean.TextPlugBean;
import com.fax.showdt.service.NLService;
import com.fax.showdt.utils.IntentUtils;

import java.util.List;


/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-18
 * Description:widget的工具类
 */
public class WidgetUtils {

    /**
     * 根据widget的json配置来为其生成可点击的区域
     *
     * @param config
     * @param remoteViews
     */
    public static void initTouchContainer(CustomWidgetConfig config, RemoteViews remoteViews) {
        int width = config.getBaseOnWidthPx();
        int height = config.getBaseOnHeightPx();
        List<DrawablePlugBean> mDrawablePlugList = config.getDrawablePlugList();
        List<TextPlugBean> mTextPlugList = config.getTextPlugList();
        for (DrawablePlugBean bean : mDrawablePlugList) {
            initStickerClickAction(bean,width,height,remoteViews);
        }
        for (TextPlugBean bean : mTextPlugList) {
            initStickerClickAction(bean,width,height,remoteViews);
        }
    }

    private static void initStickerClickAction(BasePlugBean bean,int width,int height,RemoteViews remoteViews){
        if (!TextUtils.isEmpty(bean.getJumpAppPath())) {
            int childWidth = (int) (bean.getWidth() * bean.getScale());
            int childHeight = (int) (bean.getHeight() * bean.getScale());
            PlugLocation plugLocation = bean.getLocation();
            float centerX = plugLocation.getX();
            float centerY = plugLocation.getY();
            int paddingLeft = (int) (centerX - childWidth / 2.0f);
            int paddingTop = (int) (centerY - childHeight / 2.0f);
            int paddingEnd = (width - paddingLeft - childWidth);
            int paddingBottom = (height - childHeight - paddingTop);
            RemoteViews remoteViews2 = new RemoteViews(AppContext.get().getPackageName(), R.layout.widget_touch_area);
            remoteViews2.setViewPadding(R.id.touch_padding, paddingLeft, paddingTop, paddingEnd, paddingBottom);
            Log.i("test_padding:",paddingLeft+" "+paddingEnd+" "+paddingTop+" "+paddingBottom);
            Log.i("test_padding:",bean.getJumpAppPath());
//            Log.i("test_padding:",bean.getJumpContent());
            PendingIntent pendingIntent = null;
            switch (bean.getJumpAppPath()) {
                case WidgetClickType.CLICK_APPLICATION: {
                    pendingIntent = getClickToApplication(bean.getJumpContent());
                    break;
                }
                case WidgetClickType.CLICK_MUSIC: {
                    pendingIntent = getClickToMusicController(bean.getJumpContent());
                    Log.i("test_click:","target:"+pendingIntent.toString());
                    break;
                }
                case WidgetClickType.CLICK_URL: {
                    pendingIntent = getGoToWebUrl(bean.getJumpContent());
                    break;
                }
            }
            if (pendingIntent != null) {
                remoteViews2.setOnClickPendingIntent(R.id.touch_area, pendingIntent);
                remoteViews.addView(R.id.touch_container, remoteViews2);
            }
        }

    }

    public static PendingIntent getClickToApplication(String path) {
        Intent packageIntent = IntentUtils.getAppLaunchIntent(AppContext.get(), path);
        if (packageIntent != null) {
            packageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i("test_click:","target:"+path);
            return PendingIntent.getActivity(AppContext.get(), 7, packageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return null;
    }

    public static PendingIntent getGoToWebUrl(String path) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(path);
        intent.setData(content_url);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Log.i("test_click:","target:"+path);
        return PendingIntent.getActivity(AppContext.get(), 6, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public static PendingIntent getClickToMusicController(String path) {
        Intent intent = new Intent(NLService.NOTIFY_CONTROL_MUSIC);
        Log.i("test_click:","target:"+path);
        switch (path) {
            case WidgetMusicActionType.PLAY_OR_PAUSE: {
                intent.putExtra("music_control", WidgetMusicActionType.PLAY_OR_PAUSE);
                return PendingIntent.getBroadcast(AppContext.get(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            case WidgetMusicActionType.NEXT: {
                intent.putExtra("music_control", WidgetMusicActionType.NEXT);
                return PendingIntent.getBroadcast(AppContext.get(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            case WidgetMusicActionType.PREVIOUS: {
                intent.putExtra("music_control", WidgetMusicActionType.PREVIOUS);
                return PendingIntent.getBroadcast(AppContext.get(), 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            case WidgetMusicActionType.VOICE_ADD: {
                intent.putExtra("music_control", WidgetMusicActionType.VOICE_ADD);
                return PendingIntent.getBroadcast(AppContext.get(), 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            case WidgetMusicActionType.VOICE_MULTI: {
                intent.putExtra("music_control", WidgetMusicActionType.VOICE_MULTI);
                return PendingIntent.getBroadcast(AppContext.get(), 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            case WidgetMusicActionType.OPEN_APP: {
                intent.putExtra("music_control", WidgetMusicActionType.OPEN_APP);
                return PendingIntent.getBroadcast(AppContext.get(), 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }
        return null;
    }


}
