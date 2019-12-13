package com.fax.showdt.view.sticker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.utils.ViewUtils;


/**
 * Created by fax on 19-6-12.
 */
public class StickerDrawHelper {


    public StickerDrawHelper() {
    }

    public final float WAVE_LINE_UNIT_WIDTH = ViewUtils.dp2px(2);
    public final float NORMAL_WAVE_LINE_UNIT_WIDTH = ViewUtils.dp2px(3);
    public final float DOTTED_LINE_UNIT_LINE_WIDTH = ViewUtils.dp2px(5);
    public final float DOTTED_LINE_UNIT_GAP_WIDTH = ViewUtils.dp2px(3);
    public final float DOTTED_LINE_POINT_WIDTH = ViewUtils.dp2px(1);
    public final float DOUBLE_LINE_GAP = ViewUtils.dp2px(3);
    public final float ENDPOINT_WIDTH = ViewUtils.dp2px(5);
    public final float LINE_DEFAULT_HEIGHT = ViewUtils.dp2px(0.7f);
    public final float LINE_BOLD_HEIGHT = ViewUtils.dp2px(2.0F);

    /**
     * 默认进度条笔触宽度
     */
    private final float NORMAL_PROGRESS_STROKE_WIDTH = ViewUtils.dp2px(2.0F);

    /**
     * 点型进度条笔触宽度，每个单元占据宽度
     */
    private final float DOTTED_PROGRESS_STROKE_WIDTH = ViewUtils.dp2px(3.0F);
    private final float DOTTED_PROGRESS_UNIT_WIDTH = ViewUtils.dp2px(5.0F);

    /**
     * 箭头型进度条笔触宽度，箭头形状宽度/高度，每个单元占据宽度
     */
    private final float ARROW_PROGRESS_STROKE_WIDTH = ViewUtils.dp2px(1.0F);
    private final float ARROW_PROGRESS_SHAPE_WIDTH = ViewUtils.dp2px(4.0F);
    private final float ARROW_PROGRESS_SHAPE_HEIGHT = ViewUtils.dp2px(8.0F);
    private final float ARROW_PROGRESS_UNIT_WIDTH = ViewUtils.dp2px(6.0F);

    /**
     * 爱心型进度条形状宽度/高度，每个单元占据宽度
     */
    private final float LOVE_PROGRESS_SHAPE_WIDTH = ViewUtils.dp2px(8.0F);
    private final float LOVE_PROGRESS_SHAPE_HEIGHT = ViewUtils.dp2px(6.0F);
    private final float LOVE_PROGRESS_UNIT_WDITH = ViewUtils.dp2px(9.0F);

    /**
     * 线条型进度条笔触宽度，线条形状高度，每个单元占据宽度
     */
    private final float LINE_PROGRESS_STROKE_WIDTH = ViewUtils.dp2px(1.0F);
    private final float LINE_PROGRESS_SHAPE_HEIGHT = ViewUtils.dp2px(9.0F);
    private final float LINE_PROGRESS_UNIT_WIDTH = ViewUtils.dp2px(4.0F);


    public void drawLine(int lineId, Canvas canvas, Paint paint, float rectHeight, float rectWidth) {
        switch (lineId) {
            case 0: {
                drawSingleLine(canvas, paint, rectHeight, rectWidth);
                break;
            }
            case 1: {
                drawDottedLine(canvas, paint, rectHeight, rectWidth);
                break;
            }
            case 2: {
                drawSingleLineWithBold(canvas, paint, rectHeight, rectWidth);
                break;
            }
            case 3: {
                drawLineWithCirclePoint(canvas, paint, rectHeight, rectWidth, false);
                break;
            }
            case 4: {
                drawDoubleLine(canvas, paint, rectHeight, rectWidth);
                break;
            }
            case 5: {
                drawLineWithCirclePoint(canvas, paint, rectHeight, rectWidth, true);
                break;
            }
            case 6: {
                drawDottedLineWithPoint(canvas, paint, rectHeight, rectWidth);
                break;
            }
            case 7: {
                drawLineWithRectanglePoint(canvas, paint, rectHeight, rectWidth, false);
                break;
            }
            case 8: {
                drawSineWaveLine(canvas, paint, rectHeight, rectWidth);
                break;
            }
            case 9: {
                drawLineWithRectanglePoint(canvas, paint, rectHeight, rectWidth, true);
                break;
            }
            case 10: {
                drawNormalWaveLine(canvas, paint, rectHeight, rectWidth);
                break;
            }
        }
    }

    /**
     * 绘制正弦波浪线
     *
     * @param canvas
     * @param paint
     * @param rectHeight
     * @param rectWidth
     */
    public void drawSineWaveLine(Canvas canvas, Paint paint, float rectHeight, float rectWidth) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_DEFAULT_HEIGHT);
        Path path = new Path();
        path.moveTo(0f, rectHeight / 2);
        int unitCount = (int) Math.ceil(rectWidth / WAVE_LINE_UNIT_WIDTH);
        drawSineWaveLineUnit(unitCount, path, rectHeight / 2);
        canvas.clipRect(0, 0, rectWidth, rectHeight, Region.Op.INTERSECT);
        canvas.drawPath(path, paint);
    }


    /**
     * 绘制普通波浪线
     *
     * @param canvas
     * @param paint
     * @param rectHeight
     * @param rectWidth
     */
    public void drawNormalWaveLine(Canvas canvas, Paint paint, float rectHeight, float rectWidth) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_DEFAULT_HEIGHT);
        Path path = new Path();
        path.moveTo(0f, rectHeight / 2);
        int unitCount = (int) Math.ceil(rectWidth / WAVE_LINE_UNIT_WIDTH);
        drawNormalWaveLineUnit(unitCount, path, rectHeight / 2);
        canvas.clipRect(0, 0, rectWidth, rectHeight, Region.Op.INTERSECT);
        canvas.drawPath(path, paint);
    }


    /**
     * 绘制虚线
     *
     * @param canvas
     * @param paint
     * @param rectHeight
     * @param rectWidth
     */
    public void drawDottedLine(Canvas canvas, Paint paint, float rectHeight, float rectWidth) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_DEFAULT_HEIGHT);
        paint.setPathEffect(new DashPathEffect(new float[]{DOTTED_LINE_UNIT_LINE_WIDTH, DOTTED_LINE_UNIT_GAP_WIDTH}, 0));
        Path path = new Path();
        path.moveTo(0, rectHeight / 2);
        path.lineTo(rectWidth, rectHeight / 2);
        canvas.drawPath(path, paint);
        paint.setPathEffect(null);
    }

    /**
     * 绘制由圆点组成的虚线
     *
     * @param canvas
     * @param paint
     * @param rectHeight
     * @param rectWidth
     */
    public void drawDottedLineWithPoint(Canvas canvas, Paint paint, float rectHeight, float rectWidth) {
        paint.setStrokeWidth(DOTTED_LINE_POINT_WIDTH / 2);
        paint.setStyle(Paint.Style.FILL);
        canvas.clipRect(0, 0, rectWidth, rectHeight, Region.Op.INTERSECT);
        int unitCount = (int) Math.ceil(rectWidth / (DOTTED_LINE_POINT_WIDTH * 2));
        for (int i = 1; i <= unitCount; i++) {
            canvas.drawCircle((2 * i - 3 * 1.0f / 2) * DOTTED_LINE_POINT_WIDTH, rectHeight / 2, DOTTED_LINE_POINT_WIDTH / 2, paint);
        }
        paint.setPathEffect(null);
    }

    /**
     * 绘制双线
     *
     * @param canvas
     * @param paint
     * @param rectHeight
     * @param rectWidth
     */
    public void drawDoubleLine(Canvas canvas, Paint paint, float rectHeight, float rectWidth) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_DEFAULT_HEIGHT);
        float lineHeight = paint.getStrokeWidth();
        float firstLineY = (rectHeight - DOUBLE_LINE_GAP) / 2 + lineHeight / 2;
        float secondLineY = rectHeight - (rectHeight - DOUBLE_LINE_GAP) / 2 - lineHeight / 2;
        canvas.drawLine(0f, firstLineY, rectWidth, firstLineY, paint);
        canvas.drawLine(0f, secondLineY, rectWidth, secondLineY, paint);


    }


    /**
     * 绘制单线
     *
     * @param canvas
     * @param paint
     * @param rectHeight
     * @param rectWidth
     */
    public void drawSingleLine(Canvas canvas, Paint paint, float rectHeight, float rectWidth) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_DEFAULT_HEIGHT);
        canvas.drawLine(0f, rectHeight / 2, rectWidth, rectHeight / 2, paint);
    }

    /**
     * 绘制单线(粗)
     *
     * @param canvas
     * @param paint
     * @param rectHeight
     * @param rectWidth
     */
    public void drawSingleLineWithBold(Canvas canvas, Paint paint, float rectHeight, float rectWidth) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_BOLD_HEIGHT);
        canvas.drawLine(0f, rectHeight / 2, rectWidth, rectHeight / 2, paint);
    }

    /**
     * 绘制带有圆形端点的线段
     *
     * @param canvas
     * @param paint
     * @param rectHeight
     * @param rectWidth
     * @param isFull     是否是实心
     */
    public void drawLineWithCirclePoint(Canvas canvas, Paint paint, float rectHeight, float rectWidth, boolean isFull) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_DEFAULT_HEIGHT);
        canvas.drawLine(ENDPOINT_WIDTH, rectHeight / 2, rectWidth - ENDPOINT_WIDTH, rectHeight / 2, paint);
        if (isFull) {
            paint.setStyle(Paint.Style.FILL);
        }
        paint.setStrokeWidth(LINE_DEFAULT_HEIGHT);
        canvas.drawCircle(ENDPOINT_WIDTH / 2, rectHeight / 2, ENDPOINT_WIDTH / 2, paint);
        canvas.drawCircle(rectWidth - ENDPOINT_WIDTH / 2, rectHeight / 2, ENDPOINT_WIDTH / 2, paint);
    }

    /**
     * 绘制带有矩形端点的线段
     *
     * @param canvas
     * @param paint
     * @param rectHeight
     * @param rectWidth
     * @param isFull     是否是实心
     */
    public void drawLineWithRectanglePoint(Canvas canvas, Paint paint, float rectHeight, float rectWidth, boolean isFull) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_DEFAULT_HEIGHT);
        canvas.drawLine(ENDPOINT_WIDTH, rectHeight / 2, rectWidth - ENDPOINT_WIDTH, rectHeight / 2, paint);
        if (isFull) {
            paint.setStyle(Paint.Style.FILL);
        }
        paint.setStrokeWidth(LINE_DEFAULT_HEIGHT);
        canvas.drawRect(0, (rectHeight - ENDPOINT_WIDTH) / 2, ENDPOINT_WIDTH, (rectHeight - ENDPOINT_WIDTH) / 2 + ENDPOINT_WIDTH, paint);
        canvas.drawRect(rectWidth - ENDPOINT_WIDTH, (rectHeight - ENDPOINT_WIDTH) / 2, rectWidth, (rectHeight - ENDPOINT_WIDTH) / 2 + ENDPOINT_WIDTH, paint);
    }


    /**
     * 绘制正弦波浪线的每个小单元
     *
     * @param unitCount
     * @param path
     * @param unitHeight
     */
    public void drawSineWaveLineUnit(int unitCount, Path path, float unitHeight) {
        for (int i = 1; i <= unitCount; i++) {
            path.quadTo(WAVE_LINE_UNIT_WIDTH * (2 * i - 3 * 1.0f / 2), unitHeight / 2, WAVE_LINE_UNIT_WIDTH * (2 * i - 1), unitHeight);
            path.quadTo(WAVE_LINE_UNIT_WIDTH * (2 * i - 1.0f / 2), unitHeight * 3 / 2, WAVE_LINE_UNIT_WIDTH * 2 * i, unitHeight);
        }
    }

    /**
     * 绘制普通波浪线的每个小单元
     *
     * @param unitCount
     * @param path
     * @param unitHeight
     */
    public void drawNormalWaveLineUnit(int unitCount, Path path, float unitHeight) {
        for (int i = 1; i <= unitCount; i++) {
            path.quadTo(NORMAL_WAVE_LINE_UNIT_WIDTH * (2 * i - 3 * 1.0f / 2), unitHeight / 2, NORMAL_WAVE_LINE_UNIT_WIDTH * (2 * i - 1), unitHeight);
            path.quadTo(NORMAL_WAVE_LINE_UNIT_WIDTH * (2 * i - 1.0f / 2), unitHeight / 2, NORMAL_WAVE_LINE_UNIT_WIDTH * 2 * i, unitHeight);
        }
    }

    /**
     * 绘制进度条
     *
     * @param progressId 进度条样式 id
     * @param canvas 进度条绘制 canvas
     * @param paint 进度条绘制 paint
     * @param rectHeight 进度条绘制高度
     * @param rectWidth 进度条绘制宽度
     */
    public void drawProgress(int progressId, Canvas canvas, Paint paint, float rectHeight, float rectWidth, float progress) {

        // 进度条未填满的颜色，为所用绘制颜色透明度设置为 40% (#66)
        String fadeColor = String.format("#%06X", 0xFFFFFF & paint.getColor()).replace("#", "#66");

        switch (progressId) {
            case 0: {
                drawNormalProgress(canvas, paint, rectHeight, rectWidth, progress, fadeColor);
                break;
            }
            case 1: {
                drawDottedProgress(canvas, paint, rectHeight, rectWidth, progress, fadeColor);
                break;
            }
            case 2: {
                drawArrowProgress(canvas, paint, rectHeight, rectWidth, progress, fadeColor);
                break;
            }
            case 3: {
                drawLoveProgress(canvas, paint, rectHeight, rectWidth, progress, fadeColor);
                break;
            }
            case 4: {
                drawLineProgress(canvas, paint, rectHeight, rectWidth, progress, fadeColor);
                break;
            }
        }
    }

    /**
     * 绘制普通的进度条(长条型)： ━━━━━━════
     *
     * @param canvas 进度条绘制 canvas
     * @param paint 进度条绘制 paint
     * @param rectHeight 进度条绘制高度
     * @param rectWidth 进度条绘制宽度
     */
    private void drawNormalProgress(Canvas canvas, Paint paint, float rectHeight, float rectWidth, float progress, String fadeColor) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(NORMAL_PROGRESS_STROKE_WIDTH);
        paint.setColorFilter(null);

        canvas.drawLine(
            0F, rectHeight / 2,
            rectWidth * progress, rectHeight / 2,
            paint
        );

        paint.setColor(Color.parseColor(fadeColor));
        canvas.drawLine(
            rectWidth * progress, rectHeight / 2,
            rectWidth, rectHeight / 2,
            paint
        );
    }

    /**
     * 绘制点型的进度条： ••••••••◦◦◦◦◦◦◦◦
     *
     * @param canvas 进度条绘制 canvas
     * @param paint 进度条绘制 paint
     * @param rectHeight 进度条绘制高度
     * @param rectWidth 进度条绘制宽度
     */
    private void drawDottedProgress(Canvas canvas, Paint paint, float rectHeight, float rectWidth, float progress, String fadeColor) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DOTTED_PROGRESS_STROKE_WIDTH);
        paint.setColorFilter(null);
        canvas.clipRect(0, 0, rectWidth, rectHeight, Region.Op.INTERSECT);

        int unitCount = computeUnitCount(rectWidth, DOTTED_PROGRESS_STROKE_WIDTH, DOTTED_PROGRESS_UNIT_WIDTH);
        int finishedCount = Math.round(unitCount * progress);

        for (int i = 1; i <= unitCount; i++) {
            if (i > finishedCount) paint.setColor(Color.parseColor(fadeColor));
            canvas.drawCircle(
                DOTTED_PROGRESS_STROKE_WIDTH / 2 + (i -1) * DOTTED_PROGRESS_UNIT_WIDTH,
                rectHeight / 2,
                DOTTED_PROGRESS_STROKE_WIDTH / 2,
                paint
            );
        }

        paint.setPathEffect(null);
    }

    /**
     * 绘制箭头型的进度条： >>>>>>>>>
     *
     * @param canvas 进度条绘制 canvas
     * @param paint 进度条绘制 paint
     * @param rectHeight 进度条绘制高度
     * @param rectWidth 进度条绘制宽度
     */
    private void drawArrowProgress(Canvas canvas, Paint paint, float rectHeight, float rectWidth, float progress, String fadeColor) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ARROW_PROGRESS_STROKE_WIDTH);
        paint.setColorFilter(null);
        Path path = new Path();
        path.moveTo(0F, rectHeight / 2);

        int unitCount = computeUnitCount(rectWidth, ARROW_PROGRESS_SHAPE_WIDTH, ARROW_PROGRESS_UNIT_WIDTH);
        int finishedCount = Math.round(unitCount * progress);

        for (int i = 1; i <= finishedCount; i++) {
            drawArrowProgressUnitPath(path, rectHeight, i);
        }

        canvas.clipRect(0, 0, rectWidth, rectHeight, Region.Op.INTERSECT);
        canvas.drawPath(path, paint);

        for (int i = finishedCount + 1; i <= unitCount; i++) {
            paint.setColor(Color.parseColor(fadeColor));
            drawArrowProgressUnitPath(path, rectHeight, i);
        }

        canvas.clipRect(0, 0, rectWidth, rectHeight, Region.Op.INTERSECT);
        canvas.drawPath(path, paint);
    }

    private void drawArrowProgressUnitPath(Path path, float rectHeight, int i) {
        float arrowTopX = ARROW_PROGRESS_SHAPE_WIDTH + (i - 1) * ARROW_PROGRESS_UNIT_WIDTH;
        path.moveTo(arrowTopX, rectHeight / 2);
        path.lineTo(arrowTopX - ARROW_PROGRESS_SHAPE_WIDTH, (rectHeight / 2) - (ARROW_PROGRESS_SHAPE_HEIGHT / 2));
        path.moveTo(arrowTopX, rectHeight / 2);
        path.lineTo(arrowTopX - ARROW_PROGRESS_SHAPE_WIDTH, (rectHeight / 2) + (ARROW_PROGRESS_SHAPE_HEIGHT / 2));
    }

    /**
     * 绘制爱心型的进度条： ♥♥♥♥♥♥♡♡♡♡♡
     *
     * @param canvas 进度条绘制 canvas
     * @param paint 进度条绘制 paint
     * @param rectHeight 进度条绘制高度
     * @param rectWidth 进度条绘制宽度
     */
    private void drawLoveProgress(Canvas canvas, Paint paint, float rectHeight, float rectWidth, float progress, String fadeColor) {
        ColorFilter filter = new PorterDuffColorFilter(paint.getColor(), PorterDuff.Mode.SRC_IN);
        ColorFilter fadedFilter = new PorterDuffColorFilter(Color.parseColor(fadeColor), PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        int unitCount = computeUnitCount(rectWidth, LOVE_PROGRESS_SHAPE_WIDTH, LOVE_PROGRESS_UNIT_WDITH);
        int finishedCount = Math.round(unitCount * progress);

        Bitmap bitmap = BitmapFactory.decodeResource(AppContext.get().getResources(), R.drawable.progress_bar_shape_love_finished);

        for (int i = 1; i <= unitCount; i++) {
            if (i > finishedCount) {
                paint.setColorFilter(fadedFilter);
            }

            int bitmapLeftTopX = (int) ((i - 1) * LOVE_PROGRESS_UNIT_WDITH);
            int bitmapLeftTopY = (int) ((rectHeight / 2) - (LOVE_PROGRESS_SHAPE_HEIGHT / 2));
            int bitmapRightBottomX = (int) (bitmapLeftTopX + LOVE_PROGRESS_SHAPE_WIDTH);
            int bitmapRightBottomY = (int) ((rectHeight / 2) + (LOVE_PROGRESS_SHAPE_HEIGHT / 2));
            Rect rect = new Rect(bitmapLeftTopX, bitmapLeftTopY, bitmapRightBottomX, bitmapRightBottomY);

            canvas.drawBitmap(bitmap, null, rect, paint);
        }
    }

    /**
     * 绘制线条型的进度条： ❚❚❚❚❚❚❘❘❘❘❘
     *
     * @param canvas 进度条绘制 canvas
     * @param paint 进度条绘制 paint
     * @param rectHeight 进度条绘制高度
     * @param rectWidth 进度条绘制宽度
     */
    private void drawLineProgress(Canvas canvas, Paint paint, float rectHeight, float rectWidth, float progress, String fadeColor) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_PROGRESS_STROKE_WIDTH);
        paint.setColorFilter(null);

        int unitCount = computeUnitCount(rectWidth, LINE_PROGRESS_STROKE_WIDTH, LINE_PROGRESS_UNIT_WIDTH);
        int finishedCount = Math.round(unitCount * progress);

        for (int i = 1; i <= unitCount; i++) {
            if (i > finishedCount) paint.setColor(Color.parseColor(fadeColor));
            float centerX = (LINE_PROGRESS_STROKE_WIDTH / 2) + (i - 1) * LINE_PROGRESS_UNIT_WIDTH;
            canvas.drawLine(
                centerX,
                (rectHeight / 2) - (LINE_PROGRESS_SHAPE_HEIGHT / 2),
                centerX,
                (rectHeight / 2) + (LINE_PROGRESS_SHAPE_HEIGHT / 2),
                paint
            );
        }
    }

    /**
     * 计算可绘制进度单元个数
     *
     * @param width 进度条宽度
     * @param shapeWidth 进度条单元形状宽度
     * @param unitWidth 进度条单元总宽度
     * @return 最多绘制单元数
     */
    private int computeUnitCount(float width, float shapeWidth, float unitWidth) {
        int unitCount = (int) Math.floor(width / unitWidth);
        if ((width - unitWidth * unitCount) >= shapeWidth) unitCount++;
        return unitCount;
    }
}