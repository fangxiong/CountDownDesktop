package com.fax.showdt.bean;


import com.fax.showdt.db.DIYConverters;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class CustomWidgetConfig extends Bean implements Comparable<CustomWidgetConfig> {


    @PrimaryKey
    private long id;

    private String title;

    private String desc;

    private int version;//当前版本制作

    private String bgPath;

    private int defaultScale;

    private int baseOnWidthPx;

    private int baseOnHeightPx;

    private String coverUrl;

    private long createdTime;

    private boolean drawWithBg;

    private String userIcon;
    private String userNick;

    @TypeConverters({DIYConverters.TextPlugListConverters.class})
    private List<TextPlugBean> textPlugList;

    @TypeConverters({DIYConverters.ProgressPlugListConverters.class})
    private List<ProgressPlugBean> progressPlugList;

    @TypeConverters({DIYConverters.DrawablePlugListConverters.class})
    private List<DrawablePlugBean> drawablePlugList;

    public List<TextPlugBean> getTextPlugList() {
        if (textPlugList == null) {
            return new ArrayList<>();
        }
        return textPlugList;
    }

    public void setTextPlugList(List<TextPlugBean> textPlugList) {
        this.textPlugList = textPlugList;
    }

    public List<ProgressPlugBean> getProgressPlugList() {
        if (progressPlugList == null) {
            return new ArrayList<>();
        }
        return progressPlugList;
    }

    public void setProgressPlugList(List<ProgressPlugBean> progressPlugList) {
        this.progressPlugList = progressPlugList;
    }

    public List<DrawablePlugBean> getDrawablePlugList() {
        if (drawablePlugList == null) {
            return new ArrayList<>();
        }
        return drawablePlugList;
    }

    public void setDrawablePlugList(List<DrawablePlugBean> drawablePlugList) {
        this.drawablePlugList = drawablePlugList;
    }

    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getBgPath() {
        return bgPath == null ? "" : bgPath;
    }

    public void setBgPath(String bgPath) {
        this.bgPath = bgPath;
    }

    public int getDefaultScale() {
        return defaultScale;
    }

    public void setDefaultScale(int defaultScale) {
        this.defaultScale = defaultScale;
    }

    public int getBaseOnWidthPx() {
        return baseOnWidthPx;
    }

    public void setBaseOnWidthPx(int baseOnWidthPx) {
        this.baseOnWidthPx = baseOnWidthPx;
    }

    public int getBaseOnHeightPx() {
        return baseOnHeightPx;
    }

    public void setBaseOnHeightPx(int baseOnHeightPx) {
        this.baseOnHeightPx = baseOnHeightPx;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }


    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public int compareTo(@NonNull CustomWidgetConfig o) {
        long data = o.getCreatedTime() - this.createdTime;
        int result;
        if (data > 0) {
            result = 1;
        } else if (data == 0) {
            result = 0;
        } else {
            result = -1;
        }
        return result;

    }

    public String getCoverUrl() {
        return coverUrl == null ? "" : coverUrl;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public String getDesc() {
        return desc == null ? "" : desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDrawWithBg(boolean drawWithBg) {
        this.drawWithBg = drawWithBg;
    }

    public boolean isDrawWithBg() {
        return drawWithBg;
    }
}
