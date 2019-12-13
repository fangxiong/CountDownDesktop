package com.fax.showdt.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;

import com.fax.showdt.BuildConfig;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;

@TargetApi(Constant.MIN_SDK_VERSION)
public class Environment {
    private static final String TAG = "test_env";

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    public synchronized static void init(@NonNull Application application) {
        sApplication = application;
    }

    @Nullable
    public static File getAppDataDir(String name) {
        final File dir = getApplication().getDir(name, Context.MODE_PRIVATE);
        if (!FileExUtils.checkWritableDir(dir)) {
            return null;
        }
        return dir;
    }

    public static File getFilesDir() {
        final File filesDir = getInternalFilesDir();
        if (filesDir == null) {
            return getExternalFilesDir();
        }
        return filesDir;
    }

    @Nullable
    public static File getInternalFilesDir() {
        final File filesDir = sApplication.getFilesDir();
        if (!FileExUtils.checkWritableDir(filesDir)) {
            return null;
        }
        return filesDir;
    }

    @Nullable
    public static File getExternalFilesDir() {
        final File filesDir = sApplication.getExternalFilesDir(null);
        if (!FileExUtils.checkWritableDir(filesDir)) {
            return null;
        }
        return filesDir;
    }

    public static File getShareFilesProviderDir() {
        final String dirName = "share_files";
        File dir = getAppDataDir(dirName);
        if (dir == null) {
            File filesDir = getInternalFilesDir();
            if (filesDir != null) {
                dir = new File(filesDir, dirName);
                if (!FileExUtils.checkWritableDir(dir)) {
                    dir = null;
                }
            }
        }
        if (dir == null) {
            File homeDir = getHomeDir();
            if (homeDir != null) {
                dir = new File(homeDir, dirName);
                if (!FileExUtils.checkWritableDir(dir)) {
                    dir = null;
                }
            }
        }
        return dir;
    }

    @Nullable
    public static File getCacheDir() {
        return getCacheDir(null);
    }

    @Nullable
    public static File getCacheDir(@Nullable String name) {
        final Context context = getApplication();
        if (context == null) {
            return null;
        }
        File parentDir = context.getCacheDir();
        if (!FileExUtils.checkWritableDir(parentDir)) {
            parentDir = context.getExternalCacheDir();
            if (!FileExUtils.checkWritableDir(parentDir)) {
                parentDir = getDir("cache");
            }
        }
        if (!FileExUtils.checkWritableDir(parentDir)) {
            return null;
        }
        return name == null ? parentDir : getDir(parentDir, name);
    }

    @Nullable
    public static File getHomeDir() {
        final Context context = getApplication();
        if (context == null) {
            return null;
        }
        File homeDir = checkSdcardHomeDir(context);
        if (homeDir == null) {
            homeDir = checkInternalHomeDir(context);
        }
        return homeDir;
    }

    @Nullable
    public static File getHtmlDir() {
        final File parent = getDocumentsDir();
        if (parent == null) {
            return null;
        }
        final File dir = new File(parent, "html");
        if (!FileExUtils.checkWritableDir(dir)) {
            return null;
        }
        return dir;
    }

    @Nullable
    public static File getExternalHomeDir() {
        return checkSdcardHomeDir(getApplication());
    }

    @Nullable
    public static File getInternalHomeDir() {
        return checkInternalHomeDir(getApplication());
    }

    @Nullable
    public static File getDir(@NonNull String name) {
        return getDir(getHomeDir(), name);
    }

    @Nullable
    public static File getExternalDir(@NonNull String name) {
        return getDir(getExternalHomeDir(), name);
    }

    @Nullable
    public static File getInternalDir(@NonNull String name) {
        return getDir(getInternalHomeDir(), name);
    }

    @Nullable
    private static File getDir(@Nullable File parentDir, @NonNull String name) {
        if (parentDir == null) {
            return null;
        }
        final File dir = new File(parentDir, name);
        return FileExUtils.checkWritableDir(dir) ? dir : null;
    }

    public static File getSdcardDir(String name) {
        final File parentDir = getSdcardDir();
        if (parentDir == null) {
            return null;
        }
        final File dir = new File(parentDir, name);
        return FileExUtils.checkWritableDir(dir) ? dir : null;
    }

    public static File getSdcardDir() {
        if (FileExUtils.isSDCardExists()) {
            final File dir = getExternalStorageDirectory();
            if (FileExUtils.checkWritableDir(dir)) {
                return dir;
            }
        }
        return null;
    }

    public static File getDownloadsDir() {
        final File dir = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        return FileExUtils.checkWritableDir(dir) ? dir : null;
    }

    public static File getPicturesDir() {
        final File dir = getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        return FileExUtils.checkWritableDir(dir) ? dir : null;
    }

    public static File getMusicDir() {
        final File dir = getExternalStoragePublicDirectory(DIRECTORY_MUSIC);
        return FileExUtils.checkWritableDir(dir) ? dir : null;
    }

    public static File getMoviesDir() {
        final File dir = getExternalStoragePublicDirectory(DIRECTORY_MOVIES);
        return FileExUtils.checkWritableDir(dir) ? dir : null;
    }

    public static File getDocumentsDir() {
        final File dir = getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS);
        return FileExUtils.checkWritableDir(dir) ? dir : null;
    }

    public static File getDCIMDir() {
        final File dir = getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        return FileExUtils.checkWritableDir(dir) ? dir : null;
    }

    public static File getCameraDir() {
        final File parentDir = getDCIMDir();
        if (parentDir == null) {
            return null;
        }
        final File dir = new File(parentDir, "Camera");
        return FileExUtils.checkWritableDir(dir) ? dir : null;
    }

    @Nullable
    private static File checkSdcardHomeDir(@Nullable final Context context) {
        if (context == null) {
            return null;
        }
        try {
            if (FileExUtils.isSDCardExists()) {
                File homeDir = new File(android.os.Environment.getExternalStorageDirectory(), Constant.SD_HOME);
                if (!FileExUtils.checkWritableDir(homeDir)) {
                    homeDir = context.getExternalFilesDir(null);
                    if (!FileExUtils.checkWritableDir(homeDir)) {
                        return null;
                    }
                }
                return homeDir;
            }
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
            }
        }
        return null;
    }

    @Nullable
    private static File checkInternalHomeDir(@Nullable final Context context) {
        if (context == null) {
            return null;
        }
        try {
            final File homeDir = context.getDir(Constant.SD_HOME, Context.MODE_PRIVATE);
            if (!FileExUtils.checkWritableDir(homeDir)) {
                return null;
            }
            return homeDir;
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
            }
        }
        return null;
    }

    private synchronized static Application getApplication() {
        return sApplication;
    }

}
