package com.fax.showdt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gyf.barlibrary.ImmersionBar;

import androidx.annotation.Nullable;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.hideStatusBar(getWindow());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },3000);
    }

    @Override
    protected boolean isEnableImmersionBar() {
        return false;
    }
}
