package com.fax.showdt.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.fax.showdt.BuildConfig;
import com.fax.showdt.EventMsg;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonViewPagerAdapter;
import com.fax.showdt.bean.User;
import com.fax.showdt.dialog.ios.interfaces.OnDialogButtonClickListener;
import com.fax.showdt.dialog.ios.util.BaseDialog;
import com.fax.showdt.dialog.ios.util.ShareUtils;
import com.fax.showdt.dialog.ios.v3.MessageDialog;
import com.fax.showdt.fragment.MyWidgetFragment;
import com.fax.showdt.fragment.SelectionWidgetFragment;
import com.fax.showdt.permission.EasyPermission;
import com.fax.showdt.permission.GrantResult;
import com.fax.showdt.permission.Permission;
import com.fax.showdt.permission.PermissionRequestListener;
import com.fax.showdt.permission.PermissionUtils;
import com.fax.showdt.service.NLService;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.Constant;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.ToastShowUtils;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.tab.AlphaTabsIndicator;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.just.agentweb.AgentWeb;
import com.tencent.bugly.beta.Beta;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import cn.bmob.v3.BmobUser;
import es.dmoral.toasty.Toasty;

import static com.fax.showdt.utils.CommonUtils.START_QQ_TYPE_GROUP_PROFILE;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private AlphaTabsIndicator alphaTabsIndicator;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView mIvAvatar;
    private TextView mTvNick, mTvLogin;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        checkPermission();
        fragments.add(new SelectionWidgetFragment());
        fragments.add(new MyWidgetFragment());
        pagerAdapter = new CommonViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        alphaTabsIndicator.setViewPager(viewPager);
        initDrawerLayout();
        initNavigationView();
    }

    @Override
    protected void initView() {
        super.initView();
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        viewPager = findViewById(R.id.vp);
        alphaTabsIndicator = findViewById(R.id.alphaIndicator);
        mTvLogin = navigationView.findViewById(R.id.tv_login);
        mIvAvatar = navigationView.getHeaderView(0).findViewById(R.id.iv_avatar);
        mTvNick = navigationView.getHeaderView(0).findViewById(R.id.tv_nick);
        mIvAvatar.setOnClickListener(this);
        mTvNick.setOnClickListener(this);
    }

    private void initDrawerLayout() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent));
    }

    @Subscribe(tags = {@Tag(EventMsg.sign_in_suc_notify_refresh_profile)})
    public void updateUserProfile(Object obj) {
        Log.i("test_receive_msg:","收到更新用户信息的消息");
        updateUserProfileView();
    }

    private void updateUserProfileView() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null && BmobUser.isLogin()) {
            GlideUtils.loadCircleImage(this, user.getAvatarUrl(), mIvAvatar);
            mTvNick.setText(user.getUserNick());
            mTvLogin.setText(getString(R.string.setting_logout));
        }else {
            mIvAvatar.setImageResource(R.drawable.user_avatar_not_login);
            mTvNick.setText(getString(R.string.click_login));
            mTvLogin.setText(getString(R.string.setting_login));
        }
    }

    private void initNavigationView() {
        updateUserProfileView();
        ViewUtils.setNavigationMenuLineStyle(navigationView, getResources().getColor(R.color.c_969696), ViewUtils.dpToPx(0.5f, this));
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_add_qq: {
                        CommonUtils.startQQ(MainActivity.this, START_QQ_TYPE_GROUP_PROFILE, Constant.QQ_GROUP);
                        break;

                    }
                    case R.id.nav_feedback: {
                        FeedbackAPI.openFeedbackActivity();
                        break;
                    }
                    case R.id.nav_help: {
                        Intent intent = new Intent(MainActivity.this, WebActivity.class);
                        intent.putExtra(WebActivity.URL_KEY, Constant.WIDGET_GUIDE);
                        startActivity(intent);
                        break;
                    }
                    case R.id.nav_check_update: {
                        Beta.checkUpgrade(true, false);
                        break;
                    }
                    case R.id.nav_share: {
                        ShareUtils.shareText(MainActivity.this, "我正在使用《魔秀插件》,很好用哦,推荐给你", "我正在使用《魔秀插件》,很好用哦,推荐给你哦\n http://fir.kwgt.top/x12w");
                        break;
                    }
                    case R.id.nav_permission: {
                        showOpenNotificationPermissionDialog();
                        break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected int initStatusBarColor() {
        return R.color.c_171925;
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.iv_menu) {
            drawerLayout.openDrawer(GravityCompat.START);

        } else if (resId == R.id.iv_make) {
            startActivity(new Intent(this, DiyWidgetMakeActivity.class));
        } else if (resId == R.id.iv_add) {
            startActivity(new Intent(this, DiyWidgetMakeActivity.class));
        } else if (resId == R.id.iv_avatar) {
            startActivity(new Intent(this, SignInActivity.class));
        } else if (resId == R.id.tv_nick) {
            startActivity(new Intent(this, SignInActivity.class));
        } else if (resId == R.id.tv_login) {
            if (BmobUser.isLogin()) {
                BmobUser.logOut();
                updateUserProfileView();
            } else {
                startActivity(new Intent(this, SignInActivity.class));
            }
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

    /**
     * 弹出通知监听权限开启提示框
     */
    private void showOpenNotificationPermissionDialog() {
        MessageDialog.show(this, "提示", "开通通知监听权限可更大程度保证你桌面插件运行更加稳定哦!", "推荐开启")
                .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        gotoOpen();
                        return false;
                    }
                });
    }

    private void gotoOpen() {
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                            Beta.checkUpgrade();
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
        } else {
            Beta.checkUpgrade();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
