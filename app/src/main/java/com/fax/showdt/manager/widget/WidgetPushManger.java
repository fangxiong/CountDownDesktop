package com.fax.showdt.manager.widget;

import android.text.TextUtils;
import android.util.Log;

import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.DrawablePlugBean;
import com.fax.showdt.manager.FaxUserManager;
import com.fax.showdt.utils.Environment;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.MD5Utils;
import com.fax.showdt.view.sticker.DrawableSticker;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadBatchListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WidgetPushManger {


    private static volatile WidgetPushManger mInstance;
    private Disposable disposable;

    private WidgetPushManger(){}

    public static WidgetPushManger getInstance() {
        if(mInstance == null){
            synchronized (WidgetPushManger.class){
                if(mInstance == null){
                    mInstance = new WidgetPushManger();
                }
            }
        }
        return mInstance;
    }


    public void startPost(final AppCompatActivity context, final CustomWidgetConfig config, final PushWidgetCallback callback){
        callback.pushStart();
        Observable.create(new ObservableOnSubscribe<CustomWidgetConfig>() {
            @Override
            public void subscribe(ObservableEmitter<CustomWidgetConfig> emitter) throws Exception {
                postAllFile(config,emitter);
            }
        }) .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CustomWidgetConfig>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                           disposable = d;
                    }

                    @Override
                    public void onNext(CustomWidgetConfig config) {
                        disposable.dispose();
                        callback.pushSuc();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.pushFail(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void postAllFile(final CustomWidgetConfig config,final ObservableEmitter<CustomWidgetConfig> emitter) throws ZipException{
        final String filePath_Cover = config.getCoverUrl();
        final String filePath_Zip = zipImgList(config);
        final String[] filePaths = new String[filePath_Zip == null ? 1 : 2];
        filePaths[0] = filePath_Cover;
        if(filePath_Zip != null){
            filePaths[1] = filePath_Zip;
        }
        Log.i("WidgetPushManger:","filePath_Cover:"+filePath_Cover);

        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files,List<String> urls) {
                //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                //2、urls-上传文件的完整url地址
                if(urls.size()==filePaths.length){//如果数量相等，则代表文件全部上传完成
                    config.setCoverUrl(urls.get(0));
                    if(filePath_Zip != null) {
                        config.setZipImgUrl(urls.get(1));
                        FileExUtils.deleteSingleFile(filePath_Zip);
                    }else {
                        config.setZipImgUrl("");
                    }
                    emitter.onNext(config);
                }
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                Log.i("错误码"+statuscode +",错误描述：",errormsg);
                emitter.onError(new Exception(errormsg));
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total,int totalPercent) {
                //1、curIndex--表示当前第几个文件正在上传
                //2、curPercent--表示当前上传文件的进度值（百分比）
                //3、total--表示总的上传文件数
                //4、totalPercent--表示总的上传进度（百分比）
            }
        });
    }


    /**
     * 打包图片到本地zip包 并上传本地压缩包
     */
    private String zipImgList(CustomWidgetConfig config) throws ZipException {
        List<File> fileList = new ArrayList<>();
        List<DrawablePlugBean> iconList = config.getDrawablePlugList();
        for (DrawablePlugBean bean : iconList) {
            if(DrawableSticker.SDCARD == bean.getmPicType()) {
                String path = bean.getDrawablePath();
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(path);
                    if (FileExUtils.exists(file)) {
                        fileList.add(file);
                        bean.setName(file.getName());
                    }
                }
            }
        }
        File bgFile = new File(config.getBgPath());
        fileList.add(new File(config.getBgPath()));
        config.setBgPath(bgFile.getName());
        if(fileList.size() == 0){
            return null;
        }

        String zipFileName = MD5Utils.getMD5(FaxUserManager.getInstance().getUserBean().getObjectId() + "_assets") + ".zip";
        File file = new File(Environment.getHomeDir(), zipFileName);
        if (FileExUtils.exists(file)) {
            file.delete();
        }
        zip(fileList,file.getAbsolutePath());
        return file.getAbsolutePath();
    }



    private void zip(List<File> list, String destFilePath)  throws ZipException {
        ZipFile zipFile = new ZipFile(destFilePath);
        ZipParameters par = new ZipParameters();
        par.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        par.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        for (File file : list){
            zipFile.addFile(file,par);
        }

    }

    public interface PushWidgetCallback{
        void pushStart();
        void pushSuc();
        void pushFail(String errorMsg);
    }
}
