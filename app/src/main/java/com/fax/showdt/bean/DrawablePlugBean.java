package com.fax.showdt.bean;


import com.fax.showdt.view.sticker.DrawableSticker;

/**
 * Created by fax on 19-4-25.
 */

public class DrawablePlugBean extends BasePlugBean {
    private String drawablePath;
    private String name;
    private String svgName;
    private boolean isShowFrame;
    @DrawableSticker.PicType
    private int mPicType;
    private String svgColor ="#FFFFFF";

    public String getDrawablePath() {
        return drawablePath;
    }

    public void setDrawablePath(String drawablePath) {
        this.drawablePath = drawablePath;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSvgName() {
        return svgName == null ? "" : svgName;
    }

    public void setSvgName(String svgName) {
        this.svgName = svgName;
    }

    public boolean isShowFrame() {
        return isShowFrame;
    }

    public void setShowFrame(boolean showFrame) {
        isShowFrame = showFrame;
    }

    public void setmPicType(int mPicType) {
        this.mPicType = mPicType;
    }

    public int getmPicType() {
        return mPicType;
    }

    public String getSvgColor() {
        return svgColor;
    }

    public void setSvgColor(String svgColor) {
        this.svgColor = svgColor;
    }
}