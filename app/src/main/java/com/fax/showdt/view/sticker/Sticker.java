package com.fax.showdt.view.sticker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.fax.showdt.utils.ViewUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class Sticker {


    protected long id;

    public Sticker(long id) {
        this.id = id;
    }

    @IntDef(flag = true, value = {
            Position.CENTER, Position.TOP, Position.BOTTOM, Position.LEFT, Position.RIGHT, Position.INITIAL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Position {
        int CENTER = 1;
        int TOP = 1 << 1;
        int LEFT = 1 << 2;
        int RIGHT = 1 << 3;
        int BOTTOM = 1 << 4;
        int INITIAL = 1 << 5;
    }

    private final float[] matrixValues = new float[9];
    private final float[] unrotatedWrapperCorner = new float[8];
    private final float[] unrotatedPoint = new float[2];
    private final float[] boundPoints = new float[8];
    private final float[] mappedBounds = new float[8];
    private final RectF trappedRect = new RectF();
    private final Matrix matrix = new Matrix();
    private boolean isFlippedHorizontally;
    private boolean isFlippedVertically;
    private String mJumpAppPath;
    private String mAppIconPath;
    private String appName;
    private final float minLineLength = ViewUtils.dp2px(50);

    public boolean isFlippedHorizontally() {
        return isFlippedHorizontally;
    }

    @NonNull
    public Sticker setFlippedHorizontally(boolean flippedHorizontally) {
        isFlippedHorizontally = flippedHorizontally;
        return this;
    }

    public boolean isFlippedVertically() {
        return isFlippedVertically;
    }

    @NonNull
    public Sticker setFlippedVertically(boolean flippedVertically) {
        isFlippedVertically = flippedVertically;
        return this;
    }

    @NonNull
    public Matrix getMatrix() {
        return matrix;
    }

    public Sticker setMatrix(@Nullable Matrix matrix) {
        this.matrix.set(matrix);
        return this;
    }

    public abstract void draw(@NonNull Canvas canvas, int index, boolean showNumber);

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract Sticker setDrawable(@NonNull Drawable drawable);

    @NonNull
    public abstract Drawable getDrawable();

    @NonNull
    public abstract Sticker setAlpha(@IntRange(from = 0, to = 255) int alpha);

    public float[] getBoundPoints() {
        float[] points = new float[8];
        getBoundPoints(points);
        return points;
    }

    public void getBoundPoints(@NonNull float[] points) {
        if (!isFlippedHorizontally) {
            if (!isFlippedVertically) {
                points[0] = 0f;
                points[1] = 0f;
                points[2] = getWidth();
                points[3] = 0f;
                points[4] = 0f;
                points[5] = getHeight();
                points[6] = getWidth();
                points[7] = getHeight();
            } else {
                points[0] = 0f;
                points[1] = getHeight();
                points[2] = getWidth();
                points[3] = getHeight();
                points[4] = 0f;
                points[5] = 0f;
                points[6] = getWidth();
                points[7] = 0f;
            }
        } else {
            if (!isFlippedVertically) {
                points[0] = getWidth();
                points[1] = 0f;
                points[2] = 0f;
                points[3] = 0f;
                points[4] = getWidth();
                points[5] = getHeight();
                points[6] = 0f;
                points[7] = getHeight();
            } else {
                points[0] = getWidth();
                points[1] = getHeight();
                points[2] = 0f;
                points[3] = getHeight();
                points[4] = getWidth();
                points[5] = 0f;
                points[6] = 0f;
                points[7] = 0f;
            }
        }
    }

    @NonNull
    public float[] getMappedBoundPoints() {
        float[] dst = new float[8];
        getMappedPoints(dst, getBoundPoints());
        return dst;
    }

    @NonNull
    public float[] getMappedPoints(@NonNull float[] src) {
        float[] dst = new float[src.length];
        matrix.mapPoints(dst, src);
        return dst;
    }

    public void getMappedPoints(@NonNull float[] dst, @NonNull float[] src) {
        matrix.mapPoints(dst, src);
    }

    public float[] getMappedLinePoints(float[] mappedPoints) {
        float x1 = mappedPoints[0];
        float y1 = mappedPoints[1];
        float x2 = mappedPoints[2];
        float y2 = mappedPoints[3];
        float x3 = mappedPoints[4];
        float y3 = mappedPoints[5];
        float x4 = mappedPoints[6];
        float y4 = mappedPoints[7];
        float henLineLength = (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y2 - y1, 2));
        float shuLineLength = (float) Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y3 - y1, 2));
        float henRatio, shuRatio;
        if (henLineLength < minLineLength) {
            henRatio = minLineLength / henLineLength;
        } else {
            henRatio = 1.0f;
        }
        if (shuLineLength < minLineLength) {
            shuRatio = minLineLength / shuLineLength;
        } else {
            shuRatio = 1.0f;
        }
        float ratio = henRatio > shuRatio ?  shuRatio : henRatio;
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio, getMappedCenterPoint().x, getMappedCenterPoint().y);

        float[] resultPoints = new float[8];
        matrix.mapPoints(resultPoints, mappedPoints);
        return resultPoints;

    }

    @NonNull
    public RectF getBound() {
        RectF bound = new RectF();
        getBound(bound);
        return bound;
    }

    public void getBound(@NonNull RectF dst) {
        dst.set(0, 0, getWidth(), getHeight());
    }

    @NonNull
    public RectF getMappedBound() {
        RectF dst = new RectF();
        getMappedBound(dst, getBound());
        return dst;
    }

    public void getMappedBound(@NonNull RectF dst, @NonNull RectF bound) {
        matrix.mapRect(dst, bound);
    }

    @NonNull
    public PointF getCenterPoint() {
        PointF center = new PointF();
        getCenterPoint(center);
        return center;
    }

    public void getCenterPoint(@NonNull PointF dst) {
        dst.set(getWidth() * 1f / 2, getHeight() * 1f / 2);
    }

    @NonNull
    public PointF getMappedCenterPoint() {
        PointF pointF = getCenterPoint();
        getMappedCenterPoint(pointF, new float[2], new float[2]);
        return pointF;
    }

    public void getMappedCenterPoint(@NonNull PointF dst, @NonNull float[] mappedPoints,
                                     @NonNull float[] src) {
        getCenterPoint(dst);
        src[0] = dst.x;
        src[1] = dst.y;
        getMappedPoints(mappedPoints, src);
        dst.set(mappedPoints[0], mappedPoints[1]);
    }

    public float getCurrentScale() {
        return getMatrixScale(matrix);
    }

    public float getCurrentHeight() {
        return getMatrixScale(matrix) * getHeight();
    }

    public float getCurrentWidth() {
        return getMatrixScale(matrix) * getWidth();
    }

    public String getJumpAppPath() {
        return mJumpAppPath;
    }

    public void setJumpAppPath(String jumpAppPath) {
        mJumpAppPath = jumpAppPath;
    }

    public String getAppIconPath() {
        return mAppIconPath;
    }

    public void setAppIconPath(String appIconPath) {
        mAppIconPath = appIconPath;
    }

    public String getAppName() {
        return appName == null ? "" : appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * This method calculates scale value for given Matrix object.
     */
    public float getMatrixScale(@NonNull Matrix matrix) {
        return (float) Math.sqrt(Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X), 2) + Math.pow(
                getMatrixValue(matrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * @return - current image rotation angle.
     */
    public float getCurrentAngle() {
        return getMatrixAngle(matrix);
    }

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    public float getMatrixAngle(@NonNull Matrix matrix) {
        return (float) Math.toDegrees(-(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X),
                getMatrixValue(matrix, Matrix.MSCALE_X))));
    }

    public float getMatrixValue(@NonNull Matrix matrix, @IntRange(from = 0, to = 9) int valueIndex) {
        matrix.getValues(matrixValues);
        return matrixValues[valueIndex];
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean contains(float x, float y) {
        return contains(new float[]{x, y});
    }

    public boolean contains(@NonNull float[] point) {
        Matrix tempMatrix = new Matrix();
        tempMatrix.setRotate(-getCurrentAngle());
        getBoundPoints(boundPoints);
        getMappedPoints(mappedBounds, boundPoints);
        tempMatrix.mapPoints(unrotatedWrapperCorner, mappedBounds);
        tempMatrix.mapPoints(unrotatedPoint, point);
        StickerUtils.trapToRect(trappedRect, unrotatedWrapperCorner);
        return trappedRect.contains(unrotatedPoint[0], unrotatedPoint[1]);
    }

    /**
     * 获取矩阵变换后的矩形坐标
     *
     * @return
     */
    public RectF getMappedRectF() {
        Matrix tempMatrix = new Matrix();
        tempMatrix.setRotate(-getCurrentAngle());
        getBoundPoints(boundPoints);
        getMappedPoints(mappedBounds, boundPoints);
        tempMatrix.mapPoints(unrotatedWrapperCorner, mappedBounds);
        StickerUtils.trapToRect(trappedRect, unrotatedWrapperCorner);
        return trappedRect;
    }

    public void release() {
    }
}
