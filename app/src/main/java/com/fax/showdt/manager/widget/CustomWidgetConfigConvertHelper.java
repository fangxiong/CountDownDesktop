package com.fax.showdt.manager.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
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
import com.fax.showdt.view.sticker.DrawableSticker;
import com.fax.showdt.view.sticker.ProgressSticker;
import com.fax.showdt.view.sticker.Sticker;
import com.fax.showdt.view.sticker.StickerView;
import com.fax.showdt.view.sticker.TextSticker;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.File;
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
                textPlugBean.setLetterSpacing(((TextSticker) sticker).getLetterSpacing());
                textPlugBean.setLineSpacing(((TextSticker) sticker).getLineSpacingMultiplier());
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
                Log.i("test_scale_save:",String.valueOf(sticker.getCurrentScale()));
                drawablePlugBean.setWidth(sticker.getWidth());
                drawablePlugBean.setHeight(sticker.getHeight());
                drawablePlugBean.setJumpAppPath(sticker.getJumpAppPath());
                drawablePlugBean.setJumpContent(sticker.getJumpContent());
                drawablePlugBean.setAppName(appName);
                drawablePlugBean.setClipType(((DrawableSticker) sticker).getClipType());
                drawablePlugBean.setStrokeColor(((DrawableSticker) sticker).getStrokeColor());
                drawablePlugBean.setDrawableColor(((DrawableSticker) sticker).getDrawableColor());
                drawablePlugBean.setShowFrame(((DrawableSticker) sticker).isShowFrame());
                drawablePlugBean.setmPicType(((DrawableSticker) sticker).getmPicType());
                drawablePlugBean.setShapeHeightRatio(((DrawableSticker) sticker).getShapeHeightRatio());
                drawablePlugBean.setCornerRatio(((DrawableSticker) sticker).getCornerRatio());
                drawablePlugBean.setStrokeRatio(((DrawableSticker) sticker).getStrokeRatio());
                drawablePlugBean.setGradient(((DrawableSticker) sticker).isGradient());
                drawablePlugBean.setGradientColors(((DrawableSticker) sticker).getGradientColors());
                drawablePlugBean.setGradientOrientation(((DrawableSticker) sticker).getGradientOrientation());
                PointF point = sticker.getMappedCenterPoint();
                drawablePlugBean.setLocation(new PlugLocation(point.x, point.y));
                Log.i("CustomWidgetHelper:","init center:"+point.x+" "+point.y);
                mDrawableList.add(drawablePlugBean);
            }

        }
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
            } else if (bean instanceof ProgressPlugBean) {
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
        if (mCustomWidgetConfig.isDrawWithBg()) {
            Bitmap bgBitmap = BitmapUtils.decodeFile(new File(mCustomWidgetConfig.getBgPath()));
            if (bgBitmap != null) {
                canvas.drawBitmap(bgBitmap, 0, 0, new Paint());
            }
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
            if (bean instanceof DrawablePlugBean) {
                Log.i("test_input:",bean.toJSONString());
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
        PlugLocation plugLocation = bean.getLocation();
        Log.e("test_center_point:",bean.getId()+" x3:"+plugLocation.getX()+" y3:"+plugLocation.getY());

        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        Log.e("test_center_point:",bean.getId()+" x4:"+targetPoint.x+" y4:"+targetPoint.y);
        textSticker.setStickerConfig(bean);
        textSticker.resizeText();

        RectF rectF = textSticker.getMappedRectF();
        float offsetX = 0;
        if (bean.getAlignment() == null) {
            offsetX = getWidthRatio(baseOnWidth) * (bean.getLeft() - rectF.left);
        } else {
            if (bean.getAlignment() == Layout.Alignment.ALIGN_NORMAL) {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getLeft() - rectF.left);
            } else if (bean.getAlignment() == Layout.Alignment.ALIGN_CENTER) {
                offsetX = targetPoint.x - (rectF.centerX());
            } else {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getRight() - rectF.right);
            }
        }
        float startY = textSticker.getMappedCenterPoint().y;
        float offsetY = targetPoint.y - startY;
        textSticker.getMatrix().postTranslate(offsetX, offsetY);
        if(view != null) {
            view.addSticker(textSticker, Sticker.Position.INITIAL, false);
        }
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
        progressSticker.resize((int) (bean.getWidth() * ratioWidth), (int) (bean.getHeight() * ratioWidth));
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = progressSticker.getWidth() / 2f;
        float startY = progressSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;

        progressSticker.setStickerConfig(bean);
        progressSticker.getMatrix().postTranslate(offsetX, offsetY);
        progressSticker.getMatrix().postRotate(bean.getAngle(), targetPoint.x, targetPoint.y);
        if(view != null) {
            view.addSticker(progressSticker, Sticker.Position.INITIAL,false);
        }
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
            String pathStr = bean.getDrawablePath();
            drawable = new ShapeDrawable();
            Path path = com.caverock.androidsvg1.SVG.parsePath(pathStr);
            RectF rectF = new RectF();
            path.computeBounds(rectF, false);
            Rect realBounds = new Rect(0, 0, (int) (rectF.right + rectF.left), (int) (rectF.bottom + rectF.top));
            drawable.setBounds(realBounds);

        } else if (bean.getmPicType() == DrawableSticker.SDCARD) {
            drawable = new BitmapDrawable(AppContext.get().getResources(), bean.getDrawablePath());
        } else if(bean.getmPicType() == DrawableSticker.ASSET) {
            Bitmap bitmap = BitmapUtils.decodeFromAssest(AppContext.get(), bean.getDrawablePath());
            drawable = new BitmapDrawable(AppContext.get().getResources(), bitmap);
        }else if(bean.getmPicType() == DrawableSticker.SHAPE){
            drawable = new GradientDrawable();
        }
        if (drawable != null) {
            DrawableSticker drawableSticker = new DrawableSticker(drawable, Long.valueOf(bean.getId()));
            drawableSticker.setShowFrame(bean.isShowFrame());
            drawableSticker.setmPicType(bean.getmPicType());
            drawableSticker.setStrokeColor(bean.getStrokeColor());
            drawableSticker.setClipType(bean.getClipType());
            drawableSticker.addMaskBitmap(AppContext.get(), bean.getClipType());
            PlugLocation plugLocation = bean.getLocation();
            Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
            drawableSticker.setDrawablePath(bean.getDrawablePath());
            drawableSticker.setJumpContent(bean.getJumpContent());
            drawableSticker.setAppName(bean.getAppName());
            drawableSticker.setJumpAppPath(bean.getJumpAppPath());
            drawableSticker.setDrawableColor(bean.getDrawableColor());
            drawableSticker.setStrokeRatio(bean.getStrokeRatio());
            drawableSticker.setShapeHeightRatio(bean.getShapeHeightRatio());
            drawableSticker.setCornerRatio(bean.getCornerRatio());
            drawableSticker.setGradient(bean.isGradient());
            drawableSticker.setGradientColors(bean.getGradientColors());
            drawableSticker.setGradientOrientation(bean.getGradientOrientation());
            float adaptRatio = getWidthRatio(baseOnWidth);

            drawableSticker.setScale(bean.getScale() * adaptRatio);
            Log.e("test_scale:",String.valueOf(bean.getScale()));
            Log.e("test_scale_adaptRatio:",String.valueOf(adaptRatio));
            Log.e("test_scale_width1:",String.valueOf(drawableSticker.getWidth()));
            Log.e("test_scale_width2:",String.valueOf(drawable.getIntrinsicWidth()));
            drawableSticker.resizeBounds();

            PointF center = drawableSticker.getMappedCenterPoint();
            float offsetX = targetPoint.x - center.x;
            float offsetY = targetPoint.y - center.y;
            Log.i("test_center:","center x:"+targetPoint.x+" center y:"+targetPoint.y);
            Log.i("test_center:","width:"+center.x+" height:"+center.y);
            drawableSticker.getMatrix().postTranslate(offsetX, offsetY);
            drawableSticker.getMatrix().postRotate(bean.getAngle(), targetPoint.x, targetPoint.y);
            if(view != null) {
                view.addSticker(drawableSticker, Sticker.Position.INITIAL,false);
            }

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
        textSticker.setStickerConfig(bean);
        textSticker.resizeText();
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        RectF rectF = textSticker.getMappedRectF();
        float offsetX = 0;
        if (bean.getAlignment() == null) {
            offsetX = getWidthRatio(baseOnWidth) * (bean.getLeft() - rectF.left);
        } else {
            if (bean.getAlignment() == Layout.Alignment.ALIGN_NORMAL) {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getLeft() - rectF.left);
            } else if (bean.getAlignment() == Layout.Alignment.ALIGN_CENTER) {
                offsetX = targetPoint.x - (rectF.centerX());
            } else {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getRight() - rectF.right);
            }
        }
        float startY = textSticker.getMappedCenterPoint().y;
        float offsetY = targetPoint.y - startY;
        textSticker.getMatrix().postTranslate(offsetX, offsetY);
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
        progressSticker.resize((int) (bean.getWidth() * ratioWidth), (int) (bean.getHeight() * ratioWidth));
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = progressSticker.getWidth() / 2f;
        float startY = progressSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;
        progressSticker.setStickerConfig(bean);
        progressSticker.getMatrix().postTranslate(offsetX, offsetY);
        progressSticker.getMatrix().postRotate(bean.getAngle(), targetPoint.x, targetPoint.y);
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
                SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(Color.parseColor(bean.getDrawableColor()), PorterDuff.Mode.SRC_IN))
                        .readFromAsset(AppContext.get().getAssets(), bean.getDrawablePath()).build();
                drawable = svg.getDrawable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (bean.getmPicType() == DrawableSticker.SDCARD) {
            drawable = new BitmapDrawable(AppContext.get().getResources(), bean.getDrawablePath());
            Log.i("test_draw_drawable:", drawable.toString());

        } else if(bean.getmPicType() == DrawableSticker.ASSET) {
            Bitmap bitmap = BitmapUtils.decodeFromAssest(AppContext.get(), bean.getDrawablePath());
            drawable = new BitmapDrawable(AppContext.get().getResources(), bitmap);
        }else if(bean.getmPicType() == DrawableSticker.SHAPE){
            drawable = new GradientDrawable();
        }
        if (drawable == null) {
            return;
        }
        DrawableSticker drawableSticker = new DrawableSticker(drawable, Long.valueOf(bean.getId()));
        drawableSticker.setShowFrame(bean.isShowFrame());
        drawableSticker.setmPicType(bean.getmPicType());
        drawableSticker.setStrokeColor(bean.getStrokeColor());
        drawableSticker.setClipType(bean.getClipType());
        drawableSticker.addMaskBitmap(AppContext.get(), bean.getClipType());
        PlugLocation plugLocation = bean.getLocation();
        Log.e("test_center_point:","x:"+plugLocation.getX()+" y:"+plugLocation.getY());
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        drawableSticker.setDrawablePath(bean.getDrawablePath());
        drawableSticker.setJumpContent(bean.getJumpContent());
        drawableSticker.setJumpAppPath(bean.getJumpAppPath());
        drawableSticker.setAppName(bean.getAppName());
        drawableSticker.setDrawableColor(bean.getDrawableColor());
        Log.i("test_draw_drawable:", bean.getStrokeColor());
        drawableSticker.setStrokeRatio(bean.getStrokeRatio());
        drawableSticker.setShapeHeightRatio(bean.getShapeHeightRatio());
        drawableSticker.setCornerRatio(bean.getCornerRatio());
        float adaptRatio = getWidthRatio(baseOnWidth);
        drawableSticker.setScale(bean.getScale() * adaptRatio);
        drawableSticker.resizeBounds();
        drawableSticker.setGradient(bean.isGradient());
        drawableSticker.setGradientColors(bean.getGradientColors());
        drawableSticker.setGradientOrientation(bean.getGradientOrientation());
        PointF center = drawableSticker.getMappedCenterPoint();
        float offsetX = targetPoint.x - center.x;
        float offsetY = targetPoint.y - center.y;
        drawableSticker.getMatrix().postTranslate(offsetX, offsetY);
        drawableSticker.getMatrix().postRotate(bean.getAngle(), targetPoint.x, targetPoint.y);
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
        int width = WidgetSizeConfig.getWidgetWidth();
        Log.e("test_widget_screen:",String.valueOf(width));
        Log.e("test_widget_base:",String.valueOf(baseWidth));
        return width * 1.0f / baseWidth;
    }

    private float getHeightRatio(int baseHeight) {
        int height = WidgetSizeConfig.getWidget4X4Height();
        return height * 1.0f / baseHeight;
    }
}
