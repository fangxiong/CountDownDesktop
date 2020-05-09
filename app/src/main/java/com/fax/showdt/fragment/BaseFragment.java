package com.fax.showdt.fragment;


import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fax.showdt.AppContext;
import com.fax.showdt.activity.BaseActivity;


/**
 * Description:     java类作用描述
 * Author:          fax
 * CreateDate:      2020-04-19 11:13
 * Email:           fxiong1995@gmail.com
 */

public abstract class BaseFragment extends Fragment {
    /**
     * fragment是否可见
     */
    private boolean isFragmentVisible;
    /**
     * view是否已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否是第一次加载
     */
    private boolean isFirstLoad = true;
    /**
     * <pre>
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     * 一般用于PagerAdapter需要刷新各个子Fragment的场景
     * 不要new 新的 PagerAdapter 而采取reset数据的方式
     * 所以要求Fragment重新走initData方法
     * 故使用 {@link BaseFragment#setForceLoad(boolean)}来让Fragment下次执行initData
     * </pre>
     */
    private boolean forceLoad = false;


    private View mRootView;

    private BaseActivity mHoldingActivity;

    private boolean isSharedDataFromParent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            mHoldingActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.size() > 0) {
            initVariables(bundle);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
            initViews(savedInstanceState);
            isPrepared = true;
            lazyLoad();
        }
        return mRootView;
    }

    @Nullable
    public final View findViewById(@IdRes int resId) {
        return mRootView == null ? null : mRootView.findViewById(resId);
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden 是否显示出来了
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrepared = false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void onVisible() {
        isFragmentVisible = true;
        lazyLoad();
    }

    protected void onInvisible() {
        isFragmentVisible = false;
    }

    protected void lazyLoad() {
        if (isPrepared() && isFragmentVisible()) {
            if (forceLoad || isFirstLoad()) {
                forceLoad = false;
                isFirstLoad = false;
                initData();
            }
        }
    }

    /**
     * 被ViewPager移出的Fragment 下次显示时会从getArguments()中重新获取数据
     * 所以若需要刷新被移除Fragment内的数据需要重新put数据 eg:
     * Bundle args = getArguments();
     * if (args != null) {
     * args.putParcelable(KEY, info);
     * }
     */
    public void initVariables(Bundle bundle) {
    }

    protected abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initData();

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setForceLoad(boolean forceLoad) {
        this.forceLoad = forceLoad;
    }

    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    public boolean isFragmentVisible() {
        return isFragmentVisible;
    }


    public final Application getApplication() {
        return AppContext.get();
    }


    protected BaseActivity getHoldingActivity() {
        if (mHoldingActivity == null) {
            throw new NullPointerException("HoldingActivity is null");
        }
        return mHoldingActivity;
    }

    protected View getRootView() {
        return mRootView;
    }


}

