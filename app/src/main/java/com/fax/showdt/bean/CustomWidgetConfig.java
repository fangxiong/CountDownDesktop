package com.fax.showdt.bean;



import com.fax.showdt.db.DIYConverters;
import com.fax.showdt.db.FontConverters;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class CustomWidgetConfig extends Bean implements Comparable<CustomWidgetConfig> {

    @TypeConverters({DIYConverters.TextPlugListConverters.class})
    private List<TextPlugBean> textPlugList;

    @TypeConverters({DIYConverters.LinePlugListConverters.class})
    private List<LinePlugBean> linePlugList;

    @TypeConverters({DIYConverters.ProgressPlugListConverters.class})
    private List<ProgressPlugBean> progressPlugList;

    @TypeConverters({DIYConverters.DrawablePlugListConverters.class})
    private List<DrawablePlugBean> drawablePlugList;

    String title;

    String desc;

    @PrimaryKey
    private long id;

    private int version;

    private String previewPath;

    private int defaultScale;

    private int textSize;

    private int baseOnWidthPx;

    private int baseOnHeightPx;

    private String coverUrl;

    private long createdTime;

    private int originX;

    private int originY;

    private boolean forVip;

    private boolean isFromFeatured;


    @TypeConverters({FontConverters.class})
    private ThemeFontBean fontInfo;

    public List<TextPlugBean> getTextPlugList() {
        if (textPlugList == null) {
            return new ArrayList<>();
        }
        return textPlugList;
    }

    public void setTextPlugList(List<TextPlugBean> textPlugList) {
        this.textPlugList = textPlugList;
    }

    public List<LinePlugBean> getLinePlugList() {
        if (linePlugList == null) {
            return new ArrayList<>();
        }
        return linePlugList;
    }

    public void setLinePlugList(List<LinePlugBean> linePlugList) {
        this.linePlugList = linePlugList;
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

    public String getPreviewPath() {
        return previewPath == null ? "" : previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public int getDefaultScale() {
        return defaultScale;
    }

    public void setDefaultScale(int defaultScale) {
        this.defaultScale = defaultScale;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
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

    public void setOriginX(int originX) {
        this.originX = originX;
    }

    public void setOriginY(int originY) {
        this.originY = originY;
    }

    public String getCoverUrl() {
        return coverUrl == null ? "" : coverUrl;
    }


    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
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


    public boolean isFromFeatured() {
        return isFromFeatured;
    }

    public void setFromFeatured(boolean fromFeatured) {
        isFromFeatured = fromFeatured;
    }


    public ThemeFontBean getFontInfo() {
        return fontInfo;
    }

    public void setFontInfo(ThemeFontBean fontInfo) {
        this.fontInfo = fontInfo;
    }

    public boolean isForVip() {
        return forVip;
    }

    public void setForVip(boolean forVip) {
        this.forVip = forVip;
    }

}