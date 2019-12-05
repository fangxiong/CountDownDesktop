package com.fax.cddt.manager.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Size;

import com.fax.cddt.bean.CustomWidgetConfig;
import com.fax.cddt.manager.CommonConfigManager;
import com.fax.cddt.utils.GsonUtils;
import com.google.gson.Gson;


/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-22
 * Description:封装每个桌面小部件的info
 */
public class WidgetContext {
    private CustomWidgetConfig mCustomWidgetConfig;
    //    public int widgetId;
    public Size mSize;

    public WidgetContext() {
    }

    /**
     * 为了避免反复从本地读json配置数据，导致消耗资源
     * 每次更新新的数据时才从本地文件读，配置通过对象方式存储在内存中
     */
    public void changeWidgetInfo() {
        mCustomWidgetConfig = CommonConfigManager.getInstance().getWidgetConfig();
    }

    /**
     * 获取桌面小部件的实时bitmap图
     *
     * @return
     */
    public Bitmap getViewBitmap() {
        if (mCustomWidgetConfig == null) {
            mCustomWidgetConfig = CommonConfigManager.getInstance().getWidgetConfig();
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
            helper.drawOnBitmapFromConfig(mCustomWidgetConfig, canvas);
            return bm;
        }
        return null;
    }


}
