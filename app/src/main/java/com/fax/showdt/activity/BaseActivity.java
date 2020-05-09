package com.fax.showdt.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fax.showdt.R;
import com.fax.showdt.utils.ScreenUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.hwangjr.rxbus.RxBus;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.jessyan.autosize.AutoSizeCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public abstract class BaseActivity extends AppCompatActivity {


    private boolean isViewInited = false;
    protected ImmersionBar mImmersionBar;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public Resources getResources() {
        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));
        return super.getResources();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.adaptScreen4VerticalSlide(this, 360);
        initImmersionBar();
        RxBus.get().register(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //统一每个界面都隐藏软键盘
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 隐藏软键盘
        if (imm != null) {
            imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
        }
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenUtils.cancelAdaptScreen(this);
        mCompositeDisposable.clear();
        RxBus.get().unregister(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        tryInitViews();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        tryInitViews();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        tryInitViews();
    }

    private void tryInitViews() {
        if (!isViewInited) {
            isViewInited = true;
            initView();
        }
    }

    protected void initView() {
    }

    protected boolean onLeftClick() {
        return false;
    }

    protected boolean onRightClick() {
        return false;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private String[] checkPermissions(final String[] permissions) {
        final String[] data = new String[permissions.length];
        int index = 0;
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PERMISSION_GRANTED) {
                data[index++] = permission;
            }
        }
        return index <= 0 ? null : Arrays.copyOf(data, index);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 当返回false后 状态栏的颜色默认为白色  如需修改颜色调用{@link #initStatusBarColor()}
     *
     * @return
     */
    protected boolean isEnableImmersionBar() {
        return true;
    }

    /**
     * 默认为沉浸式状态栏
     */
    private void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        if (!isEnableImmersionBar()) {
            //当设置fitsSystemWindows为true和状态栏颜色后就不是沉浸式状态栏了
            mImmersionBar.fitsSystemWindows(true);
            mImmersionBar.statusBarColor(initStatusBarColor());
        }
        mImmersionBar.navigationBarColor(R.color.c_171925);
        mImmersionBar.init();
    }

    protected ImmersionBar getImmersionBar() {
        return mImmersionBar;
    }

    /**
     * 设置状态栏颜色(设置后默认取消沉浸式状态栏)
     * 注意：不设置状态栏颜色则默认为沉浸式状态栏,布局会延伸到屏幕顶部
     * (1).当使用封装好的TitleView后 它会识别如果是沉浸式 会将标题栏的高度加上状态栏的高度 保持正常
     * (2).当没使用TitleView来充当状态栏 会出现状态栏和标题栏重叠情况,这时候重写isEnableImmersionBar()返回false即可去掉沉浸式
     */
    protected @ColorRes
    int initStatusBarColor() {
        return R.color.c_171925;
    }

    /**
     * 是否使用默认的动画
     *
     * @return
     */
    protected boolean isUseDefaultAnim() {
        return true;
    }

    public void addDisponsable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }
}
