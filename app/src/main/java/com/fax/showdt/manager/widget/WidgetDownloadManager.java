package com.fax.showdt.manager.widget;

import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.fax.showdt.AppContext;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.DrawablePlugBean;
import com.fax.showdt.bean.WidgetConfig;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.view.sticker.DrawableSticker;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import io.reactivex.disposables.Disposable;

public class WidgetDownloadManager {

    private static volatile WidgetDownloadManager mInstance;
    private File mParentFile,mSaveFile;
    private WidgetDownloadManager() {
    }

    public static WidgetDownloadManager getInstance() {
        if (mInstance == null) {
            synchronized (WidgetDownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new WidgetDownloadManager();
                }
            }
        }
        return mInstance;
    }


    public void startDownloadWidget(final WidgetConfig config, final DownloadWidgetCallback callback){
        final CustomWidgetConfig customWidgetConfig = GsonUtils.parseJsonWithGson(config.getConfig(),CustomWidgetConfig.class);
        if(TextUtils.isEmpty(customWidgetConfig.getZipImgUrl())){
            CustomWidgetScreenAdaptHelper helper = new CustomWidgetScreenAdaptHelper(AppContext.get());
            helper.adaptConfig(customWidgetConfig);
            callback.downloadSuc();
            return;
        }
        BmobFile bmobfile = new BmobFile("xxx.zip", "",customWidgetConfig.getZipImgUrl() );
        File mWidgetDirFile = new File(com.fax.showdt.utils.Environment.getHomeDir(), ".widget_file");
        FileExUtils.checkDir(mWidgetDirFile);
        mParentFile = new File(mWidgetDirFile, config.getObjectId());
        if(FileExUtils.exists(mParentFile)){
            List<DrawablePlugBean> drawablePlugBeans = customWidgetConfig.getDrawablePlugList();
            for(DrawablePlugBean drawablePlugBean : drawablePlugBeans){
                if(DrawableSticker.SDCARD == drawablePlugBean.getmPicType()) {
                    drawablePlugBean.setDrawablePath(mParentFile.getAbsolutePath() + File.separator + drawablePlugBean.getName());
                }
            }
            customWidgetConfig.setBgPath((mParentFile.getAbsolutePath() + File.separator + customWidgetConfig.getBgPath()));
            Log.i("WidgetDownloadManager:",customWidgetConfig.toJSONString());
            CustomWidgetScreenAdaptHelper helper = new CustomWidgetScreenAdaptHelper(AppContext.get());
            helper.adaptConfig(customWidgetConfig);
            config.setConfig(customWidgetConfig.toJSONString());
            callback.downloadSuc();
            return;
        }
        FileExUtils.checkDir(mParentFile);
        mSaveFile = new File(mParentFile, "widget_assets.zip");
        bmobfile.download(mSaveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                callback.downloadStart();
                Log.i("WidgetDownloadManager","开始下载...");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
                    Log.i("WidgetDownloadManager","下载成功,保存路径:" + savePath);
                    try {
                        boolean result = unZipFile(savePath, mParentFile.getAbsolutePath());
                        if(result){
                            FileExUtils.deleteSingleFile(savePath);
                            List<DrawablePlugBean> drawablePlugBeans = customWidgetConfig.getDrawablePlugList();
                            for(DrawablePlugBean drawablePlugBean : drawablePlugBeans){
                                if(DrawableSticker.SDCARD == drawablePlugBean.getmPicType()) {
                                    drawablePlugBean.setDrawablePath(mParentFile.getAbsolutePath() + File.separator + drawablePlugBean.getName());
                                }
                            }
                            customWidgetConfig.setBgPath((mParentFile.getAbsolutePath() + File.separator + customWidgetConfig.getBgPath()));
                            Log.i("WidgetDownloadManager:",customWidgetConfig.toJSONString());
                            CustomWidgetScreenAdaptHelper helper = new CustomWidgetScreenAdaptHelper(AppContext.get());
                            helper.adaptConfig(customWidgetConfig);
                            config.setConfig(customWidgetConfig.toJSONString());
                            callback.downloadSuc();
                        }else {
                            callback.downloadFail("解压失败");
                        }
                    }catch (ZipException error){
                        error.printStackTrace();
                    }
                } else {
                    callback.downloadFail("下载失败");
                    Log.i("WidgetDownloadManager","下载失败：" + e.getErrorCode() + "," + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                callback.downloadProgress(value);
                Log.i("bmob", "下载进度：" + value + "," + newworkSpeed);
            }

        });
    }

    private boolean unZipFile(String zipfile,String dest) throws ZipException {
        ZipFile zfile = new ZipFile(zipfile);
        zfile.setFileNameCharset("UTF-8");
        if (!zfile.isValidZipFile()) {
            throw new ZipException("压缩文件不合法，可能已经损坏！");
        }
        File file = new File(dest);
        if (file.isDirectory() && !file.exists()) {
            file.mkdirs();
        }
        ProgressMonitor monitor = zfile.getProgressMonitor();
        try{
            zfile.extractAll(dest);
        }catch (Exception e){
            monitor.cancelAllTasks();
            return false;
        }
        return true;
    }

    public interface DownloadWidgetCallback{
        void downloadStart();
        void downloadSuc();
        void downloadProgress(int progress);
        void downloadFail(String errorMsg);
    }
}
