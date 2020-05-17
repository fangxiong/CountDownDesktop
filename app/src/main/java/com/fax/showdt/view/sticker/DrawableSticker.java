package com.fax.showdt.view.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import com.fax.showdt.AppContext;
import com.fax.showdt.utils.TimeUtils;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeCompat;

public class DrawableSticker extends Sticker {

    public static final String CIRCLE = "circle";
    public static final String ROUND = "round";
    public static final String RECT = "rect";
    public static final String LOVE = "love";
    public static final String PENTAGON = "pentagon";
    public static int DEFAULT_DRAWABLE_HEIGHT = ViewUtils.dpToPx(100, AppContext.get());
    private Drawable drawable;
    private Rect realBounds;
    private String drawablePath;
    private float scale;
    private Bitmap mMaskBitmap;
    private String clipType = RECT;
    private Paint paint;
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    private Path path;
    /**
     * 图片是否显示描边
     */
    private boolean mShowFrame;
    /**
     * 图片的默认宽度
     */
    private int mFrame = ViewUtils.dpToPx(4, AppContext.get());
    /**
     * 图片的缩放比
     */
    private float mRatio;
    /**
     * 描边颜色包括sd卡图片的描边和背景板的描边颜色
     */
    private String strokeColor = "#FFFFFF";

    /**
     * 描边的宽
     */
    private int strokeWidth = 2;

    /**
     * 是否开启描边
     */
    private boolean stroke = false;

    /**
     * 图片描边的宽度(用ratio来计算),默认为高度的0f
     */
    private float strokeRatio = 0f;

    /**
     * 图片圆角(用ratio来计算),默认为高度的0.5f
     */
    private float cornerRatio = 0.5f;
    /**
     * 背景板默认高度比例(默认高度的)
     */
    private float shapeHeightRatio = 1.0f;

    /**
     * 背景板默认高度比例(默认高度的)
     */
    private float shapeWidthRatio = 1.0f;

    /**
     * 图片的颜色(用于svg和背景板)
     */
    private String drawableColor = "#FFFFFF";

    /**
     * 渐变色
     */
    private List<Integer> gradientColors = new ArrayList<>();

    /**
     * 是否渐变
     */
    private boolean isGradient = false;

    /**
     * 渐变色的方向
     */
    private int gradientOrientation = LEFT_RIGHT;
    /**
     * 来自asset文件夹下的图片
     */
    public static final int ASSET = 0;
    /**
     * 来自svg文件夹下的图片
     */
    public static final int SVG = 1;
    /**
     * 来自sd卡的图片
     */
    public static final int SDCARD = 2;
    /**
     * 背景板
     */
    public static final int SHAPE = 3;

    /**
     * draw the gradient from the top to the bottom
     */
    public static final int TOP_BOTTOM = 0;
    /**
     * draw the gradient from the top-right to the bottom-left
     */
    public static final int TR_BL = 1;
    /**
     * draw the gradient from the right to the left
     */
    public static final int RIGHT_LEFT = 2;
    /**
     * draw the gradient from the bottom-right to the top-left
     */
    public static final int BR_TL = 3;
    /**
     * draw the gradient from the bottom to the top
     */
    public static final int BOTTOM_TOP = 4;
    /**
     * draw the gradient from the bottom-left to the top-right
     */
    public static final int BL_TR = 5;
    /**
     * draw the gradient from the left to the right
     */
    public static final int LEFT_RIGHT = 6;
    /**
     * draw the gradient from the top-left to the bottom-right
     */
    public static final int TL_BR = 7;

    @PicType
    private int mPicType = ASSET;

    @IntDef({ASSET, SVG, SDCARD, SHAPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PicType {
    }

    @StringDef({CIRCLE, ROUND, RECT, LOVE, PENTAGON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PicClip {
    }

    @IntDef({TOP_BOTTOM, TR_BL, RIGHT_LEFT, BR_TL, BOTTOM_TOP, BL_TR, LEFT_RIGHT, TL_BR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GradientOrientation {
    }

    public DrawableSticker(Drawable drawable, long id, int defaultWidth) {
        super(id);
        AutoSizeCompat.autoConvertDensityBaseOnWidth(AppContext.get().getResources(),360);
        DEFAULT_DRAWABLE_HEIGHT = ViewUtils.dpToPx(100, AppContext.get());
        this.drawable = drawable;
        paint = new Paint();
        paint.setAntiAlias(true);
        if (drawable == null || drawable instanceof GradientDrawable) {
            this.drawable = new GradientDrawable();
            realBounds = new Rect((int) (0 - (DEFAULT_DRAWABLE_HEIGHT * strokeRatio) / 2f), (int) (0 - (DEFAULT_DRAWABLE_HEIGHT * strokeRatio) / 2f), (int) (DEFAULT_DRAWABLE_HEIGHT*shapeWidthRatio + (DEFAULT_DRAWABLE_HEIGHT * strokeRatio / 2f)), (int) ((DEFAULT_DRAWABLE_HEIGHT * shapeHeightRatio) + (DEFAULT_DRAWABLE_HEIGHT * strokeRatio / 2f)));
        } else if(drawable instanceof ShapeDrawable) {

            realBounds = drawable.getBounds();
        }else{
            realBounds = new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        int maxLength = getWidth() > getHeight() ? getWidth() : getHeight();
        float ratio = defaultWidth * 1.0f / maxLength;
        PointF pointF = new PointF();
        getCenterPoint(pointF);
        getMatrix().postScale(ratio, ratio, pointF.x, pointF.y);
        gradientColors.add(0, Color.parseColor("#005AA7"));
        gradientColors.add(1, Color.parseColor("#FFFDE4"));
        mRatio = ratio;
    }

    public DrawableSticker(Drawable drawable, long id) {
        this(drawable, id, DEFAULT_DRAWABLE_HEIGHT);
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public DrawableSticker setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public String getDrawablePath() {
        return drawablePath;
    }

    public void setDrawablePath(String drawablePath) {
        this.drawablePath = drawablePath;
    }


    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        getMatrix().reset();
        PointF pointF = this.getMappedCenterPoint();
        getMatrix().postScale(scale, scale, pointF.x, pointF.y);
        this.scale = scale;
    }

    public void resizeBounds() {
        if (drawable instanceof GradientDrawable) {
            realBounds = new Rect((int) (0 - (DEFAULT_DRAWABLE_HEIGHT * strokeRatio) / 2f), (int) (0 - (DEFAULT_DRAWABLE_HEIGHT * strokeRatio) / 2f), (int) (DEFAULT_DRAWABLE_HEIGHT*shapeWidthRatio + (DEFAULT_DRAWABLE_HEIGHT * strokeRatio / 2f)), (int) ((DEFAULT_DRAWABLE_HEIGHT * shapeHeightRatio) + (DEFAULT_DRAWABLE_HEIGHT * strokeRatio / 2f)));
        }else  if(drawable instanceof ShapeDrawable){
            path = com.caverock.androidsvg1.SVG.parsePath(getDrawablePath());
            RectF rectF = new RectF();
            path.computeBounds(rectF, false);
            realBounds = new Rect(0, 0, (int) (rectF.right + rectF.left), (int) (rectF.bottom + rectF.top));

        }
    }

    @Override
    public void draw(@NonNull Canvas canvas, int index, boolean showNumber) {
        canvas.save();
        canvas.concat(getMatrix());
        if (mPicType == SHAPE) {
            realBounds = new Rect((int) (0 - (DEFAULT_DRAWABLE_HEIGHT * strokeRatio) / 2f), (int) (0 - (DEFAULT_DRAWABLE_HEIGHT * strokeRatio) / 2f), (int) (DEFAULT_DRAWABLE_HEIGHT*shapeWidthRatio + (DEFAULT_DRAWABLE_HEIGHT * strokeRatio / 2f)), (int) ((DEFAULT_DRAWABLE_HEIGHT * shapeHeightRatio) + (DEFAULT_DRAWABLE_HEIGHT * strokeRatio / 2f)));

            if (isGradient) {
                int[] colors = new int[gradientColors.size()];

                for (int i = 0; i < gradientColors.size(); i++) {
                    colors[i] = gradientColors.get(i);
                }
                ((GradientDrawable) drawable).setOrientation(getGradientOrientation(gradientOrientation));
                ((GradientDrawable) drawable).setColors(colors);
            } else {
                ((GradientDrawable) drawable).setColor(Color.parseColor(drawableColor));
            }
            ((GradientDrawable) drawable).setStroke((int) (DEFAULT_DRAWABLE_HEIGHT * strokeRatio), Color.parseColor(strokeColor));
            ((GradientDrawable) drawable).setShape(GradientDrawable.RECTANGLE);
            ((GradientDrawable) drawable).setCornerRadius(DEFAULT_DRAWABLE_HEIGHT * shapeHeightRatio * cornerRatio);
            drawable.setBounds(realBounds);
            canvas.translate(DEFAULT_DRAWABLE_HEIGHT * strokeRatio / 2f, DEFAULT_DRAWABLE_HEIGHT * (strokeRatio) / 2f);
            drawable.draw(canvas);
        } else if (mPicType == SDCARD) {
            if (mMaskBitmap != null) {
                canvas.drawBitmap(mMaskBitmap, 0, 0, null);
            }
        } else if (mPicType == ASSET) {
            drawable.setBounds(realBounds);
            drawable.draw(canvas);
        } else if (mPicType == SVG) {
            if(path == null) {
                path = com.caverock.androidsvg1.SVG.parsePath(getDrawablePath());
            }
            paint.setColor(Color.parseColor(drawableColor));
            paint.setStyle(stroke ? Paint.Style.STROKE : Paint.Style.FILL);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawPath(path, paint);

        }
        canvas.restore();
    }

    @NonNull
    @Override
    public DrawableSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        drawable.setAlpha(alpha);
        return this;
    }

    @Override
    public int getWidth() {
//        Log.i("test_drawsticker_width:", String.valueOf(realBounds.width()));
        return realBounds.width();
    }

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

    private String getClipSvgPath(String clipType) {
        switch (clipType) {
            case CIRCLE: {
                return "shape_circle.svg";
            }
            case ROUND: {
                return "shape_round.svg";
            }
            case RECT: {
                return "shape_rect.svg";
            }
            case LOVE: {
                return "shape_love.svg";
            }
            case PENTAGON: {
                return "shape_pentagon.svg";
            }
            default:
                return "shape_rect.svg";
        }
    }

    public void addMaskBitmap(Context context, String clipType) {
        if (SDCARD != mPicType) {
            mMaskBitmap = null;
            return;
        }
        Bitmap result = null;
        setClipType(clipType);
        String svgFolderName = "svg/clipSvg";
        String svgPath = svgFolderName + File.separator + getClipSvgPath(clipType);
        if (TextUtils.isEmpty(getClipSvgPath(clipType))) {
            return;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        SVG svg;
        RectF rectF = new RectF(0, 0, width, height);
        try {
            svg = new SVGBuilder()
                    .readFromAsset(context.getAssets(), svgPath).build();
        } catch (IOException e) {
            e.printStackTrace();
            svg = null;
        }
        if (svg != null) {
            canvas.drawPicture(svg.getPicture(), rectF);
            result = bitmap;
        }
        if (result != null) {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas drawCanvas = new Canvas(newBitmap);
            drawable.setBounds(realBounds);
            drawable.draw(drawCanvas);

            Paint paint = new Paint();
            paint.reset();
            paint.setXfermode(xfermode);
            drawCanvas.drawBitmap(result, 0, 0, paint);
            paint.setXfermode(null);

            if (mShowFrame) {
                Bitmap alphaBitmap = getAlphaBitmap(result);
                Paint alphaPaint = new Paint();
                alphaPaint.setAntiAlias(true);
                alphaPaint.setFilterBitmap(true);
                Canvas canvas1 = new Canvas(alphaBitmap);
                int frame = (int) (mFrame / mRatio);
                int newWidth = width - frame;
                int newHeight = height - frame;
                int offSet = frame / 2;
                Bitmap zoomBitmap = Bitmap.createScaledBitmap(newBitmap, newWidth, newHeight, true);
                canvas1.drawBitmap(zoomBitmap, offSet, offSet, null);
                mMaskBitmap = alphaBitmap;
            } else {
                mMaskBitmap = newBitmap;
            }

        }

    }

    private Bitmap getAlphaBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap mAlphaBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mAlphaBitmap);
        Paint mPaint = new Paint();
        mPaint.setColor(Color.parseColor(strokeColor));
        Log.e("test_strokeColor:", strokeColor);
        Bitmap alphaBitmap = bitmap.extractAlpha();
        mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);
        return mAlphaBitmap;
    }

    public DrawableSticker copy() {
        DrawableSticker copySticker = new DrawableSticker(getDrawable(), TimeUtils.currentTimeMillis());
        copySticker.setJumpAppPath(getJumpAppPath());
        copySticker.setShowFrame(isShowFrame());
        copySticker.setmPicType(getmPicType());
        copySticker.setDrawablePath(getDrawablePath());
        copySticker.mMaskBitmap = mMaskBitmap;
        copySticker.setScale(getScale());
        copySticker.setMatrix(getMatrix());
        copySticker.setGradientOrientation(getGradientOrientation());
        copySticker.setGradient(isGradient());
        copySticker.setGradientColors(getGradientColors());
        copySticker.setCornerRatio(getCornerRatio());
        copySticker.setDrawableColor(getDrawableColor());
        copySticker.setShapeHeightRatio(getShapeHeightRatio());
        copySticker.setShapeWidthRatio(getShapeWidthRatio());
        copySticker.setStrokeRatio(getStrokeRatio());
        copySticker.setStrokeColor(getStrokeColor());
        copySticker.resizeBounds();
        return copySticker;
    }

    public void setShowFrame(boolean isAddFrame) {
        mShowFrame = isAddFrame;
    }

    public boolean isShowFrame() {
        return mShowFrame;
    }

    public void setDrawableColor(String drawableColor) {
        this.drawableColor = drawableColor;
    }

    public String getDrawableColor() {
        return drawableColor;
    }

    public void setClipType(String clipType) {
        this.clipType = clipType;
    }

    public String getClipType() {
        return clipType;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
        Log.e("test_strokeColor_set:", strokeColor);
    }

    public int getmPicType() {
        return mPicType;
    }

    public void setmPicType(int mPicType) {
        this.mPicType = mPicType;
    }

    public float getStrokeRatio() {
        return strokeRatio;
    }

    public void setStrokeRatio(float strokeRatio) {
        this.strokeRatio = strokeRatio;
    }

    public float getCornerRatio() {
        return cornerRatio;
    }

    public void setCornerRatio(float cornerRatio) {
        this.cornerRatio = cornerRatio;
    }

    public float getShapeHeightRatio() {
        return shapeHeightRatio;
    }

    public void setShapeHeightRatio(float shapeHeightRatio) {
        this.shapeHeightRatio = shapeHeightRatio;
    }

    public void setGradientColors(List<Integer> gradientColors) {
        if(gradientColors == null || gradientColors.size() == 0){
            gradientColors = new ArrayList<>();
            gradientColors.add(0, Color.parseColor("#005AA7"));
            gradientColors.add(1, Color.parseColor("#FFFDE4"));
        }
        this.gradientColors = gradientColors;
    }

    public void setShapeWidthRatio(float shapeWidthRatio) {
        this.shapeWidthRatio = shapeWidthRatio;
    }

    public float getShapeWidthRatio() {
        return shapeWidthRatio;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStroke(boolean stroke) {
        this.stroke = stroke;
    }

    public boolean isStroke() {
        return stroke;
    }

    public List<Integer> getGradientColors() {
        if(gradientColors == null){
            gradientColors = new ArrayList<>();
        }
        List<Integer> result = new ArrayList<>();
        if (gradientColors.size() == 0) {
            result.add(0, Color.parseColor("#005AA7"));
            result.add(1, Color.parseColor("#FFFDE4"));
        }else {
            result.addAll(gradientColors);
        }
        return result;
    }

    public void setGradient(boolean gradient) {
        isGradient = gradient;
        if (!isGradient && drawable instanceof GradientDrawable) {
            drawable = new GradientDrawable();
        }
    }

    public boolean isGradient() {
        return isGradient;
    }

    public void setGradientOrientation(int gradientOrientation) {
        this.gradientOrientation = gradientOrientation;
    }

    public int getGradientOrientation() {
        return gradientOrientation;
    }

    private GradientDrawable.Orientation getGradientOrientation(int type) {
        switch (type) {
            case 0: {
                return GradientDrawable.Orientation.TOP_BOTTOM;
            }
            case 1: {
                return GradientDrawable.Orientation.TR_BL;
            }
            case 2: {
                return GradientDrawable.Orientation.RIGHT_LEFT;
            }
            case 3: {
                return GradientDrawable.Orientation.BR_TL;
            }
            case 4: {
                return GradientDrawable.Orientation.BOTTOM_TOP;
            }
            case 5: {
                return GradientDrawable.Orientation.BL_TR;
            }
            case 6: {
                return GradientDrawable.Orientation.LEFT_RIGHT;
            }
            case 7: {
                return GradientDrawable.Orientation.TL_BR;
            }
        }
        return GradientDrawable.Orientation.LEFT_RIGHT;
    }
}
