package com.fax.showdt.manager.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.fax.showdt.AppContext;
import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.activity.WidgetSelectedActivity;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.provider.WidgetProvider4x4;
import com.fax.showdt.service.WidgetUpdateService;
import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.WidgetDataHandlerUtils;

import java.util.HashMap;


/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-22
 * Description:处理widget的相关逻辑
 */
public class WidgetManager {
    public final static String WIDGET_CLICK_ACTION = "widget_click_action";
    private static WidgetContext mWidgetContext;
    private static WidgetManager mInstance;
    private static final Object SLOCK = new Object();
    private RemoteViews views;
    private Bitmap mBitmap;
    private boolean isPause = false;
    private Handler mHandler;

    private WidgetManager() {
    }

    public static WidgetManager getInstance() {
        if (mInstance == null) {
            synchronized (SLOCK) {
                if (mInstance == null) {
                    mInstance = new WidgetManager();
                }
            }
        }
        if (mWidgetContext == null) {
            mWidgetContext = new WidgetContext();
        }
        return mInstance;
    }

    /**
     * 设置桌面小部件 每当用户编辑好后 保存则赋值给它 保证更新小部件的信息
     */
    public void changeWidgetInfo() {
        mWidgetContext.changeWidgetInfo();
    }

    /**
     * 刷新所有的widget
     */
    public void refreshAllWidgetIds(Context context) {
        Intent intent =new Intent();
        intent.setAction(WidgetUpdateService.WIDGET_CONFIG_CHANGED);
        context.sendBroadcast(intent);
    }

    public HashMap<String, CustomWidgetConfig> getCustomWidgetConfig() {
        return mWidgetContext.getCustomWidgetConfig();
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
    public void updateAppWidget(final Context context, final String widgetId) {
        try {
            if (isPause) {
                return;
            }
            if (mWidgetContext != null) {
                if (mHandler == null) {
                    mHandler = new Handler();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //刷新已经绑定过widget data的插件
                        mBitmap = mWidgetContext.getViewBitmap(widgetId);
                        if (mBitmap != null) {
                            Log.i("test_draw_bitmap:","已经绑定："+widgetId);
                            views = new RemoteViews(context.getPackageName(), R.layout.widget_content);
                            views.setImageViewBitmap(R.id.content, mBitmap);
                            CustomWidgetConfig config = WidgetDataHandlerUtils.getWidgetDataFromId(widgetId, ConstantString.widget_map_data_key);
                            if(config!= null) {
                                WidgetUtils.initTouchContainer(config, views);
                            }
                            Intent configIntent1 = new Intent();
                            configIntent1.setClass(context, WidgetSelectedActivity.class);
                            configIntent1.setAction(WIDGET_CLICK_ACTION);
                            configIntent1.putExtra(ConstantString.widget_id, widgetId);
                            PendingIntent pendingIntent1 = PendingIntent.getActivity(context, Integer.valueOf(widgetId), configIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
                            views.setOnClickPendingIntent(R.id.content, pendingIntent1);
                            AppWidgetManager.getInstance(context).updateAppWidget(Integer.valueOf(widgetId), views);
                        } else {
                            //刷新没有绑定过widget data的插件
                            Log.i("test_draw_bitmap:","没有绑定："+widgetId);
                            views = new RemoteViews(context.getPackageName(), R.layout.widget_initial_layout);
                            Intent configIntent2 = new Intent();
                            configIntent2.setClass(context, WidgetSelectedActivity.class);
                            configIntent2.setAction(WIDGET_CLICK_ACTION);
                            configIntent2.putExtra(ConstantString.widget_id, widgetId);
                            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, Integer.valueOf(widgetId), configIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
                            views.setOnClickPendingIntent(R.id.rl_body, pendingIntent2);
                            AppWidgetManager.getInstance(context).updateAppWidget(Integer.valueOf(widgetId), views);
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int[] getAllProviderWidgetIds(Context mContext) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        return appWidgetManager.getAppWidgetIds(new ComponentName(mContext, WidgetProvider4x4.class));
    }

    public  HashMap<String, String> getAllBindDataWidgetIds() {
        return WidgetDataHandlerUtils.getHashMapData(ConstantString.widget_map_data_key);
    }

}
