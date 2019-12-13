package com.fax.showdt.view.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.File;
import java.io.IOException;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

public class DrawableSticker extends Sticker {

    private Drawable drawable;
    private Rect realBounds;
    private String drawablePath;
    private float scale;
    private Bitmap mMaskBitmap;
    private String mSvgName;
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private boolean mShowFrame;
    private int mFrame = ViewUtils.dp2px(6);
    private float mRatio = 1;

    public DrawableSticker(Drawable drawable, long id, int defaultWidth) {
        super(id);
        this.drawable = drawable;
        realBounds = new Rect(0, 0, getWidth(), getHeight());
        if (getWidth() > defaultWidth) {
            float ratio = defaultWidth * 1.0f / getWidth();
            PointF pointF = new PointF();
            getCenterPoint(pointF);
            getMatrix().postScale(ratio, ratio, pointF.x, pointF.y);
            mRatio = ratio;
        }
    }

    public DrawableSticker(Drawable drawable, long id) {
        this(drawable, id, ViewUtils.dp2px(100));
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

    @Override
    public void draw(@NonNull Canvas canvas, int index, boolean showNumber) {
        canvas.save();
        canvas.concat(getMatrix());
        if (mMaskBitmap != null){
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap newBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Canvas drawCanvas = new Canvas(newBitmap);
            drawable.setBounds(realBounds);
            drawable.draw(drawCanvas);

            Paint paint = new Paint();
            paint.reset();
            paint.setXfermode(xfermode);
            drawCanvas.drawBitmap(mMaskBitmap,0,0,paint);
            paint.setXfermode(null);

            if (mShowFrame){
                Bitmap alphaBitmap = getAlphaBitmap(mMaskBitmap);
                Paint alphaPaint = new Paint();
                alphaPaint.setAntiAlias(true);
                alphaPaint.setFilterBitmap(true);

                canvas.drawBitmap(alphaBitmap,0,0,alphaPaint);
                int frame = (int) (mFrame / mRatio);
                int newWidth = width - frame;
                int newHeight = height - frame;
                int offSet = frame / 2;
                Bitmap zoomBitmap = Bitmap.createScaledBitmap(newBitmap,newWidth,newHeight,true);
                canvas.drawBitmap(zoomBitmap,offSet,offSet,null);
            }else {
                canvas.drawBitmap(newBitmap,0,0,null);
            }

        }else {
            drawable.setBounds(realBounds);
            drawable.draw(canvas);
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
        return drawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return drawable.getIntrinsicHeight();
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }

    public void addMaskBitmap(Context context, String svgName){
        this.mSvgName = svgName;
        String svgFolderName = "svg";
        String svgPath = svgFolderName + File.separator + mSvgName;
        if (TextUtils.isEmpty(mSvgName)){
            return;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        SVG svg;
        RectF rectF = new RectF(0,0,width,height);
        try {
            svg = new SVGBuilder()
                    .readFromAsset(context.getAssets(),svgPath).build();
        } catch (IOException e) {
            e.printStackTrace();
            svg = null;
        }
        if (svg != null){
            canvas.drawPicture(svg.getPicture(),rectF);
            mMaskBitmap = bitmap;
        }
    }

    public Bitmap getAlphaBitmap(Bitmap bitmap) {

        Bitmap mBitmap = bitmap;

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        Bitmap mAlphaBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas mCanvas = new Canvas(mAlphaBitmap);

        Paint mPaint = new Paint();

        mPaint.setColor(Color.WHITE);

        Bitmap alphaBitmap = bitmap.extractAlpha();

        mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);

        return mAlphaBitmap;
    }

    public void setShowFrame(boolean isAddFrame){
        mShowFrame = isAddFrame;
    }

    public boolean isShowFrame() {
        return mShowFrame;
    }

    public String getSvgName() {
        return mSvgName == null ? "" : mSvgName;
    }
}
