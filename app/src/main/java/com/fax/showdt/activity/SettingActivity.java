package com.fax.showdt.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.fax.showdt.BuildConfig;
import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.bean.TestData;
import com.fax.showdt.bean.VersionUpdateBean;
import com.fax.showdt.dialog.ios.interfaces.OnDialogButtonClickListener;
import com.fax.showdt.dialog.ios.util.BaseDialog;
import com.fax.showdt.dialog.ios.util.ShareUtils;
import com.fax.showdt.dialog.ios.v3.MessageDialog;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.utils.ToastShowUtils;
import com.kyleduo.switchbutton.SwitchButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Set;

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
    private TipDialog mTipsDialog;

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
            reqUpdateVersionData();
        }else if(resId == R.id.rl_help){

        }else if(resId == R.id.rl_share){
            ShareUtils.shareText(this,"我正在使用《桌面秀》,很好用哦,推荐给你","我正在使用《桌面秀》,很好用哦,推荐给你哦\nhttp://www.baidu.com");
        }else if(resId == R.id.rl_market_scoring){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(intent);
        }else if(resId == R.id.iv_back){
            finish();
        }
    }

    private void reqUpdateVersionData(){
        mTipsDialog = WaitDialog.show(this,"加载中...");
        OkHttpUtils.get().url(ConstantString.req_version_update).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mTipsDialog.doDismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                mTipsDialog.doDismiss();
                Log.i("test_result;",response);
                if(response != null){
                    VersionUpdateBean versionUpdateBean = GsonUtils.parseJsonWithGson(response,VersionUpdateBean.class);
                    if(versionUpdateBean != null){
                        int version = versionUpdateBean.getVersion();
                        if(BuildConfig.VERSION_CODE < version){
                            MessageDialog.show(SettingActivity.this,"提示","你的版本不是最新，是否更新到最新版")
                                    .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                                        @Override
                                        public boolean onClick(BaseDialog baseDialog, View v) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                            startActivity(intent);
                                            return false;
                                        }
                                    });
                        }else {
                            ToastShowUtils.showCommonToast(SettingActivity.this,"已经是最新版本啦",Toasty.LENGTH_LONG);
                        }
                    }
                }
            }
        });
    }

}
