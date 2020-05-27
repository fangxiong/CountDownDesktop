package com.fax.showdt.bean;

import android.graphics.drawable.Drawable;

import com.fax.showdt.view.cnPinyin.CN;


public class AppInfoData implements CN {
    public String name;
    public String packageName;
    public Drawable icon;
    public String drawablePath;
    public boolean isUser;

    public String getName() {
        return name == null ? "" : name;
    }

    public String getPackageName() {
        return packageName == null ? "" : packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getDrawablePath() {
        return drawablePath;
    }

    public void setDrawablePath(String drawablePath) {
        this.drawablePath = drawablePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    @Override
    public String chinese() {
        return name;
    }
}
