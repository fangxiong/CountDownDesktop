package com.fax.showdt.view.sticker;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.bean.ProgressPlugBean;
import com.fax.showdt.manager.widget.WidgetProgressPercentHandler;
import com.fax.showdt.utils.ViewUtils;

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

    public static final String BATTERY = "battery";
    public static final String MUSIC = "music";
    public static final String MONTH = "month";
    public static final String WEEK = "week";
    public static final String DAY = "day";
    public static final String HOUR = "hour";



    private Drawable mDrawable;

    private float percent = 0.5f;
    private String foreColor = "#80FF0000";
    private String bgColor = "#90FFFFFF";
    @ProgressDrawType
    private String drawType = SOLID;
    @ProgressType
    private String progressType = HORIZONTAL;
    @Progress
    private String progress = BATTERY;
    private int width = ViewUtils.dpToPx(150, AppContext.get());
    private int height = ViewUtils.dpToPx(10, AppContext.get());
    private int progressHeight = ViewUtils.dpToPx(10, AppContext.get());


    @StringDef({CIRCLE, HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProgressType {
    }

    @StringDef({SOLID, DEGREE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProgressDrawType {
    }

    @StringDef({BATTERY, MUSIC,MONTH,WEEK,DAY,HOUR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Progress {
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
        setPercent(WidgetProgressPercentHandler.getProgressPercent(progress));
        Log.i("test_percent:",getPercent()+"");
        builder.setWidth(width).setHeight(height).setProgressHeight(progressHeight).setDrawType(drawType).setProgressType(progressType).setPercent(percent).setProgressBgColor(bgColor).setProgressForeColor(foreColor).build();
        ProgressStickerDrawHelper.drawProgressBar(builder, canvas);
        canvas.restore();
    }

    @Override
    public int getWidth() {
        return width ;
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
        Log.i("ProgressSticker:","scale:"+scale);
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

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getProgress() {
        return progress;
    }

    public void resize(int width, int height) {
        if (width < ViewUtils.dpToPx(10, AppContext.get())) {
            this.width = ViewUtils.dpToPx(10, AppContext.get());
        } else {
            this.width = width;
        }
        this.height = height;
    }

    public void setStickerConfig(ProgressPlugBean bean) {
        setId(Long.valueOf(bean.getId()));
        setScale(bean.getScale());
        setProgressHeight(bean.getProgressHeight());
        setBgColor(bean.getBgColor());
        setForeColor(bean.getForeColor());
        setDrawType(bean.getDrawType());
        setProgressType(bean.getProgressType());
        setProgress(bean.getProgress());
    }


    @Override
    public void release() {
        super.release();
        if (mDrawable != null) {
            mDrawable = null;
        }
    }
}
