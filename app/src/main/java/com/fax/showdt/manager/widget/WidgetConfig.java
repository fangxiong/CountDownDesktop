package com.fax.showdt.manager.widget;

import android.util.DisplayMetrics;

import com.fax.showdt.AppContext;


/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-23
 * Description:
 */
public class WidgetConfig {

    public static float WIDGET_MAX_WIDTH_RATIO = 0.89F;
    public static float WIDGET_4X1_HEIGHT_RATIO = 0.2225F;
    public static float WIDGET_4X2_HEIGHT_RATIO = 0.445F;
    public static float WIDGET_4X3_HEIGHT_RATIO = 0.6675F;
    public static float WIDGET_4X4_HEIGHT_RATIO = 0.89F;

    /**
     * 获取桌面小部件的最大宽
     *
     * @return
     */
    public static int getWidgetWidth() {
        DisplayMetrics displayMetrics = AppContext.get().getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels * WIDGET_MAX_WIDTH_RATIO);
    }

    /**
     * 获取桌面小部件的1/4高度
     *
     * @return
     */
    public static int getWidget4X1Height() {
        DisplayMetrics displayMetrics = AppContext.get().getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels * WIDGET_4X1_HEIGHT_RATIO);
    }

    /**
     * 获取桌面小部件的2/4高度
     *
     * @return
     */
    public static int getWidget4X2Height() {
        DisplayMetrics displayMetrics = AppContext.get().getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels * WIDGET_4X2_HEIGHT_RATIO);
    }

    /**
     * 获取桌面小部件的3/4高度
     *
     * @return
     */
    public static int getWidget4X3Height() {
        DisplayMetrics displayMetrics = AppContext.get().getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels * WIDGET_4X3_HEIGHT_RATIO);
    }

    /**
     * 获取桌面小部件的4/4高度
     *
     * @return
     */
    public static int getWidget4X4Height() {
        DisplayMetrics displayMetrics = AppContext.get().getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels * WIDGET_4X4_HEIGHT_RATIO);
    }

}
