package com.fax.showdt.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.View;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.DrawableRes;

public class BitmapUtils {

    public static boolean isAvailable(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled();
    }

    public static Bitmap decodeFile(String filepath) {
        return decodeFile(filepath, null);
    }

    public static Bitmap decodeFile(File file) {
        if (FileExUtils.isFile(file)) {
            return decodeFile(file.getAbsolutePath());
        }
        return null;
    }


    public static Bitmap decodeResource(Resources res, @DrawableRes int resId) {
        try {
            return BitmapFactory.decodeResource(res, resId);
        } catch (Throwable tr) {
        }
        return null;
    }

    public static Bitmap decodeFile(String pathName, BitmapFactory.Options opts) {
        try {
            return BitmapFactory.decodeFile(pathName, fixBitmapOptsForSavingMemory(opts));
        } catch (Throwable tr) {
            Log.i("icon_parser", Log.getStackTraceString(tr));
        }
        return null;
    }

    public static Bitmap decodeFromAssest(Context context,String path){
        AssetManager asm = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = asm.open(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Drawable d =Drawable.createFromStream(inputStream, null);
        return  ((BitmapDrawable) d).getBitmap();
    }

    /**
     * 从一个Uri得到图片, 如果获取失败返回 null
     *
     * @param context Context (required)
     * @param uri     图片的URI (required)
     * @return Bitmap
     */
    public static Bitmap decodeUri(Context context, Uri uri) {
        return decodeUri(context, uri, null);
    }

    /**
     * 从一个Uri得到图片, 如果获取失败返回 null
     *
     * @param context Context (required)
     * @param uri     图片的URI (required)
     * @param opts    解码Bitmap的选项 (optional)
     * @return Bitmap
     */
    public static Bitmap decodeUri(Context context, Uri uri, BitmapFactory.Options opts) {

        Log.i("test_image", "image uri: " + uri);

        if (uri == null || context == null) {
            return null;
        }
        InputStream input = null;
        Bitmap bitmap = null;
        try {
            input = FileExUtils.openInputStream(context, uri);
            opts = fixBitmapOptsForSavingMemory(opts);
            if (opts != null) {
                bitmap = BitmapFactory.decodeStream(input, null, opts);
            } else {
                bitmap = BitmapFactory.decodeStream(input);
            }
            input.close();
        } catch (Throwable tr) {
            Log.e("test_image", Log.getStackTraceString(tr));
        }

        Log.i("test_image", "bitmap: " + bitmap);
        return bitmap;
    }


    private static BitmapFactory.Options fixBitmapOptsForSavingMemory(BitmapFactory.Options opts) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (opts == null) {
                opts = new BitmapFactory.Options();
            }
            opts.inInputShareable = true;
            opts.inPurgeable = true;// 以上options的两个属性必须联合使用才会有效果
        }
        return opts;
    }

    public static Bitmap decodeResource(Resources res, @DrawableRes int resId, BitmapFactory.Options options) {
        try {
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (Throwable tr) {
        }
        return null;
    }

    public static BitmapFactory.Options getImageResOptions(Context context, int res) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapUtils.decodeResource(context.getResources(), res, options);
        return options;
    }

    public static boolean save(Bitmap bitmap, File file, Bitmap.CompressFormat format, int quality) {
        OutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(format, quality, stream);
            stream.flush();
            stream.close();
            return true;
        } catch (Throwable tr) {
        } finally {
        }
        return false;
    }

    public static final int CORNER_TOP_LEFT = 1;
    public static final int CORNER_TOP_RIGHT = 1 << 1;
    public static final int CORNER_BOTTOM_LEFT = 1 << 2;
    public static final int CORNER_BOTTOM_RIGHT = 1 << 3;
    public static final int CORNER_ALL = CORNER_TOP_LEFT | CORNER_TOP_RIGHT | CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;


    /**
     * 把图片某固定角变成圆角
     *
     * @param bitmap  需要修改的图片
     * @param pixels  圆角的弧度
     * @param corners 需要显示圆弧的位置
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels, int corners) {
        //创建一个等大的画布
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        //获取一个跟图片相同大小的矩形
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        //生成包含坐标的矩形对象
        final RectF rectF = new RectF(rect);
        //圆角的半径
        final float roundPx = pixels;

        paint.setAntiAlias(true); //去锯齿
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        //绘制圆角矩形
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        //异或将需要变为圆角的位置的二进制变为0
        int notRoundedCorners = corners ^ CORNER_ALL;

        //哪个角不是圆角我再把你用矩形画出来
        if ((notRoundedCorners & CORNER_TOP_LEFT) != 0) {
            canvas.drawRect(0, 0, roundPx, roundPx, paint);
        }
        if ((notRoundedCorners & CORNER_TOP_RIGHT) != 0) {
            canvas.drawRect(rectF.right - roundPx, 0, rectF.right, roundPx, paint);
        }
        if ((notRoundedCorners & CORNER_BOTTOM_LEFT) != 0) {
            canvas.drawRect(0, rectF.bottom - roundPx, roundPx, rectF.bottom, paint);
        }
        if ((notRoundedCorners & CORNER_BOTTOM_RIGHT) != 0) {
            canvas.drawRect(rectF.right - roundPx, rectF.bottom - roundPx, rectF.right, rectF.bottom, paint);
        }
        //通过SRC_IN的模式取源图片和圆角矩形重叠部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //绘制成Bitmap对象
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * view转bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        //如果不调用这个方法，每次生成的bitmap相同
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * view转bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap viewToBitmap(View view) {
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        if (width <= 0 || height <= 0) {
            return null;
        }

        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        if (bm == null) {
            return null;
        }

        Canvas canvas = new Canvas(bm);
        view.draw(canvas);
        return bm;
    }

    public static Size getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapUtils.decodeFile(path, options);
        Size size = new Size(options.outWidth, options.outHeight);
        return size;
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (!(drawable instanceof BitmapDrawable)) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
        }
        return bitmap;
    }

    /**
     * 制作成圆形bitmap
     *
     * @param resource
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap resource) {
        //获取图片的宽度
        int width = resource.getWidth();
        Paint paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(resource, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        //创建一个与原bitmap一样宽度的正方形bitmap
        Bitmap circleBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        //以该bitmap为低创建一块画布
        Canvas canvas = new Canvas(circleBitmap);
        //以（width/2, width/2）为圆心，width/2为半径画一个圆
        canvas.drawCircle(width / 2, width / 2, width / 2 * (12f / 13), paint);
        //设置画笔为取交集模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //裁剪图片
        canvas.drawBitmap(resource, 0, 0, paint);

        return circleBitmap;
    }

    /**
     * @return a
     * @Author: Menglong Ma
     * @Email: mml2015@126.com
     * @Date: 19-9-11 下午7:34
     * @Description: 获取BitmapShader方式获取圆形图标
     * @params [a]
     */
    public static Bitmap getCircleBitmapWithShader(Bitmap resource) {
        Paint paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(resource, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));

        //创建一个与原bitmap一样宽度的正方形bitmap
        Bitmap circleBitmap = Bitmap.createBitmap(resource.getWidth(), resource.getWidth(), Bitmap.Config.ARGB_8888);
        //以该bitmap为底创建一块画布
        Canvas canvas = new Canvas(circleBitmap);
        RectF rectF = new RectF(0f, 0f, resource.getWidth(), resource.getHeight());
        int radius = resource.getWidth() / 2;
        canvas.drawRoundRect(rectF, radius, radius, paint);
        // circleBitmap = Bitmap.createBitmap(circleBitmap, 0, 0, circleBitmap.getWidth(), circleBitmap.getHeight(), matrix, true);
        return circleBitmap;
    }

    /**
     * 合并两个bitmap 用于合成分享的二维码图片
     *
     * @param firstBitmap
     * @param secondBitmap
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(),
                firstBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), new Paint());
        float firstBitmapWidth = firstBitmap.getWidth();
        float secondBitmapWidth = secondBitmap.getWidth();
        float ratio = firstBitmapWidth / secondBitmapWidth;
        Bitmap resultBitmap = BitmapUtils.scaleBitmap(secondBitmap, ratio);
        int marginTop = firstBitmap.getHeight() - resultBitmap.getHeight();
        int marginLeft = (firstBitmap.getWidth() - resultBitmap.getWidth()) / 2;
        canvas.drawBitmap(resultBitmap, marginLeft, marginTop, null);
        return bitmap;
    }
    /**
     * 合并两个bitmap 用于合成分享的二维码图片
     *
     * @param firstBitmap
     * @param secondBitmap
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap, int marginTop) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(),
                firstBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), new Paint());
        float firstBitmapWidth = firstBitmap.getWidth();
        float secondBitmapWidth = secondBitmap.getWidth();
        float ratio = firstBitmapWidth / secondBitmapWidth;
        Bitmap resultBitmap = BitmapUtils.scaleBitmap(secondBitmap, ratio);
        int marginLeft = (firstBitmap.getWidth() - resultBitmap.getWidth()) / 2;
        canvas.drawBitmap(resultBitmap, marginLeft, marginTop, null);
        return bitmap;
    }
    /**
     * 缩放bitmap
     *
     * @param origin
     * @param ratio
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 缩放bitmap
     *
     * @param origin
     * @param radius 最后得到的bitmap的半径
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap origin, int radius) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(radius*1.0f/width, radius*1.0f/height);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        return newBM;
    }

    /**
     * 获取view的截图
     *
     * @param view
     * @return
     */
    public static Bitmap getScreenShotsBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

}
