package com.fax.showdt.activity;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.fax.lib.config.ConfigManager;
import com.fax.showdt.BuildConfig;
import com.fax.showdt.ConstantString;
import com.fax.showdt.EventMsg;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonViewPagerAdapter;
import com.fax.showdt.bean.User;
import com.fax.showdt.dialog.ios.interfaces.OnDialogButtonClickListener;
import com.fax.showdt.dialog.ios.interfaces.OnInputDialogButtonClickListener;
import com.fax.showdt.dialog.ios.util.BaseDialog;
import com.fax.showdt.dialog.ios.util.InputInfo;
import com.fax.showdt.dialog.ios.util.ShareUtils;
import com.fax.showdt.dialog.ios.util.TextInfo;
import com.fax.showdt.dialog.ios.v3.InputDialog;
import com.fax.showdt.dialog.ios.v3.MessageDialog;
import com.fax.showdt.fragment.MyWidgetFragment;
import com.fax.showdt.fragment.SelectionWidgetFragment;
import com.fax.showdt.permission.EasyPermission;
import com.fax.showdt.permission.GrantResult;
import com.fax.showdt.permission.Permission;
import com.fax.showdt.permission.PermissionRequestListener;
import com.fax.showdt.permission.PermissionUtils;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.Constant;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.ToastShowUtils;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.ComboCounter;
import com.fax.showdt.view.tab.AlphaTabsIndicator;
import com.google.android.material.navigation.NavigationView;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.meituan.android.walle.WalleChannelReader;
import com.tencent.bugly.beta.Beta;
import com.umeng.commonsdk.statistics.common.DeviceConfig;

import java.util.ArrayList;
import java.util.Map;

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
    private ComboCounter comboCounter;

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
        comeInManageMode();
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
                        ShareUtils.shareText(MainActivity.this, "我正在使用《魔秀插件》,很好用哦,推荐给你", "我正在使用《魔秀插件》,很好用哦,推荐给你哦\n https://www.coolapk.com/apk/com.fax.showdt#tt_daymode=1&tt_font=m");
                        break;
                    }
                    case R.id.nav_permission: {
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
//                            Beta.checkUpgrade();
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
//            Beta.checkUpgrade();
        }
    }

    /**
     * 点击首页标题 进入管理模式
     */
    private void comeInManageMode() {
        findViewById(R.id.titleView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comboCounter == null){
                    comboCounter = new ComboCounter().setMaxComboCount(5)
                            .setOnComboListener(new ComboCounter.OnComboListener() {
                                @Override
                                public void onCombo(ComboCounter counter, int comboCount, int maxCount) {}

                                @Override
                                public void onComboReach(ComboCounter counter, int comboCount, int maxCount) {
//                            String channel = "渠道：" + WalleChannelReader.getChannel(MainActivity.this);
//                            String message = "包类型：" + BuildConfig.BUILD_TYPE +
//                                    "\n版本号：" + BuildConfig.VERSION_CODE +
//                                    "(" + BuildConfig.VERSION_NAME + ")" +
//                                    "\n" +channel;
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setMessage(message)
//                                    .setCancelable(false)
//                                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//
//                                        }
//                                    })
//                                    .setNegativeButton("复制友盟信息", (dialog, which) -> {
//                                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                                        ClipData mClipData = ClipData.newPlainText("umeng", umengJson);
//                                        cm.setPrimaryClip(mClipData);
//                                        dialog.dismiss();
//                                    })
//                                    .create()
//                                    .show();
                                    InputDialog.build(((AppCompatActivity)MainActivity.this))
                                            .setButtonTextInfo(new TextInfo().setFontColor(getResources().getColor(R.color.c_A0A0A0)))
                                            .setTitle("提示").setMessage("请输入管理员密码")
                                            .setOkButton("进入管理员模式", new OnInputDialogButtonClickListener() {
                                                @Override
                                                public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
                                                    if(!TextUtils.isEmpty(inputStr) && inputStr.equals("15850451266")) {
                                                        ConfigManager.getMainConfig().putBool("can_contribute",true);
                                                        Log.e("umeng_device_info1:",getTestDeviceInfo(MainActivity.this)[0]);
                                                        Log.e("umeng_device_info2:",getTestDeviceInfo(MainActivity.this)[1]);
                                                        ToastShowUtils.showCommonToast(MainActivity.this,"可以投稿了",Toasty.LENGTH_SHORT);
                                                    }
                                                    return false;

                                                }
                                            })
                                            .setCancelButton("取消")
                                            .setHintText("请输入密码")
                                            .setInputInfo(new InputInfo()
                                                    .setInputType(InputType.TYPE_CLASS_NUMBER)
                                                    .setTextInfo(new TextInfo()
                                                            .setFontColor(getResources().getColor(R.color.c_A0A0A0))
                                                    )
                                            )
                                            .setCancelable(false)
                                            .show();
                                }
                            });
                    comboCounter.setEnable(true);
                }
                comboCounter.click();
            }
        });
        findViewById(R.id.titleView).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(BuildConfig.DEBUG){
                    startActivity(new Intent(MainActivity.this,TestActivity.class));
                }
                return false;
            }
        });
    }

    public static String[] getTestDeviceInfo(Context context){
        String[] deviceInfo = new String[2];
        try {
            if(context != null){
                deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(context);
                deviceInfo[1] = DeviceConfig.getMac(context);
            }
        } catch (Exception e){
        }
        return deviceInfo;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
