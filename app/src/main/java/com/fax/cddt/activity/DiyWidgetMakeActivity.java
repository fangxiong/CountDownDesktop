package com.fax.cddt.activity;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.fax.cddt.R;
import com.fax.cddt.manager.widget.WidgetConfig;
import com.fax.cddt.utils.BitmapUtils;
import com.fax.cddt.utils.ViewUtils;
import com.fax.cddt.view.EventConvertView;
import com.fax.cddt.view.sticker.StickerView;
import com.gyf.barlibrary.ImmersionBar;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-9
 * Description:
 */
public class DiyWidgetMakeActivity extends BaseActivity {

    private StickerView mStickerView;
    private EventConvertView flEditBody;
    private ImageView mStickerViewBg;
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
        initStatusBar();
        initStickerView();
        initStickerViewBg();
        intervelRefreshStickerView();

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
        params = (FrameLayout.MarginLayoutParams)flEditBody.getLayoutParams();
        params.width = WidgetConfig.getWidgetWidth();
        flEditBody.setLayoutParams(params);
        flEditBody.setEventConvertView(mStickerView);
    }


    private Bitmap getSystemBitmap() {
        Drawable drawable = WallpaperManager.getInstance(this).getDrawable();
        return BitmapUtils.drawableToBitmap(drawable);
    }

    private Bitmap clipWidgetSizeBitmap(Bitmap bitmap) {
        float w = bitmap.getWidth(); // 得到图片的宽，高
        int cropWidth =(int)(w*WidgetConfig.WIDGET_MAX_WIDTH_RATIO);// 裁切后所取的正方形区域边长
        int x = (int)((w-cropWidth)*1.0f/2);
        int marginTop = ViewUtils.dp2px(60);
        cropWidth /= 2;
        int cropHeight = cropWidth;
        return Bitmap.createBitmap(bitmap, x, marginTop, cropWidth, cropHeight, null, false);
    }

    private void initStickerViewBg(){
        Bitmap bitmap = getSystemBitmap();
        if(bitmap != null){
            Bitmap resultBitmap = clipWidgetSizeBitmap(bitmap);
            mStickerViewBg.setImageBitmap(resultBitmap);
        }else {

        }
    }

    private void intervelRefreshStickerView(){
        addDisponsable(Observable.interval(30, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mStickerView.invalidate();
                    }
                }));
    }



}
