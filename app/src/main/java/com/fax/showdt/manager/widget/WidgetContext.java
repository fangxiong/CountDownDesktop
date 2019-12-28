package com.fax.showdt.manager.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;

import com.fax.showdt.ConstantString;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.manager.CommonConfigManager;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.utils.WidgetDataHandlerUtils;

import java.util.HashMap;


/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-22
 * Description:封装每个桌面小部件的info
 */
public class WidgetContext {
    private CustomWidgetConfig mCustomWidgetConfig;
    private HashMap<String, CustomWidgetConfig> map = new HashMap<>();

    public WidgetContext() {
    }

    /**
     * 为了避免反复从本地读json配置数据，导致消耗资源
     * 每次更新新的数据时才从本地文件读
     */
    public void changeWidgetInfo() {
        map.clear();
    }

    public HashMap<String,CustomWidgetConfig>  getCustomWidgetConfig() {
        return map;
    }


    /**
     * 获取桌面小部件的实时bitmap图
     *
     * @return
     */
    public Bitmap getViewBitmap(String widgetId) {
        mCustomWidgetConfig = null;
        if (map.containsKey(widgetId)) {
            mCustomWidgetConfig = map.get(widgetId);
        } else {
            mCustomWidgetConfig= WidgetDataHandlerUtils.getWidgetDataFromId(widgetId, ConstantString.widget_map_data_key);
        }
        if (mCustomWidgetConfig != null) {
            int width = mCustomWidgetConfig.getBaseOnWidthPx();
            int height = mCustomWidgetConfig.getBaseOnHeightPx();
            if (width <= 0 || height <= 0) {
                return null;
            }
            Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            if (bm == null) {
                return null;
            }
            Canvas canvas = new Canvas(bm);
            CustomWidgetConfigConvertHelper helper = new CustomWidgetConfigConvertHelper();
            helper.drawStaticOnBitmapFromConfig(mCustomWidgetConfig,canvas);
            helper.drawDynamicOnBitmapFromConfig(mCustomWidgetConfig, canvas);
            return bm;
        }
        return null;
    }


}
