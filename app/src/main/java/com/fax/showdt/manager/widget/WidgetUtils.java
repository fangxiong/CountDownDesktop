package com.fax.showdt.manager.widget;

import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;


import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.DrawablePlugBean;
import com.fax.showdt.bean.PlugLocation;
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
     * @param config
     * @param remoteViews
     */
    public static void initTouchContainer(CustomWidgetConfig config, RemoteViews remoteViews) {
        int width = config.getBaseOnWidthPx();
        int height = config.getBaseOnHeightPx();
        List<DrawablePlugBean> mDrawablePlugList = config.getDrawablePlugList();
        for (DrawablePlugBean bean : mDrawablePlugList) {
            if (!TextUtils.isEmpty(bean.getJumpAppPath())) {
                int childWidth = (int)(bean.getWidth()*bean.getScale());
                int childHeight = (int)(bean.getHeight()*bean.getScale());
                PlugLocation plugLocation = bean.getLocation();
                float centerX = plugLocation.getX();
                float centerY = plugLocation.getY();
                int paddingLeft = (int) (centerX - childWidth / 2.0f);
                int paddingTop = (int) (centerY - childHeight / 2.0f);
                int paddingEnd = (width - paddingLeft - childWidth);
                int paddingBottom = (height - childHeight-paddingTop);
                RemoteViews remoteViews2 = new RemoteViews(AppContext.get().getPackageName(), R.layout.widget_touch_area);
                remoteViews2.setViewPadding(R.id.touch_padding, paddingLeft, paddingTop, paddingEnd, paddingBottom);
                Intent packageIntent = IntentUtils.getAppLaunchIntent(AppContext.get(), bean.getJumpAppPath());
                if (packageIntent != null) {
                    packageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(AppContext.get(), 0, packageIntent, 0);
                    remoteViews2.setOnClickPendingIntent(R.id.touch_area, pendingIntent);

                }
                remoteViews.addView(R.id.touch_container, remoteViews2);
            }
        }
    }



}
