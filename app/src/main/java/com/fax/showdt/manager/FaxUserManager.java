package com.fax.showdt.manager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fax.showdt.EventMsg;
import com.fax.showdt.bean.LoginRepoUserInfo;
import com.fax.showdt.bean.User;
import com.fax.showdt.utils.ToastShowUtils;
import com.hwangjr.rxbus.RxBus;

import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

public class FaxUserManager {
    private static FaxUserManager mInstance;


    private FaxUserManager(){}
    public static FaxUserManager getInstance() {
        if(mInstance == null){
            synchronized (FaxUserManager.class){
                if(mInstance == null){
                    mInstance = new FaxUserManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * 第三方平台一键注册或登录
     * @param context
     * @param snsType
     * @param loginRepoUserInfo

     */
    public void thirdSignUpLogin(final Activity context, String snsType, final LoginRepoUserInfo loginRepoUserInfo) {
        BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(snsType, loginRepoUserInfo.getToken(), loginRepoUserInfo.getExpires(), loginRepoUserInfo.getOpenId());
        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {
            @Override
            public void done(JSONObject user, BmobException e) {
                if (e == null) {
                    ToastShowUtils.showCommonToast(context,"登录成功",Toasty.LENGTH_SHORT);
                    final User curUser = BmobUser.getCurrentUser(User.class);
                    curUser.setAvatarUrl(loginRepoUserInfo.getAvatarUrl());
                    curUser.setGender(loginRepoUserInfo.getGender());
                    curUser.setUserNick(loginRepoUserInfo.getNickname());
                    curUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
//                                ToastShowUtils.showCommonToast(context,"数据更新成功",Toasty.LENGTH_SHORT);
                                RxBus.get().post(EventMsg.sign_in_suc_notify_refresh_profile,"");
                            } else {
                                ToastShowUtils.showCommonToast(context,"数据更新失败",Toasty.LENGTH_SHORT);
                                Log.e("error", e.getMessage());
                            }
                        }
                    });


                } else {
                    Log.e("BMOB", e.toString());
                    ToastShowUtils.showCommonToast(context,e.getMessage(),Toasty.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * 同步控制台数据到缓存中
     */
    private void fetchUserInfo() {
        BmobUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {
            @Override
            public void done(BmobUser user, BmobException e) {
                if (e == null) {
//                    final MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
//                    Snackbar.make(view, "更新用户本地缓存信息成功："+myUser.getUsername()+"-"+myUser.getAge(), Snackbar.LENGTH_LONG).show();
                } else {
                    Log.e("error",e.getMessage());
//                    Snackbar.make(view, "更新用户本地缓存信息失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * 获取控制台最新JSON数据
     */
    private void fetchUserJsonInfo() {
        BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String json, BmobException e) {
                if (e == null) {
                    Log.e("success",json);
                } else {
                    Log.e("error",e.getMessage());
                }
            }
        });
    }
    /**
     * 更新用户操作并同步更新本地的用户信息
     */
    private void updateUser() {
        final User user = BmobUser.getCurrentUser(User.class);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
//                    Snackbar.make(view, "更新用户信息成功：" + user.getAge(), Snackbar.LENGTH_LONG).show();
                } else {
//                    Snackbar.make(view, "更新用户信息失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    Log.e("error", e.getMessage());
                }
            }
        });
    }

    /**
     * 获取本地用户对象
     * @return
     */
    public User getUserBean(){
        return BmobUser.getCurrentUser(User.class);
    }

    /**
     * 退出登录
     */
    public void logOut(){
        BmobUser.logOut();
    }


}
