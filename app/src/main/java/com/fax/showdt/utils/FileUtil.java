package com.fax.showdt.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;


public class FileUtil {

    /**
     * 根据Uri获取图片路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(
                        c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

    public static String checkSuffix(String path){
        if (TextUtils.isEmpty(path)){
            return null;
        }
        int index = path.lastIndexOf("@!bbs");
        if (index !=-1){
            return path.substring(0,index);
        }
        return path;
    }


    public static String getSuffix(String path, String defaultSuffix) {
        String suffix = getSuffix(path);
        return suffix != null ? suffix : defaultSuffix;
    }

    public static String getSuffix(String path) {
        if (path == null) {
            return null;
        }
        int len = path.length();
        int index = path.lastIndexOf(".");
        if (index < 0 || (index = index + 1) >= len) {
            return null;
        }
        return path.substring(index);
    }

    /**
     * 复制文件
     *
     * @param src 源文件
     * @param dst 目标文件
     * @return
     */
    public static boolean copyTo(File src, File dst) {

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(src);
            fo = new FileOutputStream(dst);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {

                if (fi != null) {
                    fi.close();
                }

                if (in != null) {
                    in.close();
                }

                if (fo != null) {
                    fo.close();
                }

                if (out != null) {
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * 获取图片保存文件夹路径
     *
     * @return
     */
    public static File getSaveDirPath(String name) {
        return Environment.getSdcardDir(name);
    }


    public static boolean CopyAssets(Context context, String assetDir, String dir) {
        String[] files;
        try {
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return false;
        }

        File mWorkingPath = new File(dir);
        if (!mWorkingPath.exists()) {
            if (!mWorkingPath.mkdirs()) {

            }
        }
        for (int i = 0; i < files.length; i++) {
            try {
                String fileName = files[i];
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        CopyAssets(context, fileName, dir + fileName + "/");
                    } else {
                        CopyAssets(context, assetDir + "/" + fileName, dir+ fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists())
//                    outFile.delete();
                    continue;
                InputStream in = null;
                if (0 != assetDir.length())
                    in = context.getAssets().open(assetDir + "/" + fileName);
                else
                    in = context.getAssets().open(fileName);
                OutputStream out = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 安全删除文件.
     * @param file
     * @return
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    public static boolean deleteFile(String path){
        if (TextUtils.isEmpty(path)){
            return false;
        }
        return deleteFile(new File(path));
    }

    public static boolean deleteFile(File file){
        if (!FileExUtils.exists(file)){
            return false;
        }
        return file.delete();
    }


    /**
     * 复制asset文件到指定目录
     * @param oldPath  asset下的路径
     * @param newPath  SD卡下保存路径
     */
    public static boolean CopyAssetsFile(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    CopyAssetsFile(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @Description 得到文件所在路径（即全路径去掉完整文件名）
     * @param filepath 文件全路径名称，like mnt/sda/XX.xx
     * @return 根路径，like mnt/sda
     */
    public static String getPathFromFilepath(final String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(0, pos);
        }
        return "";
    }

    /**
     * @Description 重新整合路径，将路径一和路径二通过'/'连接起来得到新路径
     * @param path1 路径一
     * @param path2 路径二
     * @return 新路径
     */
    public static String makePath(final String path1, final String path2) {
        if (path1.endsWith(File.separator)) {
            return path1 + path2;
        }
        return path1 + File.separator + path2;
    }


    public static String getFileName(String pathandname){
        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        if (start!=-1 && end!=-1) {
            return pathandname.substring(start+1, end);
        }
        else {
            return null;
        }
    }
}
