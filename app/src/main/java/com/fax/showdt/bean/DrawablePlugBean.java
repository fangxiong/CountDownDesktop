package com.fax.showdt.bean;


import com.fax.showdt.view.sticker.DrawableSticker;

/**
 * Created by fax on 19-4-25.
 */

public class DrawablePlugBean extends BasePlugBean {
    private String drawablePath;
    private String name;
    private String clipType;
    private boolean isShowFrame;
    @DrawableSticker.PicType
    private int mPicType;
    private String strokeColor ="#FFFFFF";
    private String drawableColor ="#FFFFFF";
    private float shapeHeightRatio;
    private float cornerRatio;
    private float strokeRatio;

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

    public void setClipType(String clipType) {
        this.clipType = clipType;
    }

    public String getClipType() {
        return clipType == null ? " ": clipType;
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

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public String getDrawableColor() {
        return drawableColor;
    }

    public void setDrawableColor(String drawableColor) {
        this.drawableColor = drawableColor;
    }

    public float getShapeHeightRatio() {
        return shapeHeightRatio;
    }

    public void setShapeHeightRatio(float shapeHeightRatio) {
        this.shapeHeightRatio = shapeHeightRatio;
    }

    public float getCornerRatio() {
        return cornerRatio;
    }

    public void setCornerRatio(float cornerRatio) {
        this.cornerRatio = cornerRatio;
    }

    public float getStrokeRatio() {
        return strokeRatio;
    }

    public void setStrokeRatio(float strokeRatio) {
        this.strokeRatio = strokeRatio;
    }
}