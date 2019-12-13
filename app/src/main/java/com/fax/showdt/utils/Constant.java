package com.fax.showdt.utils;

import android.os.Build;

import com.fax.showdt.BuildConfig;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.TimeZone;

public interface Constant {
    /**
     * 默认的编码名称
     */
    String CHARSET_NAME = CharEncoding.UTF_8;

    /**
     * 默认的编码
     */
    Charset CHARSET = Charsets.UTF_8;

    /**
     * 默认的本地区域
     */
    Locale LOCALE = Locale.SIMPLIFIED_CHINESE;

    /**
     * 默认的本地时区
     */
    TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+0800");

    /**
     * 程序最小支持的 SDK
     */
    int MIN_SDK_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    /**
     * ActivityContext 容器 Activity 的 ACTION
     */
    String CONTAINER_ACTION = BuildConfig.APPLICATION_ID + ".CONTEXT_CONTAINER";

    /**
     * 发送闪退页面 Action
     */
    String CRASH_REPORT_ACTIVITY_ACTION = BuildConfig.APPLICATION_ID + ".action.CRASH";

    /**
     * 点击多少下触发 "发送闪退" 页面
     */
    int DEBUG_MAX_CLICK_COUNT = 10;

    String SD_HOME = "maimob";

}
