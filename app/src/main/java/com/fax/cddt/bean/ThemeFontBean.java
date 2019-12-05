package com.fax.cddt.bean;


/**
 * Created by fax on 19-7-5.
 */
public class ThemeFontBean extends Bean {
    private int fid;
    private String name;
    private String srcName;
    private boolean forVip;
    private String md5;
    private String url;
    private String fontPath;
    private String icon;
    private long size;
    private String pvIcon;
    private String dlIcon;

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public boolean getForVip() {
        return forVip;
    }

    public void setForVip(boolean forVip) {
        this.forVip = forVip;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFontPath() {
        return fontPath;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPvIcon() {
        return pvIcon;
    }

    public void setPvIcon(String pvIcon) {
        this.pvIcon = pvIcon;
    }

    public String getDlIcon() {
        return dlIcon;
    }

    public void setDlIcon(String dlIcon) {
        this.dlIcon = dlIcon;
    }
}
