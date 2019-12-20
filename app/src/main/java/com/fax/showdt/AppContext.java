package com.fax.showdt;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.fax.lib.config.ConfigManager;
import com.fax.showdt.utils.OkHttpClientHelper;
import com.zhy.http.okhttp.OkHttpUtils;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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
        ConfigManager.init(mContext);
        String processName = getProcessName();
        if (!TextUtils.isEmpty(processName) &&processName.equals(this.getPackageName())) {
            Log.i("test_init_application:","初始化主进程");
            OkHttpUtils.initClient(OkHttpClientHelper.getOkHttpClient());
            MultiDex.install(this);
            FeedbackAPI.init(this);
            Toasty.Config.getInstance()
                    .allowQueue(false)
                    .apply();
        }else {
            Log.i("test_init_application:","初始化子进程");
            OkHttpUtils.initClient(OkHttpClientHelper.getOkHttpClient());
            Toasty.Config.getInstance()
                    .allowQueue(false)
                    .apply();
        }

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

     public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
