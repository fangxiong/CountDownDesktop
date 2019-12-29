package com.fax.showdt.view.sticker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.bean.LinePlugBean;
import com.fax.showdt.utils.ViewUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * @author fax
 */
public class LineSticker extends Sticker {

    private String color = "#000000";
    private float lineLength;
    private final int defaultRectHeight = ViewUtils.dp2px(20);
    private Paint mPaint;
    private Drawable mDrawable;
    private float mLineRatio = 0.25f;
    private float mLineScale =1.0f;
    private float mScreenHeight;
    private LineOrientation mLineOrientation;
    private StickerDrawHelper mStickerDrawHelper;
    private int mLineId = 0;


    public enum LineOrientation {
        VERTICAL, HORIZATIONAL
    }

    public LineSticker(long id) {
        super(id);
        mPaint = new Paint();
        mStickerDrawHelper = new StickerDrawHelper();
        if (mDrawable == null) {
            this.mDrawable = ContextCompat.getDrawable(AppContext.get(), R.drawable.sticker_transparent_background);
        }
        DisplayMetrics dm = AppContext.get().getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels;
        lineLength = mScreenHeight * mLineRatio;
        mLineOrientation = LineOrientation.HORIZATIONAL;
    }

    public void setColor(@NonNull String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    @Override
    public void draw(@NonNull Canvas canvas, int index, boolean showNumber) {
        mPaint.setColor(Color.parseColor(color));
        mPaint.setStrokeWidth(ViewUtils.dp2px(0.5f));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        canvas.save();
        canvas.concat(getMatrix());
        if (mDrawable != null) {
            Rect realBounds = new Rect(0, 0, (int) getLineLength(), defaultRectHeight);
            mDrawable.setBounds(realBounds);
            mDrawable.draw(canvas);
        }
        mStickerDrawHelper.drawLine(mLineId,canvas, mPaint, defaultRectHeight, (int) getLineLength());
        canvas.restore();
    }

    public void setLineOrientation(LineOrientation lineOrientation) {
        PointF midPoint = this.getMappedCenterPoint();
        if (this.mLineOrientation == lineOrientation) {
            return;
        }
        if (lineOrientation == LineOrientation.HORIZATIONAL) {
            getMatrix().postRotate(-90, midPoint.x, midPoint.y);
        } else {
            getMatrix().postRotate(90, midPoint.x, midPoint.y);
        }
        this.mLineOrientation = lineOrientation;
    }

    public LineOrientation getLineOrientation() {
        return mLineOrientation;
    }

    public void setLineLength(float length) {
        lineLength = length;
    }

    public float getLineLength() {
        return lineLength;
    }

    public float getLineRatio() {
        return mLineRatio;
    }

    public void setLineRatio(float lineRatio) {
        setLineLength(mScreenHeight * lineRatio);
        mLineRatio = lineRatio;
    }


    public float getLineScale(){
        return mLineScale;
    }


    @Override
    public int getWidth() {
        return (int) lineLength;
    }

    @Override
    public int getHeight() {
        return defaultRectHeight;
    }

    @Override
    public Sticker setDrawable(@NonNull Drawable drawable) {
        this.mDrawable = drawable;
        return this;
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return mDrawable;
    }

    @NonNull
    @Override
    public Sticker setAlpha(int alpha) {
        return this;
    }

    public int getLineId() {
        return mLineId;
    }

    public void setLineId(int lineId) {
        mLineId = lineId;
    }

    public void setStickerConfig(LinePlugBean bean) {
        setId(Long.valueOf(bean.getId()));
        setColor(bean.getColor());
        setJumpContent(bean.getJumpContent());
        setLineId(bean.getLineId());
        LineOrientation mLineOrientation = bean.getStyle() == 1 ? LineOrientation.VERTICAL : LineOrientation.HORIZATIONAL;
        if (mLineOrientation == LineOrientation.VERTICAL) {
            setLineOrientation(mLineOrientation);
        }
    }

    @Override
    public void release() {
        super.release();
        if (mDrawable != null) {
            mDrawable = null;
        }
    }
}
