package com.fax.showdt.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import com.fax.lib.config.ConfigManager;
import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.dialog.ios.interfaces.OnDialogButtonClickListener;
import com.fax.showdt.dialog.ios.util.BaseDialog;
import com.fax.showdt.dialog.ios.v3.MessageDialog;
import com.fax.showdt.utils.Constant;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-16
 * Description:
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private SwitchButton mSwitchButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        mSwitchButton = findViewById(R.id.switch_btn);
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getMainConfig().putBool(ConstantString.not_support_click_widget_switch,isChecked);
            }
        });
        mSwitchButton.setChecked(ConfigManager.getMainConfig().getBool(ConstantString.not_support_click_widget_switch,false));
    }

    @Override
    protected boolean isEnableImmersionBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int resId =v.getId();
        if(resId == R.id.rl_open_permission){
            showOpenNotificationPermissionDialog();
        }else if(resId == R.id.iv_back){
            finish();
        }else if(resId == R.id.tv_privacy){
            Intent intent = new Intent(SettingActivity.this, WebActivity.class);
            intent.putExtra(WebActivity.URL_KEY, Constant.WIDGET_PRIVACY_URL);
            startActivity(intent);
        }else if(resId == R.id.tv_agreement){
            Intent intent = new Intent(SettingActivity.this, WebActivity.class);
            intent.putExtra(WebActivity.URL_KEY, Constant.WIDGET_PRIVACY_URL);
            startActivity(intent);
        }
    }
    /**
     * 弹出通知监听权限开启提示框
     */
    private void showOpenNotificationPermissionDialog() {
        MessageDialog.show(this, "提示", "开通通知监听权限可更大程度保证你桌面插件运行更加稳定哦!", "推荐开启")
                .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
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
                        }                        return false;
                    }
                });
    }

}
