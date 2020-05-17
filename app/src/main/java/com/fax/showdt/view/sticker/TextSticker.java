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
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.bean.TextPlugBean;
import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.FileUtil;
import com.fax.showdt.utils.FontCache;
import com.fax.showdt.utils.TimeUtils;
import com.fax.showdt.utils.ViewUtils;

import androidx.annotation.Dimension;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;

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

    public static final int DEFAULT_BG_CORNER = ViewUtils.dpToPx(5, AppContext.get());
    public static final String DEFAULT_BG_COLOR = "#38FFFFFF";
    public static final int DEFAULT_TEXT_SIZE = 18;
    private final Context context;
    private Rect realBounds;
    private final TextPaint textPaint;
    private Drawable drawable;
    private String text;
    private String resultText;
    private int textWidth;
    private String color = "#000000";
    private int scaleParam = DEFAULT_TEXT_SIZE;
    private int textSize = DEFAULT_TEXT_SIZE;
    private String mFontPath = "";
    private boolean isShimmerText;
    private String shimmerColor;
    private boolean isShadow = false;
    private float shadowRadius = 5f;
    private float shadowX = 5f;
    private float shadowY = 0f;
    private String shadowColor = "#000000";

    /**
     * 文本的对齐方式(在屏幕中的对齐方式)
     */
    private Layout.Alignment mAlignment = Layout.Alignment.ALIGN_NORMAL;
    /**
     * 是否支持设置背景
     */
    private boolean supportBg = false;

    private int bgCorner = DEFAULT_BG_CORNER;
    /**
     * 背景颜色
     */
    private String bgColor = DEFAULT_BG_COLOR;
    /**
     * 用于绘制多行文本
     */
    private StaticLayout mStaticLayout;

    /**
     * 字间距{@link Paint#setLetterSpacing(float)}
     */
    private float letterSpacing = 0.2f;

    /**
     * 行间距{@link StaticLayout#StaticLayout(CharSequence, int, int, TextPaint, int, Layout.Alignment, float, float, boolean, TextUtils.TruncateAt, int)}
     */
    private float lineSpacingMultiplier = 1.2f;

    /**
     * 额外的行间距{@link StaticLayout#StaticLayout(CharSequence, int, int, TextPaint, int, Layout.Alignment, float, float, boolean, TextUtils.TruncateAt, int)}
     */
    private float lineSpacingExtra = 0f;

    public TextSticker(long id) {
        this(null, id);
    }

    public TextSticker(@Nullable Drawable drawable, long id) {
        super(id);
        this.context = AppContext.get();
        this.drawable = drawable;
        if (drawable == null) {
            this.drawable = ContextCompat.getDrawable(context, R.drawable.sticker_transparent_background);
            this.drawable = new GradientDrawable();

        }
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        text = "双击修改文字";
        textPaint.setTextSize(ViewUtils.dpToPx(textSize,AppContext.get()));
        resizeText();
    }

    public void resizeText() {
        if (!TextUtils.isEmpty(mFontPath)) {
            Typeface typeface = FontCache.get(mFontPath,context);
            if (typeface != null) {
                textPaint.setTypeface(typeface);
            }
        } else {
            textPaint.setTypeface(Typeface.DEFAULT);
        }
        textPaint.setLetterSpacing(letterSpacing);

        if(isShadow) {
            textPaint.setShadowLayer(shadowRadius, shadowX, shadowY, Color.parseColor(shadowColor));
        }else {
            textPaint.setShadowLayer(0,0,0, Color.TRANSPARENT);
        }
        resultText = CustomPlugUtil.getPlugTextFromSigns(text);

        StaticLayout staticLayout = new StaticLayout(resultText, textPaint, AppContext.get().getResources().getDisplayMetrics().widthPixels * 4, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier,
                lineSpacingExtra, true);
        mStaticLayout =
                new StaticLayout(resultText, textPaint, getMaxLineWidth(staticLayout), mAlignment, lineSpacingMultiplier,
                        lineSpacingExtra, true);
        textWidth = getMaxLineWidth(staticLayout);
        if (mAlignment == null) {
            realBounds = new Rect((int) getTextDrawLeftX() , 0, getMaxLineWidth(staticLayout), mStaticLayout.getHeight());

        } else if (mAlignment == Layout.Alignment.ALIGN_NORMAL) {
            realBounds = new Rect((int) getTextDrawLeftX(), 0, getMaxLineWidth(staticLayout), mStaticLayout.getHeight());

        } else if (mAlignment == Layout.Alignment.ALIGN_CENTER) {
            realBounds = new Rect((int) getTextDrawLeftX(), 0, getMaxLineWidth(staticLayout) / 2 , mStaticLayout.getHeight());

        } else if (mAlignment == Layout.Alignment.ALIGN_OPPOSITE) {
            realBounds = new Rect((int) getTextDrawLeftX(), 0, 0, mStaticLayout.getHeight());
        }

    }

    @Override
    public void draw(@NonNull Canvas canvas, int index, boolean showNumber) {
        resizeText();
        canvas.save();
        Matrix matrix = getMatrix();
        canvas.concat(matrix);
        if (drawable != null && isSupportBg()) {
            ((GradientDrawable) drawable).setShape(GradientDrawable.RECTANGLE);
            ((GradientDrawable) drawable).setCornerRadius(bgCorner);//设置4个角的弧度
            ((GradientDrawable) drawable).setColor(Color.parseColor(getBgColor()));// 设置颜色
            drawable.setBounds(realBounds);
            drawable.draw(canvas);
        }
        if (isShimmerText) {
            initShimmer(textPaint, resultText);
            canvas.translate(getTextDrawLeftX(), 0);
            startShimmer();
            mStaticLayout.draw(canvas);

        } else {
            stopShimmer();
            canvas.translate(getTextDrawLeftX(), 0);
            textPaint.setShader(null);
            mStaticLayout.draw(canvas);

        }
        canvas.restore();
    }

    /**
     * 根据其对齐方式获取边界的上下左右的四个点的值
     *
     * @param points
     */
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


    /**
     * 获取中心点坐标
     * 当改变了对齐方式 那么需要设置其绘制插件的初始中心点坐标位置
     * 默认是从左到右绘制沿其（0,0）坐标开始绘制  当对齐方式改变
     * 则通过canvas.translate（x,y）的方式平移  比如居中对齐 就要平移中心点到(0,0)
     *
     * @param dst
     */
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

    /**
     * 决定选中的边框的宽度
     * @return
     */
    @Override
    public int getWidth() {
        return realBounds.width();
    }

    /**
     * 决定选中的边框的高度
     * @return
     */
    @Override
    public int getHeight() {
        return realBounds.height();
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

    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.lineSpacingMultiplier = lineSpacingMultiplier;
    }

    public float getLineSpacingMultiplier() {
        return lineSpacingMultiplier;
    }

    public TextSticker copy(){
        TextSticker copySticker = new TextSticker(TimeUtils.currentTimeMillis());
        copySticker.setFontPath(getFontPath());
        copySticker.setAlignment(getAlignment());
        copySticker.setSupportBg(isSupportBg());
        copySticker.setBgColor(getBgColor());
        copySticker.setBgCorner(getBgCorner());
        copySticker.setText(getText());
        copySticker.setTextColor(getTextColor());
        copySticker.setShimmerColor(getShimmerColor());
        copySticker.setShimmerText(isShimmerText());
        copySticker.setDrawable(getDrawable());
        copySticker.setMatrix(getMatrix());
        copySticker.setScaleParam(getScaleParam());
        copySticker.setLetterSpacing(getLetterSpacing());
        copySticker.setLineSpacingMultiplier(getLineSpacingMultiplier());
        return copySticker;
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

    /**
     * 获取StaticLayout的宽度
     *
     * @return
     */
    private int getMaxLineWidth(StaticLayout staticLayout) {
        int lineCount = staticLayout.getLineCount();
        float maxLineLength = 0;
        for (int i = 0; i < lineCount; i++) {
            float lineLength = staticLayout.getLineWidth(i);
            if (lineLength > maxLineLength) {
                maxLineLength = lineLength;
            }
        }
        return (int) maxLineLength;

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
        this.scaleParam = scaleParam;
    }

    public void setAlignment(Layout.Alignment alignment) {
        RectF rectF = getMappedRectF();
        float offsetX = 0;
        float mappedTextLength = rectF.right - rectF.left;
        if (this.mAlignment == Layout.Alignment.ALIGN_NORMAL) {
            if (alignment == Layout.Alignment.ALIGN_CENTER) {
                offsetX = mappedTextLength / 2f;
            } else if (alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                offsetX = mappedTextLength;
            }
        } else if (this.mAlignment == Layout.Alignment.ALIGN_CENTER) {
            if (alignment == Layout.Alignment.ALIGN_NORMAL) {
                offsetX = -mappedTextLength / 2f;
            } else if (alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                offsetX = mappedTextLength / 2f;
            }
        } else if (this.mAlignment == Layout.Alignment.ALIGN_OPPOSITE) {
            if (alignment == Layout.Alignment.ALIGN_NORMAL) {
                offsetX = -mappedTextLength;
            } else if (alignment == Layout.Alignment.ALIGN_CENTER) {
                offsetX = -mappedTextLength / 2f;
            }
        }
        this.mAlignment = alignment;
        getMatrix().postTranslate(offsetX, 0);
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
        setText(bean.getText());
        setJumpAppPath(bean.getJumpAppPath());
        setFontPath(bean.getFontPath());
        setTextColor(bean.getColor());
        setScaleParam((int) bean.getScale());
        setJumpContent(bean.getJumpContent());
        setAppName(bean.getAppName());
        setShimmerColor(bean.getShimmerColor());
        setShimmerText(bean.isShimmerText());
        setAlignment(bean.getAlignment());
        setLetterSpacing(bean.getLetterSpacing());
        setLineSpacingMultiplier(bean.getLineSpacing());
        setShadow(bean.isShadow());
        setShadowColor(bean.getShadowColor());
        setShadowX(bean.getShadowX());
        setShadowY(bean.getShadowY());
        setShadowRadius(bean.getShadowRadius());
        Log.i("test_text_draw:","letter:"+letterSpacing);
        Log.i("test_text_draw:","line:"+lineSpacingMultiplier);
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

    public boolean isSupportBg() {
        return supportBg;
    }

    public void setSupportBg(boolean supportBg) {
        this.supportBg = supportBg;
    }

    public void setBgCorner(int bgCorner) {
        this.bgCorner = bgCorner > 0 ? DEFAULT_BG_CORNER : 0;
    }

    public int getBgCorner() {
        return bgCorner;
    }

    public String getBgColor() {
        return TextUtils.isEmpty(bgColor) ? DEFAULT_BG_COLOR : bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getShimmerColor() {
        return shimmerColor != null ? shimmerColor : "#FFFFFF";
    }

    public void setShimmerColor(String shimmerColor) {
        this.shimmerColor = shimmerColor;
    }

    public boolean isShadow() {
        return isShadow;
    }

    public void setShadow(boolean shadow) {
        isShadow = shadow;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    public float getShadowX() {
        return shadowX;
    }

    public void setShadowX(float shadowX) {
        this.shadowX = shadowX;
    }

    public float getShadowY() {
        return shadowY;
    }

    public void setShadowY(float shadowY) {
        this.shadowY = shadowY;
    }

    public String getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.shadowColor = shadowColor;
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
