package com.fax.showdt.view.sticker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.bean.ProgressPlugBean;
import com.fax.showdt.utils.TimeUtils;
import com.fax.showdt.utils.ViewUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


/**
 * Created by showzeng on 2019-09-02.
 * email: kingstageshow@gmail.com
 *
 * description: 进度条 sticker
 */
public class ProgressSticker extends Sticker {

    private String color = "#5990FF";
    private float progressLength;
    private final int defaultRectHeight = ViewUtils.dp2px(20);
    private float mScreenHeight;
    private float mProgressRatio = 0.25f;
    private float mProgressScale = 1.0f;
    private float progress;
    private long startDateTimeMillis;
    private long targetDateTimeMillis;

    private Paint mPaint;

    private Drawable mDrawable;
    private StickerDrawHelper mStickerDrawHelper;

    private int mProgressId = 0;

    public ProgressSticker(long id) {
        super(id);
        mPaint = new Paint();
        mStickerDrawHelper = new StickerDrawHelper();

        if (mDrawable == null) {
            this.mDrawable = ContextCompat.getDrawable(
                AppContext.get(),
                R.drawable.sticker_transparent_background
            );
        }

        DisplayMetrics dm = AppContext.get().getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels;
        progressLength = mScreenHeight * mProgressRatio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(@NonNull String color) {
        this.color = color;
    }

    public int getProgressId() {
        return mProgressId;
    }

    public void setProgressId(int progressId) {
        mProgressId = progressId;
    }

    public float getProgressLength() {
        return progressLength;
    }

    public void setProgressLength(float progressLength) {
        this.progressLength = progressLength;
    }

    public float getProgressRatio() {
        return mProgressRatio;
    }

    public void setProgressRatio(float progressRatio) {
        mProgressRatio = progressRatio;
    }

    public float getProgressScale() {
        return mProgressScale;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public long getStartDateTimeMillis() {
        return startDateTimeMillis;
    }

    public void setStartDateTimeMillis(long startDateTimeMillis) {
        this.startDateTimeMillis = startDateTimeMillis;
    }

    public long getTargetDateTimeMillis() {
        return targetDateTimeMillis;
    }

    public void setTargetDateTimeMillis(long targetDateTimeMillis) {
        this.targetDateTimeMillis = targetDateTimeMillis;
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
            Rect realBounds = new Rect(0, 0, (int) getProgressLength(), defaultRectHeight);
            mDrawable.setBounds(realBounds);
            mDrawable.draw(canvas);
        }

        progress = ((float) System.currentTimeMillis() - (float) TimeUtils.getYearStartTime())
            / ((float) targetDateTimeMillis - (float) TimeUtils.getYearStartTime());
        mStickerDrawHelper.drawProgress(mProgressId, canvas, mPaint, defaultRectHeight, (int) getProgressLength(), progress);
        canvas.restore();
    }

    @Override
    public int getWidth() {
        return (int) progressLength;
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

    public void setStickerConfig(ProgressPlugBean bean) {
        setId(Long.valueOf(bean.getId()));
        setColor(bean.getColor());
        setProgressId(bean.getProgressId());
        setStartDateTimeMillis(bean.getStartTime());
        setTargetDateTimeMillis(bean.getTargetTime());
    }


    @Override
    public void release() {
        super.release();
        if (mDrawable != null) {
            mDrawable = null;
        }
    }
}
