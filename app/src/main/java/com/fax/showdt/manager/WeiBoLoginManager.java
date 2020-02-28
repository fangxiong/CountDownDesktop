package com.fax.showdt.manager;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.fax.showdt.AppContext;
import com.fax.showdt.ConstantString;
import com.fax.showdt.bean.LoginRepoUserInfo;
import com.fax.showdt.callback.ILoginCallback;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class WeiBoLoginManager {

    private ILoginCallback mCallback;
    private static WeiBoLoginManager mInstance;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    private LoginRepoUserInfo loginRepoUserInfo;

    private WeiBoLoginManager(ILoginCallback callback) {
        mCallback = callback;
        WbSdk.install(AppContext.get(),new AuthInfo(AppContext.get(), ConstantString.APP_KEY_SINA, ConstantString.REDIRECT_URL,
                ConstantString.SCOPE));
        loginRepoUserInfo = new LoginRepoUserInfo();
    }

    public static WeiBoLoginManager getInstance(ILoginCallback callback) {
        if(mInstance == null){
            synchronized (QQLoginManager.class){
                if(mInstance == null){
                    mInstance = new WeiBoLoginManager(callback);
                }
            }
        }
        return mInstance;
    }

    //吊起新浪微博客户端授权，如果未安装这使用web授权
    public void loginToSina(Activity context) {
        //授权方式有三种，第一种对客户端授权 第二种对Web短授权，第三种结合前两中方式
        mSsoHandler = new SsoHandler(context);
        mSsoHandler.authorize(new SelfWbAuthListener());
    }

    public SsoHandler getmSsoHandler(){
        return mSsoHandler;
    }

    private class SelfWbAuthListener implements WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mAccessToken = token;
                    loginRepoUserInfo.setExpires(String.valueOf(token.getExpiresTime()));
                    loginRepoUserInfo.setOpenId(token.getUid());
                    loginRepoUserInfo.setToken(token.getToken());
                    if (mAccessToken.isSessionValid()) {
                        OkHttpUtils.get()
                                .url("https://api.weibo.com/2/users/show.json")
                                .addParams("access_token",mAccessToken.getToken())
                                .addParams("uid",mAccessToken.getUid())
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        e.printStackTrace();
                                        mCallback.authorizeFail("请求用户信息失败");
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        Log.i("test_weibo:",response);
                                        JSONObject jsonObject = JSONObject.parseObject(response);
                                        loginRepoUserInfo.setGender(jsonObject.getString("gender").equals("m") ?"男":"女");
                                        loginRepoUserInfo.setNickname(jsonObject.getString("name"));
                                        loginRepoUserInfo.setAvatarUrl(jsonObject.getString("profile_image_url"));
                                        mCallback.authorizeSuc(loginRepoUserInfo);
                                    }
                                });

                    }
                }
            });
        }

        @Override
        public void cancel() {
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            mCallback.authorizeFail(errorMessage.getErrorMessage());
        }
    }



}
