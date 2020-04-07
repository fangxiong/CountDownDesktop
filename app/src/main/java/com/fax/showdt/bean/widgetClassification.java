package com.fax.showdt.bean;

import cn.bmob.v3.BmobObject;

public class widgetClassification extends BmobObject {

    private String cName;
    private String cid;
    private int sort;

    public widgetClassification(){
        this.setTableName("widgetClassification");
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
