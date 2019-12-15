package com.fax.showdt.manager.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.LongSparseArray;

import com.fax.showdt.AppContext;
import com.fax.showdt.bean.BasePlugBean;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.DrawablePlugBean;
import com.fax.showdt.bean.LinePlugBean;
import com.fax.showdt.bean.PlugLocation;
import com.fax.showdt.bean.ProgressPlugBean;
import com.fax.showdt.bean.TextPlugBean;
import com.fax.showdt.bean.ThemeFontBean;
import com.fax.showdt.utils.BitmapUtils;
import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.fax.showdt.view.sticker.LineSticker;
import com.fax.showdt.view.sticker.ProgressSticker;
import com.fax.showdt.view.sticker.Sticker;
import com.fax.showdt.view.sticker.StickerView;
import com.fax.showdt.view.sticker.TextSticker;

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
        CustomWidgetConfig newConfig = originConfig;
        List<TextPlugBean> mTextList = new ArrayList<>();
        List<LinePlugBean> mLineList = new ArrayList<>();
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
                textPlugBean.setAppIconPath(sticker.getAppIconPath());
                textPlugBean.setAppName(sticker.getAppName());
                textPlugBean.setFontPath(((TextSticker) sticker).getFontPath());
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
                mTextList.add(textPlugBean);
            } else if (sticker instanceof LineSticker) {
                LinePlugBean linePlugBean = new LinePlugBean();
                linePlugBean.setId(String.valueOf(key));
                linePlugBean.setSize(((LineSticker) sticker).getLineLength());
                linePlugBean.setStyle(((LineSticker) sticker).getLineOrientation() == LineSticker.LineOrientation.VERTICAL ? 1 : 2);
                linePlugBean.setColor(((LineSticker) sticker).getColor());
                linePlugBean.setHeight(sticker.getHeight());
                linePlugBean.setWidth(sticker.getWidth());
                linePlugBean.setLineId(((LineSticker) sticker).getLineId());
                linePlugBean.setJumpAppPath(sticker.getJumpAppPath());
                linePlugBean.setScaleRatio(((LineSticker) sticker).getLineScale());
                linePlugBean.setAppIconPath(sticker.getAppIconPath());
                RectF rectF = sticker.getMappedRectF();
                linePlugBean.setLeft(rectF.left);
                linePlugBean.setRight(rectF.right);
                linePlugBean.setTop(rectF.top);
                linePlugBean.setBottom(rectF.bottom);
                PointF point = sticker.getMappedCenterPoint();
                linePlugBean.setLocation(new PlugLocation(point.x, point.y));
                mLineList.add(linePlugBean);
            } else if (sticker instanceof ProgressSticker) {
                ProgressPlugBean progressPlugBean = new ProgressPlugBean();
                progressPlugBean.setId(String.valueOf(key));
                progressPlugBean.setSize(((ProgressSticker) sticker).getProgressLength());
                progressPlugBean.setWidth(sticker.getWidth());
                progressPlugBean.setHeight(sticker.getHeight());
                progressPlugBean.setColor(((ProgressSticker) sticker).getColor());
                progressPlugBean.setProgressId(((ProgressSticker) sticker).getProgressId());
                progressPlugBean.setStartTime(((ProgressSticker) sticker).getStartDateTimeMillis());
                progressPlugBean.setTargetTime(((ProgressSticker) sticker).getTargetDateTimeMillis());
                progressPlugBean.setScaleRatio(((ProgressSticker) sticker).getProgressScale());
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
                RectF rectF = sticker.getMappedRectF();
                drawablePlugBean.setLeft(rectF.left);
                drawablePlugBean.setRight(rectF.right);
                drawablePlugBean.setTop(rectF.top);
                drawablePlugBean.setBottom(rectF.bottom);
                drawablePlugBean.setScale(sticker.getCurrentScale());
                drawablePlugBean.setWidth(sticker.getWidth());
                drawablePlugBean.setHeight(sticker.getHeight());
                drawablePlugBean.setJumpAppPath(sticker.getJumpAppPath());
                drawablePlugBean.setAppIconPath(sticker.getAppIconPath());
                drawablePlugBean.setAppName(appName);
                drawablePlugBean.setSvgName(((DrawableSticker) sticker).getSvgName());
                drawablePlugBean.setShowFrame(((DrawableSticker) sticker).isShowFrame());
                drawablePlugBean.setmPicType(((DrawableSticker) sticker).getmPicType());

                PointF point = sticker.getMappedCenterPoint();
                drawablePlugBean.setLocation(new PlugLocation(point.x, point.y));
                mDrawableList.add(drawablePlugBean);
            }

        }
        newConfig.setTextSize(ViewUtils.dp2px(TextSticker.DEFAULT_TEXT_SIZE));
        newConfig.setDefaultScale(TextSticker.DEFAULT_TEXT_SIZE);
        newConfig.setLinePlugList(mLineList);
        newConfig.setProgressPlugList(mProgressList);
        newConfig.setTextPlugList(mTextList);
        newConfig.setDrawablePlugList(mDrawableList);
        newConfig.setCreatedTime(System.currentTimeMillis());
        HashMap<Long, BasePlugBean> mStickerBeanList = new HashMap<>();

        for (int i = 0; i < mTextList.size(); i++) {
            TextPlugBean bean = mTextList.get(i);
            long key = Long.valueOf(bean.getId());
            mStickerBeanList.put(key, bean);
        }
        for (int i = 0; i < mLineList.size(); i++) {
            LinePlugBean bean = mLineList.get(i);
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
            bean.setId(String.valueOf(i));
        }
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
        List<LinePlugBean> mLineList = mThemeConfig.getLinePlugList();
        List<ProgressPlugBean> mProgressList = mThemeConfig.getProgressPlugList();
        List<DrawablePlugBean> mDrawableList = mThemeConfig.getDrawablePlugList();
        HashMap<Long, BasePlugBean> mStickerBeanList = new HashMap<>();
        mStickerList.clear();
        for (int i = 0; i < mTextList.size(); i++) {
            TextPlugBean bean = mTextList.get(i);
            long key = Long.valueOf(bean.getId());
            mStickerBeanList.put(key, bean);
        }
        for (int i = 0; i < mLineList.size(); i++) {
            LinePlugBean bean = mLineList.get(i);
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
            } else if (bean instanceof LinePlugBean) {
                sticker = initLineSticker(view, (LinePlugBean) bean, mThemeConfig.getBaseOnWidthPx(), mThemeConfig.getBaseOnHeightPx());
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
    public void drawOnBitmapFromConfig(CustomWidgetConfig mCustomWidgetConfig, Canvas canvas) {
        List<TextPlugBean> mTextList = mCustomWidgetConfig.getTextPlugList();
        List<LinePlugBean> mLineList = mCustomWidgetConfig.getLinePlugList();
        List<ProgressPlugBean> mProgressList = mCustomWidgetConfig.getProgressPlugList();
        List<DrawablePlugBean> mDrawableList = mCustomWidgetConfig.getDrawablePlugList();
        HashMap<Long, BasePlugBean> mStickerBeanList = new HashMap<>();
        for (int i = 0; i < mTextList.size(); i++) {
            TextPlugBean bean = mTextList.get(i);
            long key = Long.valueOf(bean.getId());
            mStickerBeanList.put(key, bean);
        }
        for (int i = 0; i < mLineList.size(); i++) {
            LinePlugBean bean = mLineList.get(i);
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
            if (bean instanceof TextPlugBean) {
                drawTextSticker(canvas, (TextPlugBean) bean, mCustomWidgetConfig.getBaseOnWidthPx(), mCustomWidgetConfig.getBaseOnHeightPx());
            } else if (bean instanceof LinePlugBean) {
                drawLineSticker(canvas, (LinePlugBean) bean, mCustomWidgetConfig.getBaseOnWidthPx(), mCustomWidgetConfig.getBaseOnHeightPx());
            } else if (bean instanceof ProgressPlugBean) {
                drawProgressSticker(canvas, (ProgressPlugBean) bean, mCustomWidgetConfig.getBaseOnWidthPx(), mCustomWidgetConfig.getBaseOnHeightPx());
            } else if (bean instanceof DrawablePlugBean) {
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
        textSticker.setAppIconPath(bean.getAppIconPath());
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
        } else {
            if (bean.getAlignment() == Layout.Alignment.ALIGN_NORMAL) {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getLeft() - rectF.left);
            } else if (bean.getAlignment() == Layout.Alignment.ALIGN_CENTER) {
                offsetX = targetPoint.x;
            } else {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getRight() - rectF.right);
            }
        }
        float startY = textSticker.getHeight() / 2f;
        float offsetY = targetPoint.y - startY;
        textSticker.getMatrix().postTranslate(offsetX, offsetY);
        return textSticker;
    }

    /**
     * 初始化线条插件
     *
     * @param bean
     * @param baseOnWidth
     * @param baseOnHeight
     */
    private LineSticker initLineSticker(StickerView view, LinePlugBean bean, int baseOnWidth, int baseOnHeight) {
        LineSticker lineSticker = new LineSticker(Long.valueOf(bean.getId()));
        float targetLength;
        if (bean.getStyle() == 2) {
            float ratioWidth = getWidthRatio(baseOnWidth);
            targetLength = bean.getWidth() * bean.getScaleRatio() * ratioWidth;
        } else {
            float heightRatio = getHeightRatio(baseOnHeight);
            targetLength = bean.getWidth() * bean.getScaleRatio() * heightRatio;
        }
        lineSticker.setLineLength(targetLength);
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = lineSticker.getWidth() / 2f;
        float startY = lineSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;
        view.addSticker(lineSticker, Sticker.Position.INITIAL);
        lineSticker.setStickerConfig(bean);
        lineSticker.getMatrix().postTranslate(offsetX, offsetY);
        return lineSticker;

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
        float targetLength = bean.getWidth() * bean.getScaleRatio() * ratioWidth;
        progressSticker.setProgressLength(targetLength);
        progressSticker.setStartDateTimeMillis(bean.getStartTime());
        progressSticker.setTargetDateTimeMillis(bean.getTargetTime());
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = progressSticker.getWidth() / 2f;
        float startY = progressSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;
        view.addSticker(progressSticker, Sticker.Position.INITIAL);
        progressSticker.setStickerConfig(bean);
        progressSticker.getMatrix().postTranslate(offsetX, offsetY);
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
        Bitmap bitmap = BitmapUtils.decodeFile(bean.getDrawablePath());
        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(AppContext.get().getResources(), bitmap);
            DrawableSticker drawableSticker = new DrawableSticker(drawable, Long.valueOf(bean.getId()));
            drawableSticker.addMaskBitmap(AppContext.get(), bean.getSvgName());
            drawableSticker.setShowFrame(bean.isShowFrame());
            PlugLocation plugLocation = bean.getLocation();
            Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
            float startX = drawableSticker.getWidth() / 2f;
            float startY = drawableSticker.getHeight() / 2f;
            float offsetX = targetPoint.x - startX;
            float offsetY = targetPoint.y - startY;
            drawableSticker.setDrawablePath(bean.getDrawablePath());
            drawableSticker.setAppIconPath(bean.getAppIconPath());
            drawableSticker.setJumpAppPath(bean.getJumpAppPath());
            float adaptRatio = getWidthRatio(baseOnWidth);
            drawableSticker.setScale(bean.getScale() * adaptRatio);
            view.addSticker(drawableSticker, Sticker.Position.INITIAL);
            drawableSticker.getMatrix().postTranslate(offsetX, offsetY);
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
        textSticker.setAppIconPath(bean.getAppIconPath());
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
            offsetX = targetPoint.x - textSticker.getWidth() / 2f;
        } else {
            if (bean.getAlignment() == Layout.Alignment.ALIGN_NORMAL) {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getLeft() - rectF.left);
            } else if (bean.getAlignment() == Layout.Alignment.ALIGN_CENTER) {
                offsetX = targetPoint.x;
            } else {
                offsetX = getWidthRatio(baseOnWidth) * (bean.getRight() - rectF.right);
            }
        }
        float startY = textSticker.getHeight() / 2f;
        float offsetY = targetPoint.y - startY;
        textSticker.getMatrix().postTranslate(offsetX, offsetY);
        textSticker.draw(canvas, -1, false);
    }

    /**
     * 绘制线条插件到画布上
     *
     * @param bean
     * @param baseOnWidth
     * @param baseOnHeight
     */
    private void drawLineSticker(Canvas canvas, LinePlugBean bean, int baseOnWidth, int baseOnHeight) {
        LineSticker lineSticker = new LineSticker(Long.valueOf(bean.getId()));
        float targetLength;
        if (bean.getStyle() == 2) {
            float ratioWidth = getWidthRatio(baseOnWidth);
            targetLength = bean.getWidth() * ratioWidth;
        } else {
            float heightRatio = getHeightRatio(baseOnHeight);
            targetLength = bean.getWidth() * heightRatio;
        }
        lineSticker.setLineLength(targetLength);
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = lineSticker.getWidth() / 2f;
        float startY = lineSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;
        lineSticker.setStickerConfig(bean);
        lineSticker.getMatrix().postTranslate(offsetX, offsetY);
        lineSticker.draw(canvas, -1, false);
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
        float targetLength = bean.getWidth() * ratioWidth;

        progressSticker.setProgressLength(targetLength);
        progressSticker.setStartDateTimeMillis(bean.getStartTime());
        progressSticker.setTargetDateTimeMillis(bean.getTargetTime());

        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = progressSticker.getWidth() / 2f;
        float startY = progressSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;
        progressSticker.setStickerConfig(bean);
        progressSticker.getMatrix().postTranslate(offsetX, offsetY);
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
        Bitmap bitmap = BitmapUtils.decodeFile(bean.getDrawablePath());
        Drawable drawable = new BitmapDrawable(AppContext.get().getResources(), bitmap);
        DrawableSticker drawableSticker = new DrawableSticker(drawable, Long.valueOf(bean.getId()));
        drawableSticker.addMaskBitmap(AppContext.get(), bean.getSvgName());
        drawableSticker.setShowFrame(bean.isShowFrame());
        PlugLocation plugLocation = bean.getLocation();
        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        float startX = drawableSticker.getWidth() / 2f;
        float startY = drawableSticker.getHeight() / 2f;
        float offsetX = targetPoint.x - startX;
        float offsetY = targetPoint.y - startY;
        drawableSticker.setDrawablePath(bean.getDrawablePath());
        drawableSticker.setAppIconPath(bean.getAppIconPath());
        drawableSticker.setJumpAppPath(bean.getJumpAppPath());
        float adaptRatio = getWidthRatio(baseOnWidth);
        drawableSticker.setScale(bean.getScale() * adaptRatio);
        drawableSticker.getMatrix().postTranslate(offsetX, offsetY);
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
