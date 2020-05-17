package com.fax.showdt.manager.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.Layout;
import android.util.Log;

import com.fax.showdt.AppContext;
import com.fax.showdt.bean.BasePlugBean;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.DrawablePlugBean;
import com.fax.showdt.bean.PlugLocation;
import com.fax.showdt.bean.ProgressPlugBean;
import com.fax.showdt.bean.TextPlugBean;
import com.fax.showdt.service.WidgetUpdateService;
import com.fax.showdt.utils.BitmapUtils;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.fax.showdt.view.sticker.ProgressSticker;
import com.fax.showdt.view.sticker.Sticker;
import com.fax.showdt.view.sticker.StickerView;
import com.fax.showdt.view.sticker.TextSticker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:     绘制小插件到桌面
 * Author:          fax
 * CreateDate:      2020-05-14 18:01
 * Email:           fxiong1995@gmail.com
 */
public class WidgetDrawHelper {

    private static volatile WidgetDrawHelper instance;
    private Map<String,List<Sticker> > stickers = new HashMap<>();
    private Map<String,Long > configIds = new HashMap<>();

    private WidgetDrawHelper(){}

    public static WidgetDrawHelper getInstance(){
        if(instance == null){
            synchronized (WidgetDrawHelper.class){
                if(instance == null){
                    instance = new WidgetDrawHelper();
                }
            }
        }
        return instance;
    }

    public void filterWidgetData(){
        int[] widgetIds = WidgetManager.getAllProviderWidgetIds(AppContext.get());
        Map<String,List<Sticker> > widgetMap = new HashMap<>();
        for(String key:configIds.keySet()) {
        for(int i= 0;i<widgetIds.length;i++) {
            int widgetId = widgetIds[i];
            if(String.valueOf(widgetId).equals(key)){
                widgetMap.put(key,stickers.get(key));
            }
        }
        }
        stickers = widgetMap;
    }

    /**
     * 通过配置绘制在canvas上 并生成bitmap来显示在小部件上
     *
     * @param mThemeConfig
     * @param canvas
     */
    public void drawStickers(String widgetId,CustomWidgetConfig mThemeConfig, Canvas canvas) {

        Log.e("test_widget_count:",String.valueOf(stickers.size()));
        Long configId = configIds.get(widgetId);
        List<Sticker> list = stickers.get(widgetId);
        if(list != null && list.size() >0&&(configId==null||configId == mThemeConfig.getId())){
            for(Sticker sticker:list){
                sticker.draw(canvas, 0, false);
            }
            return;
        }
        list = new ArrayList<>();
        List<TextPlugBean> mTextList = mThemeConfig.getTextPlugList();
        List<ProgressPlugBean> mProgressList = mThemeConfig.getProgressPlugList();
        List<DrawablePlugBean> mDrawableList = mThemeConfig.getDrawablePlugList();
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
                sticker = initTextSticker(null, (TextPlugBean) bean, mThemeConfig.getBaseOnWidthPx(), mThemeConfig.getBaseOnHeightPx());
            } else if (bean instanceof ProgressPlugBean) {
                sticker = initProgressSticker(null, (ProgressPlugBean) bean, mThemeConfig.getBaseOnWidthPx(), mThemeConfig.getBaseOnHeightPx());
            } else if (bean instanceof DrawablePlugBean) {
                sticker = initDrawableSticker(null, (DrawablePlugBean) bean, mThemeConfig.getBaseOnWidthPx(), mThemeConfig.getBaseOnHeightPx());
            }
            if (sticker != null) {
                list.add(sticker);
                sticker.draw(canvas, i, false);
            }

        }
        configIds.put(widgetId,mThemeConfig.getId());
        stickers.put(widgetId,list);
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
        Log.e("test_center_point:", bean.getId() + " x3:" + plugLocation.getX() + " y3:" + plugLocation.getY());

        Point targetPoint = reSizeWidthAndHeight(plugLocation.getX(), plugLocation.getY(), baseOnWidth, baseOnHeight);
        Log.e("test_center_point:", bean.getId() + " x4:" + targetPoint.x + " y4:" + targetPoint.y);
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
        if (view != null) {
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
        if (view != null) {
            view.addSticker(progressSticker, Sticker.Position.INITIAL, false);
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
        float drawableRatio = 1.0f;
        if (bean.getmPicType() == DrawableSticker.SVG) {
            String pathStr = bean.getDrawablePath();
            drawable = new ShapeDrawable();
            Path path = com.caverock.androidsvg1.SVG.parsePath(pathStr);
            RectF rectF = new RectF();
            path.computeBounds(rectF, false);
            Rect realBounds = new Rect(0, 0, (int) (rectF.right + rectF.left), (int) (rectF.bottom + rectF.top));
            drawable.setBounds(realBounds);
        } else if (bean.getmPicType() == DrawableSticker.SDCARD) {
            Bitmap bitmap = BitmapUtils.decodeFile(bean.getDrawablePath());
            if (bitmap != null) {
                drawable = new BitmapDrawable(AppContext.get().getResources(), bitmap);
                drawableRatio = bean.getWidth() * 1.0f / drawable.getIntrinsicWidth();
                Log.e("test_scale_width00", String.valueOf(bitmap.getWidth()));
                Log.e("test_scale_width01", String.valueOf(drawable.getIntrinsicWidth()));
                Log.e("test_scale_width02", String.valueOf(bean.getWidth()));
                Log.e("test_scale_width03", String.valueOf(drawableRatio));
            }

        } else if (bean.getmPicType() == DrawableSticker.ASSET) {
            Bitmap bitmap = BitmapUtils.decodeFromAssest(AppContext.get(), bean.getDrawablePath());
            drawable = new BitmapDrawable(AppContext.get().getResources(), bitmap);
        } else if (bean.getmPicType() == DrawableSticker.SHAPE) {
            drawable = new GradientDrawable();
            Log.e("test_drawable_init:", GsonUtils.toJsonWithSerializeNulls(bean));
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
            drawableSticker.setShapeWidthRatio(bean.getShapeWidthRatio());
            drawableSticker.setCornerRatio(bean.getCornerRatio());
            drawableSticker.setGradient(bean.isGradient());
            drawableSticker.setGradientColors(bean.getGradientColors());
            drawableSticker.setGradientOrientation(bean.getGradientOrientation());
            drawableSticker.setStroke(bean.isStroke());
            drawableSticker.setStrokeWidth(bean.getStrokeWidth());
            drawableSticker.resizeBounds();
            Log.e("test_scale_width1:", String.valueOf(drawableSticker.getWidth()));
            Log.e("test_scale_width2:", String.valueOf(drawable.getIntrinsicWidth()));
            drawableSticker.setScale(bean.getScale() * getWidthRatio(baseOnWidth) * drawableRatio);
            PointF center = drawableSticker.getMappedCenterPoint();
            float offsetX = targetPoint.x - center.x;
            float offsetY = targetPoint.y - center.y;
            drawableSticker.getMatrix().postTranslate(offsetX, offsetY);
            drawableSticker.getMatrix().postRotate(bean.getAngle(), targetPoint.x, targetPoint.y);
            if (view != null) {
                view.addSticker(drawableSticker, Sticker.Position.INITIAL, false);
            }

            return drawableSticker;
        } else {
            return null;
        }
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
        Log.e("test_widget_screen:", String.valueOf(width));
        Log.e("test_widget_base:", String.valueOf(baseWidth));
        return width * 1.0f / baseWidth;
    }

    private float getHeightRatio(int baseHeight) {
        int height = WidgetSizeConfig.getWidget4X4Height();
        return height * 1.0f / baseHeight;
    }
}
