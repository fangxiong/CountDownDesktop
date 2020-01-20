package com.fax.showdt.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.fax.showdt.R;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 20-1-20
 * Description:
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tv_login){
            Platform qq = ShareSDK.getPlatform(QQ.NAME);
            authorize(qq);
        }
    }

    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }
        //判断指定平台是否已经完成授权
        if(plat.isAuthValid()) {
            String userId = plat.getDb().getUserId();
            if (userId != null) {
//                login(plat.getName(), userId, null);
//                return;
            }
        }
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        // true不使用SSO授权，false使用SSO授权
        plat.SSOSetting(true);
        ShareSDK.setActivity(this);//抖音登录适配安卓9.0
        //获取用户资料
        plat.showUser(null);
    }
}
