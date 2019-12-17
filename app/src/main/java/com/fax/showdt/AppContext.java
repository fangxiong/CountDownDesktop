package com.fax.showdt;

import android.app.Application;
import android.content.Context;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.fax.showdt.utils.OkHttpClientHelper;
import com.zhy.http.okhttp.OkHttpUtils;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import es.dmoral.toasty.Toasty;

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
        FeedbackAPI.init(this);
        Toasty.Config.getInstance()
       .allowQueue(false)
       .apply();

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
