package com.fax.showdt.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.fax.lib.config.ConfigManager;
import com.fax.showdt.ConstantString;
import com.fax.showdt.manager.widget.WidgetManager;
import com.fax.showdt.service.WidgetUpdateService;
import com.fax.showdt.service.NLService;

public class BaseWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i("test_widget:","更新widget");
        Log.i("test_widget:","widget的个数："+appWidgetIds.length);
        for(int i = 0;i<appWidgetIds.length;i++){
            Log.i("test_widget:","widget的个数："+appWidgetIds[i]);
        }
        WidgetUpdateService.startSelf(context);
        NLService.startSelf(context);
        ConfigManager.getMainConfig().putBool(ConstantString.countdown_widget_is_open,true);
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.i("test_widget:","删除widget");
        int[] appWidgetIdArray = WidgetManager.getAllProviderWidgetIds(context);
        if(appWidgetIdArray == null || appWidgetIdArray.length == 0){
            //当桌面widget全部删除后,关闭服务,避免无谓消耗资源
            WidgetUpdateService.stopSelf(context);
            NLService.stopSelf(context);
            ConfigManager.getMainConfig().putBool(ConstantString.countdown_widget_is_open,false);

        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        WidgetManager.getInstance().changeWidgetInfo();
        Log.i("test_widget:","添加widget");
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
        Log.i("test_widget:",intent.getAction());
        super.onReceive(context, intent);
        if(WidgetManager.WIDGET_CLICK_ACTION.equals(intent.getAction())) {
            //当用户点击widget时候,重启一次service,可起到服务被杀后重新拉起作用
            WidgetUpdateService.startSelf(context);
            NLService.startSelf(context);
        }
    }

}
