package com.fax.showdt.manager.musicPlug;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fax on 19-7-26.
 */
public class DiskLruCacheUtils {


    /**
     * @param cacheDirName - 缓存的最终目录文件夹名称
     * @return - 获取硬盘缓存的目录
     */
    public static File getDiskLruCacheDir(Context context, String cacheDirName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File file = new File(cachePath + "/" + cacheDirName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }






    /**
     * @param key
     * @return 对key进行MD5加密并返回加密过的散列值
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


}
