package com.fax.showdt.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.File;
import java.net.URISyntaxException;

import static com.fax.showdt.utils.CommonUtils.startActivity;


public class IntentUtils {

    private static final String URI_HEAD_QQ = "http://qm.qq.com/cgi-bin/qm/qr?k=";
    private static final String PKG_QQ = "com.tencent.mobileqq";

    public static boolean sendFile(File file, Context context) {
        final String fileName = file.getName();
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("subject", fileName);
        intent.putExtra("body", "发送的内容:"); // 正文
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); // 添加附件，附件为file对象
        if (fileName.endsWith(".gz")) {
            intent.setType("application/x-gzip"); // 如果是gz使用gzip的mime
        } else if (fileName.endsWith(".txt") || fileName.endsWith(".log")) {
            intent.setType("text/plain"); // 纯文本则用text/plain的mime
        } else if (fileName.endsWith(".zip")) {
            intent.setType("application/zip"); // 纯文本则用text/plain的mime
        } else {
            intent.setType("application/octet-stream"); // 其他的均使用流当做二进制数据来发送
        }
        return startActivity(context, Intent.createChooser(intent, "选择发送方式"));
    }
    /**
     * 双管齐下, 刷新图库
     *
     * @param context
     * @param filePath
     */
    public static void refreshMedia(final Context context, String filePath, String mimeType,
                                    MediaScannerConnection.OnScanCompletedListener callback) {

        if (TextUtils.isEmpty(filePath)) {
            if (callback != null) {
                callback.onScanCompleted(filePath, null);
            }
            return;
        }

        final File file = new File(filePath);
        if (!FileExUtils.isFile(file)) {
            if (callback != null) {
                callback.onScanCompleted(filePath, null);
            }
            return;
        }

        try {
            if (RomUtils.isOnePlus()||RomUtils.isVivo()||RomUtils.isEmui()||RomUtils.isOppo()) {
                android.os.Handler handler = new android.os.Handler(context.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        context.sendBroadcast(intent);
                    }
                }, 2000);
            }else {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                context.sendBroadcast(intent);
            }
        } catch (Throwable tr) {
          tr.printStackTrace();
        }
        try {
            MediaScannerConnection.scanFile(context, new String[]{filePath}, new String[]{mimeType}, callback);
        } catch (Throwable tr) {
            if (callback != null) {
                callback.onScanCompleted(filePath, null);
            }
        }

    }

    /**
     * 加 QQ 群
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        try {
            startActivity(context, intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void shareText(Context context, String title, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(context, Intent.createChooser(intent, title));
    }

    public static void openURL(Context context, String url) {

        if (url == null) {
            return;
        }

        url = url.trim();

        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(context, intent);
    }



    public static ActivityInfo getDefaultLauncher(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            return intent.resolveActivityInfo(context.getPackageManager(),
                    PackageManager.GET_SHARED_LIBRARY_FILES);
        } catch (Exception ignore) {
        }
        return null;
    }

    public static boolean toLauncher(Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        return startActivity(context, intent);
    }

    public static Intent fromString(String string) {
        Intent intent = null;
        if (string != null) {
            try {
                intent = Intent.parseUri(string, 0);
            } catch (URISyntaxException e) {
            }
        }
        return intent;
    }

    public static String toString(Intent intent) {
        return intent == null ? null : intent.toUri(0);
    }

    @Nullable
    public static Intent getAppLaunchIntent(Context context, String packageName) {
        try {
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                return pm.getLaunchIntentForPackage(packageName);
            }
        } catch (Throwable tr) {
        }
        return null;
    }

    /**
     * 获取指定版本号的应用启动 Intent
     *
     * @param context 当前上下文
     * @param packageName 指定『应用』的包名
     * @param versionCode 最新的版本号
     * @return 启动 Intent
     */
    @Nullable
    public static Intent getAppLaunchIntentLatest(Context context, String packageName, int versionCode) {
        try {
            final PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                    if (packageInfo.versionCode == versionCode) return intent;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
