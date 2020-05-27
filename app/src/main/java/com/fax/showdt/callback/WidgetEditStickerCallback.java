package com.fax.showdt.callback;

public interface WidgetEditStickerCallback {

    void onAddSticker(String path,String pkgName,String appName);
    void closePanel();
    void onPickPhoto();
}
