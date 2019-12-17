package com.fax.showdt.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.fax.showdt.BuildConfig;
import com.fax.showdt.R;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-16
 * Description:
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout mRlLock,mRlFeedback,mRlCheckVersion,mRlHelp,mRlShare,mRlMarket;
    private SwitchButton mSwitchButton;
    private TextView mTvVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        mRlLock = findViewById(R.id.rl_lock_screen);
        mRlFeedback = findViewById(R.id.rl_feedback);
        mRlCheckVersion = findViewById(R.id.rl_check_version);
        mRlHelp = findViewById(R.id.rl_help);
        mRlShare = findViewById(R.id.rl_share);
        mRlMarket = findViewById(R.id.rl_market_scoring);
        mSwitchButton = findViewById(R.id.switch_btn);
        mTvVersion = findViewById(R.id.tv_version);
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        mTvVersion.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    protected boolean isEnableImmersionBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int resId =v.getId();
        if(resId == R.id.rl_feedback){
            FeedbackAPI.openFeedbackActivity();
        }else if(resId == R.id.rl_check_version){

        }else if(resId == R.id.rl_help){

        }else if(resId == R.id.rl_share){

        }else if(resId == R.id.rl_market_scoring){

        }
    }
}
