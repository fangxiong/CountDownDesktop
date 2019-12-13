package com.fax.showdt.permission;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.util.Arrays;
import java.util.List;

/**
 * @className: PermissionUtils
 * @classDescription:
 * @author: Pan_
 * @createTime: 2018/10/25
 */

public final class PermissionUtils {
    /**
     * 是否是6.0以上版本
     */
    static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否是8.0以上版本
     */
    static boolean isOverOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    static List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return null;
        }
    }



    /**
     * 检测权限有没有在清单文件中注册
     *
     * @param activity              Activity对象
     * @param requestPermissions    请求的权限组
     */
    static void checkPermissions(Activity activity, List<String> requestPermissions) {
        List<String> manifest = PermissionUtils.getManifestPermissions(activity);
        if (manifest != null && manifest.size() != 0) {
            for (String permission : requestPermissions) {
                if (!manifest.contains(permission)) {
                    throw new RuntimeException("you must add this permission:"+permission+" to AndroidManifest");
                }
            }
        }
    }

    /**
     * 是否有【安装】权限
     */
    static boolean isHasInstallPermission(Context context) {
        if (isOverOreo()) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 是否有【悬浮窗权限/显示在应用上层】权限
     */
    public static boolean isHasOverlaysPermission(Context context) {
        if (isOverMarshmallow()) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 是否有【查看应用使用情况】权限
     * @param context
     * @return
     */
    public static boolean isUsagePermissionGranted(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 1;
            if (appOps != null) {
                mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
            }
            return mode == AppOpsManager.MODE_ALLOWED;
        }else {
            return true;
        }
    }

    public static boolean isHasElfPermission(Context context){
        boolean hasPermissions = EasyPermission.isPermissionGrant(context,getElfPermission());
        if (!hasPermissions) {
            return false;
        }
        return true;
    }

    public static String[] getElfPermission(){
        final String[] perms = new String[]{Permission.WRITE_EXTERNAL_STORAGE,
                Permission.READ_PHONE_STATE, Permission.ACCESS_COARSE_LOCATION,
                Permission.ACCESS_FINE_LOCATION};
        return perms;
    }
}