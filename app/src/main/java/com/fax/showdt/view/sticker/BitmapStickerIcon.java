package com.fax.showdt.view.sticker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.fax.showdt.utils.ViewUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

public class BitmapStickerIcon extends DrawableSticker implements StickerIconEvent {
    public static final float DEFAULT_ICON_RADIUS = 30f;
    public static final float DEFAULT_ICON_EXTRA_RADIUS = 10f;

    @IntDef({LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Gravity {

    }

    public static final int LEFT_TOP = 0;
    public static final int RIGHT_TOP = 1;
    public static final int LEFT_BOTTOM = 2;
    public static final int RIGHT_BOTTOM = 3;

    private float iconRadius = DEFAULT_ICON_RADIUS;
    private float iconExtraRadius = DEFAULT_ICON_EXTRA_RADIUS;
    private float x;
    private float y;
    private String tag;
    @Gravity
    private int position = LEFT_TOP;

    private StickerIconEvent iconEvent;


    public BitmapStickerIcon(Drawable drawable, @Gravity int gravity) {
        super(drawable, 0, ViewUtils.dp2px(20));
        this.position = gravity;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, ViewUtils.dp2px(12), paint);
        super.draw(canvas, 0, false);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getIconRadius() {
        return iconRadius;
    }

    public void setIconRadius(float iconRadius) {
        this.iconRadius = iconRadius;
    }

    public float getIconExtraRadius() {
        return iconExtraRadius;
    }

    public void setIconExtraRadius(float iconExtraRadius) {
        this.iconExtraRadius = iconExtraRadius;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public DrawableSticker setDrawable(@NonNull Drawable drawable) {
        DrawableSticker drawableSticker = super.setDrawable(drawable);
        if (getWidth() > 60) {
            float ratio = 60 * 1.0f / getWidth();
            PointF pointF = new PointF();
            getCenterPoint(pointF);
            getMatrix().reset();
            getMatrix().postTranslate(x - pointF.x , y - pointF.y);
            getMatrix().postScale(ratio, ratio, pointF.x, pointF.y);
        }
        return drawableSticker;
    }

    @Override
    public void onActionDown(StickerView stickerView, MotionEvent event) {
        if (iconEvent != null) {
            iconEvent.onActionDown(stickerView, event);
        }
    }

    @Override
    public void onActionMove(StickerView stickerView, MotionEvent event) {
        if (iconEvent != null) {
            iconEvent.onActionMove(stickerView, event);
        }
    }

    @Override
    public void onActionUp(StickerView stickerView, MotionEvent event) {
        if (iconEvent != null) {
            iconEvent.onActionUp(stickerView, event);
        }
    }

    public StickerIconEvent getIconEvent() {
        return iconEvent;
    }

    public void setIconEvent(StickerIconEvent iconEvent) {
        this.iconEvent = iconEvent;
    }

    @Gravity
    public int getPosition() {
        return position;
    }

    public void setPosition(@Gravity int position) {
        this.position = position;
    }
}
