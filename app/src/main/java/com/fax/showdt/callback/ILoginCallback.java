package com.fax.showdt.callback;


import com.fax.showdt.bean.LoginRepoUserInfo;

/**
 * 三方登录授权回调
 */
public interface ILoginCallback {
    void authorizeSuc(LoginRepoUserInfo info);

    void authorizeFail(String msg);

}
