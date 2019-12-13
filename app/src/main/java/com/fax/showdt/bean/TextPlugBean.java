package com.fax.showdt.bean;

import android.text.Layout;

public class TextPlugBean extends BasePlugBean {

    private String text;
    private String fontPath;
    private String shimmerColor;
    private boolean isShimmerText;
    private int alignment;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFontPath() {
        return fontPath;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }

    public String getShimmerColor() {
        return shimmerColor;
    }

    public void setShimmerColor(String shimmerColor) {
        this.shimmerColor = shimmerColor;
    }

    public boolean isShimmerText() {
        return isShimmerText;
    }

    public void setShimmerText(boolean shimmerText) {
        isShimmerText = shimmerText;
    }

    public void setAlignment(Layout.Alignment alignment) {
        if (alignment == null) {
            this.alignment = 0;
        } else if (alignment == Layout.Alignment.ALIGN_NORMAL) {
            this.alignment = 1;
        } else if (alignment == Layout.Alignment.ALIGN_CENTER) {
            this.alignment = 2;
        } else {
            this.alignment = 3;
        }
    }

    public Layout.Alignment getAlignment() {
        if (alignment == 1) {
            return Layout.Alignment.ALIGN_NORMAL;
        } else if (alignment == 2) {
            return Layout.Alignment.ALIGN_CENTER;
        } else if (alignment == 3) {
            return Layout.Alignment.ALIGN_OPPOSITE;
        } else {
            return null;
        }
    }

}
