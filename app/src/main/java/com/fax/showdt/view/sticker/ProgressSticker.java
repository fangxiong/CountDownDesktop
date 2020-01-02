package com.fax.showdt.view.sticker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.util.DisplayMetrics;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.bean.ProgressPlugBean;
import com.fax.showdt.utils.TimeUtils;
import com.fax.showdt.utils.ViewUtils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.core.content.ContextCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ProgressSticker extends Sticker {

    //圆形进度条
    public static final String CIRCLE = "circle";
    //水平进度条
    public static final String HORIZONTAL = "horizontal";
    //实心进度条
    public static final String SOLID = "solid";
    //刻度进度条
    public static final String DEGREE = "degree";
    private Drawable mDrawable;

    private float percent = 0.5f;
    private String foreColor = "#80FF0000";
    private String bgColor = "#90FFFFFF";
    @ProgressDrawType
    private String drawType = SOLID;
    @ProgressType
    private String progressType=HORIZONTAL;
    private int width = ViewUtils.dp2px(200);
    private int height =ViewUtils.dp2px(20);
    private int progressHeight =ViewUtils.dp2px(20);


    @StringDef({CIRCLE, HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProgressType {
    }

    @StringDef({SOLID, DEGREE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProgressDrawType {
    }

    public ProgressSticker(long id) {
        super(id);

        if (mDrawable == null) {
            this.mDrawable = ContextCompat.getDrawable(
                    AppContext.get(),
                    R.drawable.sticker_transparent_background
            );
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas, int index, boolean showNumber) {
        canvas.save();
        canvas.concat(getMatrix());
        ProgressStickerDrawHelper.Builder builder = new ProgressStickerDrawHelper.Builder();
        builder.setWidth(width).setHeight(height).setProgressHeight(progressHeight).setDrawType(drawType).setProgressType(progressType).setPercent(percent).setProgressBgColor(bgColor).setProgressForeColor(foreColor).build();
        ProgressStickerDrawHelper.drawProgressBar(builder, canvas);
        canvas.restore();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
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

    public void setScale(float scale) {
        getMatrix().reset();
        PointF pointF = this.getMappedCenterPoint();
        getMatrix().postScale(scale, scale, pointF.x, pointF.y);
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public String getForeColor() {
        return foreColor;
    }

    public void setForeColor(String foreColor) {
        this.foreColor = foreColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setProgressHeight(int progressHeight) {
        this.progressHeight = progressHeight;
    }

    public int getProgressHeight() {
        return progressHeight;
    }

    public String getDrawType() {
        return drawType;
    }

    public void setDrawType(String drawType) {
        this.drawType = drawType;
    }

    public String getProgressType() {
        return progressType;
    }

    public void setProgressType(String progressType) {
        this.progressType = progressType;
    }

    public void resize(int width,int height){
        this.width = width;
        this.height = height;

    }

    public void setStickerConfig(ProgressPlugBean bean) {
        setId(Long.valueOf(bean.getId()));
        setProgressHeight(bean.getProgressHeight());
        setBgColor(bean.getBgColor());
        setForeColor(bean.getForeColor());
        setDrawType(bean.getDrawType());
        setProgressType(bean.getProgressType());
        setPercent(bean.getPercent());
    }


    @Override
    public void release() {
        super.release();
        if (mDrawable != null) {
            mDrawable = null;
        }
    }
}
