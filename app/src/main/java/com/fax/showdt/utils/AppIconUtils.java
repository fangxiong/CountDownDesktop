package com.fax.showdt.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.fax.showdt.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-30
 * Description:
 */
public class AppIconUtils {

    public static List<AppInfo> getInstallApps(Context mContext) {
        ArrayList<AppInfo> list = new ArrayList<AppInfo>();
        try {
            PackageManager pm = mContext.getPackageManager();
            List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

            for (PackageInfo packageInfo : installedPackages) {
                AppInfo info = new AppInfo();
                String packageName = packageInfo.packageName;
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                String name = applicationInfo.loadLabel(pm).toString();
                Drawable icon = applicationInfo.loadIcon(pm);
                info.name = name;
                info.packageName = packageName;
                info.icon = icon;

                int flags = applicationInfo.flags;

                if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo
                        .FLAG_SYSTEM) {
                    info.isUser = false;
                } else {
                    info.isUser = true;
                }

                list.add(info);
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据包名获取应用名称
     * @param context
     * @param pkgName
     * @return
     */
    public static  String getAppName(Context context,String pkgName) {
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
