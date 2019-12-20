package com.fax.lib.config;

import android.text.TextUtils;

public class CheckStringKeyBroadConfigure extends AbsStringKeyBroadConfigure {

    private final AbsStringKeyBroadConfigure mCore;

    CheckStringKeyBroadConfigure(AbsStringKeyBroadConfigure core) {
        mCore = core;
    }

    @Override
    public void put(String key, Object value) {
        checkKey(key);
        mCore.put(key, value);
    }

    @Override
    public Object get(String key) {
        checkKey(key);
        return mCore.get(key);
    }

    private static void checkKey(String key) {
        if (TextUtils.isEmpty(key)) {
            throw new RuntimeException("key: " + key + " is a invalid key");
        }
    }
}
