package com.fax.showdt.view.sticker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.bean.TextPlugBean;
import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.FontCache;
import com.fax.showdt.utils.ViewUtils;

import androidx.annotation.Dimension;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Customize your sticker with text and image background.
 * You can place some text into a given region, however,
 * you can also add a plain text sticker. To support text
 * auto resizing , I take most of the code from AutoResizeTextView.
 * See https://adilatwork.blogspot.com/2014/08/android-textview-which-resizes-its-text.html
 * Notice: It's not efficient to add long text due to too much of
 * StaticLayout object allocation.
 * Created by liutao on 30/11/2016.
 */

public class TextSticker extends Sticker {

    private final Context context;
    private Rect realBounds;
    private final TextPaint textPaint;
    private Drawable drawable;
    private String text;
    private String tempText;
    private int textWidth, textHeight;
    private String color = "#000000";
    public static final int DEFAULT_TEXT_SIZE = 18;
    private int scaleParam = DEFAULT_TEXT_SIZE;
    private int textSize = DEFAULT_TEXT_SIZE;
    private String mFontPath = "";
    private boolean isShimmerText;
    private String shimmerColor;
//    private Shimmer mShimmer;
    private Layout.Alignment mAlignment = null;
    /**
     * Upper bounds for text size.
     * This acts as a starting point for resizing.
     */
    private float maxTextSizePixels;

    /**
     * Lower bounds for text size.
     */
    private float minTextSizePixels;

    /**
     * Line spacing multiplier.
     */
    private float lineSpacingMultiplier = 1.0f;

    /**
     * Additional line spacing.
     */
    private float lineSpacingExtra = 0.0f;

    public TextSticker(long id) {
        this(null, id);
    }

    public TextSticker(@Nullable Drawable drawable, long id) {
        super(id);
        this.context = AppContext.get();
        this.drawable = drawable;
        if (drawable == null) {
            this.drawable = ContextCompat.getDrawable(context, R.drawable.sticker_transparent_background);
        }

        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        text = "双击修改文字";
        minTextSizePixels = ViewUtils.dp2px(6);
        maxTextSizePixels = ViewUtils.dp2px(50);
        textPaint.setTextSize(ViewUtils.dp2px(textSize));
        initRect();
    }

    public void resizeText() {
        if (!TextUtils.isEmpty(mFontPath)) {
            Typeface typeface = FontCache.get(mFontPath,context);
            if (typeface != null) {
//                Log.i("test_font:",mFontPath);
                textPaint.setTypeface(typeface);
            }
        } else {
            textPaint.setTypeface(Typeface.DEFAULT);
        }
        tempText = CustomPlugUtil.getPlugTextFromSigns(text);

        int fontWidth = (int) textPaint.measureText(tempText);
        int fontHeight = (int) (textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent);
        textWidth = fontWidth ;
        textHeight = fontHeight;
    }

    private void initRect() {
//        Log.i("test_font:",mFontPath== null ? " null" : mFontPath);
        if (!TextUtils.isEmpty(mFontPath)) {
            Typeface typeface = FontCache.get(mFontPath,context);
            if (typeface != null) {
//                Log.i("test_font:","设置字体");
                textPaint.setTypeface(typeface);
            }
        } else {
            textPaint.setTypeface(Typeface.DEFAULT);
        }
        tempText = CustomPlugUtil.getPlugTextFromSigns(text);
        int fontWidth = (int) textPaint.measureText(tempText);
        int fontHeight = (int) (textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent);
        textWidth = fontWidth ;
        textHeight = fontHeight;

        realBounds = new Rect((int) getTextDrawLeftX(), 0, textWidth / 2, textHeight);

        drawable.setBounds(realBounds);
    }


    @Override
    public void draw(@NonNull Canvas canvas, int index, boolean showNumber) {
        initRect();
        canvas.save();
        Matrix matrix = getMatrix();
        canvas.concat(matrix);
//        if (drawable != null) {
//            drawable.setBounds(realBounds);
//            drawable.draw(canvas);
//        }
        String textStr = CustomPlugUtil.getPlugTextFromSigns(text);
//        Log.i("test_text_draw:","origin text:"+text+" result text:"+textStr);
        Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
        if (isShimmerText) {
            initShimmer(textPaint, textStr);
            startShimmer();
//            canvas.drawText(textStr, getTextDrawLeftX(), 0 - fm.ascent + internalMargin, mShimmer.getMPaint());
        } else {
            stopShimmer();
            textPaint.setShader(null);
            canvas.drawText(textStr, getTextDrawLeftX(), 0 - fm.ascent, textPaint);
        }

        canvas.restore();


    }

    @Override
    public void getBoundPoints(@NonNull float[] points) {
        if (mAlignment == null) {
            super.getBoundPoints(points);
        } else {
            if (mAlignment == Layout.Alignment.ALIGN_NORMAL) {
                super.getBoundPoints(points);
            } else if (mAlignment == Layout.Alignment.ALIGN_CENTER) {
                points[0] = -getWidth() / 2f;
                points[1] = 0f;
                points[2] = getWidth() / 2f;
                points[3] = 0f;
                points[4] = -getWidth() / 2f;
                points[5] = getHeight();
                points[6] = getWidth() / 2f;
                points[7] = getHeight();
            } else if (mAlignment == Layout.Alignment.ALIGN_OPPOSITE) {
                points[0] = -getWidth();
                points[1] = 0f;
                points[2] = 0f;
                points[3] = 0f;
                points[4] = -getWidth();
                points[5] = getHeight();
                points[6] = 0f;
                points[7] = getHeight();
            }
        }
    }


    @Override
    public void getCenterPoint(@NonNull PointF dst) {
        if (mAlignment == null) {
            super.getCenterPoint(dst);
        } else {
            if (mAlignment == Layout.Alignment.ALIGN_NORMAL) {
                super.getCenterPoint(dst);
            } else if (mAlignment == Layout.Alignment.ALIGN_CENTER) {
                dst.set(0f, getHeight() * 1f / 2);
            } else {
                dst.set(-getWidth() * 1f / 2, getHeight() * 1f / 2);
            }
        }
    }

    /**
     * 由于对齐方式的不同,根据对齐方式不同获取对应的左坐标x
     *
     * @return
     */
    private float getTextDrawLeftX() {
        if (mAlignment == null) {
            return 0;
        } else {
            if (mAlignment == Layout.Alignment.ALIGN_NORMAL) {
                return 0;
            } else if (mAlignment == Layout.Alignment.ALIGN_CENTER) {
                return -textWidth / 2.0f ;
            } else {
                return -textWidth;
            }
        }
    }

    @Override
    public int getWidth() {
//        return drawable.getIntrinsicWidth();
        return textWidth;
    }

    @Override
    public int getHeight() {
//        return drawable.getIntrinsicHeight();
        return textHeight;
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }

    @NonNull
    @Override
    public TextSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        textPaint.setAlpha(alpha);
        return this;
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public TextSticker setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        realBounds.set(0, 0, getWidth(), getHeight());
        return this;
    }

    @NonNull
    public TextSticker setDrawable(@NonNull Drawable drawable, @Nullable Rect region) {
        this.drawable = drawable;
        realBounds.set(0, 0, getWidth(), getHeight());
        return this;
    }

    @NonNull
    public TextSticker setTypeface(@Nullable Typeface typeface) {
        textPaint.setTypeface(typeface);
        return this;
    }

    @NonNull
    public TextSticker setTextColor(@Nullable String color) {
        textPaint.setColor(Color.parseColor(color));
        this.color = color;
        return this;
    }

    public String getTextColor() {
        return color;
    }


    @NonNull
    public TextSticker setMaxTextSize(@Dimension(unit = Dimension.SP) float size) {
        textPaint.setTextSize(ViewUtils.dp2px(size));
        maxTextSizePixels = textPaint.getTextSize();
        return this;
    }

    /**
     * Sets the lower text size limit
     *
     * @param minTextSizeScaledPixels the minimum size to use for text in this view,
     *                                in scaled pixels.
     */
    @NonNull
    public TextSticker setMinTextSize(float minTextSizeScaledPixels) {
        minTextSizePixels = ViewUtils.dp2px(minTextSizeScaledPixels);
        return this;
    }

    @NonNull
    public TextSticker setLineSpacing(float add, float multiplier) {
        lineSpacingMultiplier = multiplier;
        lineSpacingExtra = add;
        return this;
    }

    @NonNull
    public TextSticker setText(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            text = "双击修改文字";
        }
        this.text = text;
        return this;
    }

    @Nullable
    public String getText() {
        if("双击修改文字".equals(text)){
            return "";
        }
        return text;
    }


    public int getScaleParam() {
        return scaleParam;
    }

    public void setScaleParam(int scaleParam) {
        PointF pointF = this.getMappedCenterPoint();
        if (scaleParam > this.scaleParam) {
            double scale = Math.pow(11 / 10f, scaleParam - this.scaleParam);
            this.getMatrix().postScale((float) scale, (float) scale, pointF.x, pointF.y);
        } else {
            double scale = Math.pow(10 / 11f, this.scaleParam - scaleParam);
            this.getMatrix().postScale((float) scale, (float) scale, pointF.x, pointF.y);
        }
        PointF point = this.getMappedCenterPoint();
        this.scaleParam = scaleParam;
    }

    public void setAlignment(Layout.Alignment alignment) {
        RectF rectF = getMappedRectF();
        float offsetX = 0;
        float mappedTextLength = rectF.right - rectF.left;
        if(this.mAlignment == Layout.Alignment.ALIGN_NORMAL){
            if(alignment == Layout.Alignment.ALIGN_CENTER){
                offsetX = mappedTextLength/2f;
            }else if(alignment == Layout.Alignment.ALIGN_OPPOSITE){
                offsetX = mappedTextLength;
            }
        }else if(this.mAlignment == Layout.Alignment.ALIGN_CENTER){
            if(alignment == Layout.Alignment.ALIGN_NORMAL){
                offsetX = -mappedTextLength/2f;
            }else if(alignment == Layout.Alignment.ALIGN_OPPOSITE){
                offsetX = mappedTextLength/2f;
            }
        }else if(this.mAlignment == Layout.Alignment.ALIGN_OPPOSITE) {
            if(alignment == Layout.Alignment.ALIGN_NORMAL){
                offsetX = -mappedTextLength;
            }else if(alignment == Layout.Alignment.ALIGN_CENTER){
                offsetX = -mappedTextLength/2f;
            }
        }
        this.mAlignment = alignment;
        getMatrix().postTranslate(offsetX,0);
    }

    public Layout.Alignment getAlignment() {
        return mAlignment;
    }

    @Override
    public Sticker setMatrix(@Nullable Matrix matrix) {
        Sticker sticker = super.setMatrix(matrix);
        float currentScale = getCurrentScale();
        if (getCurrentScale() >= 1.0f) {
            scaleParam = (int) Math.round((Math.log(currentScale) / Math.log(11 / 10f)) + DEFAULT_TEXT_SIZE);
        } else {
            scaleParam = (int) Math.round(DEFAULT_TEXT_SIZE - (Math.log(currentScale) / Math.log(10 / 11f)));
        }
        if (6 >= scaleParam) {
            setScaleParam(6);
        } else if (scaleParam >= 50) {
            setScaleParam(50);
        }
        setScaleParam(scaleParam);
        return sticker;
    }

    public void setStickerConfig(@NonNull TextPlugBean bean) {
        setId(Long.valueOf(bean.getId()));
        setTextColor(bean.getColor());
        setScaleParam((int) bean.getScale());
        setAppIconPath(bean.getAppIconPath());
        setShimmerColor(bean.getShimmerColor());
        setShimmerText(bean.isShimmerText());
        setAlignment(bean.getAlignment());
    }

    public boolean isContainTimer() {
        return CustomPlugUtil.checkContainTimerCode(text);
    }

    /**
     * @return lower text size limit, in pixels.
     */
    public float getMinTextSizePixels() {
        return minTextSizePixels;
    }

    /**
     * Sets the text size of a clone of the view's {@link TextPaint} object
     * and uses a {@link StaticLayout} instance to measure the height of the text.
     *
     * @return the height of the text when placed in a view
     * with the specified width
     * and when the text has the specified size.
     */
    protected int getTextHeightPixels(@NonNull CharSequence source, int availableWidthPixels,
                                      float textSizePixels) {
        textPaint.setTextSize(textSizePixels);
        // It's not efficient to create a StaticLayout instance
        // every time when measuring, we can use StaticLayout.Builder
        // since api 23.
        StaticLayout staticLayout =
                new StaticLayout(source, textPaint, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
                        lineSpacingMultiplier, lineSpacingExtra, true);
        return staticLayout.getHeight();
    }

    /**
     * @return the number of pixels which scaledPixels corresponds to on the device.
     */
    private float convertSpToPx(float scaledPixels) {
        return scaledPixels * context.getResources().getDisplayMetrics().scaledDensity;
    }


    public int getMaxTextSizeSp() {
        return (int) (maxTextSizePixels / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public String getFontPath() {
        return mFontPath;
    }

    public void setFontPath(String fontPath) {
        mFontPath = fontPath;
    }

    public boolean isShimmerText() {
        return isShimmerText;
    }

    public void setShimmerText(boolean shimmerText) {
        isShimmerText = shimmerText;
//        if (!isShimmerText && mShimmer != null) {
//            mShimmer = null;
//        }
    }

    public void setSliding(boolean sliding) {
    }

    public String getShimmerColor() {
        return shimmerColor != null ? shimmerColor : "#FFFFFF";
    }

    public void setShimmerColor(String shimmerColor) {
        this.shimmerColor = shimmerColor;
    }

    private void initShimmer(Paint paint, String content) {
//        if (mShimmer == null) {
//            mShimmer = new Shimmer();
//        }
//        int[] color = {Color.parseColor(getTextColor()), Color.parseColor(getShimmerColor()), Color.parseColor(getTextColor())};
//        mShimmer.setPaint(paint);
//        mShimmer.setWidthDivideBy(30);
//        mShimmer.setTextSize(paint.getTextSize());
//        mShimmer.setContent(content);
//        mShimmer.setLinearGradientColors(color);
//        mShimmer.init();
    }

    private void startShimmer() {
//        if (mShimmer != null) {
//            mShimmer.start();
//            mShimmer.run();
//        }
    }

    private void stopShimmer() {
//        if (mShimmer != null) {
//            mShimmer.stop();
//        }
    }
}
