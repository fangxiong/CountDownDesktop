package com.fax.cddt.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import java.util.ArrayList;

public class CommonUtils {

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

    public  static String toHexEncoding(int color) {
        String A,R, G, B;
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
}
