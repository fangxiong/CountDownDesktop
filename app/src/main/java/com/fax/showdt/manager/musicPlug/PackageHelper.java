package com.fax.showdt.manager.musicPlug;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PackageHelper {

    public static String getBroadReceiverPackage(Context context, String str) {
        if (!TextUtils.isEmpty(str)) {
            Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
            List<ResolveInfo> arrayList = new ArrayList();
            try {
                arrayList = context.getPackageManager().queryBroadcastReceivers(intent, 0);
            } catch (RuntimeException e) {
                try {
                    arrayList = context.getPackageManager().queryBroadcastReceivers(intent, 0);
                } catch (Throwable e2) {
                    Log.i("PackageHelper", "Unable to get broadcast receivers list", e2);
                }
            }
            for (ResolveInfo resolveInfo : arrayList) {
                if (str.equals(resolveInfo.activityInfo.packageName)) {
                    return resolveInfo.activityInfo.name;
                }
            }
        }
        return "";
    }
}
