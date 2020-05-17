package com.fax.showdt.bean;

import android.text.Layout;
import android.text.TextUtils;

public class TextPlugBean extends BasePlugBean {

    private String text;
    private String fontPath;
    private String shimmerColor;
    private boolean isShimmerText;
    private int alignment;
    private float letterSpacing;
    private float lineSpacing;
    private boolean isShadow ;
    private float shadowRadius;
    private float shadowX;
    private float shadowY;
    private String shadowColor;

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

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public boolean isShadow() {
        return isShadow;
    }

    public void setShadow(boolean shadow) {
        isShadow = shadow;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    public float getShadowX() {
        return shadowX;
    }

    public void setShadowX(float shadowX) {
        this.shadowX = shadowX;
    }

    public float getShadowY() {
        return shadowY;
    }

    public void setShadowY(float shadowY) {
        this.shadowY = shadowY;
    }

    public String getShadowColor() {
        return TextUtils.isEmpty(shadowColor) ? "#FFFFFF" : shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.shadowColor = shadowColor;
    }
}


