package com.fax.showdt.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import org.apache.commons.io.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.List;

import androidx.annotation.Nullable;

@TargetApi(Constant.MIN_SDK_VERSION)
public class FileExUtils {

    public static final String MODE_READWRITE = "rw";
    public static final String MODE_READONLY = "r";

    public static final String SCHEME_ASSET = "asset";
    public static final String SCHEME_FILE = "file";
    public static final String SCHEME_ANDROID_RESOURCE = "android.resource";

    /**
     * mode = 777 or 752 and so on. <br />
     * mode的三个数字，分别表示owner,group,others所具备的权限。<br />
     * 1＝width 执行<br />
     * 2＝w 写 <br />
     * 4＝r 读
     *
     * @author iooly
     */
    public static enum Permission {
        PRIVATE("600"), WOLED_WRITE("666"), WOLED_READ("644"), WOLED_READ_WRITE("666");
        private String value;

        Permission(String value) {
            this.value = value;
        }
    }

    public static final void setPermission(File file, Permission permission) {
        setPermission(file, permission.value);
    }

    public static final void setPermission(File file, String permission) {
        try {
            Runtime.getRuntime().exec("chmod " + permission + " " + file.getAbsolutePath());
        } catch (Exception e) {
        }
    }

    public static boolean getFileStatus(String path, FileStatus status) {
        final File file = safeNewFile(path);
        if (exists(file)) {
            status.mtime = file.lastModified();
            return true;
        }
        return false;
    }

    public static final void setPermission(String filePath, int permission) {
        setPermission(safeNewFile(filePath), permission);
    }

    public static final void setPermission(File file, int permission) {
        setPermission(file, String.format("%o", permission));
    }

    public static boolean isSDCardExists() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkDir(String dirPath) {
        return checkDir(safeNewFile(dirPath));
    }

    public static boolean checkDir(File dir) {
        if (dir == null) {
            return false;
        }
        boolean result;
        if (!dir.exists()) {
            result = dir.mkdirs();
        } else {
            result = dir.isDirectory() || (FileUtils.deleteQuietly(dir) && dir.mkdirs());
        }
        return result;
    }

    public static boolean checkWritableDir(String dirPath) {
        return checkWritableDir(safeNewFile(dirPath));
    }

    public static boolean checkWritableDir(File dir) {
        return checkDir(dir) && dir.canWrite();
    }

    /**
     * <p style="color:#ff0000;font-weight:bolder;">
     * 一定要用这个函数生成缓存文件, 不然在某些系统上生成的缓存文件可能无权限访问
     * </p>
     *
     * @param context Context
     * @param prefix  String
     * @param suffix  String
     * @return File
     */
    public static File createTempFile(Context context, String prefix, String suffix) {

        File tmpFile = null;

        try {
            tmpFile = File.createTempFile(prefix, suffix);
        } catch (Exception ex) {
        }

        if (tmpFile == null) {
            File tmpDir = context.getCacheDir();
            if (tmpDir != null) {
                try {
                    tmpFile = File.createTempFile(prefix, suffix, tmpDir);
                } catch (Exception ex) {
                }
            }
        }

        if (tmpFile == null) {

            File sdcard = Environment.getExternalStorageDirectory();

            if (sdcard != null) {
                File tmpDir = new File(sdcard.getAbsoluteFile() + File.separator + "iooly"
                        + File.separator + ".tmp");
                try {
                    tmpDir.mkdirs();
                    tmpFile = File.createTempFile(prefix, suffix, tmpDir);
                } catch (Exception ex) {
                }
            }
        }

        return tmpFile;
    }

    public static boolean exists(File file) {
        return file != null && file.exists();
    }

    public static boolean exists(String filePath) {
        return exists(safeNewFile(filePath));
    }

    public static boolean isFile(File file) {
        return exists(file) && file.isFile();
    }

    public static boolean isReadableFile(String path) {
        return isReadableFile(new File(path));
    }

    public static boolean isReadableFile(File file) {
        return exists(file) && file.isFile() && file.canRead();
    }

    public static boolean isDirectory(File file) {
        return exists(file) && file.isDirectory();
    }

    public static InputStream openInputStream(Context context, Uri uri)
            throws FileNotFoundException {
        String scheme = uri.getScheme();
        InputStream in = null;
        try {
            switch (scheme) {
                case SCHEME_ASSET: {
                    try {
                        in = context.getAssets().open(uri.getHost() + uri.getPath());
                    } catch (Exception ex1) {
                        FileNotFoundException ex2 = new FileNotFoundException("Asset File Not Found: " + uri);
                        ex2.initCause(ex1);
                    }
                    break;
                }
                case SCHEME_FILE: {
                    try {
                        in = FileUtils.openInputStream(new File(uri.getPath()));
                    } catch (Exception ex1) {
                        FileNotFoundException ex2 = new FileNotFoundException("File Not Found: " + uri);
                        ex2.initCause(ex1);
                    }
                    break;
                }
                case SCHEME_ANDROID_RESOURCE: {
                    OpenResourceIdResult r = getResourceId(context, uri);
                    try {
                        in = r.r.openRawResource(r.id);
                    } catch (Exception ex1) {
                        FileNotFoundException ex2 = new FileNotFoundException("File Not Found: " + uri);
                        ex2.initCause(ex1);
                        throw new FileNotFoundException("Resource does not exist: " + uri);
                    }
                    break;
                }

            }
        } catch (FileNotFoundException ex) {
            QuietFinalUtils.close(in);
            in = null;
        }
        if (in == null) {
            in = context.getContentResolver().openInputStream(uri);
        }
        return in;
    }

    private static OpenResourceIdResult getResourceId(Context context, Uri uri) throws FileNotFoundException {
        String authority = uri.getAuthority();
        Resources r = null;
        if (TextUtils.isEmpty(authority)) {
            throw new FileNotFoundException("No authority: " + uri);
        } else {
            try {
                PackageManager pm = context.getPackageManager();
                if (pm != null) {
                    r = pm.getResourcesForApplication(authority);
                }
            } catch (PackageManager.NameNotFoundException ex) {
                throw new FileNotFoundException("No package found for authority: " + uri);
            }
        }
        List<String> path = uri.getPathSegments();
        if (path == null) {
            throw new FileNotFoundException("No path: " + uri);
        }
        int len = path.size();
        int id;
        if (len == 1) {
            try {
                id = Integer.parseInt(path.get(0));
            } catch (NumberFormatException e) {
                throw new FileNotFoundException("Single path segment is not a resource ID: " + uri);
            }
        } else if (len == 2) {
            id = r.getIdentifier(path.get(1), path.get(0), authority);
        } else {
            throw new FileNotFoundException("More than two path segments: " + uri);
        }
        if (id == 0) {
            throw new FileNotFoundException("No resource found for: " + uri);
        }
        OpenResourceIdResult res = new OpenResourceIdResult();
        res.r = r;
        res.id = id;
        return res;
    }



    public static void sync(FileOutputStream out) {
        if (out != null) {
            try {
                out.flush();
            } catch (IOException e1) {
            }
            try {
                out.getFD().sync();
            } catch (Exception e) {
            }
        }
    }


    private static class OpenResourceIdResult {
        public Resources r;
        public int id;
    }

    @Nullable
    private static File safeNewFile(@Nullable String path) {
        return path == null ? null : new File(path);
    }

//    public static boolean copyFile(String srcPath, File dst) {
//        return copyFile(safeNewFile(srcPath), dst);
//    }

    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }




    public static long getFileSize(File f) throws Exception {
        FileChannel fc= null;
        long size = 0;
        try {
            if (f.exists() && f.isFile()) {
                FileInputStream fis = new FileInputStream(f);
                fc = fis.getChannel();
                size = fc.size();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (null!=fc){
                try{
                    fc.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

        /**
         * File status information. This class maps directly to the POSIX stat structure.
         */
    private static final class FileStatus {
        public int dev;
        public int ino;
        public int mode;
        public int nlink;
        public int uid;
        public int gid;
        public int rdev;
        public long size;
        public int blksize;
        public long blocks;
        public long atime;
        public long mtime;
        public long ctime;
    }

    public static String getJsonFromAssest(Context context, String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
