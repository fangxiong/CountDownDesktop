package com.fax.showdt.manager.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.RemoteViews;

import com.fax.showdt.R;
import com.fax.showdt.activity.MainActivity;
import com.fax.showdt.provider.WidgetProvider4x1;
import com.fax.showdt.provider.WidgetProvider4x2;
import com.fax.showdt.provider.WidgetProvider4x3;
import com.fax.showdt.provider.WidgetProvider4x4;

import java.util.ArrayList;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-22
 * Description:处理widget的相关逻辑
 */
public class WidgetManager {

    private static WidgetContext mWidgetContext;
    public static WidgetManager mInstance;
    private static final Object SLOCK = new Object();
    private RemoteViews views;
    private Bitmap mBitmap;
    private boolean isPause = false;
    private Handler mHandler;
    private static int[] widget4x1;
    private static int[] widget4x2;
    private static int[] widget4x3;
    private static int[] widget4x4;
    public final static String WIDGET_CLICK_ACTION = "widget_click_action";

    private WidgetManager() {
    }

    public static WidgetManager getInstance() {
        synchronized (SLOCK) {
            if (mInstance == null) {
                mInstance = new WidgetManager();
            }
        }
        mWidgetContext = new WidgetContext();
        return mInstance;
    }

    /**
     * 设置桌面小部件 每当用户编辑好后 保存则赋值给它 保证更新小部件的信息
     */
    public void changeWidgetInfo() {
        mWidgetContext.changeWidgetInfo();
    }

    /**
     * 重启对小部件的更新
     */
    public void reStart() {
        isPause = false;
    }

    /**
     * 暂停对小部件的更新
     * 避免无用的更新消耗用户电量
     */
    public void pause() {
        isPause = true;
    }

    /**
     * 更新桌面小部件 需要用到mWidgetContext 所以该方法的调用需要在changeWidgetInfo调用之后 保证需要用到mWidgetContext不为空
     * 才能正常更新
     *
     * @param context
     */
    public void updateAppWidget(final Context context) {
        try {
            if (isPause) {
                return;
            }
            if (mWidgetContext != null) {
                if(mHandler == null){
                    mHandler = new Handler();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBitmap = mWidgetContext.getViewBitmap();
                        if (mBitmap != null) {
                            views = new RemoteViews(context.getPackageName(), R.layout.widget_content);
                            views.setImageViewBitmap(R.id.content, mBitmap);
                            Intent configIntent = new Intent();
                            configIntent.setClass(context, MainActivity.class);
                            configIntent.setAction(WIDGET_CLICK_ACTION);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
                            views.setOnClickPendingIntent(R.id.content, pendingIntent);
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                            int[] widgetIds = getAllProviderWidgetId(context);
                            for (int i = 0; i < widgetIds.length; i++) {
                                appWidgetManager.updateAppWidget(widgetIds[i], views);
                            }
                        }else {
                            views = new RemoteViews(context.getPackageName(), R.layout.widget_initial_layout);
                            Intent configIntent = new Intent();
                            configIntent.setClass(context,MainActivity.class);
                            configIntent.setAction(WIDGET_CLICK_ACTION);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
                            views.setOnClickPendingIntent(R.id.rl_body, pendingIntent);
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                            int[] widgetIds = getAllProviderWidgetId(context);
                            for (int i = 0; i < widgetIds.length; i++) {
                                appWidgetManager.updateAppWidget(widgetIds[i], views);
                            }
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int[] getAllProviderWidgetId(Context mContext) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int []widget4x1_cur;
        int []widget4x2_cur;
        int []widget4x3_cur;
        int []widget4x4_cur;
        widget4x1_cur = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, WidgetProvider4x1.class));
        widget4x2_cur = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, WidgetProvider4x2.class));
        widget4x3_cur = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, WidgetProvider4x3.class));
        widget4x4_cur = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, WidgetProvider4x4.class));
        widget4x1 = widget4x1_cur;
        widget4x2 = widget4x2_cur;
        widget4x3 = widget4x3_cur;
        widget4x4 = widget4x4_cur;

        return combine_two_intdata(combine_two_intdata(widget4x1, widget4x2), combine_two_intdata(widget4x3, widget4x4));
    }

    public static int[] combine_two_intdata(int[] a, int[] b) {
        if(a == null){
            a= new int[0];
        }
        if(b == null){
            b = new int[0];
        }

        ArrayList<Integer> alist = new ArrayList<Integer>(a.length + b.length);

        for (int j = 0; j < a.length; j++) {
            alist.add(a[j]);
        }

        for (int k = 0; k < b.length; k++) {
            alist.add(b[k]);
        }

        int c[] = new int[alist.size()];

        for (int i = 0; i < alist.size(); i++) {
            c[i] = alist.get(i);
        }
        return c;

    }
}
