package com.fax.showdt.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.fax.showdt.AppContext;
import com.fax.showdt.utils.ViewUtils;

public class ShadowDrawable extends Drawable {

    private Paint mShadowPaint;
    private Paint mBgPaint;
    private int mShapeRadius;
    private int mShadowRadius;
    private int mOffsetX;
    private int mOffsetY;
    private int mStrokeWidth;
    private boolean isStroke;
    private int[] mBgColor;
    private RectF mRect;
    private int mShadowColor;


    public ShadowDrawable() {

        this.mBgColor = new int[]{Color.WHITE};
        this.mShapeRadius = 0;
        this.mShadowRadius = 0;
        this.mStrokeWidth = ViewUtils.dpToPx(3, AppContext.get());
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mShadowColor = Color.BLACK;
        this.isStroke = false;

        mShadowPaint = new Paint();
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
    }

    public void setColors(int[] color){
        this.mBgColor = color;
    }

    public void setShapeRadius(int shapeRadius){
        this.mShapeRadius = shapeRadius;
    }

    public void setShadowColor(int shadowColor){
        this.mShadowColor = shadowColor;
    }

    public void setOffsetX(int offsetX){
        this.mOffsetX = offsetX;
    }

    public void setOffsetY(int offsetY){
        this.mOffsetY = offsetY;
    }

    public void setStrokeWidth(int mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
    }

    public void setStroke(boolean stroke) {
        isStroke = stroke;
    }

    public void setmShadowRadius(int mShadowRadius) {
        this.mShadowRadius = mShadowRadius;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mRect = new RectF(left + mShapeRadius - mOffsetX, top + mShapeRadius - mOffsetY, right - mShapeRadius - mOffsetX,
                bottom - mShapeRadius - mOffsetY);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mBgColor != null) {
            if (mBgColor.length == 1) {
                mBgPaint.setColor(mBgColor[0]);
            } else {
                mBgPaint.setShader(new LinearGradient(mRect.left, mRect.height() / 2, mRect.right,
                        mRect.height() / 2, mBgColor, null, Shader.TileMode.CLAMP));
            }
        }
        if(isStroke){
            mShadowPaint.setStyle(Paint.Style.STROKE);
            mBgPaint.setStyle(Paint.Style.STROKE);
            mShadowPaint.setStrokeWidth(mStrokeWidth);
            mBgPaint.setStrokeWidth(mStrokeWidth);
        }
        mShadowPaint.setColor(Color.TRANSPARENT);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        canvas.drawRoundRect(mRect, mShapeRadius, mShapeRadius, mShadowPaint);
        canvas.drawRoundRect(mRect, mShapeRadius, mShapeRadius, mBgPaint);

    }

    @Override
    public void setAlpha(int alpha) {
        mShadowPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mShadowPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
