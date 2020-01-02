package com.fax.showdt.view.sticker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import com.fax.showdt.bean.ProgressBarDrawConfig;
import com.fax.showdt.utils.ViewUtils;


/**
 * Created by fax on 19-6-12.
 */
public class ProgressStickerDrawHelper {

    public static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public static float LINE_DEFAULT_HEIGHT = ViewUtils.dp2px(5f);
    public static final float LINE_DEFAULT_WIDTH = 3f;
    public static final float LINE_GAP = ViewUtils.dp2px(2f);
    public static int TOTAL_LINE_COUNT;
    public static final int TOTAL_CIRCLE_DEGREE_COUNT = 100;
    public static final int CIRCLE_PROGRESS_START_ANGLE = -90;

    /**
     * 绘制进度条
     *
     * @param builder
     * @param canvas
     */
    public static void drawProgressBar(Builder builder, Canvas canvas) {
        ProgressBarDrawConfig config = builder.build();
        @ProgressSticker.ProgressType String progressType = config.getProgressType();
        @ProgressSticker.ProgressDrawType String drawType = config.getDrawType();
        if (progressType.equals(ProgressSticker.CIRCLE)) {
            if (drawType.equals(ProgressSticker.SOLID)) {
                drawCircleProgressBarWithSolid(canvas, config);
            } else if (drawType.equals(ProgressSticker.DEGREE)) {
                drawCircleProgressBarWithDegree(canvas, config);
            }
        } else if (progressType.equals(ProgressSticker.HORIZONTAL)) {
            if (drawType.equals(ProgressSticker.SOLID)) {
                drawHorizontalProgressBarWithSolid(canvas, config);

            } else if (drawType.equals(ProgressSticker.DEGREE)) {
                drawHorizontalProgressBarWithDegree(canvas, config);
            }
        }
    }

    /**
     * 绘制水平实心进度条
     *
     * @param canvas
     * @param config
     */
    public static void drawHorizontalProgressBarWithSolid(Canvas canvas, ProgressBarDrawConfig config) {
        paint.setStrokeWidth(config.getHeight());
        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setDither(true);
        paint.setColor(Color.parseColor(config.getProgressBgColor()));
        canvas.drawLine(0, config.getHeight() / 2, config.getWidth(), config.getHeight() / 2, paint);
        paint.setColor(Color.parseColor(config.getProgressForeColor()));
        canvas.drawLine(0, config.getHeight() / 2, config.getWidth() * config.getPercent(), config.getHeight() / 2, paint);
    }

    /**
     * 绘制水平刻度进度条
     *
     * @param canvas
     * @param config
     */
    public static void drawHorizontalProgressBarWithDegree(Canvas canvas, ProgressBarDrawConfig config) {

        Paint linePaint = new Paint();
        TOTAL_LINE_COUNT = (int) ((config.getWidth() + LINE_GAP) / (LINE_DEFAULT_WIDTH + LINE_GAP));
        int foreUnitCount = (int) (config.getPercent() * TOTAL_LINE_COUNT);
        Log.i("test_line_count:", TOTAL_LINE_COUNT + "");
        Log.i("test_line_count:", foreUnitCount + "");
        LINE_DEFAULT_HEIGHT = config.getHeight();
        linePaint.setColor(Color.parseColor(config.getProgressForeColor()));
        for (int i = 0; i < TOTAL_LINE_COUNT; i++) {
            float startX = 0;
            if (i < foreUnitCount) {
                startX = (2 * i + 1) * LINE_DEFAULT_WIDTH / 2 + LINE_GAP * i;
                drawSingleLine(canvas, linePaint, startX);
            }
        }
        linePaint.setColor(Color.parseColor(config.getProgressBgColor()));
        for (int i = foreUnitCount; i < TOTAL_LINE_COUNT; i++) {
            float startX = 0;
            startX = (2 * i + 1) * LINE_DEFAULT_WIDTH / 2 + LINE_GAP * i;
            drawSingleLine(canvas, linePaint, startX);
        }


    }

    /**
     * 绘制圆形实心进度条
     *
     * @param canvas
     * @param config
     */
    public static void drawCircleProgressBarWithSolid(Canvas canvas, ProgressBarDrawConfig config) {
        paint.setStrokeWidth(config.getProgressHeight());
        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setDither(true);
        paint.setColor(Color.parseColor(config.getProgressBgColor()));
        RectF rectF = new RectF(0+config.getProgressHeight()/2,0+config.getProgressHeight()/2,config.getHeight()-config.getProgressHeight()/2,config.getHeight()-config.getProgressHeight()/2);
        canvas.drawArc(rectF,0,360,false,paint);
        paint.setColor(Color.parseColor(config.getProgressForeColor()));
        canvas.drawArc(rectF,-90,360*config.getPercent(),false,paint);
    }

    /**
     * 绘制圆心刻度进度条
     *
     * @param canvas
     * @param config
     */
    public static void drawCircleProgressBarWithDegree(Canvas canvas, ProgressBarDrawConfig config) {
        paint.setStrokeWidth(config.getProgressHeight());
        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setDither(true);
        paint.setColor(Color.parseColor(config.getProgressForeColor()));
        RectF rectF = new RectF(0+config.getProgressHeight()/2,0+config.getProgressHeight()/2,config.getHeight()-config.getProgressHeight()/2,config.getHeight()-config.getProgressHeight()/2);
        int foreUnitCount = (int)(TOTAL_CIRCLE_DEGREE_COUNT*config.getPercent());
        for(int i=0 ;i<foreUnitCount;i++){
            canvas.drawArc(rectF,CIRCLE_PROGRESS_START_ANGLE+i*360f/TOTAL_CIRCLE_DEGREE_COUNT,1.8f,false,paint);
        }
        paint.setColor(Color.parseColor(config.getProgressBgColor()));
        for(int i=foreUnitCount ;i<TOTAL_CIRCLE_DEGREE_COUNT;i++){
            canvas.drawArc(rectF,CIRCLE_PROGRESS_START_ANGLE+i*360f/TOTAL_CIRCLE_DEGREE_COUNT,1.8f,false,paint);
        }

    }

    public static void drawSingleLine(Canvas canvas, Paint paint, float startX) {
        paint.setStrokeWidth(LINE_DEFAULT_WIDTH);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawLine(startX, 0, startX, LINE_DEFAULT_HEIGHT, paint);
    }

    public static class Builder {
        private float percent;
        private float width;
        private float height;
        private float progressHeight;
        private String progressBgColor;
        private String progressForeColor;
        @ProgressSticker.ProgressDrawType
        private String drawType;
        @ProgressSticker.ProgressType
        private String progressType;

        public ProgressBarDrawConfig build() {
            ProgressBarDrawConfig config = new ProgressBarDrawConfig();
            config.setPercent(percent);
            config.setWidth(width);
            config.setHeight(height);
            config.setProgressHeight(progressHeight);
            config.setProgressBgColor(progressBgColor);
            config.setProgressForeColor(progressForeColor);
            config.setDrawType(drawType);
            config.setProgressType(progressType);
            return config;
        }

        public Builder setPercent(float percent) {
            this.percent = percent;
            return this;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(float height) {
            this.height = height;
            return this;
        }

        public Builder setProgressHeight(float progressHeight) {
            this.progressHeight = progressHeight;
            return this;
        }

        public Builder setProgressBgColor(String progressBgColor) {
            this.progressBgColor = progressBgColor;
            return this;
        }

        public Builder setProgressForeColor(String progressForeColor) {
            this.progressForeColor = progressForeColor;
            return this;
        }

        public Builder setDrawType(@ProgressSticker.ProgressDrawType String drawType) {
            this.drawType = drawType;
            return this;
        }

        public Builder setProgressType(@ProgressSticker.ProgressType String progressType) {
            this.progressType = progressType;
            return this;
        }
    }

}