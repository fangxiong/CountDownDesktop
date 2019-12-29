package com.fax.showdt;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.fax.lib.config.ConfigManager;
import com.fax.showdt.utils.Environment;
import com.fax.showdt.utils.OkHttpClientHelper;
import com.simple.spiderman.SpiderMan;
import com.zhy.http.okhttp.OkHttpUtils;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import org.xml.sax.ErrorHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import es.dmoral.toasty.Toasty;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import jp.wasabeef.takt.Audience;
import jp.wasabeef.takt.Seat;
import jp.wasabeef.takt.Takt;

public class AppContext extends Application {
    private static Application mContext;
    public synchronized static Application get() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SpiderMan.init(this);
        Environment.init(mContext);
        ConfigManager.init(mContext);
        initTakt();
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
        //全局处理Rxjava异常导致的崩溃
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
            }
        });

    }

    /**
     * 初始化Takt 检测FPS
     */
    private void initTakt(){
        Takt.stock(this)
                .seat(Seat.BOTTOM_RIGHT)
                .interval(250)
                .color(Color.YELLOW)
                .size(14f)
                .listener(new Audience() {
                    @Override
                    public void heartbeat(double fps) {
                        Log.d("Excellent!", fps+ " fps");
                    }
                });

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
