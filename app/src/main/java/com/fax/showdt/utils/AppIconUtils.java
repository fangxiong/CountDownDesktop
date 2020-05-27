package com.fax.showdt.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.fax.showdt.bean.AppInfo;
import com.fax.showdt.bean.AppInfoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-30
 * Description:
 */
public class AppIconUtils {

    public static List<AppInfoData> getInstallApps(Context mContext) {
        ArrayList<AppInfoData> list = new ArrayList<AppInfoData>();
        try {
            Intent intent = new Intent();
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setAction(Intent.ACTION_MAIN);

            PackageManager pm = mContext.getPackageManager();
            List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : resolveInfoList) {
                AppInfoData info = new AppInfoData();
                String packageName = resolveInfo.activityInfo.packageName;
                String name = resolveInfo.loadLabel(pm).toString();
                Drawable icon = resolveInfo.loadIcon(pm);
                info.name = name;
                info.packageName = packageName;
                info.icon = icon;
                list.add(info);

            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据包名获取应用名称
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static String getAppName(Context context, String pkgName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    pkgName, 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
