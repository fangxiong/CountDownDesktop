package com.fax.showdt.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.fax.showdt.BuildConfig;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonViewPagerAdapter;
import com.fax.showdt.dialog.ios.interfaces.OnDialogButtonClickListener;
import com.fax.showdt.dialog.ios.util.BaseDialog;
import com.fax.showdt.dialog.ios.v3.MessageDialog;
import com.fax.showdt.fragment.MyWidgetFragment;
import com.fax.showdt.fragment.SelectionWidgetFragment;
import com.fax.showdt.permission.EasyPermission;
import com.fax.showdt.permission.GrantResult;
import com.fax.showdt.permission.Permission;
import com.fax.showdt.permission.PermissionRequestListener;
import com.fax.showdt.permission.PermissionUtils;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.ViewUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static com.fax.showdt.utils.CommonUtils.START_QQ_TYPE_GROUP_PROFILE;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout mTabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = new String[]{"精选", "我的"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        checkPermission();
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarColor(initStatusBarColor());
        mImmersionBar.navigationBarColor(R.color.c_F7FAFA);
        mImmersionBar.statusBarDarkFont(false).init();
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
        fragments.add(new SelectionWidgetFragment());
        fragments.add(new MyWidgetFragment());
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

    private void initDrawerLayout() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent));
    }

    private void initNavigationView() {
        ViewUtils.setNavigationMenuLineStyle(navigationView, getResources().getColor(R.color.white), ViewUtils.dp2px(0.5f));
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_widget: {

                        break;
                    }
                    case R.id.nav_lock: {
                        break;
                    }
                    case R.id.nav_add_qq: {
                        CommonUtils.startQQ(MainActivity.this, START_QQ_TYPE_GROUP_PROFILE, "721030399");
                        break;

                    }
                    case R.id.nav_setting: {
                        startActivity(new Intent(MainActivity.this,SettingActivity.class));
                        break;

                    }
                }
                return false;
            }
        });

    }

    @Override
    protected int initStatusBarColor() {
        return R.color.c_222222;
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.iv_menu) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (resId == R.id.iv_make) {
            startActivity(new Intent(this, DiyWidgetMakeActivity.class));
        }
    }

    private void showPermissionReqDialog(final String[] perms) {
        MessageDialog.show(this, "提示", "你需要开启以下权限", "知道了")
                .setCustomView(R.layout.req_app_permission_dialog, new MessageDialog.OnBindView() {
                    @Override
                    public void onBind(MessageDialog dialog, View v) {
                        //绑定布局事件
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                requestPermission(perms);
                            }
                        });
                    }
                }).setOnOkButtonClickListener(new OnDialogButtonClickListener() {
            @Override
            public boolean onClick(BaseDialog baseDialog, View v) {
                requestPermission(perms);
                return false;
            }
        });
    }

    private void requestPermission(String[] perms) {
        EasyPermission.with(this)
                .addPermissions(perms)
                .request(new PermissionRequestListener() {
                    @Override
                    public void onGrant(Map<String, GrantResult> result) {
                        boolean isEnableNext = true;
                        for (GrantResult grantResult : result.values()) {
                            if (grantResult.getValue() == GrantResult.DENIED.getValue()) {
                                isEnableNext = false;
                                break;
                            }
                        }
                        if (isEnableNext) {
                            if (BuildConfig.DEBUG) {
//                                Toast.makeText().Short("获得全部权限");
                            }
                        } else {
                            checkPermission();
                        }
                    }

                    @Override
                    public void onCancel(String stopPermission) {
                        checkPermission();
                    }
                });
    }

    private void checkPermission() {
        final String[] perms = new String[]{Permission.WRITE_EXTERNAL_STORAGE,
                Permission.READ_PHONE_STATE, Permission.ACCESS_COARSE_LOCATION,
                Permission.ACCESS_FINE_LOCATION};
        if (!PermissionUtils.isHasElfPermission(this)) {
            showPermissionReqDialog(perms);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
