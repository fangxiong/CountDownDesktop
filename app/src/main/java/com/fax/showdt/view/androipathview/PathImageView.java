package com.fax.showdt.view.androipathview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * PathView is a View that animates paths.
 */
@SuppressWarnings("unused")
public class PathImageView extends View  {


    private Paint paint = new Paint();
    private Path path;

    private RectF rectF = new RectF();

    /**
     * Default constructor.
     *
     * @param context The Context of the application.
     */
    public PathImageView(Context context) {
        this(context, null);
    }

    /**
     * Default constructor.
     *
     * @param context The Context of the application.
     * @param attrs   attributes provided from the resources.
     */
    public PathImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Default constructor.
     *
     * @param context  The Context of the application.
     * @param attrs    attributes provided from the resources.
     * @param defStyle Default style.
     */
    public PathImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * Set path to be drawn and animated.
     *
     * @param path - Paths that can be drawn.
     */
    public void setPath(final Path path) {
        this.path = path;
        if(path != null) {
            path.computeBounds(rectF, false);
            invalidate();
        }
    }

    public void setColor(int color){
        if(paint != null){
            paint.setColor(color);
        }
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(path == null){
            return;
        }
        canvas.save();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        path.computeBounds(rectF,false);
        float width = rectF.width()+rectF.left*2;
        float height  = rectF.height()+rectF.top*2;
        float curWidth = getWidth();
        float curHeight = getHeight();
        float ratio = width > height ? curWidth/width : curHeight/height;
        canvas.scale(ratio,ratio,0,0);
//        canvas.translate(-rectF.left,-rectF.top);
        canvas.drawPath(path,paint);
        canvas.restore();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 0;
        int desiredHeight = 0;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);

        int measuredWidth, measuredHeight;

        if (widthMode == MeasureSpec.AT_MOST) {
            measuredWidth = desiredWidth;
        } else {
            measuredWidth = widthSize;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            measuredHeight = desiredHeight;
        } else {
            measuredHeight = heightSize;
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }



    /**
     * Get the path color.
     *
     * @return The color of the paint.
     */
    public int getPathColor() {
        return paint.getColor();
    }

    /**
     * Set the path color.
     *
     * @param color -The color to set to the paint.
     */
    public void setPathColor(final int color) {
        paint.setColor(color);
    }

    /**
     * Get the path width.
     *
     * @return The width of the paint.
     */
    public float getPathWidth() {
        return paint.getStrokeWidth();
    }

    /**
     * Set the path width.
     *
     * @param width - The width of the path.
     */
    public void setPathWidth(final float width) {
        paint.setStrokeWidth(width);
    }




}
