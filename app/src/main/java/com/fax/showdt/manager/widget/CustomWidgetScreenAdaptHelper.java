package com.fax.showdt.manager.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import com.fax.showdt.AppContext;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.DrawablePlugBean;
import com.fax.showdt.bean.PlugLocation;
import com.fax.showdt.bean.ProgressPlugBean;
import com.fax.showdt.bean.TextPlugBean;
import com.fax.showdt.utils.BitmapUtils;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.fax.showdt.view.sticker.TextSticker;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-27
 * Description:下载了的桌面widget的json数据进行分辨率适配
 */
public class CustomWidgetScreenAdaptHelper {
    private CustomWidgetConfig mResultConfig;
    private Context mContext;
    private int mAdaptWidth, mAdaptHeight;


    public CustomWidgetScreenAdaptHelper(Context context) {
        this.mContext = context;
        mAdaptWidth = WidgetSizeConfig.getWidgetWidth();
        mAdaptHeight = WidgetSizeConfig.getWidgetWidth();
    }

    public CustomWidgetConfig adaptConfig(@NonNull CustomWidgetConfig config) {

        mResultConfig = new CustomWidgetConfig();
        List<TextPlugBean> mTextList;
        List<DrawablePlugBean> mDrawableList;
        List<ProgressPlugBean> mProgressList;
        mResultConfig.setId(config.getId());
        mResultConfig.setCoverUrl(config.getCoverUrl());
        mResultConfig.setTitle(config.getTitle());
        mResultConfig.setBaseOnWidthPx(mAdaptWidth);
        mResultConfig.setBaseOnHeightPx(mAdaptHeight);
        mResultConfig.setDefaultScale(TextSticker.DEFAULT_TEXT_SIZE);
        mResultConfig.setCreatedTime(System.currentTimeMillis());
        mResultConfig.setDesc(config.getDesc());
        mResultConfig.setBgPath(config.getBgPath());
        mResultConfig.setVersion(config.getVersion());
        mTextList = config.getTextPlugList();
        mDrawableList = config.getDrawablePlugList();
        mProgressList = config.getProgressPlugList();
        for (int i = 0; i < mTextList.size(); i++) {
            TextPlugBean bean = mTextList.get(i);
            initTextSticker(bean, config.getBaseOnWidthPx(), config.getBaseOnHeightPx());
        }
        for (int i = 0; i < mDrawableList.size(); i++) {
            DrawablePlugBean bean = mDrawableList.get(i);
            initDrawableSticker(bean, config.getBaseOnWidthPx(), config.getBaseOnHeightPx());
        }
        for (int i = 0; i < mProgressList.size(); i++) {
            ProgressPlugBean bean = mProgressList.get(i);
            initProgressSticker(bean, config.getBaseOnWidthPx(), config.getBaseOnHeightPx());
        }
        mResultConfig.setTextPlugList(mTextList);
        mResultConfig.setDrawablePlugList(mDrawableList);
        mResultConfig.setProgressPlugList(mProgressList);
        Log.e("test_adapt_config:", mResultConfig.toJSONString());
        Log.e("test_adapt_config1:", String.valueOf(mResultConfig.getBaseOnWidthPx()));
        return mResultConfig;

    }

    /**
     * 初始化文字插件
     *
     * @param bean
     * @param baseOnWidth
     * @param baseOnHeight
     */
    private void initTextSticker(TextPlugBean bean, int baseOnWidth, int baseOnHeight) {
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        Log.e("test_center_point:", bean.getId() + " x1:" + plugLocation.getX() + " y1:" + plugLocation.getY());
        plugLocation.setX(targetPoint.x);
        plugLocation.setY(targetPoint.y);
        bean.setLocation(plugLocation);
        Log.e("test_center_point:", bean.getId() + " x2:" + plugLocation.getX() + " y2:" + plugLocation.getY());
        float adaptHeightRatio = getHeightRatio(baseOnHeight);
        float adaptWidthRatio = getWidthRatio(baseOnWidth);
        float width = bean.getRight() - bean.getLeft();
        float height = bean.getBottom() - bean.getTop();
        bean.setLeft((plugLocation.getX() - (width / 2) * adaptWidthRatio));
        bean.setRight((plugLocation.getX() + (width / 2) * adaptWidthRatio));
        bean.setTop((plugLocation.getY() - (height / 2) * adaptHeightRatio));
        bean.setBottom((plugLocation.getY() + (height / 2) * adaptHeightRatio));
        bean.setText(bean.getText());
        bean.setAlignment(bean.getAlignment());
    }

    private void initDrawableSticker(DrawablePlugBean bean, int baseOnWidth, int baseOnHeight) {
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        plugLocation.setX(targetPoint.x);
        plugLocation.setY(targetPoint.y);
        bean.setLocation(plugLocation);
        float adaptRatio = getWidthRatio(baseOnWidth);
        bean.setLeft(plugLocation.getX() - bean.getWidth() * bean.getScale() / 2f);
        bean.setRight(plugLocation.getX() + bean.getWidth() * bean.getScale() / 2f);
        bean.setTop(plugLocation.getY() - bean.getHeight() * bean.getScale() / 2f);
        bean.setBottom(plugLocation.getY() + bean.getHeight() * bean.getScale() / 2f);
        if (bean.getmPicType() == DrawableSticker.SVG) {
            bean.setScale(bean.getScale() * adaptRatio);

        } else if (bean.getmPicType() == DrawableSticker.SDCARD) {
           bean.setScale(bean.getScale() * adaptRatio);


        } else if (bean.getmPicType() == DrawableSticker.ASSET) {

        } else if (bean.getmPicType() == DrawableSticker.SHAPE) {
            bean.setWidth((int)((bean.getRight()-bean.getLeft())*adaptRatio));
        }
    }

    private void initProgressSticker(ProgressPlugBean bean, int baseOnWidth, int baseOnHeight) {
        float ratio = getWidthRatio(baseOnWidth);
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        plugLocation.setX(targetPoint.x);
        plugLocation.setY(targetPoint.y);
        bean.setLocation(plugLocation);
        bean.setWidth((int) (bean.getWidth() * ratio));
        bean.setHeight((int) (bean.getHeight() * ratio));
        bean.setLeft(plugLocation.getX() - bean.getWidth() / 2f);
        bean.setRight(plugLocation.getX() + bean.getWidth() / 2f);
        bean.setTop(plugLocation.getY() - bean.getHeight() / 2f);
        bean.setBottom(plugLocation.getY() + bean.getHeight() / 2f);
    }

    private Point reSizeWidthAndHeight(float x, float y, int baseWidth, int baseHeight) {
        float widthRatio = getWidthRatio(baseWidth);
        float heightRatio = getHeightRatio(baseHeight);
        int targetX = (int) (x * widthRatio);
        int targetY = (int) (y * heightRatio);
        return new Point(targetX, targetY);
    }

    private float getWidthRatio(int baseWidth) {
        Log.e("test_widget_screen:", String.valueOf(mAdaptWidth));
        Log.e("test_widget_base:", String.valueOf(baseWidth));
        return mAdaptWidth * 1.0f / baseWidth;
    }

    private float getHeightRatio(int baseHeight) {
        return mAdaptHeight * 1.0f / baseHeight;
    }


}
