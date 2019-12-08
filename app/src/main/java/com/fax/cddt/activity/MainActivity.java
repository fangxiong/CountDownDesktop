package com.fax.cddt.activity;


import android.os.Bundle;
import android.view.View;

import com.fax.cddt.R;
import com.fax.cddt.adapter.CommonViewPagerAdapter;
import com.fax.cddt.fragment.SelectionWidgetFragment;
import com.fax.cddt.utils.ViewUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout mTabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private SelectionWidgetFragment selectionWidgetFragment;
    private String[] titles = new String[]{"精选", "锁屏", "我的"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mImmersionBar = ImmersionBar.with(this);
        //当设置fitsSystemWindows为true和状态栏颜色后就不是沉浸式状态栏了
//        mImmersionBar.fitsSystemWindows(true);
        mImmersionBar.statusBarColor(initStatusBarColor());
        mImmersionBar.navigationBarColor(R.color.c_F7FAFA);
        mImmersionBar.statusBarDarkFont(false).init();

        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));

        for (int i = 0; i < titles.length; i++) {
            fragments.add(new SelectionWidgetFragment());
            mTabLayout.addTab(mTabLayout.newTab());
        }
        pagerAdapter = new CommonViewPagerAdapter(getSupportFragmentManager(), fragments);
        mTabLayout.setupWithViewPager(viewPager, false);
        viewPager.setAdapter(pagerAdapter);

        for (int i = 0; i < titles.length; i++) {
            mTabLayout.getTabAt(i).setText(titles[i]);
        }
        initDrawerLayout();
        initNavigationView();
    }

    @Override
    protected void initView() {
        super.initView();
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        mTabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.vp);

    }

    private void initDrawerLayout(){
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent));
    }
    private void initNavigationView(){
        ViewUtils.setNavigationMenuLineStyle(navigationView,getResources().getColor(R.color.white),ViewUtils.dp2px(0.5f));
        navigationView.setItemIconTintList(null);
    }
    @Override
    protected int initStatusBarColor() {
        return R.color.c_222222;
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if(resId == R.id.iv_menu){
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
