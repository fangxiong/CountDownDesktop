package com.fax.showdt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import cn.bmob.v3.BmobUser;
import es.dmoral.toasty.Toasty;

import com.fax.showdt.EventMsg;
import com.fax.showdt.R;
import com.fax.showdt.bean.LoginRepoUserInfo;
import com.fax.showdt.callback.ILoginCallback;
import com.fax.showdt.manager.FaxUserManager;
import com.fax.showdt.manager.QQLoginManager;
import com.fax.showdt.manager.WeiBoLoginManager;
import com.fax.showdt.utils.ToastShowUtils;


/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 20-1-20
 * Description:
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener, ILoginCallback {

    public static final String SIGN_QQ="sign_qq";
    public static final String SIGN_WEIBO="sign_weibo";
    private String currentSignType = SIGN_QQ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ll_qq_login_content){
            currentSignType = SIGN_QQ;
            QQLoginManager.getInstance(this,this).loginWithQQ();
        }else if(v.getId() == R.id.ll_weibo_login_content){
            currentSignType = SIGN_WEIBO;
            WeiBoLoginManager.getInstance(this,this).loginToSina(this);
        }else if(v.getId() == R.id.iv_back){
            finish();
        }
    }

    @Override
    protected boolean isEnableImmersionBar() {
        return false;
    }

    @Override
    public void authorizeSuc(LoginRepoUserInfo info) {
        Log.i("test_auth_info:",info.toJSONString());
        if(SIGN_QQ.equals(currentSignType)) {
            FaxUserManager.getInstance().thirdSingupLogin(this, BmobUser.BmobThirdUserAuth.SNS_TYPE_QQ, info);
        }else if(SIGN_WEIBO.equals(currentSignType)){
            FaxUserManager.getInstance().thirdSingupLogin(this, BmobUser.BmobThirdUserAuth.SNS_TYPE_WEIBO, info);
        }
    }

    @Override
    public void authorizeFail(String msg) {
        ToastShowUtils.showCommonToast(this,msg,Toasty.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SIGN_WEIBO.equals(currentSignType)) {
            WeiBoLoginManager.getInstance(this,this).getmSsoHandler().authorizeCallBack(requestCode,resultCode,data);
        }else if(SIGN_QQ.equals(currentSignType)){
            QQLoginManager.getInstance(this,this).onActivityResultData(requestCode, resultCode, data);
        }
    }
}
