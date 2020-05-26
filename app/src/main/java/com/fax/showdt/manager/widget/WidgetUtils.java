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
import java.util.Random;

import static com.fax.showdt.utils.CommonUtils.QQ_APP_ID;


/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-18
 * Description:widget的工具类
 */
public class WidgetUtils {

    public static String ACTION_WIDGET_ICON_CLICK = "widget_icon_click";

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
            initStickerClickAction(bean, width, height, remoteViews);
        }
        for (TextPlugBean bean : mTextPlugList) {
            initStickerClickAction(bean, width, height, remoteViews);
        }
    }

    private static void initStickerClickAction(BasePlugBean bean, int width, int height, RemoteViews remoteViews) {
        if (!TextUtils.isEmpty(bean.getJumpAppPath())) {
            int paddingLeft = (int) bean.getLeft();
            int paddingTop = (int) bean.getTop();
            int paddingEnd = width - (int) bean.getRight();
            int paddingBottom = height - (int) bean.getBottom();
            RemoteViews remoteViews2 = new RemoteViews(AppContext.get().getPackageName(), R.layout.widget_touch_area);
            remoteViews2.setViewPadding(R.id.touch_padding, paddingLeft, paddingTop, paddingEnd, paddingBottom);
            PendingIntent pendingIntent = null;
            switch (bean.getJumpAppPath()) {
                case WidgetClickType.CLICK_APPLICATION: {
                    pendingIntent = getClickToApplication(bean.getJumpContent());
                    break;
                }
                case WidgetClickType.CLICK_MUSIC: {
                    pendingIntent = getClickToMusicController(bean.getJumpContent());
                    break;
                }
                case WidgetClickType.CLICK_URL: {
                    pendingIntent = getGoToWebUrl(bean.getJumpContent());
                    break;
                }
                case WidgetClickType.CLICK_QQ_CONTACT: {
                    pendingIntent = goQQContactPage(bean.getJumpContent());
                    break;
                }
            }
            if (pendingIntent != null) {
                remoteViews2.setOnClickPendingIntent(R.id.touch_area, pendingIntent);
            }
            remoteViews.addView(R.id.touch_container, remoteViews2);
        }

    }

    public static PendingIntent getClickToApplication(String path) {
        if (!TextUtils.isEmpty(path)) {
            Intent intent = new Intent(ACTION_WIDGET_ICON_CLICK);
            intent.putExtra("clickType", WidgetClickType.CLICK_APPLICATION);
            intent.putExtra("jumpContent", path);
            return PendingIntent.getBroadcast(AppContext.get(), (int) (Math.random() * 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return null;
        }
    }

    public static PendingIntent getGoToWebUrl(String path) {
        if (!TextUtils.isEmpty(path)) {
            Intent intent = new Intent(ACTION_WIDGET_ICON_CLICK);
            intent.putExtra("clickType", WidgetClickType.CLICK_URL);
            intent.putExtra("jumpContent", path);
            return PendingIntent.getBroadcast(AppContext.get(), (int) (Math.random() * 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            return null;
        }

    }

    public static PendingIntent goQQContactPage(String qqnum) {
        if (!TextUtils.isEmpty(qqnum)) {
            Intent intent = new Intent(ACTION_WIDGET_ICON_CLICK);
            intent.putExtra("clickType", WidgetClickType.CLICK_QQ_CONTACT);
            intent.putExtra("jumpContent", qqnum);
            return PendingIntent.getBroadcast(AppContext.get(), (int) (Math.random() * 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            return null;
        }

    }

    public static PendingIntent getClickToMusicController(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        Intent intent = new Intent(NLService.NOTIFY_CONTROL_MUSIC);
        Log.i("test_click:", "target:" + path);
        if (!TextUtils.isEmpty(path)) {
            switch (path) {
                case WidgetMusicActionType.PLAY_OR_PAUSE: {
                    intent.putExtra("music_control", WidgetMusicActionType.PLAY_OR_PAUSE);
                    return PendingIntent.getBroadcast(AppContext.get(), (int) (Math.random() * 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                case WidgetMusicActionType.NEXT: {
                    intent.putExtra("music_control", WidgetMusicActionType.NEXT);
                    return PendingIntent.getBroadcast(AppContext.get(), (int) (Math.random() * 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                case WidgetMusicActionType.PREVIOUS: {
                    intent.putExtra("music_control", WidgetMusicActionType.PREVIOUS);
                    return PendingIntent.getBroadcast(AppContext.get(), (int) (Math.random() * 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                case WidgetMusicActionType.VOICE_ADD: {
                    intent.putExtra("music_control", WidgetMusicActionType.VOICE_ADD);
                    return PendingIntent.getBroadcast(AppContext.get(), (int) (Math.random() * 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                case WidgetMusicActionType.VOICE_MULTI: {
                    intent.putExtra("music_control", WidgetMusicActionType.VOICE_MULTI);
                    return PendingIntent.getBroadcast(AppContext.get(), (int) (Math.random() * 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                case WidgetMusicActionType.OPEN_APP: {
                    intent.putExtra("music_control", WidgetMusicActionType.OPEN_APP);
                    return PendingIntent.getBroadcast(AppContext.get(), (int) (Math.random() * 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            }
        }
        return null;
    }


}
