package com.fax.cddt.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.widget.AppCompatImageView;

public class FadeInImageView extends AppCompatImageView implements
        Animation.AnimationListener {

    private Animation mFadeInAnimation;

    public FadeInImageView(Context context) {
        super(context);
    }

    public FadeInImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadeInImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void startAnimation(Animation animation) {
        startFadeInAnim();
    }

    private synchronized void startFadeInAnim() {
        if (mFadeInAnimation == null) {
            initAnim();
        }
        setAnimation(mFadeInAnimation);
        mFadeInAnimation.start();
    }

    @Override
    public void setImageResource(int resId) {
        removeAnim();
        super.setImageResource(resId);
        startFadeInAnim();
    }

    @Override
    public void setImageURI(Uri uri) {
        removeAnim();
        super.setImageURI(uri);
        startFadeInAnim();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        removeAnim();
        super.setImageDrawable(drawable);
        startFadeInAnim();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        removeAnim();
        super.setImageBitmap(bm);
        startFadeInAnim();
    }

    private void initAnim() {
        mFadeInAnimation = new AlphaAnimation(0, 1);
        mFadeInAnimation.setDuration(500);
        mFadeInAnimation.setAnimationListener(this);
    }

    private synchronized void removeAnim() {
        if (mFadeInAnimation != null) {
            mFadeInAnimation.cancel();
            setAnimation(null);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        setAnimation(null);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        setImageDrawable(null);
//        setImageBitmap(null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
