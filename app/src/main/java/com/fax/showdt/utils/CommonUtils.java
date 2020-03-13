package com.fax.showdt.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.fax.showdt.AppContext;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {

    public static final int START_QQ_TYPE_CHAT = 1;
    public static final int START_QQ_TYPE_PERSON_PROFILE = 2;
    public static final int START_QQ_TYPE_GROUP_PROFILE = 3;
    public static final String QQ_APP_ID = "com.tencent.mobileqq";

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (TextUtils.isEmpty(ServiceName)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(200);
        if (runningService != null && runningService.size() > 0) {
            for (int i = 0; i < runningService.size(); i++) {
                if (runningService.get(i).service.getClassName().toString()
                        .equals(ServiceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String toHexEncoding(int color) {
        String A, R, G, B;
        StringBuffer sb = new StringBuffer();
        A = Integer.toHexString(Color.alpha(color));
        R = Integer.toHexString(Color.red(color));
        G = Integer.toHexString(Color.green(color));
        B = Integer.toHexString(Color.blue(color));
        //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
        A = A.length() == 1 ? "0" + A : A;
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append("#");
        sb.append(A);
        sb.append(R);
        sb.append(G);
        sb.append(B);
        return sb.toString();
    }

    public static boolean startQQ(Context context, int type, CharSequence qqnum) {
        String url = null;
        switch (type) {
            case START_QQ_TYPE_CHAT:
                // 直接进入QQ聊天(对QQ号)
                url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqnum;
                break;
            case START_QQ_TYPE_PERSON_PROFILE:
                // 打开个人介绍界面（对QQ号）
                url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + qqnum
                        + "&card_type=person&source=qrcode";
                break;
            case START_QQ_TYPE_GROUP_PROFILE:
                // 打开QQ群介绍界面(对QQ群号)
                url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + qqnum
                        + "&card_type=group&source=qrcode";
                break;
        }

        if (url == null) {
            return false;
        }

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage(QQ_APP_ID);
        return startActivity(context, intent);
    }

    public static boolean startActivity(Context context, Intent intent) {
        return startActivity(context, intent, null);
    }

    public static boolean startActivity(Context context, Intent intent, @Nullable Bundle options) {
        // 如果context不是Activity，必须加FLAG_ACTIVITY_NEW_TASK，否则会崩溃
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // 避免有些情况下ActivityNotFoundException使整个程序崩溃
        try {
            ActivityCompat.startActivity(context, intent, options);
            return true;
        } catch (Throwable tr) {
        }
        return false;
    }

    public static Bitmap getAssetPic(Context context,String fileName) {
        Bitmap bitmap = null;
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            //filename是assets目录下的图片名
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

        /**
     * 判断是否安装了QQ
     *
     * @param context
     * @return
     */
    public static boolean isQQAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 跳转应用市场
     * @param packageName 包名
     */
    public static void goAppMarket(String packageName) {
        //获取包名packageName
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(AppContext.get(), goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
