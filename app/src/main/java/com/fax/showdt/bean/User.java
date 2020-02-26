package com.fax.showdt.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created on 2018/11/22 18:01
 *
 * @author zhangchaozhou
 */
public class User extends BmobUser {


    /**
     * 昵称
     */
    private String userNick;

    /**
     * 国家
     */

    private String gender;

    /**
     * 得分数
     */
    private String avatarUrl;

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
