package com.fax.showdt.callback;

public interface OnCropImgCallback{
    void cropSuc(String path);
    void cropFail(Throwable throwable);
}