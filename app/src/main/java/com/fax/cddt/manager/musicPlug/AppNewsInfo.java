package com.fax.cddt.manager.musicPlug;


import android.graphics.Bitmap;

import com.fax.cddt.bean.Bean;

/**
 * Created by fax on 19-7-25.
 */
public class AppNewsInfo extends Bean {
    private String senderName;
    private String senderNews;
    private Bitmap senderIcon;
    private String senderIconPath;
    private long sendTime;
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getSenderNews() {
        return senderNews;
    }

    public void setSenderNews(String senderNews) {
        this.senderNews = senderNews;
    }

    public Bitmap getSenderIcon() {
        return senderIcon;
    }

    public void setSenderIcon(Bitmap senderIcon) {
        this.senderIcon = senderIcon;
    }

    public String getSenderIconPath() {
        return senderIconPath;
    }

    public void setSenderIconPath(String senderIconPath) {
        this.senderIconPath = senderIconPath;
    }
}
