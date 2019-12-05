package com.fax.cddt.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fax.cddt.manager.widget.WidgetManager;

public class BaseWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        CountdownService.startSelf(context);
        NLService.startSelf(context);
        ConfigManager.getMainConfig().putBool(MagicNumber.key.countdown_widget_is_open,true);
        DebugLog.i("test_widget_service:","start");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        DebugLog.i("test_umeng:", "CountdownService onDeleted");
        super.onDeleted(context, appWidgetIds);
        int[] appWidgetIdArray = WidgetManager.getAllProviderWidgetId(context);
        if(appWidgetIdArray == null || appWidgetIdArray.length == 0){
            //当桌面widget全部删除后,关闭服务,避免无谓消耗资源
            CountdownService.stopSelf(context);
            NLService.stopSelf(context);
            ConfigManager.getMainConfig().putBool(MagicNumber.key.countdown_widget_is_open,false);
            DebugLog.i("test_widget_service:","stop");
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
//        DebugLog.i("test_widget:", "minwidth;" + Math.round(TypedValue.applyDimension(1,newOptions.getInt(OPTION_APPWIDGET_MIN_WIDTH),context.getResources().getDisplayMetrics())));
//        DebugLog.i("test_widget:", "minHeight;" + Math.round(TypedValue.applyDimension(1,newOptions.getInt(OPTION_APPWIDGET_MIN_HEIGHT),context.getResources().getDisplayMetrics())));
//        DebugLog.i("test_widget:", "maxHeight;" + Math.round(TypedValue.applyDimension(1,newOptions.getInt(OPTION_APPWIDGET_MAX_HEIGHT),context.getResources().getDisplayMetrics())));
//        DebugLog.i("test_widget:", "maxWidth;" + Math.round(TypedValue.applyDimension(1,newOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH),context.getResources().getDisplayMetrics())));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(WidgetManager.WIDGET_CLICK_ACTION.equals(intent.getAction())) {
            //当用户点击widget时候,重启一次service,可起到服务被杀后重新拉起作用
            CountdownService.startSelf(context);
            NLService.startSelf(context);
        }
    }

}
