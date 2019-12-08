package com.fax.cddt;

import android.app.Application;
import android.content.Context;

import com.fax.cddt.utils.OkHttpClientHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import androidx.multidex.MultiDex;
import okhttp3.OkHttpClient;

public class AppContext extends Application {
    private static Application mContext;
    public synchronized static Application get() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        OkHttpUtils.initClient(OkHttpClientHelper.getOkHttpClient());
        MultiDex.install(this);

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
