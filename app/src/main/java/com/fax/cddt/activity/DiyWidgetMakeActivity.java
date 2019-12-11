package com.fax.cddt.activity;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fax.cddt.R;
import com.fax.cddt.callback.WidgetEditTextCallback;
import com.fax.cddt.fragment.WidgetProgressEditFragment;
import com.fax.cddt.fragment.WidgetShapeEditFragment;
import com.fax.cddt.fragment.WidgetStickerEditFragment;
import com.fax.cddt.fragment.WidgetTextEditFragment;
import com.fax.cddt.manager.widget.WidgetConfig;
import com.fax.cddt.utils.BitmapUtils;
import com.fax.cddt.utils.ViewUtils;
import com.fax.cddt.view.EventConvertView;
import com.fax.cddt.view.sticker.BitmapStickerIcon;
import com.fax.cddt.view.sticker.DeleteIconEvent;
import com.fax.cddt.view.sticker.Sticker;
import com.fax.cddt.view.sticker.StickerView;
import com.fax.cddt.view.sticker.TextSticker;
import com.fax.cddt.view.sticker.ZoomIconEvent;
import com.gyf.barlibrary.ImmersionBar;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-9
 * Description:
 */
public class DiyWidgetMakeActivity extends BaseActivity implements View.OnClickListener {

    private StickerView mStickerView;
    private EventConvertView flEditBody;
    private ImageView mStickerViewBg;
    private RelativeLayout mRLEditBody;
    private boolean mEditPaneShowing = false;
    private WidgetTextEditFragment mTextEditFragment;
    private WidgetStickerEditFragment mStickerEditFragment;
    private WidgetShapeEditFragment mShapeEditFragment;
    private WidgetProgressEditFragment mProgressEditFragment;
    private Sticker mHandlingSticker;
    private LongSparseArray<Sticker> mStickerList;

    enum EditType {
        EDIT_TEXT, EDIT_STICKER, EDIT_SHAPE, EDIT_PROGRESS
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diy_widget_make_activity);


    }

    @Override
    protected void initView() {
        super.initView();
        mStickerView = findViewById(R.id.sticker_view);
        mStickerViewBg = findViewById(R.id.iv_select_bg);
        flEditBody = findViewById(R.id.fl_edit_body);
        mRLEditBody = findViewById(R.id.rl_edit_body);
        initStatusBar();
        initStickerView();
        initStickerViewBg();
        intervelRefreshStickerView();
        initAllEditFragments();
        initData();

    }

    private void initData(){
        mStickerList = new LongSparseArray<>();
    }
    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.tv_text) {
            TextSticker textSticker = new TextSticker(System.currentTimeMillis());
            textSticker.setTextColor("#FFFFFF");
            textSticker.setFontPath("fonts/ButterTangXin-Italic.ttf");
            mTextEditFragment.setWidgetEditTextSticker(textSticker);
            mStickerView.addSticker(textSticker, Sticker.Position.TOP);
            switchToOneFragment(EditType.EDIT_TEXT);
        } else if (resId == R.id.tv_sticker) {
            switchToOneFragment(EditType.EDIT_STICKER);
        } else if (resId == R.id.tv_shape) {
            switchToOneFragment(EditType.EDIT_SHAPE);
        } else if (resId == R.id.tv_progress) {
            switchToOneFragment(EditType.EDIT_PROGRESS);
        }
    }

    /**
     * 初始化状态栏
     */
    private void initStatusBar() {
        ImmersionBar.hideStatusBar(getWindow());
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.fullScreen(true);
        mImmersionBar.init();
    }

    private void initStickerView() {
        FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams) mStickerView.getLayoutParams();
        params.width = WidgetConfig.getWidgetWidth();
        params.height = WidgetConfig.getWidget4X4Height();
        mStickerView.setLayoutParams(params);
        params = (FrameLayout.MarginLayoutParams) flEditBody.getLayoutParams();
        params.width = WidgetConfig.getWidgetWidth();
        flEditBody.setLayoutParams(params);
        flEditBody.setEventConvertView(mStickerView);
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());
        mStickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon));
        mStickerView.setConstrained(true);
        mStickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                mStickerList.put(sticker.getId(), sticker);
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                Log.i("test_click:","click");
                if(sticker instanceof TextSticker){
                    switchToOneFragment(EditType.EDIT_TEXT);
                }
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                mStickerList.delete(sticker.getId());
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                mHandlingSticker = sticker;
            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {
                Log.i("test_click:","touch");
                mHandlingSticker = mStickerView.getCurrentSticker();
                switchToOneFragment(EditType.EDIT_TEXT);
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                if(sticker instanceof TextSticker){
                     mTextEditFragment.showInputDialog(100);
                }
            }

            @Override
            public void onStickerNoTouched() {
                if(mEditPaneShowing) {
                    setEditBodySlideOutAnimation();
                    mEditPaneShowing = false;
                }
                mHandlingSticker = null;
            }

            @Override
            public void onClickedBindAppIcon(@NonNull Sticker sticker) {

            }

            @Override
            public void onCopySticker(@NonNull Sticker sticker) {

            }

            @Override
            public void onUnlock() {

            }
        });
    }


    private Bitmap getSystemBitmap() {
        Drawable drawable = WallpaperManager.getInstance(this).getDrawable();
        return BitmapUtils.drawableToBitmap(drawable);
    }

    private Bitmap clipWidgetSizeBitmap(Bitmap bitmap) {
        float w = bitmap.getWidth(); // 得到图片的宽，高
        int cropWidth = (int) (w * WidgetConfig.WIDGET_MAX_WIDTH_RATIO);// 裁切后所取的正方形区域边长
        int x = (int) ((w - cropWidth) * 1.0f / 2);
        int marginTop = ViewUtils.dp2px(60);
        int cropHeight = cropWidth;
        return Bitmap.createBitmap(bitmap, x, marginTop, cropWidth, cropHeight, null, false);
    }

    private void initStickerViewBg() {
        Bitmap bitmap = getSystemBitmap();
        if (bitmap != null) {
            Bitmap resultBitmap = clipWidgetSizeBitmap(bitmap);
            mStickerViewBg.setImageBitmap(resultBitmap);
        } else {

        }
    }

    private void intervelRefreshStickerView() {
        addDisponsable(Observable.interval(30, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mStickerView.invalidate();
                    }
                }));
    }

    private void initAllEditFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mTextEditFragment = new WidgetTextEditFragment(this);
        mStickerEditFragment = new WidgetStickerEditFragment();
        mShapeEditFragment = new WidgetShapeEditFragment();
        mProgressEditFragment = new WidgetProgressEditFragment();
        transaction.add(R.id.rl_edit_body, mTextEditFragment);
        transaction.add(R.id.rl_edit_body, mStickerEditFragment);
        transaction.add(R.id.rl_edit_body, mShapeEditFragment);
        transaction.add(R.id.rl_edit_body, mProgressEditFragment);
        transaction.commitAllowingStateLoss();
        ((WidgetTextEditFragment)mTextEditFragment).setWidgetEditTextCallback(new WidgetEditTextCallback() {
            @Override
            public void onAddSticker() {
                TextSticker textSticker = new TextSticker(System.currentTimeMillis());
                textSticker.setTextColor("#FFFFFF");
                textSticker.setFontPath("fonts/ButterTangXin-Italic.ttf");
                mTextEditFragment.setWidgetEditTextSticker(textSticker);
                mStickerView.addSticker(textSticker, Sticker.Position.TOP);
            }
        });
    }

    private void setEditBodySlideInAnimation() {
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        mRLEditBody.startAnimation(ctrlAnimation);
    }

    private void setEditBodySlideOutAnimation() {
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        ctrlAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRLEditBody.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRLEditBody.startAnimation(ctrlAnimation);
    }

    private void switchToOneFragment(EditType editType) {
        if(!mEditPaneShowing) {
            setEditBodySlideInAnimation();
        }
        mEditPaneShowing = true;
        mRLEditBody.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (editType) {
            case EDIT_TEXT: {
                transaction.hide(mStickerEditFragment);
                transaction.hide(mShapeEditFragment);
                transaction.hide(mProgressEditFragment);
                transaction.show(mTextEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case EDIT_STICKER: {
                transaction.hide(mTextEditFragment);
                transaction.hide(mShapeEditFragment);
                transaction.hide(mProgressEditFragment);
                transaction.show(mStickerEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case EDIT_SHAPE: {
                transaction.hide(mTextEditFragment);
                transaction.hide(mStickerEditFragment);
                transaction.hide(mProgressEditFragment);
                transaction.show(mShapeEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case EDIT_PROGRESS: {
                transaction.hide(mTextEditFragment);
                transaction.hide(mStickerEditFragment);
                transaction.hide(mShapeEditFragment);
                transaction.show(mProgressEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mEditPaneShowing){
            mEditPaneShowing = false;
            setEditBodySlideOutAnimation();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
