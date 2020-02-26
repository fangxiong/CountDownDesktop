package com.fax.showdt.manager;

import android.app.Activity;
import android.content.Intent;
import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.bean.LoginRepoUserInfo;
import com.fax.showdt.callback.ILoginCallback;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.ToastShowUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class QQLoginManager implements IUiListener {

    private Activity context;
    private ILoginCallback mCallback;
    private Tencent mTencent;
    private LoginRepoUserInfo mLoginRepoUserInfo;
    private static QQLoginManager mInstance;

    private QQLoginManager(Activity activity, ILoginCallback callback) {
        context = activity;
        mCallback = callback;
    }

    public static QQLoginManager getInstance(Activity activity, ILoginCallback callback) {
        if(mInstance == null){
            synchronized (QQLoginManager.class){
                if(mInstance == null){
                    mInstance = new QQLoginManager(activity,callback);
                }
            }
        }
        return mInstance;
    }

    public void loginWithQQ() {
        if (CommonUtils.isQQAvilible(context)) {
            mTencent  = Tencent.createInstance(ConstantString.TENCENT_APP_ID, context);
            mTencent.login(context, "all", this);
        } else {
            if (mCallback != null) {
                mCallback.authorizeFail(context.getString(R.string.not_installed_qq));
            }
        }
    }


    /**
     * 登录成功回调
     * 返回 openId token 再利用这些参数去请求用户信息
     *
     * @param o
     */
    @Override
    public void onComplete(Object o) {
        JSONObject jsonObject = (JSONObject) o;
        initOpenidAndToken(jsonObject);

        getUserInfo();
    }

    @Override
    public void onError(UiError uiError) {
        if (mCallback != null) {
            mCallback.authorizeFail(uiError.errorMessage);
        }
        ToastShowUtils.showCommonToast(context, context.getString(R.string.authorize_fail_msg), Toasty.LENGTH_SHORT);

    }

    @Override
    public void onCancel() {
        if (mCallback != null) {
            mCallback.authorizeFail("取消授权");
        }
    }

    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String openid = jsonObject.getString("openid");//获取OpenId
            String token = jsonObject.getString("access_token");
            String expires = jsonObject.getString("expires_in");
            mLoginRepoUserInfo = new LoginRepoUserInfo();
            mLoginRepoUserInfo.setOpenId(openid);
            mLoginRepoUserInfo.setToken(token);
            mLoginRepoUserInfo.setExpires(expires);
            mTencent.setAccessToken(token, expires);
            mTencent.setOpenId(openid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getUserInfo() {
        QQToken mQQToken = mTencent.getQQToken();
        UserInfo userInfo = new UserInfo(context, mQQToken);
        userInfo.getUserInfo(new IUiListener() {
                                 @Override
                                 public void onComplete(final Object o) {
                                     try {
                                         JSONObject jsonObject = new JSONObject(o.toString());
                                         String mNickname = jsonObject.getString("nickname");//获取用户QQ名称
                                         String mGender = jsonObject.getString("gender");//获取用户性别
                                         String mUserHeader = jsonObject.getString("figureurl_qq");//获取用户头像
                                         String city = jsonObject.getString("city");
                                         String province = jsonObject.getString("province");
                                         String year = jsonObject.getString("year");
                                         mLoginRepoUserInfo.setNickname(mNickname);
                                         mLoginRepoUserInfo.setAvatarUrl(mUserHeader);
                                         mLoginRepoUserInfo.setGender(mGender);
                                         mLoginRepoUserInfo.setCity(city);
                                         mLoginRepoUserInfo.setProvince(province);
                                         mLoginRepoUserInfo.setYear(year);
                                         if (mCallback != null) {
                                             mCallback.authorizeSuc(mLoginRepoUserInfo);
                                         }
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }
                                 }

                                 @Override
                                 public void onError(UiError uiError) {
                                     if (mCallback != null) {
                                         mCallback.authorizeFail(uiError.errorMessage);
                                     }
                                 }

                                 @Override
                                 public void onCancel() {
                                     if (mCallback != null) {
                                         mCallback.authorizeFail(context.getString(R.string.get_qq_userinfo_fail));
                                     }
                                 }
                             }
        );
    }

    public void onActivityResultData(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, this);

    }


}
