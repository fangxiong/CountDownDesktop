package com.fax.cddt.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.fax.cddt.R;
import com.fax.cddt.instrument.GlideCircleTransform;
import com.fax.cddt.instrument.GlideRoundTransform;

import androidx.core.content.ContextCompat;

public class GlideUtils {


    /**
     * 加载 gif 方法
     *
     * @param context 当前上下文
     * @param url gif url 地址
     * @param imageView 填充视图
     */
    public static void loadImageWithGif(Context context, String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("image url is null");
        }

        if (isValidContextForGlide(context)) {
            Glide.with(context)
                    .load(url)
                    .asGif()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        }
    }

    public static void loadImageDefault(Context context, String url, ImageView imageView){
        if (TextUtils.isEmpty(url)){
            throw new NullPointerException("image url is null");
        }
        if (isValidContextForGlide(context)){
            RequestManager requestManager = Glide.with(context);
            requestManager.load(url)
                    .into(imageView);
        }
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public static void loadImageCircle(Context context, String url, ImageView imageView){
        loadImageCircleWithBorder(context,url,imageView,0);
    }

    public static void loadImageCircleWithBorder(Context context, String url, ImageView imageView, int border){
        if (TextUtils.isEmpty(url)){
            throw new NullPointerException("image url is null");
        }
        if (isValidContextForGlide(context)){
            Glide.with(context).load( url)
                    .bitmapTransform(new GlideCircleTransform(context,border, ContextCompat.getColor(context,android.R.color.white)))
                    .into(imageView);
        }
    }


    public static void loadImageRound(Context context, String url, ImageView imageView, int radius){
        if (TextUtils.isEmpty(url)){
            throw new NullPointerException("image url is null");
        }
        if (isValidContextForGlide(context)){
            Glide.with(context).load(url)
                    .bitmapTransform(new CenterCrop(context),new GlideRoundTransform(context,radius))
                    .placeholder(R.drawable.loading_img)
                    .error(R.drawable.loading_img)
                    .into(imageView);
        }
    }

    public static void loadImageNoCache(Context context, String url, ImageView imageView){
        if (TextUtils.isEmpty(url)){
            throw new NullPointerException("image url is null");
        }
        if (isValidContextForGlide(context)){
            Glide.with(context)
                    .load(url)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .signature(new StringSignature(String.valueOf(TimeUtils.currentTimeMillis())))
                    .into(imageView);
        }
    }

    public static void loadImgWithPlaceHolder(Context context, String url, ImageView target){
        if (TextUtils.isEmpty(url)){
            throw new NullPointerException("image url is null");
        }
        if (isValidContextForGlide(context)){
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .placeholder(R.drawable.loading_img)
                    .error(R.drawable.loading_img)
                    .into(target);
        }
    }

    private static boolean isLoadNetWorkImage(String url){
        return url.startsWith("http");
    }
}
