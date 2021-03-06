package com.fax.showdt.bean;


import com.fax.showdt.view.sticker.DrawableSticker;

import java.util.ArrayList;
import java.util.List;

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
    private float shapeWidthRatio;
    private float cornerRatio;
    private float strokeRatio;
    private boolean isGradient;
    private List<Integer> gradientColors = new ArrayList<>();
    private int gradientOrientation;
    private int strokeWidth;
    private boolean stroke;

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

    public float getShapeWidthRatio() {
        return shapeWidthRatio == 0 ? 1 : shapeWidthRatio;
    }

    public void setShapeWidthRatio(float shapeWidthRatio) {
        this.shapeWidthRatio = shapeWidthRatio;
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

    public boolean isGradient() {
        return isGradient;
    }

    public void setGradient(boolean gradient) {
        isGradient = gradient;
    }

    public List<Integer> getGradientColors() {
        return gradientColors;
    }

    public void setGradientColors(List<Integer> gradientColors) {
        this.gradientColors = gradientColors;
    }

    public int getGradientOrientation() {
        return gradientOrientation;
    }

    public void setGradientOrientation(int gradientOrientation) {
        this.gradientOrientation = gradientOrientation;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public boolean isStroke() {
        return stroke;
    }

    public void setStroke(boolean stroke) {
        this.stroke = stroke;
    }
}