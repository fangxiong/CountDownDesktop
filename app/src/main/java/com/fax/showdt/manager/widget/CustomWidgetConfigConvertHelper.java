package com.fax.showdt.manager.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.Log;

import androidx.collection.LongSparseArray;

import com.fax.showdt.AppContext;
import com.fax.showdt.bean.BasePlugBean;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.DrawablePlugBean;
import com.fax.showdt.bean.PlugLocation;
import com.fax.showdt.bean.ProgressPlugBean;
import com.fax.showdt.bean.TextPlugBean;
import com.fax.showdt.utils.BitmapUtils;
import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.fax.showdt.view.sticker.ProgressSticker;
import com.fax.showdt.view.sticker.Sticker;
import com.fax.showdt.view.sticker.StickerView;
import com.fax.showdt.view.sticker.TextSticker;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-22
 * Description:处理widget配置负责初始化widget编辑页和绘制bitmap
 */
public class CustomWidgetConfigConvertHelper {

    /**
     * 保存widget配置
     *
     * @param originConfig
     * @param mStickerList
     * @return
     */
    public CustomWidgetConfig saveConfig(CustomWidgetConfig originConfig, LongSparseArray<Sticker> mStickerList) {
        Log.i("test_widget_config1:", originConfig.toJSONString());
        CustomWidgetConfig newConfig = originConfig;
        List<TextPlugBean> mTextList = new ArrayList<>();
        List<ProgressPlugBean> mProgressList = new ArrayList<>();
        List<DrawablePlugBean> mDrawableList = new ArrayList<>();
        for (int i = 0; i < mStickerList.size(); i++) {
            long key = mStickerList.keyAt(i);
            Sticker sticker = mStickerList.get(key);
            if (sticker instanceof TextSticker) {
                TextPlugBean textPlugBean = new TextPlugBean();
                textPlugBean.setId(String.valueOf(key));
                textPlugBean.setText(((TextSticker) sticker).getText());
                textPlugBean.setColor(((TextSticker) sticker).getTextColor());
                textPlugBean.setHeight(sticker.getHeight());
                textPlugBean.setWidth(sticker.getWidth());
                textPlugBean.setJumpAppPath(sticker.getJumpAppPath());
                textPlugBean.setScale(((TextSticker) sticker).getScaleParam());
                textPlugBean.setJumpContent(sticker.getJumpContent());
                textPlugBean.setAppName(sticker.getAppName());
                textPlugBean.setFontPath(((TextSticker) sticker).getFontPath());
                textPlugBean.setAngle(sticker.getCurrentAngle());
                RectF rectF = sticker.getMappedRectF();
                textPlugBean.setLeft(rectF.left);
                textPlugBean.setRight(rectF.right);
                textPlugBean.setTop(rectF.top);
                textPlugBean.setBottom(rectF.bottom);
                textPlugBean.setShimmerColor(((TextSticker) sticker).getShimmerColor());
                textPlugBean.setShimmerText(((TextSticker) sticker).isShimmerText());
                textPlugBean.setAlignment(((TextSticker) sticker).getAlignment());
                PointF point = sticker.getMappedCenterPoint();
                textPlugBean.setLocation(new PlugLocation(point.x, point.y));
                Log.i("test_text_sticker:", textPlugBean.toJSONString());
                mTextList.add(textPlugBean);
            } else if (sticker instanceof ProgressSticker) {
                ProgressPlugBean progressPlugBean = new ProgressPlugBean();
                progressPlugBean.setId(String.valueOf(key));
                progressPlugBean.setWidth(sticker.getWidth());
                progressPlugBean.setHeight(sticker.getHeight());
                progressPlugBean.setBgColor(((ProgressSticker) sticker).getBgColor());
                progressPlugBean.setForeColor(((ProgressSticker) sticker).getForeColor());
                progressPlugBean.setDrawType(((ProgressSticker) sticker).getDrawType());
                progressPlugBean.setProgressType(((ProgressSticker) sticker).getProgressType());
                progressPlugBean.setProgress(((ProgressSticker) sticker).getProgress());
                progressPlugBean.setPercent(((ProgressSticker) sticker).getPercent());
                progressPlugBean.setProgressHeight(((ProgressSticker) sticker).getProgressHeight());
                progressPlugBean.setAngle(sticker.getCurrentAngle());
                progressPlugBean.setScale(sticker.getCurrentScale());
                RectF rectF = sticker.getMappedRectF();
                progressPlugBean.setLeft(rectF.left);
                progressPlugBean.setRight(rectF.right);
                progressPlugBean.setTop(rectF.top);
                progressPlugBean.setBottom(rectF.bottom);
                PointF point = sticker.getMappedCenterPoint();
                progressPlugBean.setLocation(new PlugLocation(point.x, point.y));
                mProgressList.add(progressPlugBean);
            } else if (sticker instanceof DrawableSticker) {
                String path = ((DrawableSticker) sticker).getDrawablePath();
                String appName = sticker.getAppName();
                DrawablePlugBean drawablePlugBean = new DrawablePlugBean();
                drawablePlugBean.setId(String.valueOf(key));
                drawablePlugBean.setDrawablePath(path);
                drawablePlugBean.setAngle(sticker.getCurrentAngle());
                RectF rectF = sticker.getMappedRectF();
                drawablePlugBean.setLeft(rectF.left);
                drawablePlugBean.setRight(rectF.right);
                drawablePlugBean.setTop(rectF.top);
                drawablePlugBean.setBottom(rectF.bottom);
                drawablePlugBean.setScale(sticker.getCurrentScale());
                drawablePlugBean.setWidth(sticker.getWidth());
                drawablePlugBean.setHeight(sticker.getHeight());
                drawablePlugBean.setJumpAppPath(sticker.getJumpAppPath());
                drawablePlugBean.setJumpContent(sticker.getJumpContent());
                drawablePlugBean.setAppName(appName);
                drawablePlugBean.setClipType(((DrawableSticker) sticker).getClipType());
                drawablePlugBean.setStrokeColor(((DrawableSticker) sticker).getStrokeColor());
                drawablePlugBean.setSvgColor(((DrawableSticker) sticker).getSvgColor());
                drawablePlugBean.setShowFrame(((DrawableSticker) sticker).isShowFrame());
                drawablePlugBean.setmPicType(((DrawableSticker) sticker).getmPicType());

                PointF point = sticker.getMappedCenterPoint();
                drawablePlugBean.setLocation(new PlugLocation(point.x, point.y));
                mDrawableList.add(drawablePlugBean);
            }

        }
        newConfig.setTextSize(ViewUtils.dpToPx(TextSticker.DEFAULT_TEXT_SIZE,AppContext.get()));
        newConfig.setDefaultScale(TextSticker.DEFAULT_TEXT_SIZE);
        newConfig.setProgressPlugList(mProgressList);
        newConfig.setTextPlugList(mTextList);
        newConfig.setDrawablePlugList(mDrawableList);
        newConfig.setCreatedTime(System.currentTimeMillis());
        Log.i("test_widget_config2:", originConfig.toJSONString());
        return newConfig;
    }

    /**
     * 初始化widget配置到widget编辑页
     *
     * @param view
     * @param mThemeConfig
     * @param mStickerList
     */
    public void initAllStickerPlugs(StickerView view, CustomWidgetConfig mThemeConfig, LongSparseArray<Sticker> mStickerList) {
        List<TextPlugBean> mTextList = mThemeConfig.getTextPlugList();
        List<ProgressPlugBean> mProgressList = mThemeConfig.getProgressPlugList();
        List<DrawablePlugBean> mDrawableList = mThemeConfig.getDrawablePlugList();
        HashMap<Long, BasePlugBean> mStickerBeanList = new HashMap<>();
        mStickerList.clear();
        for (int i = 0; i < mTextList.size(); i++) {
            TextPlugBean bean = mTextList.get(i);
            long key = Long.valueOf(bean.getId());
            mStickerBeanList.put(key, bean);
        }

        for (int i = 0; i < mProgressList.size(); i++) {
            ProgressPlugBean bean = mProgressList.get(i);
            long key = Long.valueOf(bean.getId());
            mStickerBeanList.put(key, bean);
        }
        for (int i = 0; i < mDrawableList.size(); i++) {
            DrawablePlugBean bean = mDrawableList.get(i);
            long key = Long.valueOf(bean.getId());
            mStickerBeanList.put(key, bean);
        }
        Object[] key = mStickerBeanList.keySet().toArray();
        Arrays.sort(key);
        for (int i = 0; i < mStickerBeanList.size(); i++) {
            BasePlugBean bean = mStickerBeanList.get(key[i]);
            Sticker sticker = null;
            if (bean instanceof TextPlugBean) {
                sticker = initTextSticker(view, (TextPlugBean) bean, mThemeConfig.getBaseOnWidthPx(), mThemeConfig.getBaseOnHeightPx());
            }  else if (bean instanceof ProgressPlugBean) {
                sticker = initProgressSticker(view, (ProgressPlugBean) bean, mThemeConfig.getBaseOnWidthPx(), mThemeConfig.getBaseOnHeightPx());
            } else if (bean instanceof DrawablePlugBean) {
                sticker = initDrawableSticker(view, (DrawablePlugBean) bean, mThemeConfig.getBaseOnWidthPx(), mThemeConfig.getBaseOnHeightPx());
            }
            mStickerList.put(Long.valueOf(bean.getId()), sticker);
        }
        mStickerBeanList.clear();

    }

    /**
     * 通过配置绘制在canvas上 并生成bitmap来显示在小部件上
     *
     * @param mCustomWidgetConfig
     * @param canvas
     */
    public void drawDynamicOnBitmapFromConfig(CustomWidgetConfig mCustomWidgetConfig, Canvas canvas) {
        List<TextPlugBean> mTextList = mCustomWidgetConfig.getTextPlugList();
        List<ProgressPlugBean> mProgressList = mCustomWidgetConfig.getProgressPlugList();
        HashMap<Long, BasePlugBean> mStickerBeanList = new HashMap<>();
        for (int i = 0; i < mTextList.size(); i++) {
            TextPlugBean bean = mTextList.get(i);
            long key = Long.valueOf(bean.getId());
            mStickerBeanList.put(key, bean);
        }
        for (int i = 0; i < mProgressList.size(); i++) {
            ProgressPlugBean bean = mProgressList.get(i);
            long key = Long.valueOf(bean.getId());
            mStickerBeanList.put(key, bean);
        }

        Object[] key = mStickerBeanList.keySet().toArray();
        Arrays.sort(key);
        for (int i = 0; i < mStickerBeanList.size(); i++) {
            BasePlugBean bean = mStickerBeanList.get(key[i]);
            if (bean instanceof TextPlugBean) {
                drawTextSticker(canvas, (TextPlugBean) bean, mCustomWidgetConfig.getBaseOnWidthPx(), mCustomWidgetConfig.getBaseOnHeightPx());
            } else if (bean instanceof ProgressPlugBean) {
                drawProgressSticker(canvas, (ProgressPlugBean) bean, mCustomWidgetConfig.getBaseOnWidthPx(), mCustomWidgetConfig.getBaseOnHeightPx());
            }
        }
    }

    public void drawStaticOnBitmapFromConfig(CustomWidgetConfig mCustomWidgetConfig, Canvas canvas) {
        List<DrawablePlugBean> mDrawableList = mCustomWidgetConfig.getDrawablePlugList();
        HashMap<Long, BasePlugBean> mStickerBeanList = new HashMap<>();

        for (int i = 0; i < mDrawableList.size(); i++) {
            DrawablePlugBean bean = mDrawableList.get(i);
            long key = Long.valueOf(bean.getId());
            mStickerBeanList.put(key, bean);
        }
        Object[] key = mStickerBeanList.keySet().toArray();
        Arrays.sort(key);
        for (int i = 0; i < mStickerBeanList.size(); i++) {
            BasePlugBean bean = mStickerBeanList.get(key[i]);
           if (bean instanceof DrawablePlugBean) {
                drawDrawableSticker(canvas, (DrawablePlugBean) bean, mCustomWidgetConfig.getBaseOnWidthPx(), mCustomWidgetConfig.getBaseOnHeightPx());
            }
        }
    }

    /**
     * 初始化文字插件
     *
     * @param bean
     * @param baseOnWidth
     * @param baseOnHeight
     */
    private TextSticker initTextSticker(StickerView view, TextPlugBean bean, int baseOnWidth, int baseOnHeight) {
        TextSticker textSticker = new TextSticker(Long.valueOf(bean.getId()));
        textSticker.setText(CustomPlugUtil.adaptOldVersionTimer(bean.getText()));
        textSticker.setJumpContent(bean.getJumpContent());
        textSticker.setJumpAppPath(bean.getJumpAppPath());
        textSticker.setFontPath(bean.getFontPath());
        textSticker.resizeText();
        textSticker.setAlignment(bean.getAlignment());
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        view.addSticker(textSticker, Sticker.Position.INITIAL);
        textSticker.setStickerConfig(bean);
        RectF rectF = textSticker.getMappedRectF();
        float offsetX = 0;
        if (bean.getAlignment() == null) {
            offsetX = targetPoint.x - textSticker.getWidth() / 2f;
            Log.i("test_offestX:","targetPoint.x:"+targetPoint.x+" textSticker.getWidth() / 2f:"+textSticker.getWidth() / 2f);
        } else {
            if (bean.getAlignment() == Layout.Alignment.ALIGN_NORMAL) {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getLeft() - rectF.left);
            } else if (bean.getAlignment() == Layout.Alignment.ALIGN_CENTER) {
                offsetX = targetPoint.x;
            } else {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getRight() - rectF.right);
            }
        }
        Log.i("test_offestX:",offsetX+"");
        float startY = textSticker.getHeight() / 2f;
        float offsetY = targetPoint.y - startY;
        textSticker.getMatrix().postTranslate(offsetX, offsetY);
        textSticker.getMatrix().postRotate(bean.getAngle(),targetPoint.x,targetPoint.y);
        return textSticker;
    }

    /**
     * 初始化进度条插件
     *
     * @param view         sticker 父布局
     * @param bean         进度条配置实体类
     * @param baseOnWidth  sticker 父布局宽度
     * @param baseOnHeight sticker 父布局高度
     * @return 进度条 sticker
     */
    private Sticker initProgressSticker(StickerView view, ProgressPlugBean bean, int baseOnWidth, int baseOnHeight) {
        ProgressSticker progressSticker = new ProgressSticker(Long.valueOf(bean.getId()));
        float ratioWidth = getWidthRatio(baseOnWidth);
        progressSticker.resize((int)(bean.getWidth()*ratioWidth),(int)(bean.getHeight()*ratioWidth));
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = progressSticker.getWidth() / 2f;
        float startY = progressSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;
        view.addSticker(progressSticker, Sticker.Position.INITIAL);
        progressSticker.setStickerConfig(bean);
        progressSticker.getMatrix().postTranslate(offsetX, offsetY);
        progressSticker.getMatrix().postRotate(bean.getAngle(),targetPoint.x,targetPoint.y);
        return progressSticker;
    }

    /**
     * 初始化图标插件
     *
     * @param view
     * @param bean
     * @param baseOnWidth
     * @param baseOnHeight
     * @return
     */
    private DrawableSticker initDrawableSticker(StickerView view, DrawablePlugBean bean, int baseOnWidth, int baseOnHeight) {
        Drawable drawable = null;
        if (bean.getmPicType() == DrawableSticker.SVG) {
            try {
                SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(Color.parseColor(bean.getSvgColor()), PorterDuff.Mode.SRC_IN))
                        .readFromAsset(AppContext.get().getAssets(), bean.getDrawablePath()).build();
                drawable = svg.getDrawable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (bean.getmPicType() == DrawableSticker.SDCARD) {
            drawable = new BitmapDrawable(AppContext.get().getResources(), bean.getDrawablePath());
        } else {
            Bitmap bitmap = BitmapUtils.decodeFromAssest(AppContext.get(), bean.getDrawablePath());
            drawable = new BitmapDrawable(AppContext.get().getResources(), bitmap);
        }
        if (drawable != null) {
            DrawableSticker drawableSticker = new DrawableSticker(drawable, Long.valueOf(bean.getId()));
            drawableSticker.setShowFrame(bean.isShowFrame());
            PlugLocation plugLocation = bean.getLocation();
            Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
            float startX = drawableSticker.getWidth() / 2f;
            float startY = drawableSticker.getHeight() / 2f;
            float offsetX = targetPoint.x - startX;
            float offsetY = targetPoint.y - startY;
            drawableSticker.setDrawablePath(bean.getDrawablePath());
            drawableSticker.setJumpContent(bean.getJumpContent());
            drawableSticker.setJumpAppPath(bean.getJumpAppPath());
            drawableSticker.setmPicType(bean.getmPicType());
            drawableSticker.setStrokeColor(bean.getStrokeColor());
            drawableSticker.setSvgColor(bean.getSvgColor());
            drawableSticker.addMaskBitmap(AppContext.get(), bean.getClipType());
            float adaptRatio = getWidthRatio(baseOnWidth);
            drawableSticker.setScale(bean.getScale() * adaptRatio);
            view.addSticker(drawableSticker, Sticker.Position.INITIAL);
            drawableSticker.getMatrix().postTranslate(offsetX, offsetY);
            drawableSticker.getMatrix().postRotate(bean.getAngle(),targetPoint.x,targetPoint.y);
            return drawableSticker;
        } else {
            return null;
        }
    }

    /**
     * 绘制文字插件到画布上
     *
     * @param bean
     * @param baseOnWidth
     * @param baseOnHeight
     */
    private void drawTextSticker(Canvas canvas, TextPlugBean bean, int baseOnWidth, int baseOnHeight) {
        TextSticker textSticker = new TextSticker(Long.valueOf(bean.getId()));
        String text = "";
        text = CustomPlugUtil.adaptOldVersionTimer(bean.getText());
        textSticker.setText(text);
        textSticker.setJumpContent(bean.getJumpContent());
        textSticker.setJumpAppPath(bean.getJumpAppPath());
        textSticker.setFontPath(bean.getFontPath());
        textSticker.resizeText();
        textSticker.setAlignment(bean.getAlignment());
        textSticker.setStickerConfig(bean);
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        RectF rectF = textSticker.getMappedRectF();
        float offsetX = 0;
        if (bean.getAlignment() == null) {
            offsetX = targetPoint.x - bean.getWidth() / 2f;
        } else {
            if (bean.getAlignment() == Layout.Alignment.ALIGN_NORMAL) {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getLeft() - rectF.left);
            } else if (bean.getAlignment() == Layout.Alignment.ALIGN_CENTER) {
                offsetX = targetPoint.x;
            } else {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getRight() - rectF.right);
            }
        }
        Log.i("test_offestX:","targetPoint.x:"+targetPoint.x+" textSticker.getWidth() / 2f:"+textSticker.getWidth() / 2f);
        Log.i("test_offestX:",offsetX+"");
        float startY = textSticker.getHeight() / 2f;
        float offsetY = targetPoint.y - startY;
        textSticker.getMatrix().postTranslate(offsetX, offsetY);
        textSticker.getMatrix().postRotate(bean.getAngle(),targetPoint.x,targetPoint.y);
        textSticker.draw(canvas, -1, false);
    }

    /**
     * 绘制进度条插件到画布上
     *
     * @param canvas       画布
     * @param bean         进度条配置实体类
     * @param baseOnWidth  sticker 父布局宽度
     * @param baseOnHeight sticker 父布局高度
     */
    private void drawProgressSticker(Canvas canvas, ProgressPlugBean bean, int baseOnWidth, int baseOnHeight) {
        ProgressSticker progressSticker = new ProgressSticker(Long.valueOf(bean.getId()));
        float ratioWidth = getWidthRatio(baseOnWidth);
        progressSticker.resize((int)(bean.getWidth()*ratioWidth),(int)(bean.getHeight()*ratioWidth));
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = progressSticker.getWidth() / 2f;
        float startY = progressSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;
        progressSticker.setStickerConfig(bean);
        progressSticker.setScale(bean.getScale());
        progressSticker.getMatrix().postTranslate(offsetX, offsetY);
        progressSticker.getMatrix().postRotate(bean.getAngle(),targetPoint.x,targetPoint.y);
        progressSticker.draw(canvas, -1, false);
    }

    /**
     * 绘制图标插件到画布上
     *
     * @param canvas
     * @param bean
     * @param baseOnWidth
     * @param baseOnHeight
     */
    private void drawDrawableSticker(Canvas canvas, DrawablePlugBean bean, int baseOnWidth, int baseOnHeight) {
        Drawable drawable = null;
        Log.i("test_draw_drawable:", bean.toJSONString());
        if (bean.getmPicType() == DrawableSticker.SVG) {
            try {
                SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(Color.parseColor(bean.getSvgColor()), PorterDuff.Mode.SRC_IN))
                        .readFromAsset(AppContext.get().getAssets(), bean.getDrawablePath()).build();
                drawable = svg.getDrawable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (bean.getmPicType() == DrawableSticker.SDCARD) {
            drawable = new BitmapDrawable(AppContext.get().getResources(), bean.getDrawablePath());
            Log.i("test_draw_drawable:", drawable.toString());

        } else {
            Bitmap bitmap = BitmapUtils.decodeFromAssest(AppContext.get(), bean.getDrawablePath());
            drawable = new BitmapDrawable(AppContext.get().getResources(), bitmap);
        }
        if (drawable == null) {
            return;
        }
        DrawableSticker drawableSticker = new DrawableSticker(drawable, Long.valueOf(bean.getId()));
        drawableSticker.setShowFrame(bean.isShowFrame());
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = drawableSticker.getWidth() / 2f;
        float startY = drawableSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;
        drawableSticker.setDrawablePath(bean.getDrawablePath());
        drawableSticker.setJumpContent(bean.getJumpContent());
        drawableSticker.setJumpAppPath(bean.getJumpAppPath());
        drawableSticker.setSvgColor(bean.getSvgColor());
        drawableSticker.setStrokeColor(bean.getStrokeColor());
        drawableSticker.setClipType(bean.getClipType());
        drawableSticker.addMaskBitmap(AppContext.get(), bean.getClipType());
        float adaptRatio = getWidthRatio(baseOnWidth);
        drawableSticker.setScale(bean.getScale() * adaptRatio);
        drawableSticker.getMatrix().postTranslate(offsetX, offsetY);
        drawableSticker.getMatrix().postRotate(bean.getAngle(),targetPoint.x,targetPoint.y);
        drawableSticker.draw(canvas, -1, false);

    }


    private Point reSizeWidthAndHeight(float x, float y, int baseWidth, int baseHeight) {
        float widthRatio = getWidthRatio(baseWidth);
        float heightRatio = getHeightRatio(baseHeight);
        int targetX = (int) (x * widthRatio);
        int targetY = (int) (y * heightRatio);
        return new Point(targetX, targetY);
    }

    private float getWidthRatio(int baseWidth) {
        int width = WidgetConfig.getWidgetWidth();
        return width * 1.0f / baseWidth;
    }

    private float getHeightRatio(int baseHeight) {
        int height = WidgetConfig.getWidget4X4Height();
        return height * 1.0f / baseHeight;
    }
}
