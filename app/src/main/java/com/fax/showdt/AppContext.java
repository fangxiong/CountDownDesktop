package com.fax.showdt;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.fax.lib.config.ConfigManager;
import com.fax.showdt.activity.MainActivity;
import com.fax.showdt.utils.Environment;
import com.fax.showdt.utils.OkHttpClientHelper;
import com.meituan.android.walle.WalleChannelReader;
import com.simple.spiderman.SpiderMan;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.zhy.http.okhttp.OkHttpUtils;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import org.xml.sax.ErrorHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.v3.Bmob;
import es.dmoral.toasty.Toasty;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import jp.wasabeef.takt.Audience;
import jp.wasabeef.takt.Seat;
import jp.wasabeef.takt.Takt;
import me.jessyan.autosize.AutoSize;

public class AppContext extends Application {
    private static Application mContext;
    public synchronized static Application get() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        AutoSize.initCompatMultiProcess(this);
        SpiderMan.init(this);
        Environment.init(mContext);
        ConfigManager.init(mContext);
        Bmob.initialize(this, ConstantString.BMOB_APP_ID);
//        initTakt();
        String processName = getProcessName();
        if (!TextUtils.isEmpty(processName) &&processName.equals(this.getPackageName())) {
            Log.i("test_init_application:","初始化主进程");
            OkHttpUtils.initClient(OkHttpClientHelper.getOkHttpClient());

            FeedbackAPI.init(this);
            Toasty.Config.getInstance()
                    .allowQueue(false)
                    .apply();
            initBugly();
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

        //初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
        //建议在宿主App的Application.onCreate函数中调用基础组件库初始化函数。
        UMConfigure.init(this, ConstantString.UMENG_APP_KEY, WalleChannelReader.getChannel(this,"home"), UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);

    }

    private void initBugly(){
        CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(this);
        userStrategy.setAppChannel(WalleChannelReader.getChannel(this,"home"));
        userStrategy.setAppVersion(BuildConfig.VERSION_NAME);
        userStrategy.setAppPackageName(BuildConfig.APPLICATION_ID);
//        CrashReport.initCrashReport(getApplicationContext(), ConstantString.BUGLY_APP_ID, false);
        CrashReport.setIsDevelopmentDevice(this,BuildConfig.DEBUG);
        Beta.canShowUpgradeActs.add(MainActivity.class);
        Beta.autoCheckUpgrade = false;
        Bugly.init(getApplicationContext(), ConstantString.BUGLY_APP_ID, BuildConfig.DEBUG);

    }


    /**
     * 初始化Takt 检测FPS
     */
    private void initTakt(){
        Takt.stock(this)
                .seat(Seat.TOP_RIGHT)
                .interval(250)
                .color(Color.YELLOW)
                .size(14f)
                .showOverlaySetting(true)
                .listener(new Audience() {
                    @Override
                    public void heartbeat(double fps) {
                        Log.d("Excellent!", fps+ " fps");
                    }
                });

    }


    @Override
    public void onTerminate() {
        Takt.finish();
        super.onTerminate();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
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
